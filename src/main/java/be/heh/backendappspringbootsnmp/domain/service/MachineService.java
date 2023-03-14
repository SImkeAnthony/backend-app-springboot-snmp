package be.heh.backendappspringbootsnmp.domain.service;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpGetInfoPortOut;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@Service
public class MachineService implements MachinePortIn {
    @Getter
    private final MachinePortOut machinePortOut;

    @Getter
    private final DeviceScannerPortOut deviceScannerPortOut;

    @Getter
    private final SnmpGetInfoPortOut snmpGetInfoPortOut;
    @Getter
    @Setter
    private List<MachineEntity>registerMachineEntities=new ArrayList<>();
    @Getter
    @Setter
    private List<MachineEntity>discoverMachineEntities=new ArrayList<>();


    @Override
    public Iterable<MachineEntity> getAllMachineEntities() throws NMapExecutionException, NMapInitializationException, IOException {
        List<String> ipAddress= getDeviceScannerPortOut().getAllIpOnNetwork("192.168.0.1-254");
        ipAddress.forEach(System.out::println);

        //Not working well -> implement os,hostname and snmp with snmp4j

        //setDiscoverMAchineEntities(getDeviceScannerPortOut().getAllInfoOfMachines(ipAddress));
        // display -> getDiscoverMAchineEntities().forEach(System.out::println);

        getSnmpGetInfoPortOut().getSomeInfo("192.168.0.12");

        return discoverMachineEntities;
    }

}
