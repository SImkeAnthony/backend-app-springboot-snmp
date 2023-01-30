package be.heh.backendappspringbootsnmp.domain.port.in;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;

import java.util.List;

public interface MachinePortIn {
    public Iterable<MachineEntity> getAllMachineEntities();
}
