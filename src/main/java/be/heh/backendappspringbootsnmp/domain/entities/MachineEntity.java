package be.heh.backendappspringbootsnmp.domain.entities;

public record MachineEntity(String macAdd,String ipAdd,String hostName,String type, Boolean snmp) {
}
