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
import org.snmp4j.smi.OID;
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
    private int mServiceNumber;
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
        //System.out.println("Received message : "+event.getResponse());
        if(event.getResponse()!=null){
            if(event.getResponse().getErrorStatus() == 0){
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
                    //System.err.println("The response is not supported yet");
                    addUnknownMachineEntity(getRequestController().get(event.getRequest().getRequestID().getValue()).getValue0());
                }
            }else {
                //System.err.println("Error status in response PDU : "+event.getResponse().getErrorStatus()+" => "+event.getResponse().getErrorStatusText());
                addUnknownMachineEntity(getRequestController().get(event.getRequest().getRequestID().getValue()).getValue0());
            }
        } else{
            //System.out.println("host is not reachable or incompatible with SNMPv1");
            addUnknownMachineEntity(getRequestController().get(event.getRequest().getRequestID().getValue()).getValue0());
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
                System.err.println("Error can't supported the pdu received : "+pdu);
            }
        });
    }
    private void processMaterialsVariableBindings(PDU pdu){

    }
    private void processServicesVariableBindings(PDU pdu){

    }
    private void addInterface(String hostname,List<? extends VariableBinding> parameters){
        parameters.forEach(variableBinding -> {
            String oid = variableBinding.getOid().format();
            MOManager ifManager = getOidPersistanceAdaptater().getMOManagerByName("interfaces");
            Interface domainInterface = new Interface("","","");
            if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("ifDescription",ifManager))){
                domainInterface.setDescription(variableBinding.getVariable().toString());
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("ifPhysAddress",ifManager))){
                domainInterface.setMacAddress(variableBinding.getVariable().toString());
            }else if(oid.equals(getOidPersistanceAdaptater().getOIDColumnByName("ifAddress",ifManager))){
                domainInterface.setIpAddress(variableBinding.getVariable().toString());
            }else {
                System.err.println("Error can't supported OID "+oid+" to add interface");
            }
            if(domainInterface.getDescription().isEmpty() || domainInterface.getMacAddress().isEmpty() || domainInterface.getIpAddress().isEmpty()){}
            else {
                getMachineEntities().forEach(machineEntity -> {
                    if(machineEntity.getHostname().equals(hostname)){
                        machineEntity.getInterfaces().add(domainInterface);
                    }
                });
            }
        });
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
