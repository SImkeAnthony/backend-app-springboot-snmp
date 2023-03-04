package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.out.snmpGetInfoPortOut;
import lombok.Getter;
import org.snmp4j.mp.SnmpConstants;

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
    public MachineEntity getSomeInfo(String ipAddress) {

        return null;
    }
}
