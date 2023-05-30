package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.responder;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.entities.OIDEntity;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.MachineMapper;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.OIDPersistanceAdaptateur;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Var;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@RequiredArgsConstructor
public class SnmpListener implements ResponseListener {
    @Getter
    private final List<MachineEntity> machineEntities;
    @Getter
    private final OIDPersistanceAdaptateur oidPersistanceAdaptateur;

    @Override
    public <A extends Address> void onResponse(ResponseEvent<A> event) {
        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
        System.out.println("Received message : "+event.getResponse());
        if(event.getResponse().getErrorStatus() == 0){
            System.out.println("Size : "+event.getResponse().getVariableBindings().size());
            switch (event.getResponse().getVariableBindings().size()){
                case 5:
                    responseSomeInfoMachineEntities(event);
                    break;
                default:
                    System.err.println("The response is not supported yet");
            }
        }else {
            System.err.println("Error status in response PDU : "+event.getResponse().getErrorStatus()+" => "+event.getResponse().getErrorStatusText());
        }
    }
    private void responseSomeInfoMachineEntities(ResponseEvent event){
        List<? extends VariableBinding> workedList = event.getResponse().getVariableBindings();
        for(VariableBinding variableBinding : workedList){
            int id = getOidPersistanceAdaptateur().getIdToOid(variableBinding.getOid().format());
            List<String> ipAddress = new ArrayList<>();
            List<String> macAddress = new ArrayList<>();
            String hostname  = "";
            String os = "";
            boolean snmp = false;
            System.out.println("id : "+id);
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
                    System.err.println("Error OID : "+variableBinding.getOid().format()+" is not supported yet");
                    break;
            }
            MachineEntity machineEntity = new MachineEntity(macAddress,ipAddress,hostname,os,snmp);
            getMachineEntities().add(machineEntity);
        }
    }
}
