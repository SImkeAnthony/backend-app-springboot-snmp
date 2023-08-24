package be.heh.backendappspringbootsnmp.domain.port.out;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;

import java.util.List;

public interface MachinePortOut {
    List<MachineEntity> getAllMachineEntities();
    void registerMachineEntities(List<MachineEntity> machineEntities);
    void updateMachineEntity(MachineEntity machineEntity);
    void registerMachineEntity(MachineEntity machineEntity);
}
