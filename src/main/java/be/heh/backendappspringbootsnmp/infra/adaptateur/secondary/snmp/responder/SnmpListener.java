package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.OIDPersistanceAdaptateur;
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
    private final List<MachineEntity> machineEntities;
    @Getter
    private final OIDPersistanceAdaptateur oidPersistanceAdaptateur;
    @Getter
    private Map<Integer, Pair<String, Boolean>> requestController = new HashMap<>();
    @Setter
    @Getter
    private LockResponseCounter lockResponseCounter;

    @Override
    public <A extends Address> void onResponse(ResponseEvent<A> event) {
        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
        //System.out.println("Received message : "+event.getResponse());

        if(event.getResponse()!=null){
            if(event.getResponse().getErrorStatus() == 0){
                switch (event.getResponse().getVariableBindings().size()){
                    case 5:
                        responseSomeInfoMachineEntities(event);
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
    private void responseSomeInfoMachineEntities(ResponseEvent event){
        List<? extends VariableBinding> workedList = event.getResponse().getVariableBindings();
        List<String> ipAddress = new ArrayList<>();
        List<String> macAddress = new ArrayList<>();
        String hostname  = "";
        String os = "";
        boolean snmp = false;
        for(VariableBinding variableBinding : workedList){
            int id = getOidPersistanceAdaptateur().getIdToOid(variableBinding.getOid().format());
            switch (id){
                case 1:
                    String[] macAddresses = variableBinding.getVariable().toString().split("/");
                    macAddress.addAll(Arrays.asList(macAddresses));
                    break;
                case 2:
                    String[] ipAddresses = variableBinding.getVariable().toString().split("/");
                    ipAddress.addAll(Arrays.asList(ipAddresses));
                    break;
                case 3:
                    hostname = variableBinding.getVariable().toString();
                    break;
                case 4:
                    os = variableBinding.getVariable().toString();
                    break;
                case 5:
                    snmp = variableBinding.getVariable().toString().equals("true");
                    break;
                default:
                    //System.err.println("Error OID : "+variableBinding.getOid().format()+" is not supported yet");
                    break;
            }
        }
        MachineEntity machineEntity = new MachineEntity(macAddress,ipAddress,hostname,os,snmp);
        getMachineEntities().add(machineEntity);
    }

    private void addUnknownMachineEntity(String ipAddr){
        List<String> ipAddress = new ArrayList<>();
        ipAddress.add(ipAddr);
        List<String> macAddress = new ArrayList<>();
        macAddress.add("unknown");
        getMachineEntities().add(new MachineEntity(macAddress,ipAddress,"unknown","unknown",false));
    }
}
