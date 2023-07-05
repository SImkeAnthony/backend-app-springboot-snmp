package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.LockResponseCounter;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.SnmpListener;
import org.javatuples.Pair;
import org.snmp4j.PDU;

import java.io.IOException;
import java.util.List;

public class SnmpInterfaceManager extends AbstractSnmpManager{

    public SnmpInterfaceManager(SnmpListener snmpListener) {
        super(snmpListener);
    }

    private int getInterfaceNumber(String ipAddress){
        try{
            initSnmpV1();
            getOIDs().clear();
            getOIDs().add(getSnmpListener().getOidPersistenceAdaptater().getOidNumberIndexOfTable("ifTable").getValue0());
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
            System.err.println("Error to get ifNumber : "+e.getMessage());
            return 0;
        }
    }
    private void completeInterface(int index,String ipAddress){
        try{
            String oidIndex = getSnmpListener().getOidPersistenceAdaptater().getOidNumberIndexOfTable("ifTable").getValue1();
            initSnmpV1();
            getOIDs().clear();
            getOIDs().add(getSnmpListener().getOidPersistenceAdaptater().getOIDHostname());
            getSnmpListener().getOidPersistenceAdaptater().getColumnOfTable("ifTable").forEach(moVariable -> {getOIDs().add(moVariable.getOid());});
            initPDU(PDU.GET,index,oidIndex);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress+" for index "+index);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
            getSnmp().close();
        }catch (IOException | InterruptedException e) {
            System.err.println("Error complete interface : "+e.getMessage());
        }
    }
    private void completeInterfaceForMachineEntity(String ipAddress){
        int ifNumber = getInterfaceNumber(ipAddress);
        if(ifNumber!=0){
            for(int i = 1;i<=ifNumber;i++){
                completeInterface(i,ipAddress);
            }
        }
    }
    public void completeInterfacesForEachMachineEntities(List<String> ipAddress){
        ipAddress.forEach(this::completeInterfaceForMachineEntity);
    }
}
