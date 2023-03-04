package be.heh.backendappspringbootsnmp.domain.port.out;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;

public interface snmpGetInfoPortOut {

    public MachineEntity getSomeInfo(String ipAddress);
}
