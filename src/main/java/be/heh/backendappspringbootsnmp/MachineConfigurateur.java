package be.heh.backendappspringbootsnmp;

import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpGetInfoPortOut;
import be.heh.backendappspringbootsnmp.domain.service.MachineService;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.DeviceScanner;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.MachineMapper;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.MachinePersistanceAdaptateur;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.SnmpGet;
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
    private SnmpGet snmpGet;

    @Primary
    @Bean
    public MachinePortIn getMachinePortIn(){
        machinePersistanceAdaptateur = new MachinePersistanceAdaptateur(machineRepository,machineMapper);
        deviseScanner = new DeviceScanner();
        snmpGet = new SnmpGet();
        return new MachineService(machinePersistanceAdaptateur,deviseScanner,snmpGet);
    }

    @Bean
    public DeviceScannerPortOut getDeviceScannerPortOut(){
        return new DeviceScanner();
    }

    @Bean
    public SnmpGetInfoPortOut getSnmpGetInfoPortOut(){return  new SnmpGet();}

    @Bean
    public MachinePortOut getMachinePortOut(){
        return new MachinePersistanceAdaptateur(machineRepository,machineMapper);
    }

}
