package be.heh.backendappspringbootsnmp.domain.port.out;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;

import java.util.List;

public interface DeviceScannerPortOut {
    List<String> getAllIpOnNetwork(String ipRange) throws NMapExecutionException, NMapInitializationException;
    MachineEntity getAllInfoOfMachineEntity(MachineEntity machineEntity,List<String> ipAddress) throws NMapExecutionException, NMapInitializationException;
}
