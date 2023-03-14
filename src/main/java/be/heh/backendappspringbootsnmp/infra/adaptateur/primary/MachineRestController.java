package be.heh.backendappspringbootsnmp.infra.adaptateur.primary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MachineRestController {
    @Getter
    private final MachinePortIn machinePortIn;

    @GetMapping("/")
    public Iterable<MachineEntity> getAllMachineEntities() throws NMapExecutionException, NMapInitializationException, IOException {
        return machinePortIn.getAllMachineEntities();
    }
}
