package be.heh.backendappspringbootsnmp.domain.service;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MachineService implements MachinePortIn {

    private List<MachineEntity>registerMAchineEntities=new ArrayList<>();
    private List<MachineEntity>discoverMAchineEntities=new ArrayList<>();

    @Override
    public List<MachineEntity> getAllMachineEntities() {
        return registerMAchineEntities;
    }

}
