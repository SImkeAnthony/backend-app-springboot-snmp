package be.heh.backendappspringbootsnmp.domain.port.out;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;

import java.util.List;

public interface MachinePortOut {
    public List<MachineEntity> getAllMachineEntities();
    public void registerMachineEntities(List<MachineEntity> machineEntities);
}
