package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder;

import be.heh.backendappspringbootsnmp.domain.entities.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.OIDPersistenceAdaptater;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.javatuples.Pair;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.VariableBinding;

import java.util.*;

@RequiredArgsConstructor
public class SnmpListener implements ResponseListener {
    @Getter
    private final OIDPersistenceAdaptater oidPersistenceAdaptater;
    @Getter
    private List<MachineEntity> machineEntities = new ArrayList<>();
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
        if(event.getResponse()!=null){
            System.out.println("Received message : "+event.getResponse());
            if(event.getResponse().getErrorStatus() == 0){
                dispatchVariableBindings(event.getResponse().getVariableBindings());
                if(!getSystemVariableBindings().isEmpty() && getSystemVariableBindings().size()!=1){
                    processSystemVariableBindings();
                    Pair<String, Boolean> currentPair = getRequestController().get(event.getResponse().getRequestID().getValue());
                    getRequestController().put(event.getResponse().getRequestID().getValue(),currentPair.setAt1(true));
                }
                if(!getInterfacesVariableBindings().isEmpty()){
                    processInterfacesVariableBindings(getHostnameFromVariableBindings(event.getResponse().getVariableBindings()));
                }
                if(!getMaterialsVariableBindings().isEmpty()){
                    processMaterialsVariableBindings(getHostnameFromVariableBindings(event.getResponse().getVariableBindings()));
                }
                if(!getServicesVariableBindings().isEmpty()){
                    processServicesVariableBindings(getHostnameFromVariableBindings(event.getResponse().getVariableBindings()));
                }
            }else {
                System.err.println("Error status in response PDU : "+event.getResponse().getErrorStatus()+" => "+event.getResponse().getErrorStatusText());
            }
        } else{
            //System.out.println("host is not reachable or incompatible with SNMPv1");
        }
        getLockResponseCounter().increment();
    }

    private void dispatchVariableBindings(List<?extends VariableBinding> variableBindings){
        initManagerController();
        clearVariableBindings();
        variableBindings.forEach(variableBinding -> {
            getOidPersistenceAdaptater().getMoManagers().forEach(moManager -> {
                if(variableBinding.getOid().format().contains(moManager.getOidRoot())){
                    //System.out.println("find a manager to dispatch => "+variableBinding.getOid().format()+" : "+moManager.getOidRoot());
                    //System.out.println("case : "+getManagerController().get(moManager.getName()));
                    switch (getManagerController().get(moManager.getName())){
                        case 1:{getSystemVariableBindings().add(variableBinding);break;}
                        case 2:{getInterfacesVariableBindings().add(variableBinding);break;}
                        case 3:{getMaterialsVariableBindings().add(variableBinding);break;}
                        case 4:{getServicesVariableBindings().add(variableBinding);break;}
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
    private void processSystemVariableBindings(){
        //System.out.println("Process system");
        MOManager sysManager = getOidPersistenceAdaptater().getMOManagerByName("system");
        MachineEntity machineEntity = new MachineEntity("","",true);
        getSystemVariableBindings().forEach(variableBinding -> {
            String oid = variableBinding.getOid().format();
            if(oid.equals(getOidPersistenceAdaptater().getOIDByName("sysHostname",sysManager))){
                machineEntity.setHostname(variableBinding.getVariable().toString());
            }else if(oid.equals(getOidPersistenceAdaptater().getOIDByName("sysOs",sysManager))){
                machineEntity.setOs(variableBinding.getVariable().toString());
            }else {
                System.err.println("Error can't supported OID "+oid+" to process system");
            }
        });
        if(machineEntity.getHostname().isEmpty()|machineEntity.getOs().isEmpty()){}
        else {
            getMachineEntities().add(machineEntity);
        }
    }
    private void processInterfacesVariableBindings(String hostname){
        //System.out.println("Process interfaces");
        if(checkIfPduContainsNumber(getInterfacesVariableBindings(),"ifTable")){
            setIfNumber(getNumberFromVariableBindings(getInterfacesVariableBindings(),"ifTable"));
        }else if(checkIfPduContainsIndex(getInterfacesVariableBindings(),"ifTable")){
            addInterface(hostname,getInterfacesVariableBindings());
        }else {
            System.err.println("Error can't supported the pdu received to process interfaces");
        }
    }
    private void processMaterialsVariableBindings(String hostname){
        //System.out.println("Process materials");
        if(checkIfPduContainsNumber(getMaterialsVariableBindings(),"mProcessorTable") || checkIfPduContainsIndex(getMaterialsVariableBindings(),"mProcessorTable")){
            processProcessor(hostname);
        }else if(checkIfPduContainsNumber(getMaterialsVariableBindings(),"mDiskTable") || checkIfPduContainsIndex(getMaterialsVariableBindings(),"mDiskTable")){
            processDisk(hostname);
        }else if(checkIfPduContainsNumber(getMaterialsVariableBindings(),"mVStorageTable") || checkIfPduContainsIndex(getMaterialsVariableBindings(),"mVStorageTable")){
            processVStorage(hostname);
        }else{
            System.err.println("Error can't supported the pdu received : to process materials");
        }
    }
    private void processProcessor(String hostname) {
        //System.out.println("Process processor");
        String tableName = "mProcessorTable";
        if(checkIfPduContainsNumber(getMaterialsVariableBindings(),tableName)){
            setMProcessorNumber(getNumberFromVariableBindings(getMaterialsVariableBindings(),tableName));
        }else if(checkIfPduContainsIndex(getMaterialsVariableBindings(),tableName)){
            addProcessor(hostname,getMaterialsVariableBindings());
        }else {
            System.err.println("Error the pdu doesn't contains the variables bindings expected to process processor");
        }
    }
    private void processDisk(String hostname) {
        //System.out.println("Process disk");
        String tableName = "mDiskTable";
        if(checkIfPduContainsNumber(getMaterialsVariableBindings(),tableName)){
            setMPersiStorageNumber(getNumberFromVariableBindings(getMaterialsVariableBindings(),tableName));
        }else if(checkIfPduContainsIndex(getMaterialsVariableBindings(),tableName)){
            addDisk(hostname,getMaterialsVariableBindings());
        }else {
            System.err.println("Error the pdu doesn't contains the variables bindings expected to process disk");
        }
    }

    private void processVStorage(String hostname) {
        //System.out.println("Process vStorage");
        String tableName = "mVStorageTable";
        if(checkIfPduContainsNumber(getMaterialsVariableBindings(),tableName)){
            setMVolStorageNumber(getNumberFromVariableBindings(getMaterialsVariableBindings(),tableName));
        }else if(checkIfPduContainsIndex(getMaterialsVariableBindings(),tableName)){
            addVStorage(hostname,getMaterialsVariableBindings());
        }else {
            System.err.println("Error the pdu doesn't contains the variables bindings expected to process volatile storage");
        }
    }
    private void processServicesVariableBindings(String hostname){
        //System.out.println("Process service");
        if(checkIfPduContainsNumber(getServicesVariableBindings(),"sTable")){
            setSNumber(getNumberFromVariableBindings(getServicesVariableBindings(),"sTable"));
        }else if(checkIfPduContainsIndex(getServicesVariableBindings(),"sTable")){
            addService(hostname,getServicesVariableBindings());
        }else{
            System.err.println("Error can't supported the pdu received : to process services");
        }
    }

    private void addInterface(String hostname,List<? extends VariableBinding> parameters){
        MOManager ifManager = getOidPersistenceAdaptater().getMOManagerByName("interfaces");
        Interface domainInterface = new Interface("","","");
        parameters.forEach(variableBinding -> {
            try{
                String oid = variableBinding.getOid().format();
                if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("ifDescription",ifManager))){
                    domainInterface.setDescription(variableBinding.getVariable().toString());
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("ifPhysAddress",ifManager))){
                    domainInterface.setMacAddress(variableBinding.getVariable().toString());
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("ifAddress",ifManager))){
                    domainInterface.setIpAddress(variableBinding.getVariable().toString());
                } else if (oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("ifIndex",ifManager))) {
                    //don't manage id for entity is not registered yet
                } else {
                    System.err.println("Error can't supported OID "+oid+" to add interface");
                }
            }catch (Exception e){
                System.err.println("Error to add interface : "+e.getMessage());
            }
        });
        if(domainInterface.getDescription().isEmpty() || domainInterface.getMacAddress().isEmpty() || domainInterface.getIpAddress().isEmpty()){
            System.err.println("Error has occurred during adding new interface");
        }
        else {
            getMachineEntities().forEach(machineEntity -> {
                if(machineEntity.getHostname().equals(hostname)){
                    machineEntity.getInterfaces().add(domainInterface);
                }
            });
        }
    }
    private void addService(String hostname, List<? extends VariableBinding> parameters) {
        MOManager serviceManager = getOidPersistenceAdaptater().getMOManagerByName("services");
        Service service =new Service("","","");
        parameters.forEach(variableBinding -> {
            try{
                String oid = variableBinding.getOid().format();
                if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("sName",serviceManager))){
                    service.setName(variableBinding.getVariable().toString());
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("sDescription",serviceManager))){
                    service.setDescription(variableBinding.getVariable().toString());
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("sPort",serviceManager))){
                    service.setPort(variableBinding.getVariable().toString());
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("sIndex",serviceManager))){
                    //don't manage index of service is not registered yet
                }else {
                    System.err.println("Error can't supported OID "+oid+" to add service");
                }
            }catch (Exception e){
                System.err.println("Error to add service : "+e.getMessage());
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
        MOManager materialsManager = getOidPersistenceAdaptater().getMOManagerByName("materials");
        Processor processor = new Processor("",0,0,0.0);
        parameters.forEach(variableBinding -> {
            try{
                String oid = variableBinding.getOid().format();
                if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mProcessorRef",materialsManager))){
                    processor.setReference(variableBinding.getVariable().toString());
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mProcessorCore",materialsManager))){
                    processor.setCore(Integer.parseInt(variableBinding.getVariable().toString()));
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mProcessorVCore",materialsManager))){
                    processor.setVCore(Integer.parseInt(variableBinding.getVariable().toString()));
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mProcessorFreq",materialsManager))){
                    processor.setFrequency(Double.valueOf(variableBinding.getVariable().toString()));
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mProcessorIndex",materialsManager))){
                    //don't manage index for a not registered processor
                }else {
                    System.err.println("Error can't supported OID "+oid+" to add processor");
                }
            }catch (Exception e){
                System.err.println("Error to add processor : "+e.getMessage());
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
        MOManager materialsManager = getOidPersistenceAdaptater().getMOManagerByName("materials");
        PersistentStorage persistentStorage = new PersistentStorage("",0.0,0.0);
        parameters.forEach(variableBinding -> {
            try{
                String oid = variableBinding.getOid().format();
                if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mDiskRef",materialsManager))){
                    persistentStorage.setReference(variableBinding.getVariable().toString());
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mDiskAvailable",materialsManager))){
                    persistentStorage.setAvailable(Double.valueOf(variableBinding.getVariable().toString()));
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mDiskUsed",materialsManager))){
                    persistentStorage.setUsed(Double.valueOf(variableBinding.getVariable().toString()));
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mDiskIndex",materialsManager))){
                    //don't manage index for a not registered disk
                }else {
                    System.err.println("Error can't supported OID "+oid+" to add disk");
                }
            }catch (Exception e){
                System.err.println("Error to add disk : "+e.getMessage());
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
        MOManager materialsManager = getOidPersistenceAdaptater().getMOManagerByName("materials");
        VolatileStorage volatileStorage = new VolatileStorage("",0.0,0.0,0.0);
        parameters.forEach(variableBinding -> {
            try{
                String oid = variableBinding.getOid().format();
                if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mVStorageRef",materialsManager))){
                    volatileStorage.setReference(variableBinding.getVariable().toString());
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mVStorageAvailable",materialsManager))){
                    volatileStorage.setAvailable(Double.valueOf(variableBinding.getVariable().toString()));
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mVStorageFreq",materialsManager))){
                    volatileStorage.setFrequency(Double.valueOf(variableBinding.getVariable().toString()));
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mVStorageLatency",materialsManager))){
                    volatileStorage.setLatency(Double.valueOf(variableBinding.getVariable().toString()));
                }else if(oid.equals(getOidPersistenceAdaptater().getOIDColumnByName("mVStorageIndex",materialsManager))){
                    //don't manage index for a not registered VStorage
                }else {
                    System.err.println("Error can't supported OID "+oid+" to add volatile storage");
                }
            }catch (Exception e){
                System.err.println("Error to add VStorage : "+e.getMessage());
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
            if(variableBinding.getOid().format().equals(getOidPersistenceAdaptater().getOIDHostname())){
                return variableBinding.getVariable().toString();
            }
        }
        return "default";
    }
    private int getNumberFromVariableBindings(List<VariableBinding> variableBindings,String tableName){
        for(VariableBinding variableBinding : variableBindings){
            if (variableBinding.getOid().format().equals(getOidPersistenceAdaptater().getOidNumberIndexOfTable(tableName).getValue0())){
                if(!variableBinding.getVariable().toString().equals("Null")){
                    return Integer.parseInt(variableBinding.getVariable().toString());
                }else {
                    return 0;
                }
            }
        }
        return 0;
    }
    private boolean checkIfPduContainsNumber(List<VariableBinding> variableBindings,String tableName){
        String numberOID = oidPersistenceAdaptater.getOidNumberIndexOfTable(tableName).getValue0();
        for(VariableBinding variableBinding : variableBindings){
            if(variableBinding.getOid().format().equals(numberOID)){
                return true;
            }
        }
        return false;
    }
    private boolean checkIfPduContainsIndex(List<VariableBinding> variableBindings, String tableName){
        String indexOID = oidPersistenceAdaptater.getOidNumberIndexOfTable(tableName).getValue1();
        for(VariableBinding variableBinding : variableBindings){
            if(variableBinding.getOid().format().equals(indexOID)){
                return true;
            }
        }
        return false;
    }

}
