package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.OIDPersistanceAdaptater;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.LockResponseCounter;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.SnmpListener;
import lombok.RequiredArgsConstructor;
import org.javatuples.Pair;
import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class SnmpInterfaceManager extends AbstractSnmpManager{

    public SnmpInterfaceManager(SnmpListener snmpListener, OIDPersistanceAdaptater oidPersistanceAdaptater) {
        super(snmpListener, oidPersistanceAdaptater);
    }

    private int getInterfaceNumber(String ipAddress){
        try{
            getOIDs().clear();
            getOIDs().add(getOidPersistanceAdaptater().getOIDHostname());
            getOIDs().add(getOidPersistanceAdaptater().getOIDNumberIndexOFTable("ifTable").getValue0());
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ipAddress,false));
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
            getOIDs().clear();
            getOIDs().add(getOidPersistanceAdaptater().getOIDHostname());
            getOidPersistanceAdaptater().getColumnOfTable("ifTable").forEach(moVariable -> {getOIDs().add(moVariable.getOid());});
            initPDU(PDU.GET);
            getPdu().add(new VariableBinding(new OID(getOidPersistanceAdaptater().getOIDNumberIndexOFTable("ifTable").getValue0()),String.valueOf(index)));
            setLockResponseCounter(new LockResponseCounter(1));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            initPDU(PDU.GET);
            System.out.println("send PDU : "+getPdu()+" at "+ipAddress+" for index "+index);
            getSnmp().send(getPdu(),getCommunityTarget(ipAddress),null,getSnmpListener());
            getLockResponseCounter().waitResponse();
        }catch (IOException | ParseException | InterruptedException e) {
            System.err.println("Error complete interface : "+e.getMessage());
        }
    }
    private void completeInterfaceForMachineEntity(String ipAddress){
        int ifNumber = getInterfaceNumber(ipAddress);
        for(int i = 0;i<ifNumber;i++){
            completeInterface(i,ipAddress);
        }
    }
    public void completeInterfacesForEachMachineEntities(List<String> ipAddress){
        ipAddress.forEach(this::completeInterfaceForMachineEntity);
    }
}
