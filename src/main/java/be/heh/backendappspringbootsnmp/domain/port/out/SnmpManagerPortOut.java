package be.heh.backendappspringbootsnmp.domain.port.out;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;

import java.io.IOException;
import java.util.List;

public interface SnmpManagerPortOut {

    public List<MachineEntity> getInfoMachineEntities(List<String> ipAddress) throws IOException, InterruptedException;
    public List<MachineEntity> updateMachineEntities(List<MachineEntity> machineEntities);
}
