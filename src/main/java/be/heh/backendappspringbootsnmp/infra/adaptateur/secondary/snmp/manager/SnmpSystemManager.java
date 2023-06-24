package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.OIDPersistanceAdaptater;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.LockResponseCounter;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.SnmpListener;
import org.javatuples.Pair;
import org.snmp4j.PDU;

import java.io.IOException;
import java.util.List;

public class SnmpSystemManager extends AbstractSnmpManager{
    public SnmpSystemManager(SnmpListener snmpListener, OIDPersistanceAdaptater oidPersistanceAdaptater) {
        super(snmpListener, oidPersistanceAdaptater);
    }
    private void initOIDForSystem(){
        getOIDs().clear();
        getOidPersistanceAdaptater().getMoManagers().forEach(moManager -> {
            if(moManager.getName().equals("system")){
                if(!moManager.getMoVariables().isEmpty()){
                    moManager.getMoVariables().forEach(moVariable -> {getOIDs().add(moVariable.getOid());});
                }
            }
        });
    }
    public void completeMachineEntitiesWithSystemVariables(List<String> ipAddress){
        try{
            initSnmpV1();
            if(!getSnmpListener().getMachineEntities().isEmpty()){
                getSnmpListener().getMachineEntities().clear();
            }
            initOIDForSystem();
            //initialize Locker
            setLockResponseCounter(new LockResponseCounter(ipAddress.size()));
            getSnmpListener().setLockResponseCounter(getLockResponseCounter());
            //send request
            for(String ip:ipAddress){
                initPDU(PDU.GET);
                getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(), Pair.with(ip,false));
                System.out.println("send PDU : "+getPdu()+" at "+ip);
                getSnmp().send(getPdu(),getCommunityTarget(ip),null,getSnmpListener());
            }
            getLockResponseCounter().waitResponse();
            getSnmp().close();
            getSnmpListener().getMachineEntities().forEach(System.out::println);
        }catch (IOException | InterruptedException e){
            System.err.println("Error to compete system variable : "+e.getMessage());
        }
    }
}
