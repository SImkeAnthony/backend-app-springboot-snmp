package be.heh.backendappspringbootsnmp.infra.adaptateur.primary;

import be.heh.backendappspringbootsnmp.domain.entities.IpRange;
import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    @PostMapping("/scan")
    public ResponseEntity<Iterable<MachineEntity>> rescanNetwork(@RequestBody IpRange jsonIpRange) throws IOException, NMapExecutionException, NMapInitializationException {
        return ResponseEntity.ok(machinePortIn.rescanNetwork(jsonIpRange.getIpRange()));
    }
}

