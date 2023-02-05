package be.heh.backendappspringbootsnmp.domain.service;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@Service
public class MachineService implements MachinePortIn {
    @Getter
    private final MachinePortOut machinePortOut;

    @Getter
    private final DeviceScannerPortOut deviceScannerPortOut;
    private List<MachineEntity>registerMAchineEntities=new ArrayList<>();
    private List<MachineEntity>discoverMAchineEntities=new ArrayList<>();


    @Override
    public Iterable<MachineEntity> getAllMachineEntities() throws NMapExecutionException, NMapInitializationException {
        discoverMAchineEntities=getDeviceScannerPortOut().getAllMachineOnNetwork("10.10.10.1-254");
        return discoverMAchineEntities;
    }

}
