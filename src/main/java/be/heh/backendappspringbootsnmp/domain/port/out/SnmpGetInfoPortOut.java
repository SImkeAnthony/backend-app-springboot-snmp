package be.heh.backendappspringbootsnmp.domain.port.out;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;

import java.io.IOException;
import java.net.SocketException;

public interface SnmpGetInfoPortOut {

    public MachineEntity getSomeInfo(String ipAddress) throws IOException;
}
