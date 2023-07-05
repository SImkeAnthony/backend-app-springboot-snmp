package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.LockResponseCounter;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.SnmpListener;
import org.javatuples.Pair;
import org.snmp4j.PDU;

import java.io.IOException;
import java.util.List;

public class SnmpMaterialsManager extends AbstractSnmpManager{
    public SnmpMaterialsManager(SnmpListener snmpListener) {
        super(snmpListener);
    }
    private int getProcessorNumber(String ipAddress){
        try{
            initSnmpV1();
            getOIDs().clear();
            getOIDs().add(getSnmpListener().getOidPersistanceAdaptater().getOidNumberIndexOfTable("mProcessorTable").getValue0());
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
            getSnmp().close();
            return getSnmpListener().getMProcessorNumber();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error to get ProcessorNumber : "+e.getMessage());
            return 0;
        }
    }
    private void completeProcessor(int index,String ipAddress){
        try{
            String oidIndex = getSnmpListener().getOidPersistanceAdaptater().getOidNumberIndexOfTable("mProcessorTable").getValue1();
            initSnmpV1();
            getOIDs().clear();
            getOIDs().add(getSnmpListener().getOidPersistanceAdaptater().getOIDHostname());
            getSnmpListener().getOidPersistanceAdaptater().getColumnOfTable("mProcessorTable").forEach(moVariable -> {getOIDs().add(moVariable.getOid());});
            initPDU(PDU.GET,index,oidIndex);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress+" for index "+index);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
            getSnmp().close();
        }catch (IOException | InterruptedException e) {
            System.err.println("Error complete processor : "+e.getMessage());
        }
    }
    private int getDiskNumber(String ipAddress){
        try{
            initSnmpV1();
            getOIDs().clear();
            getOIDs().add(getSnmpListener().getOidPersistanceAdaptater().getOidNumberIndexOfTable("mDiskTable").getValue0());
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
            getSnmp().close();
            return getSnmpListener().getMProcessorNumber();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error to get disk number : "+e.getMessage());
            return 0;
        }
    }
    private void completeDisk(int index,String ipAddress){
        try{
            String oidIndex = getSnmpListener().getOidPersistanceAdaptater().getOidNumberIndexOfTable("mDiskTable").getValue1();
            initSnmpV1();
            getOIDs().clear();
            getOIDs().add(getSnmpListener().getOidPersistanceAdaptater().getOIDHostname());
            getSnmpListener().getOidPersistanceAdaptater().getColumnOfTable("mDiskTable").forEach(moVariable -> {getOIDs().add(moVariable.getOid());});
            initPDU(PDU.GET,index,oidIndex);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress+" for index "+index);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
            getSnmp().close();
        }catch (IOException | InterruptedException e) {
            System.err.println("Error complete disk : "+e.getMessage());
        }
    }
    private int getVStorageNumber(String ipAddress){
        try{
            initSnmpV1();
            getOIDs().clear();
            getOIDs().add(getSnmpListener().getOidPersistanceAdaptater().getOidNumberIndexOfTable("mVStorageTable").getValue0());
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
            getSnmp().close();
            return getSnmpListener().getMProcessorNumber();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error to get volatile storage number : "+e.getMessage());
            return 0;
        }
    }
    private void completeVStorage(int index,String ipAddress){
        try{
            String oidIndex = getSnmpListener().getOidPersistanceAdaptater().getOidNumberIndexOfTable("mVStorageTable").getValue1();
            initSnmpV1();
            getOIDs().clear();
            getOIDs().add(getSnmpListener().getOidPersistanceAdaptater().getOIDHostname());
            getSnmpListener().getOidPersistanceAdaptater().getColumnOfTable("mVStorageTable").forEach(moVariable -> {getOIDs().add(moVariable.getOid());});
            initPDU(PDU.GET,index,oidIndex);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress+" for index "+index);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
            getSnmp().close();
        }catch (IOException | InterruptedException e) {
            System.err.println("Error complete volatile storage : "+e.getMessage());
        }
    }
    private void completeMaterialsForMachineEntity(String ipAddress){
        int mProcessorNumber = getProcessorNumber(ipAddress);
        for(int i = 1;i<=mProcessorNumber;i++){
            completeProcessor(i,ipAddress);
        }
        int mDiskNumber = getDiskNumber(ipAddress);
        for(int i=1;i<=mDiskNumber;i++){
            completeDisk(i,ipAddress);
        }
        int mVStorage = getVStorageNumber(ipAddress);
        for(int i=1;i<=mVStorage;i++){
            completeVStorage(i,ipAddress);
        }
    }
    public void completeMaterialsForEachMachineEntities(List<String> ipAddress){
        ipAddress.forEach(this::completeMaterialsForMachineEntity);
    }
}
