package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpManagerPortOut;
import lombok.Getter;
import lombok.Setter;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    @Setter
    @Getter
    private ResponseListener listener;
    @Getter
    private String communityString = "Silver-King-Rogue-16";
    @Setter
    @Getter
    private CommunityTarget community;
    @Getter
    private List<String> OIDs = new ArrayList<>();
    @Getter
    private int port = 162;

    private void initSnmpV1() throws IOException {
        setSnmp(new Snmp());
        getSnmp().addTransportMapping(new DefaultUdpTransportMapping());
        getSnmp().getMessageDispatcher().addMessageProcessingModel(new MPv1(new DefaultPDUFactory()));
        getSnmp().listen();
        System.out.println("Snmp listen ...");
    }
    private void initSnmpV3() throws IOException{
        setSnmp(new Snmp());
        getSnmp().getMessageDispatcher().addCommandResponder(new CommandResponder() {
            @Override
            public <A extends Address> void processPdu(CommandResponderEvent<A> commandResponderEvent) {
                System.out.println(commandResponderEvent.toString());
            }
        });
        // Very important to add snmp as command responder which will finally process the PDU:
        getSnmp().getMessageDispatcher().addCommandResponder(getSnmp());

        getSnmp().addTransportMapping(new DefaultUdpTransportMapping());
        getSnmp().getMessageDispatcher().addMessageProcessingModel(new MPv3());
        SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthMD5());
        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivDES());
        OctetString localEngineID = new OctetString(MPv3.createLocalEngineID());
        setUsm(new USM(SecurityProtocols.getInstance(), localEngineID, 0));
        getUsm().setEngineDiscoveryEnabled(true);
        SecurityModels.getInstance().addSecurityModel(getUsm());

        getSnmp().getMessageDispatcher().addMessageProcessingModel(new MPv3(usm.getLocalEngineID().getValue()));
        getSnmp().listen();
        System.out.println("Snmp listen ...");
    }
    private void initializeListeningEvent(){
        setListener(new ResponseListener() {
            public void onResponse(ResponseEvent event) {
                ((Snmp)event.getSource()).cancel(event.getRequest(), this);
                PDU response = event.getResponse();
                PDU request = event.getRequest();
                if (response == null) {
                    System.out.println("Request "+request+" timed out");
                }
                else {
                    System.out.println("Received response "+response+" on request "+
                            request);
                }
            }
        });
    }
    private void initOIDs(){
        getOIDs().clear();
        getOIDs().add("1.3.2.3.6.2.1.1.2");
    }
    private void initPDU(int pduType){
        if(getPdu() == null){
            setPdu(new PDU());
        }
        for(String oid:getOIDs()){
            getPdu().add(new VariableBinding(new OID(oid)));
        }
        getPdu().setType(pduType);
    }
    private void initScopedPDU(int scopedPduType){
        if(getScopedPDU()==null){
            setScopedPDU(new ScopedPDU());
        }
        for(String oid:getOIDs()){
            getScopedPDU().add(new VariableBinding(new OID(oid)));
        }
        getScopedPDU().setType(scopedPduType);
    }
    private CommunityTarget getCommunityTarget(String ip){
        //create Address
        Address address = GenericAddress.parse(String.format("udp:%s/%s", ip, getPort()));
        //Create community
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(getCommunityString()));
        target.setAddress(address);
        target.setRetries(3);
        target.setTimeout(5000);
        target.setVersion(SnmpConstants.version1);
        initOIDs();
        return target;
    }
    private Target<Address> getUserTargetV3(String ip){
        //create user
        UsmUser user = new UsmUser(new OctetString("anthony"),AuthMD5.ID,new OctetString("Silver-Major-Knight-16"),PrivDES.ID,new OctetString("Silver-Major-Knight-16"));
        System.out.println("User Created :\n\tuser : "+user);
        getUsm().addUser(user);
        System.out.println("USM user :\n\tuser : "+getUsm().getUser(new OctetString(MPv3.createLocalEngineID()),new OctetString("anthony")));
        initOIDs();
        //create a target
        Address address = GenericAddress.parse(String.format("udp:%s/%s",ip , getPort()));
        Target<Address> userTarget = new UserTarget<>();
        userTarget.setAddress(address);
        System.out.println("User Address : "+userTarget.getAddress());
        userTarget.setRetries(3);
        userTarget.setTimeout(5000);
        userTarget.setVersion(SnmpConstants.version3);
        userTarget.setSecurityLevel(SecurityLevel.AUTH_PRIV);
        userTarget.setSecurityName(user.getSecurityName());
        return userTarget;
    }
    @Override
    public List<MachineEntity> getInfoMachineEntities(List<String> ipAddress) throws IOException {
        List<MachineEntity> machineEntities = new ArrayList<>();
        initSnmpV3();
        initScopedPDU(PDU.GET);
        initializeListeningEvent();
        for(String ip:ipAddress){
            getSnmp().send(getScopedPDU(),getUserTargetV3(ip),null,getListener());
        }
        return machineEntities;
    }
}
