package be.heh.backendappspringbootsnmp;

import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpManagerPortOut;
import be.heh.backendappspringbootsnmp.domain.service.MachineService;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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

    @Primary
    @Bean
    public MachinePortIn getMachinePortIn(){
        machinePersistanceAdaptateur = new MachinePersistanceAdaptateur(machineRepository,machineMapper);
        deviseScanner = new DeviceScanner();
        oidMapper = new OIDMapper();
        oidPersistanceAdaptateur = new OIDPersistanceAdaptateur(oidMapper);
        snmpManager = new SnmpManager(oidPersistanceAdaptateur);
        return new MachineService(machinePersistanceAdaptateur,deviseScanner,snmpManager);
    }

    @Bean
    public DeviceScannerPortOut getDeviceScannerPortOut(){return new DeviceScanner();}

    @Bean
    public SnmpManagerPortOut getSnmpManagerPortOut(){
        oidMapper = new OIDMapper();
        oidPersistanceAdaptateur = new OIDPersistanceAdaptateur(oidMapper);
        return new SnmpManager(oidPersistanceAdaptateur);
    }

    @Bean
    public MachinePortOut getMachinePortOut(){
        return new MachinePersistanceAdaptateur(machineRepository,machineMapper);
    }

}
