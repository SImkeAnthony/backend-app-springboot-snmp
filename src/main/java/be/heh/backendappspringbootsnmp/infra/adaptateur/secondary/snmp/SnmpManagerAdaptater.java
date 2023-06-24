package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpManagerPortOut;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.OIDPersistanceAdaptater;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpInterfaceManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpSystemManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.LockRequestID;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.LockResponseCounter;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.SnmpListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.javatuples.Pair;
import org.snmp4j.*;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<MachineEntity> getInfoMachineEntities(List<String> ipAddress){
        snmpSystemManager.completeMachineEntitiesWithSystemVariables(ipAddress);
        snmpInterfaceManager.completeInterfacesForEachMachineEntities(ipAddress);
        return getSnmpListener().getMachineEntities();
    }

}
