package be.heh.backendappspringbootsnmp.domain.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
public class MachineEntity {
    @Getter
    private final List<String> macAddr;
    @Getter
    private final List<String> ipAddr;
    @Getter
    private final String hostname;
    @Getter
    private final String os;
    @Getter
    private final Boolean snmp;

    @Override
    public String toString(){
        return String.join("/",macAddr)+" : "+String.join("/",ipAddr)+" : "+hostname+" : "+os+" : "+snmp;
    }
}
