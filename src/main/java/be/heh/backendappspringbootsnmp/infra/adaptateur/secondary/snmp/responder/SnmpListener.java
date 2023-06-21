package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.OIDPersistanceAdaptater;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.javatuples.Pair;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Variable;
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
                switch (event.getResponse().getVariableBindings().size()){
                    case 5:
                        Pair<String, Boolean> currentPair = getRequestController().get(event.getResponse().getRequestID().getValue());
                        getRequestController().put(event.getResponse().getRequestID().getValue(),currentPair.setAt1(true));
                        break;
                    default:
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
        getManagerController().put("system",1);
        getManagerController().put("interfaces",2);
        getManagerController().put("materials",3);
        getManagerController().put("services",4);
    }

    private void addUnknownMachineEntity(String ipAddr){
        getMachineEntities().add(new MachineEntity("unknown","unknown",false));
    }
}
