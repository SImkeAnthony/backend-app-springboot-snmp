package be.heh.backendappspringbootsnmp.domain.service;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@Service
public class MachineService implements MachinePortIn {

    @Getter
    private final MachinePortOut machinePortOut;
    private List<MachineEntity>registerMAchineEntities=new ArrayList<>();
    private List<MachineEntity>discoverMAchineEntities=new ArrayList<>();

    @Override
    public List<MachineEntity> getAllMachineEntities() {
        return registerMAchineEntities;
    }

}
