package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpManagerPortOut;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.OIDPersistanceAdaptater;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpInterfaceManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpMaterialsManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpServiceManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpSystemManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.SnmpListener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.javatuples.Pair;
import org.snmp4j.security.dh.DHOperations;

import java.util.*;

@RequiredArgsConstructor
public class SnmpManagerAdaptater implements SnmpManagerPortOut {
    @Getter
    private final SnmpSystemManager snmpSystemManager;
    @Getter
    private final SnmpInterfaceManager snmpInterfaceManager;
    @Getter
    private final SnmpMaterialsManager snmpMaterialsManager;
    @Getter
    private final SnmpServiceManager snmpServiceManager;

    @Override
    public List<MachineEntity> getInfoMachineEntities(List<String> ipAddress){
        snmpSystemManager.completeMachineEntitiesWithSystemVariables(ipAddress);
        Set<String> ipReachableSnmp = new HashSet<>();
        for(Map.Entry<Integer, Pair<String,Boolean>> entry : snmpSystemManager.getSnmpListener().getRequestController().entrySet()){
            if(entry.getValue().getValue1()){
                ipReachableSnmp.add(entry.getValue().getValue0());
            }
        }
        snmpInterfaceManager.completeInterfacesForEachMachineEntities(ipReachableSnmp.stream().toList());
        snmpMaterialsManager.completeMaterialsForEachMachineEntities(ipReachableSnmp.stream().toList());
        snmpServiceManager.completeServicesForEachMachineEntities(ipReachableSnmp.stream().toList());
        return snmpSystemManager.getSnmpListener().getMachineEntities();
    }

}
