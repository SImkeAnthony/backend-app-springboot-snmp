package be.heh.backendappspringbootsnmp.domain.port.in;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;

import java.util.List;

public interface MachinePortIn {
    public Iterable<MachineEntity> getAllMachineEntities() throws NMapExecutionException, NMapInitializationException;
}
