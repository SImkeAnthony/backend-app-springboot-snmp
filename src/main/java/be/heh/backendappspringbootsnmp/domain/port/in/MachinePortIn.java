package be.heh.backendappspringbootsnmp.domain.port.in;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;

import java.io.IOException;
import java.util.List;

public interface MachinePortIn {
    public Iterable<MachineEntity> getAllMachineEntities() throws NMapExecutionException, NMapInitializationException, IOException;
    public Iterable<MachineEntity> rescanNetwork() throws NMapExecutionException, NMapInitializationException, IOException;
}
