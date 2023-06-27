package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.OIDPersistanceAdaptater;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.LockResponseCounter;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.SnmpListener;
import org.javatuples.Pair;
import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class SnmpServiceManager extends AbstractSnmpManager{
    public SnmpServiceManager(SnmpListener snmpListener, OIDPersistanceAdaptater oidPersistanceAdaptater) {
        super(snmpListener, oidPersistanceAdaptater);
    }
    private int getServiceNumber(String ipAddress){
        try{
            initSnmpV1();
            getOIDs().clear();
            getOIDs().add(getOidPersistanceAdaptater().getOIDNumberIndexOFTable("sTable").getValue0());
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
            getSnmp().close();
            return getSnmpListener().getIfNumber();
        }catch (IOException | InterruptedException e){
            System.err.println("Error to get sNumber : "+e.getMessage());
            return 0;
        }
    }
    private void completeService(int index,String ipAddress){
        try{
            initSnmpV1();
            getOIDs().clear();
            getOIDs().add(getOidPersistanceAdaptater().getOIDHostname());
            getOidPersistanceAdaptater().getColumnOfTable("sTable").forEach(moVariable -> {getOIDs().add(moVariable.getOid());});
            initPDU(PDU.GET);
            getPdu().add(new VariableBinding(new OID(getOidPersistanceAdaptater().getOIDNumberIndexOFTable("sTable").getValue0()),String.valueOf(index)));
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress+" for index "+index);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
        }catch (IOException | ParseException | InterruptedException e) {
            System.err.println("Error complete service : "+e.getMessage());
        }
    }
    private void completeServicesForMachineEntity(String ipAddress){
        int ifNumber = getServiceNumber(ipAddress);
        for(int i = 0;i<ifNumber;i++){
            completeService(i,ipAddress);
        }
    }
    public void completeServicesForEachMachineEntities(List<String> ipAddress){
        ipAddress.forEach(this::completeServicesForMachineEntity);
    }
}
