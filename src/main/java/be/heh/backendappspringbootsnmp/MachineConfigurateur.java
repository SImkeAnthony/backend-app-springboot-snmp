package be.heh.backendappspringbootsnmp;

import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpManagerPortOut;
import be.heh.backendappspringbootsnmp.domain.service.MachineService;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.MachineRepository;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.responder.SnmpListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;

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

    @Primary
    @Bean
    public MachinePortIn getMachinePortIn(){
        machinePersistanceAdaptateur = new MachinePersistanceAdaptateur(machineRepository,machineMapper);
        deviseScanner = new DeviceScanner();
        //oidMapper = new OIDMapper();
        //oidPersistanceAdaptateur = new OIDPersistanceAdaptateur(oidMapper);
        //snmpListener = new SnmpListener(new ArrayList<>(),oidPersistanceAdaptateur);
        //snmpManager = new SnmpManager(snmpListener);
        return new MachineService(machinePersistanceAdaptateur,deviseScanner,getSnmpManagerPortOut());
    }

    @Bean
    public DeviceScannerPortOut getDeviceScannerPortOut(){return new DeviceScanner();}

    @Bean
    public SnmpManagerPortOut getSnmpManagerPortOut(){
        oidMapper = new OIDMapper();
        oidPersistanceAdaptateur = new OIDPersistanceAdaptateur(oidMapper);
        snmpListener = new SnmpListener(new ArrayList<>(),oidPersistanceAdaptateur);

        return new SnmpManager(snmpListener);
    }

    @Bean
    public MachinePortOut getMachinePortOut(){
        return new MachinePersistanceAdaptateur(machineRepository,machineMapper);
    }

}
