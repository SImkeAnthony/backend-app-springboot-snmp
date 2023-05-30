package be.heh.backendappspringbootsnmp.domain.service;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpManagerPortOut;
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
    private final SnmpManagerPortOut snmpManagerPortOut;
    @Getter
    @Setter
    private List<MachineEntity>registerMachineEntities=new ArrayList<>();
    @Getter
    @Setter
    private List<MachineEntity>discoverMachineEntities=new ArrayList<>();


    @Override
    public Iterable<MachineEntity> getAllMachineEntities()  {
        try{
            List<String> ipAddress= getDeviceScannerPortOut().getAllIpOnNetwork("192.168.0.1-254");
            ipAddress.forEach(System.out::println);
            List<String> dummyIpAddress = new ArrayList<>();
            dummyIpAddress.add("127.0.0.1");
            discoverMachineEntities = getSnmpManagerPortOut().getInfoMachineEntities(dummyIpAddress);
        } catch (IOException | NMapInitializationException | NMapExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        for(MachineEntity machineEntity : discoverMachineEntities){
            System.out.println("hostname : "+machineEntity.getHostname()+" / os : "+machineEntity.getOs());
        }
        return discoverMachineEntities;
    }

}
