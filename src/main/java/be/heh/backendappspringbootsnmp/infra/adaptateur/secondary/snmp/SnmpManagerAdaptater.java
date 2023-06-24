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
    private final SnmpListener snmpListener;
    @Getter
    private final OIDPersistanceAdaptater oidPersistanceAdaptater;
    @Getter
    private SnmpSystemManager snmpSystemManager = new SnmpSystemManager(snmpListener,oidPersistanceAdaptater);
    @Getter
    private SnmpInterfaceManager snmpInterfaceManager = new SnmpInterfaceManager(snmpListener,oidPersistanceAdaptater);
    @Getter
    private SnmpMaterialsManager snmpMaterialsManager = new SnmpMaterialsManager(snmpListener,oidPersistanceAdaptater);
    @Getter
    private SnmpServiceManager snmpServiceManager = new SnmpServiceManager(snmpListener,oidPersistanceAdaptater);

    @Override
    public List<MachineEntity> getInfoMachineEntities(List<String> ipAddress){
        snmpSystemManager.completeMachineEntitiesWithSystemVariables(ipAddress);
        snmpInterfaceManager.completeInterfacesForEachMachineEntities(ipAddress);
        snmpMaterialsManager.completeMaterialsForEachMachineEntities(ipAddress);
        snmpServiceManager.completeServicesForEachMachineEntities(ipAddress);
        return getSnmpListener().getMachineEntities();
    }

}
