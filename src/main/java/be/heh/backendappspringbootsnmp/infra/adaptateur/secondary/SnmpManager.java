package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpManagerPortOut;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.responder.LockResponseCounter;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.responder.SnmpListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.javatuples.Pair;
import org.snmp4j.*;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SnmpManager implements SnmpManagerPortOut {

    @Setter
    @Getter
    private Snmp snmp;
    @Setter
    @Getter
    private USM usm;
    @Setter
    @Getter
    private PDU pdu;
    @Setter
    @Getter
    private ScopedPDU scopedPDU;
    @Getter
    private String communityString = "Silver-King-Rogue-16";
    @Setter
    @Getter
    private CommunityTarget community;
    @Setter
    @Getter
    private OctetString contextName;
    @Setter
    @Getter
    private OctetString contextEngineId;
    @Getter
    private List<String> OIDs = new ArrayList<>();
    @Getter
    private int port = 162;
    @Getter
    @Setter
    private int requestID = 1;
    @Getter
    private final SnmpListener snmpListener;
    @Getter
    @Setter
    private LockResponseCounter lockResponseCounter;

    private void initSnmpV1() throws IOException {
        setSnmp(new Snmp());
        getSnmp().setMessageDispatcher(new MessageDispatcherImpl());
        DefaultUdpTransportMapping transportMapping = new DefaultUdpTransportMapping();
        transportMapping.listen();
        getSnmp().getMessageDispatcher().addTransportMapping(transportMapping);
        getSnmp().addTransportMapping(transportMapping);

        getSnmp().getMessageDispatcher().addMessageProcessingModel(new MPv1(new DefaultPDUFactory()));
        getSnmp().getMessageDispatcher().addMessageProcessingModel(new MPv3());
        OctetString localEngineID = new OctetString(MPv3.createLocalEngineID());
        getSnmp().setLocalEngine(localEngineID.getValue(),0,0);
        getSnmp().listen();
    }
    private void initSnmpV3() throws IOException{
        setSnmp(new Snmp());
        getSnmp().setMessageDispatcher(new MessageDispatcherImpl());

        // Very important to add snmp as command responder which will finally process the PDU:
        getSnmp().getMessageDispatcher().addCommandResponder(getSnmp());

        DefaultUdpTransportMapping defaultUdpTransportMapping = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/161"));
        defaultUdpTransportMapping.listen();
        getSnmp().getMessageDispatcher().addTransportMapping(defaultUdpTransportMapping);
        getSnmp().addTransportMapping(defaultUdpTransportMapping);

        getSnmp().getMessageDispatcher().addMessageProcessingModel(new MPv3());
        SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthMD5());
        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivDES());
        OctetString localEngineID = new OctetString(MPv3.createLocalEngineID());
        setUsm(new USM(SecurityProtocols.getInstance(), localEngineID, 0));
        getUsm().setEngineDiscoveryEnabled(true);
        SecurityModels.getInstance().addSecurityModel(getUsm());

        //create user
        UsmUser user = new UsmUser(new OctetString("anthony"),AuthMD5.ID,new OctetString("Silver-Major-Knight-16"),PrivDES.ID,new OctetString("Silver-Major-Knight-16"));
        System.out.println("User Created :\n\tuser : "+user);
        getUsm().addUser(user);
        System.out.println("USM user :\n\tuser : "+getUsm().getUser(new OctetString(MPv3.createLocalEngineID()),new OctetString("anthony")));

        setContextEngineId(localEngineID);
        setContextName(new OctetString("Silver-King-Rogue-16"));

        getSnmp().getMessageDispatcher().addMessageProcessingModel(new MPv3(usm.getLocalEngineID().getValue()));
        getSnmp().setLocalEngine(localEngineID.getValue(),0,0);
        getSnmp().listen();
    }
    private void initOIDs(){
        getOIDs().clear();
        getOIDs().add("1.3.2.3.6.2.1.1.1");
        getOIDs().add("1.3.2.3.6.2.1.2.1");
        getOIDs().add("1.3.2.3.6.2.1.3.1");
        getOIDs().add("1.3.2.3.6.2.1.4.1");
        getOIDs().add("1.3.2.3.6.2.1.5.1");
    }
    private void initPDU(int pduType){
        initOIDs();
        setPdu(new PDU());
        for(String oid:getOIDs()){
            getPdu().add(new VariableBinding(new OID(oid)));
        }
        getPdu().setType(pduType);
        getPdu().setRequestID(new Integer32(getRequestID()));
        if(getRequestID()>10000){setRequestID(0);}
        setRequestID(getRequestID()+1);
        if(getPdu().getErrorStatus()!=0){
            System.err.println("Error pdu : "+getPdu().getErrorStatus()+" => "+getPdu().getErrorStatusText());
        }
    }
    private void initScopedPDU(int scopedPduType){
        initOIDs();
        setScopedPDU(new ScopedPDU());
        for(String oid:getOIDs()){
            getScopedPDU().add(new VariableBinding(new OID(oid)));
        }
        getScopedPDU().setType(scopedPduType);
        getScopedPDU().setContextEngineID(getContextEngineId());
        getScopedPDU().setContextName(getContextName());
        getScopedPDU().setRequestID(new Integer32(getRequestID()));
        if(getRequestID()>10000){setRequestID(0);}
        setRequestID(getRequestID()+1);
        if(getScopedPDU().getErrorStatus()!=0){
            System.err.println("Error pdu : "+getScopedPDU().getErrorStatus()+" => "+getScopedPDU().getErrorStatusText());
        }
    }
    private CommunityTarget getCommunityTarget(String ip){
        //create Address
        Address address = GenericAddress.parse(String.format("udp:%s/%s", ip, getPort()));
        //Create community
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(getCommunityString()));
        target.setAddress(address);
        target.setRetries(3);
        target.setTimeout(1000);
        target.setVersion(SnmpConstants.version1);
        return target;
    }
    private Target<Address> getUserTargetV3(String ip){
        initOIDs();
        //create a target
        Address address = GenericAddress.parse(String.format("udp:%s/%s",ip , getPort()));
        Target<Address> userTarget = new UserTarget<>();
        userTarget.setAddress(address);
        System.out.println("User Address : "+userTarget.getAddress());
        userTarget.setRetries(3);
        userTarget.setTimeout(1000);
        userTarget.setVersion(SnmpConstants.version3);
        userTarget.setSecurityLevel(SecurityLevel.AUTH_PRIV);
        userTarget.setSecurityName(new OctetString("anthony"));
        return userTarget;
    }
    @Override
    public List<MachineEntity> getInfoMachineEntities(List<String> ipAddress) throws IOException, InterruptedException {
        initSnmpV1();
        if(!getSnmpListener().getMachineEntities().isEmpty()){
            getSnmpListener().getMachineEntities().clear();
        }
        //initialize Locker
        setLockResponseCounter(new LockResponseCounter(ipAddress.size()));
        getSnmpListener().setLockResponseCounter(getLockResponseCounter());
        //send request
        for(String ip:ipAddress){
            initPDU(PDU.GET);
            getSnmpListener().getRequestController().put(getPdu().getRequestID().getValue(),Pair.with(ip,false));
            //System.out.println("send PDU : "+getPdu());
            getSnmp().send(getPdu(),getCommunityTarget(ip),null,getSnmpListener());
        }
        getLockResponseCounter().waitResponse();
        getSnmpListener().getMachineEntities().forEach(System.out::println);
        return getSnmpListener().getMachineEntities();
    }

}
