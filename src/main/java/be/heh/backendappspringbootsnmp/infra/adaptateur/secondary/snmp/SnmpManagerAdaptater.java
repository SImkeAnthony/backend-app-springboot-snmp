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

import java.util.List;

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
        snmpInterfaceManager.completeInterfacesForEachMachineEntities(ipAddress);
        //snmpMaterialsManager.completeMaterialsForEachMachineEntities(ipAddress);
        //snmpServiceManager.completeServicesForEachMachineEntities(ipAddress);
        return snmpSystemManager.getSnmpListener().getMachineEntities();
    }

}
