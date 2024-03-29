package be.heh.backendappspringbootsnmp.domain.port.out;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;

import java.io.IOException;
import java.util.List;

public interface SnmpManagerPortOut {

    List<MachineEntity> getInfoMachineEntities(List<String> ipAddress) throws IOException, InterruptedException;
}
