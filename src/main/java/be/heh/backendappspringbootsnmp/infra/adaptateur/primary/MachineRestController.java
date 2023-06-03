package be.heh.backendappspringbootsnmp.infra.adaptateur.primary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MachineRestController {
    @Getter
    private final MachinePortIn machinePortIn;

    @GetMapping("/")
    public Iterable<MachineEntity> getAllMachineEntities() throws IOException, NMapExecutionException, NMapInitializationException {
        return machinePortIn.getAllMachineEntities();
    }
}
