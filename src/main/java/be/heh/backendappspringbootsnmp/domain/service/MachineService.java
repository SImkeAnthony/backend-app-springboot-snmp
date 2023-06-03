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
    @Getter
    @Setter
    private List<String> ipAddress =new ArrayList<>();

    @Override
    public Iterable<MachineEntity> getAllMachineEntities()  {
        try{
            if(getIpAddress().isEmpty()){
                setIpAddress(getDeviceScannerPortOut().getAllIpOnNetwork("192.168.0.1-254"));
                getIpAddress().forEach(System.out::println);
            }
            if(getDiscoverMachineEntities().isEmpty()){
                setDiscoverMachineEntities(getSnmpManagerPortOut().getInfoMachineEntities(getIpAddress()));
            }
            setDiscoverMachineEntities(getSnmpManagerPortOut().updateMachineEntities(getDiscoverMachineEntities()));
        } catch (IOException | NMapInitializationException | NMapExecutionException e) {
            System.out.println("Error scanning ip : "+e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Error interrupt : "+e.getMessage());
        }
        return ()->getDiscoverMachineEntities().iterator();
    }

}
