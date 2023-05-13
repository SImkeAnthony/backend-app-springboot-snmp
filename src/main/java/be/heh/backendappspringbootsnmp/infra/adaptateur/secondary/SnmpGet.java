package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpGetInfoPortOut;
import lombok.Getter;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SnmpGet implements SnmpGetInfoPortOut {

    @Getter
    Snmp snmp;
    @Getter
    USM usm;
    @Getter
    private static int snmpVersion = SnmpConstants.version3;
    @Getter
    private static int port = 161;
    @Getter
    private static int secondPort = 162;
    @Getter
    private static List<String> oidList = new ArrayList<>();

    private void initSnmp() throws IOException {
        snmp = new Snmp();
        snmp.getMessageDispatcher().addCommandResponder(new CommandResponder() {
            @Override
            public <A extends Address> void processPdu(CommandResponderEvent<A> commandResponderEvent) {
                System.out.println(commandResponderEvent.toString());
            }
        });
        // Very important to add snmp as command responder which will finally process the PDU:
        snmp.getMessageDispatcher().addCommandResponder(snmp);

        snmp.addTransportMapping(new DefaultUdpTransportMapping());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
        SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthMD5());
        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivDES());
        OctetString localEngineID = new OctetString(MPv3.createLocalEngineID());
        usm = new USM(SecurityProtocols.getInstance(), localEngineID, 0);
        usm.setEngineDiscoveryEnabled(true);
        SecurityModels.getInstance().addSecurityModel(usm);

        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3(usm.getLocalEngineID().getValue()));
        snmp.listen();
        System.out.println("Snmp listen ...");
    }


    @Override
    public MachineEntity getSomeInfo(String ipAddress) throws IOException {

        initSnmp();

        //create user
        UsmUser user = new UsmUser(new OctetString("anthony"),AuthMD5.ID,new OctetString("Silver-Major-Knight-16"),PrivDES.ID,new OctetString("Silver-Major-Knight-16"));
        System.out.println("User Created :\n\tuser : "+user);
        getUsm().addUser(user);
        System.out.println("USM user :\n\tuser : "+getUsm().getUser(new OctetString(MPv3.createLocalEngineID()),new OctetString("anthony")));
        //add oid
        getOidList().add("1.3.6.1.2.1.1.1.0");

        //create a target
        Address address = GenericAddress.parse(String.format("udp:%s/%s", ipAddress, getSecondPort()));
        Target<Address> userTarget = new UserTarget<>();
        userTarget.setAddress(address);
        System.out.println("User Address : "+userTarget.getAddress());
        userTarget.setRetries(3);
        userTarget.setTimeout(5000);
        userTarget.setVersion(getSnmpVersion());
        userTarget.setSecurityLevel(SecurityLevel.AUTH_PRIV);
        userTarget.setSecurityName(user.getSecurityName());

        //create tram
        ScopedPDU pdu = new ScopedPDU();
        for(String oid : getOidList()){
            pdu.add(new VariableBinding(new OID(oid)));
        }

        System.out.println("Add OID : ");
        pdu.getAll().forEach(variableBinding -> {
            System.out.println("\toid : "+variableBinding.getOid().toString());
        });
        pdu.setType(ScopedPDU.GET);

        ResponseListener responseListener = new ResponseListener() {
            @Override
            public synchronized  <A extends Address> void onResponse(ResponseEvent<A> event) {
                //free ressources
                snmp.cancel(event.getRequest(),this);

                //process response here
                if(event.getResponse()!=null){
                    System.out.println("Response received : "+event.getResponse());
                }else{
                    if(event.getError() != null){
                        System.out.println("Error from event : \n\t"+event.getError().toString());
                    }else{
                        System.out.println("Time out.");
                    }
                }
                notify();
            }

        };
        synchronized (responseListener){
            System.out.println("Start snmp Get");
            snmp.get(pdu,userTarget,null,responseListener);
            try{
                responseListener.wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return null;
    }


}
