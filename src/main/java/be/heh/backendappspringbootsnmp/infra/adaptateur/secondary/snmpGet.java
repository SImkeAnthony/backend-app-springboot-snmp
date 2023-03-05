package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.out.snmpGetInfoPortOut;
import lombok.Getter;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class snmpGet implements snmpGetInfoPortOut {

    @Getter
    private static int snmpVersion = SnmpConstants.version3;
    @Getter
    private static int port = 161;
    @Getter
    private static int secondPort = 161;
    @Getter
    private static List<String> oidList = new ArrayList<String>();
    @Getter
    private static String community = "public";
    @Override
    public MachineEntity getSomeInfo(String ipAddress) throws IOException {

        System.out.println("Start snmp Get");

        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        //create the target (host to reach)
        CommunityTarget communityTarget = new CommunityTarget();
        communityTarget.setCommunity(new OctetString(getCommunity()));
        communityTarget.setVersion(getSnmpVersion());
        communityTarget.setAddress(new UdpAddress(ipAddress+"/"+getPort()));
        communityTarget.setRetries(2);
        communityTarget.setTimeout(1000);

        //create the PDU Tram
        PDU pdu = new PDU();
        for (String oid : getOidList()){
            pdu.add(new VariableBinding(new OID(oid)));
        }

        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));

        //create object snmp for sending request to agent
        Snmp snmp = new Snmp(transport);

        System.out.println("Sending pdu to agent ...");
        ResponseEvent responseEvent = snmp.get(pdu,communityTarget);

        //manage process agent
        if(responseEvent!=null){
            System.out.println("Get response from agent");
            PDU responseAgent = responseEvent.getResponse();

            if(responseAgent!=null){
                int errorStatus = responseAgent.getErrorStatus();
                int errorIndex = responseAgent.getErrorIndex();
                String errorStatusText = responseAgent.getErrorStatusText();

                if(errorStatus==PDU.noError){
                    System.out.println("Response GET : "+responseAgent.getVariableBindings());
                }else{
                    System.out.println("Error : request failed!");
                    System.out.println("Error status : "+errorStatus);
                    System.out.println("Error index : "+errorIndex);
                    System.out.println("Error status text : "+errorStatusText);
                }
            }else{
                System.out.println("Error response from agent is null!");
            }
        }else{
            System.out.println("Error agent timeout ...");
        }
        snmp.close();
        return null;
    }
}
