package be.heh.backendappspringbootsnmp.domain.port.out;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;

import java.util.List;

public interface DeviceScannerPortOut {
    public List<String> getAllIpOnNetwork(String ipRange) throws NMapExecutionException, NMapInitializationException;
    public MachineEntity getAllInfoOfMachineEntity(MachineEntity machineEntity) throws NMapExecutionException, NMapInitializationException;
}
