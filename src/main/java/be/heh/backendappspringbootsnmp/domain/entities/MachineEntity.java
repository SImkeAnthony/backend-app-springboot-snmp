package be.heh.backendappspringbootsnmp.domain.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MachineEntity {
    @Getter
    private final String macAddr;
    @Getter
    private final String ipAddr;
    @Getter
    private final String hostname;
    @Getter
    private final String device;
    @Getter
    private final Boolean snmp;

}
