package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.OIDPersistanceAdaptater;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.LockResponseCounter;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.SnmpListener;
import org.javatuples.Pair;
import org.snmp4j.PDU;

import java.io.IOException;

public class SnmpMaterialsManager extends AbstractSnmpManager{
    public SnmpMaterialsManager(SnmpListener snmpListener, OIDPersistanceAdaptater oidPersistanceAdaptater) {
        super(snmpListener, oidPersistanceAdaptater);
    }

    private int getProcessorNumber(String ipAddress){
        try{
            getOIDs().clear();
            getOIDs().add(getOidPersistanceAdaptater().getOIDHostname());
            getOIDs().add(getOidPersistanceAdaptater().getOIDNumberIndexOFTable("mProcessorTable").getValue0());
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
            getSnmp().close();
            return getSnmpListener().getMProcessorNumber();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error to get ProcessorNumber : "+e.getMessage());
            return 0;
        }
    }
}
