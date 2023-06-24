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

public class SnmpMaterialsManager extends AbstractSnmpManager{
    public SnmpMaterialsManager(SnmpListener snmpListener, OIDPersistanceAdaptater oidPersistanceAdaptater) {
        super(snmpListener, oidPersistanceAdaptater);
    }
    private int getProcessorNumber(String ipAddress){
        try{
            getOIDs().clear();
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
    private void completeProcessor(int index,String ipAddress){
        try{
            getOIDs().clear();
            getOIDs().add(getOidPersistanceAdaptater().getOIDHostname());
            getOidPersistanceAdaptater().getColumnOfTable("mProcessorTable").forEach(moVariable -> {getOIDs().add(moVariable.getOid());});
            initPDU(PDU.GET);
            getPdu().add(new VariableBinding(new OID(getOidPersistanceAdaptater().getOIDNumberIndexOFTable("mProcessorTable").getValue0()),String.valueOf(index)));
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress+" for index "+index);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
        }catch (IOException | ParseException | InterruptedException e) {
            System.err.println("Error complete processor : "+e.getMessage());
        }
    }
    private int getDiskNumber(String ipAddress){
        try{
            getOIDs().clear();
            getOIDs().add(getOidPersistanceAdaptater().getOIDNumberIndexOFTable("mDiskTable").getValue0());
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
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
            getOIDs().clear();
            getOIDs().add(getOidPersistanceAdaptater().getOIDHostname());
            getOidPersistanceAdaptater().getColumnOfTable("mDiskTable").forEach(moVariable -> {getOIDs().add(moVariable.getOid());});
            initPDU(PDU.GET);
            getPdu().add(new VariableBinding(new OID(getOidPersistanceAdaptater().getOIDNumberIndexOFTable("mDiskTable").getValue0()),String.valueOf(index)));
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress+" for index "+index);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
        }catch (IOException | ParseException | InterruptedException e) {
            System.err.println("Error complete disk : "+e.getMessage());
        }
    }
    private int getVStorageNumber(String ipAddress){
        try{
            getOIDs().clear();
            getOIDs().add(getOidPersistanceAdaptater().getOIDNumberIndexOFTable("mVStorageTable").getValue0());
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
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
            getOIDs().clear();
            getOIDs().add(getOidPersistanceAdaptater().getOIDHostname());
            getOidPersistanceAdaptater().getColumnOfTable("mVStorageTable").forEach(moVariable -> {getOIDs().add(moVariable.getOid());});
            initPDU(PDU.GET);
            getPdu().add(new VariableBinding(new OID(getOidPersistanceAdaptater().getOIDNumberIndexOFTable("mVStorageTable").getValue0()),String.valueOf(index)));
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress+" for index "+index);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
        }catch (IOException | ParseException | InterruptedException e) {
            System.err.println("Error complete volatile storage : "+e.getMessage());
        }
    }
    private void completeMaterialsForMachineEntity(String ipAddress){
        int mProcessorNumber = getProcessorNumber(ipAddress);
        for(int i = 0;i<mProcessorNumber;i++){
            completeProcessor(i,ipAddress);
        }
        int mDiskNumber = getDiskNumber(ipAddress);
        for(int i=0;i<mDiskNumber;i++){
            completeDisk(i,ipAddress);
        }
        int mVStorage = getVStorageNumber(ipAddress);
        for(int i=0;i<mVStorage;i++){
            completeVStorage(i,ipAddress);
        }
    }
    public void completeMaterialsForEachMachineEntities(List<String> ipAddress){
        ipAddress.forEach(this::completeMaterialsForMachineEntity);
    }
}
