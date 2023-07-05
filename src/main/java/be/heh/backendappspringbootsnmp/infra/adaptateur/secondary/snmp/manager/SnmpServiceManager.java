package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.LockResponseCounter;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.SnmpListener;
import org.javatuples.Pair;
import org.snmp4j.PDU;

import java.io.IOException;
import java.util.List;

public class SnmpServiceManager extends AbstractSnmpManager{
    public SnmpServiceManager(SnmpListener snmpListener) {
        super(snmpListener);
    }
    private int getServiceNumber(String ipAddress){
        try{
            initSnmpV1();
            getOIDs().clear();
            getOIDs().add(getSnmpListener().getOidPersistanceAdaptater().getOidNumberIndexOfTable("sTable").getValue0());
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress);
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
            String oidIndex = getSnmpListener().getOidPersistanceAdaptater().getOidNumberIndexOfTable("sTable").getValue1();
            initSnmpV1();
            getOIDs().clear();
            getOIDs().add(getSnmpListener().getOidPersistanceAdaptater().getOIDHostname());
            getSnmpListener().getOidPersistanceAdaptater().getColumnOfTable("sTable").forEach(moVariable -> {getOIDs().add(moVariable.getOid());});
            initPDU(PDU.GET,index,oidIndex);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress+" for index "+index);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
        }catch (IOException | InterruptedException e) {
            System.err.println("Error complete service : "+e.getMessage());
        }
    }
    private void completeServicesForMachineEntity(String ipAddress){
        int ifNumber = getServiceNumber(ipAddress);
        for(int i = 1;i<=ifNumber;i++){
            completeService(i,ipAddress);
        }
    }
    public void completeServicesForEachMachineEntities(List<String> ipAddress){
        ipAddress.forEach(this::completeServicesForMachineEntity);
    }
}
