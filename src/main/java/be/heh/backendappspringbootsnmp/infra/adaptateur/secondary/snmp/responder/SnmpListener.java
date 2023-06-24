package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder;

import be.heh.backendappspringbootsnmp.domain.entities.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.OIDPersistanceAdaptater;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.javatuples.Pair;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.VariableBinding;

import java.util.*;

@RequiredArgsConstructor
public class SnmpListener implements ResponseListener {
    @Getter
    private final List<MachineEntity> machineEntities;
    @Getter
    private final OIDPersistanceAdaptater oidPersistanceAdaptater;
    @Getter
    private Map<Integer, Pair<String, Boolean>> requestController = new HashMap<>();
    @Getter
    private Map<String, Integer> managerController = new HashMap<>();
    @Setter
    @Getter
    private LockResponseCounter lockResponseCounter;
    @Setter
    @Getter
    private int ifNumber;
    @Setter
    @Getter
    private int mProcessorNumber;
    @Setter
    @Getter
    private int mPersiStorageNumber;
    @Setter
    @Getter
    private int mVolStorageNumber;
    @Setter
    @Getter
    private int sNumber;
    @Getter
    private List<VariableBinding> systemVariableBindings = new ArrayList<>();
    @Getter
    private List<VariableBinding> interfacesVariableBindings = new ArrayList<>();
    @Getter
    private List<VariableBinding> materialsVariableBindings = new ArrayList<>();
    @Getter
    private List<VariableBinding> servicesVariableBindings = new ArrayList<>();

    @Override
    public <A extends Address> void onResponse(ResponseEvent<A> event) {
        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
        System.out.println("Received message : "+event.getResponse());
        if(event.getResponse()!=null){
            if(event.getResponse().getErrorStatus() == 0){
                System.out.println("received message contains pdu : "+event.getResponse());
                dispatchVariableBindings(event.getResponse().getVariableBindings());
                if(!getSystemVariableBindings().isEmpty()){
                    processSystemVariableBindings(event.getResponse());
                    Pair<String, Boolean> currentPair = getRequestController().get(event.getResponse().getRequestID().getValue());
                    getRequestController().put(event.getResponse().getRequestID().getValue(),currentPair.setAt1(true));
                }
                if(!getInterfacesVariableBindings().isEmpty()){
                    processInterfacesVariableBindings(event.getResponse());
                    Pair<String, Boolean> currentPair = getRequestController().get(event.getResponse().getRequestID().getValue());
                    getRequestController().put(event.getResponse().getRequestID().getValue(),currentPair.setAt1(true));
                }
                if(!getMaterialsVariableBindings().isEmpty()){
                    processMaterialsVariableBindings(event.getResponse());
                    Pair<String, Boolean> currentPair = getRequestController().get(event.getResponse().getRequestID().getValue());
                    getRequestController().put(event.getResponse().getRequestID().getValue(),currentPair.setAt1(true));
                }
                if(getServicesVariableBindings().isEmpty()){
                    processServicesVariableBindings(event.getResponse());
                    Pair<String, Boolean> currentPair = getRequestController().get(event.getResponse().getRequestID().getValue());
                    getRequestController().put(event.getResponse().getRequestID().getValue(),currentPair.setAt1(true));
                }
                else{
                    System.err.println("The response is not supported yet");
                    //addUnknownMachineEntity(getRequestController().get(event.getRequest().getRequestID().getValue()).getValue0());
                }
            }else {
                System.err.println("Error status in response PDU : "+event.getResponse().getErrorStatus()+" => "+event.getResponse().getErrorStatusText());
                //addUnknownMachineEntity(getRequestController().get(event.getRequest().getRequestID().getValue()).getValue0());
            }
        } else{
            System.out.println("host is not reachable or incompatible with SNMPv1");
            //addUnknownMachineEntity(getRequestController().get(event.getRequest().getRequestID().getValue()).getValue0());
        }
        getLockResponseCounter().increment();
    }

    private void dispatchVariableBindings(List<?extends VariableBinding> variableBindings){
        initManagerController();
        clearVariableBindings();
        variableBindings.forEach(variableBinding -> {
            getOidPersistanceAdaptater().getMoManagers().forEach(moManager -> {
                if(variableBinding.getOid().format().contains(moManager.getOidRoot())){
                    switch (getManagerController().get(moManager.getName())){
                        case 1:{getSystemVariableBindings().add(variableBinding);}
                        case 2:{getInterfacesVariableBindings().add(variableBinding);}
                        case 3:{getMaterialsVariableBindings().add(variableBinding);}
                        case 4:{getServicesVariableBindings().add(variableBinding);}
                        default:{System.err.println("the variables "+variableBinding.getOid()+" can't be dispatched in list because is not be supported");}
                    }
                }
            });
        });
    }
    private void clearVariableBindings(){
        getSystemVariableBindings().clear();
        getInterfacesVariableBindings().clear();
        getMaterialsVariableBindings().clear();
        getServicesVariableBindings().clear();
    }
    private void initManagerController(){
        if(getManagerController().isEmpty()){
            getManagerController().put("system",1);
            getManagerController().put("interfaces",2);
            getManagerController().put("materials",3);
            getManagerController().put("services",4);
        }
    }

    private void addUnknownMachineEntity(String ipAddr){
        getMachineEntities().add(new MachineEntity("unknown","unknown",false));
    }
    private void processSystemVariableBindings(PDU pdu){
        MOManager sysManager = getOidPersistanceAdaptater().getMOManagerByName("system");
        MachineEntity machineEntity = new MachineEntity("","",true);
        pdu.getVariableBindings().forEach(variableBinding -> {
            String oid = variableBinding.getOid().format();
            if(oid.equals(getOidPersistanceAdaptater().getOIDByName("sysHostname",sysManager))){
                machineEntity.setHostname(variableBinding.getVariable().toString());
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDByName("sysOs",sysManager))){
                machineEntity.setOs(variableBinding.getVariable().toString());
            }else {
                System.err.println("Error can't supported OID "+oid+" to add disk");
            }
        });
        if(machineEntity.getHostname().isEmpty()|machineEntity.getOs().isEmpty()){}
        else {
            getMachineEntities().add(machineEntity);
        }
    }
    private void processInterfacesVariableBindings(PDU pdu){
        String ifNumberOID = oidPersistanceAdaptater.getOIDNumberIndexOFTable("ifTable").getValue0();
        String ifIndexOID = oidPersistanceAdaptater.getOIDNumberIndexOFTable("ifTable").getValue1();
        pdu.getVariableBindings().forEach(variableBinding -> {
            if(variableBinding.getOid().format().equals(ifNumberOID)){
                setIfNumber(variableBinding.getVariable().toInt());
            }else if(variableBinding.getOid().format().equals(ifIndexOID)){
                addInterface(getHostnameFromVariableBindings(pdu.getVariableBindings()),pdu.getVariableBindings());
            }else{
                System.err.println("Error can't supported the pdu received : "+pdu+" to process interfaces");
            }
        });
    }
    private void processMaterialsVariableBindings(PDU pdu){
        Pair<String,String> mProcessorOID = oidPersistanceAdaptater.getOIDNumberIndexOFTable("mProcessorTable");
        Pair<String,String> mDiskOID = oidPersistanceAdaptater.getOIDNumberIndexOFTable("mDiskTable");
        Pair<String,String> mVStorageOID = oidPersistanceAdaptater.getOIDNumberIndexOFTable("mVStorageTable");

        pdu.getVariableBindings().forEach(variableBinding -> {
            if(variableBinding.getOid().format().equals(mProcessorOID.getValue0()) | variableBinding.getOid().format().equals(mProcessorOID.getValue1())){
                processProcessor(pdu.getVariableBindings(),mProcessorOID);
            }else if(variableBinding.getOid().format().equals(mDiskOID.getValue0()) | variableBinding.getOid().format().equals(mDiskOID.getValue1())){
                processDisk(pdu.getVariableBindings(),mDiskOID);
            }else if(variableBinding.getOid().format().equals(mVStorageOID.getValue0()) | variableBinding.getOid().format().equals(mVStorageOID.getValue1())){
                processVStorage(pdu.getVariableBindings(),mVStorageOID);
            }else{
                System.err.println("Error can't supported the pdu received : "+pdu+" to process materials");
            }
        });
    }

    private void processProcessor(List<? extends  VariableBinding> variableBindings,Pair<String,String> numberIndex) {
        variableBindings.forEach(variableBinding -> {
            if(variableBinding.getOid().format().equals(numberIndex.getValue0())){
                setMProcessorNumber(variableBinding.getVariable().toInt());
            }else if(variableBinding.getOid().format().equals(numberIndex.getValue1())){
                addProcessor(getHostnameFromVariableBindings(variableBindings),variableBindings);
            }else {
                System.err.println("Error the pdu doesn't contains the variables bindings expected to process processor");
            }
        });
    }
    private void processDisk(List<? extends  VariableBinding> variableBindings,Pair<String,String> numberIndex) {
        variableBindings.forEach(variableBinding -> {
            if(variableBinding.getOid().format().equals(numberIndex.getValue0())){
                setMPersiStorageNumber(variableBinding.getVariable().toInt());
            }else if(variableBinding.getOid().format().equals(numberIndex.getValue1())){
                addDisk(getHostnameFromVariableBindings(variableBindings),variableBindings);
            }else {
                System.err.println("Error the pdu doesn't contains the variables bindings expected to process disk");
            }
        });
    }

    private void processVStorage(List<? extends  VariableBinding> variableBindings,Pair<String,String> numberIndex) {
        variableBindings.forEach(variableBinding -> {
            if(variableBinding.getOid().format().equals(numberIndex.getValue0())){
                setMVolStorageNumber(variableBinding.getVariable().toInt());
            }else if(variableBinding.getOid().format().equals(numberIndex.getValue1())){
                addVStorage(getHostnameFromVariableBindings(variableBindings),variableBindings);
            }else {
                System.err.println("Error the pdu doesn't contains the variables bindings expected to process volatile storage");
            }
        });
    }
    private void processServicesVariableBindings(PDU pdu){
        String sNumberOID = oidPersistanceAdaptater.getOIDNumberIndexOFTable("sTable").getValue0();
        String sIndexOID = oidPersistanceAdaptater.getOIDNumberIndexOFTable("sTable").getValue1();
        pdu.getVariableBindings().forEach(variableBinding -> {
            if(variableBinding.getOid().format().equals(sNumberOID)){
                setSNumber(variableBinding.getVariable().toInt());
            }else if(variableBinding.getOid().format().equals(sIndexOID)){
                addService(getHostnameFromVariableBindings(pdu.getVariableBindings()),pdu.getVariableBindings());
            }else{
                System.err.println("Error can't supported the pdu received : "+pdu+" to process interfaces");
            }
        });
    }

    private void addInterface(String hostname,List<? extends VariableBinding> parameters){
        MOManager ifManager = getOidPersistanceAdaptater().getMOManagerByName("interfaces");
        Interface domainInterface = new Interface("","","");
        parameters.forEach(variableBinding -> {
            String oid = variableBinding.getOid().format();
            if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("ifDescription",ifManager))){
                domainInterface.setDescription(variableBinding.getVariable().toString());
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("ifPhysAddress",ifManager))){
                domainInterface.setMacAddress(variableBinding.getVariable().toString());
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("ifAddress",ifManager))){
                domainInterface.setIpAddress(variableBinding.getVariable().toString());
            }else {
                System.err.println("Error can't supported OID "+oid+" to add interface");
            }
        });
        if(domainInterface.getDescription().isEmpty() || domainInterface.getMacAddress().isEmpty() || domainInterface.getIpAddress().isEmpty()){}
        else {
            getMachineEntities().forEach(machineEntity -> {
                if(machineEntity.getHostname().equals(hostname)){
                    machineEntity.getInterfaces().add(domainInterface);
                }
            });
        }
    }
    private void addService(String hostname, List<? extends VariableBinding> parameters) {
        MOManager serviceManager = getOidPersistanceAdaptater().getMOManagerByName("services");
        Service service =new Service("","","");
        parameters.forEach(variableBinding -> {
            String oid = variableBinding.getOid().format();
            if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("sName",serviceManager))){
                service.setName(variableBinding.getVariable().toString());
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("sDescription",serviceManager))){
                service.setDescription(variableBinding.getVariable().toString());
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("sPort",serviceManager))){
                service.setPort(variableBinding.getVariable().toString());
            }else {
                System.err.println("Error can't supported OID "+oid+" to add interface");
            }
        });
        if(service.getName().isEmpty() || service.getDescription().isEmpty() || service.getPort().isEmpty()){}
        else {
            getMachineEntities().forEach(machineEntity -> {
                if(machineEntity.getHostname().equals(hostname)){
                    machineEntity.getServices().add(service);
                }
            });
        }
    }
    private void addProcessor(String hostname, List<? extends VariableBinding> parameters){
        MOManager materialsManager = getOidPersistanceAdaptater().getMOManagerByName("materials");
        Processor processor = new Processor("",0,0,0.0);
        parameters.forEach(variableBinding -> {
            String oid = variableBinding.getOid().format();
            if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("mProcessorRef",materialsManager))){
                processor.setReference(variableBinding.getVariable().toString());
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("mProcessorCore",materialsManager))){
                processor.setCore(variableBinding.getVariable().toInt());
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("mProcessorVCore",materialsManager))){
                processor.setVCore(variableBinding.getVariable().toInt());
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("mProcessorFreq",materialsManager))){
                processor.setFrequency(Double.valueOf(variableBinding.getVariable().toString()));
            }else {
                System.err.println("Error can't supported OID "+oid+" to add processor");
            }
        });
        if(processor.getReference().isEmpty()|processor.getCore()==0|processor.getVCore()==0|processor.getFrequency()==0.0){}
        else {
            getMachineEntities().forEach(machineEntity -> {
                if(machineEntity.getHostname().equals(hostname)){
                    machineEntity.getProcessors().add(processor);
                }
            });
        }
    }
    private void addDisk(String hostname, List<? extends VariableBinding> parameters) {
        MOManager materialsManager = getOidPersistanceAdaptater().getMOManagerByName("materials");
        PersistentStorage persistentStorage = new PersistentStorage("",0.0,0.0);
        parameters.forEach(variableBinding -> {
            String oid = variableBinding.getOid().format();
            if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("mDiskRef",materialsManager))){
                persistentStorage.setReference(variableBinding.getVariable().toString());
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("mDiskAvailable",materialsManager))){
                persistentStorage.setAvailable(Double.valueOf(variableBinding.getVariable().toString()));
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("mDiskUsed",materialsManager))){
                persistentStorage.setUsed(Double.valueOf(variableBinding.getVariable().toString()));
            }else {
                System.err.println("Error can't supported OID "+oid+" to add disk");
            }
        });
        if(persistentStorage.getReference().isEmpty()|persistentStorage.getAvailable()==0.0|persistentStorage.getUsed()==0.0){}
        else {
            getMachineEntities().forEach(machineEntity -> {
                if(machineEntity.getHostname().equals(hostname)){
                    machineEntity.getPersistentStorages().add(persistentStorage);
                }
            });
        }
    }
    private void addVStorage(String hostname, List<? extends VariableBinding> parameters) {
        MOManager materialsManager = getOidPersistanceAdaptater().getMOManagerByName("materials");
        VolatileStorage volatileStorage = new VolatileStorage("",0.0,0.0,0.0);
        parameters.forEach(variableBinding -> {
            String oid = variableBinding.getOid().format();
            if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("mVStorageRef",materialsManager))){
                volatileStorage.setReference(variableBinding.getVariable().toString());
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("mVStorageAvailable",materialsManager))){
                volatileStorage.setAvailable(Double.valueOf(variableBinding.getVariable().toString()));
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("mVStorageFreq",materialsManager))){
                volatileStorage.setFrequency(Double.valueOf(variableBinding.getVariable().toString()));
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("mVStorageLatency",materialsManager))){
                volatileStorage.setLatency(Double.valueOf(variableBinding.getVariable().toString()));
            }else {
                System.err.println("Error can't supported OID "+oid+" to add volatile storage");
            }
        });
        if(volatileStorage.getReference().isEmpty()|volatileStorage.getAvailable()==0.0|volatileStorage.getFrequency()==0.0|volatileStorage.getLatency()==0.0){}
        else {
            getMachineEntities().forEach(machineEntity -> {
                if(machineEntity.getHostname().equals(hostname)){
                    machineEntity.getVolatileStorages().add(volatileStorage);
                }
            });
        }
    }
    private String getHostnameFromVariableBindings(List<? extends VariableBinding> variableBindings){
        for(VariableBinding variableBinding : variableBindings){
            if(variableBinding.getOid().equals(getOidPersistanceAdaptater().getOIDHostname())){
                return variableBinding.getVariable().toString();
            }
        }
        return "default";
    }


}
