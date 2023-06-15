package be.heh.backendappspringbootsnmp;

import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpManagerPortOut;
import be.heh.backendappspringbootsnmp.domain.service.MachineService;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.MachineRepository;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.responder.SnmpListener;
import org.snmp4j.agent.MOAccess;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories
public class MachineConfigurateur {
    @Autowired
    private MachineRepository machineRepository;
    private MachineMapper machineMapper=new MachineMapper();
    private MachinePersistanceAdaptateur machinePersistanceAdaptateur;
    private DeviceScanner deviseScanner;
    private SnmpManager snmpManager;
    private OIDPersistanceAdaptateur oidPersistanceAdaptateur;
    private OIDMapper oidMapper;
    private SnmpListener snmpListener;
    private Map<String, MOAccess> access = new HashMap<>();

    private void completeAccess(){
        access.put("read-only", MOAccessImpl.ACCESS_READ_ONLY);
        access.put("write-only",MOAccessImpl.ACCESS_WRITE_ONLY);
        access.put("read-write",MOAccessImpl.ACCESS_READ_WRITE);
        access.put("for-notify",MOAccessImpl.ACCESS_FOR_NOTIFY);
        access.put("read-create",MOAccessImpl.ACCESS_READ_CREATE);
        access.put("not-accessible",MOAccessImpl.ACCESS_READ_ONLY);
    }

    @Primary
    @Bean
    public MachinePortIn getMachinePortIn(){
        machinePersistanceAdaptateur = new MachinePersistanceAdaptateur(machineRepository,machineMapper);
        deviseScanner = new DeviceScanner();
        return new MachineService(machinePersistanceAdaptateur,deviseScanner,getSnmpManagerPortOut());
    }

    @Bean
    public DeviceScannerPortOut getDeviceScannerPortOut(){return new DeviceScanner();}

    @Bean
    public SnmpManagerPortOut getSnmpManagerPortOut(){
        completeAccess();
        oidMapper = new OIDMapper(access);
        oidPersistanceAdaptateur = new OIDPersistanceAdaptateur(oidMapper);
        snmpListener = new SnmpListener(new ArrayList<>(),oidPersistanceAdaptateur);

        return new SnmpManager(snmpListener);
    }

    @Bean
    public MachinePortOut getMachinePortOut(){
        return new MachinePersistanceAdaptateur(machineRepository,machineMapper);
    }

}
