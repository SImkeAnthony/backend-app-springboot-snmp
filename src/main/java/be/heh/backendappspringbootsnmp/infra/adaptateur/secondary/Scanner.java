package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;
import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import lombok.RequiredArgsConstructor;

import java.net.*;
import java.util.List;
@RequiredArgsConstructor
public class Scanner {
    private final String pathNmapExecutale;

    public List<MachineEntity> getAllMachineOnNetwork(String ipRange){

        return null;
    }
}
