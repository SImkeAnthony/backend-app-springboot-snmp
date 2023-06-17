package be.heh.backendappspringbootsnmp;

import be.heh.backendappspringbootsnmp.domain.entities.Service;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpManagerPortOut;
import be.heh.backendappspringbootsnmp.domain.service.MachineService;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.SnmpListener;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.DeviceScanner;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.SnmpManager;
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
    @Autowired
    private InterfaceRepository interfaceRepository;
    @Autowired
    private ProcessorRepository processorRepository;
    @Autowired
    private PersistentStorageRepository persistentStorageRepository;
    @Autowired
    private VolatileStorageRepository volatileStorageRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    private MachineMapper machineMapper=new MachineMapper();
    private InterfaceMapper interfaceMapper = new InterfaceMapper();
    private ProcessorMapper processorMapper = new ProcessorMapper();
    private PersistentStorageMapper persistentStorageMapper = new PersistentStorageMapper();
    private VolatileStorageMapper volatileStorageMapper = new VolatileStorageMapper();
    private ServiceMapper serviceMapper = new ServiceMapper();
    private MachinePersistanceAdaptater machinePersistanceAdaptater;
    private InterfacePersistanceAdaptater interfacePersistanceAdaptater;
    private ProcessorPersistanceAdaptater processorPersistanceAdaptater;
    private PersistentStoragePersistanceAdaptater persistentStoragePersistanceAdaptater;
    private VolatileStoragePersistanceAdaptater volatileStoragePersistanceAdaptater;
    private ServicePersistenceAdaptater servicePersistenceAdaptater;
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
        interfacePersistanceAdaptater = new InterfacePersistanceAdaptater(interfaceRepository,interfaceMapper);
        processorPersistanceAdaptater = new ProcessorPersistanceAdaptater(processorRepository,processorMapper);
        persistentStoragePersistanceAdaptater = new PersistentStoragePersistanceAdaptater(persistentStorageRepository,persistentStorageMapper);
        volatileStoragePersistanceAdaptater = new VolatileStoragePersistanceAdaptater(volatileStorageRepository,volatileStorageMapper);
        servicePersistenceAdaptater = new ServicePersistenceAdaptater(serviceRepository,serviceMapper);
        machinePersistanceAdaptater = new MachinePersistanceAdaptater(machineRepository,machineMapper,interfacePersistanceAdaptater,processorPersistanceAdaptater,persistentStoragePersistanceAdaptater,volatileStoragePersistanceAdaptater,servicePersistenceAdaptater);
        deviseScanner = new DeviceScanner();
        return new MachineService(machinePersistanceAdaptater,deviseScanner,getSnmpManagerPortOut());
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
        interfacePersistanceAdaptater = new InterfacePersistanceAdaptater(interfaceRepository,interfaceMapper);
        processorPersistanceAdaptater = new ProcessorPersistanceAdaptater(processorRepository,processorMapper);
        persistentStoragePersistanceAdaptater = new PersistentStoragePersistanceAdaptater(persistentStorageRepository,persistentStorageMapper);
        volatileStoragePersistanceAdaptater = new VolatileStoragePersistanceAdaptater(volatileStorageRepository,volatileStorageMapper);
        servicePersistenceAdaptater = new ServicePersistenceAdaptater(serviceRepository,serviceMapper);
        return new MachinePersistanceAdaptater(machineRepository,machineMapper,interfacePersistanceAdaptater,processorPersistanceAdaptater,persistentStoragePersistanceAdaptater,volatileStoragePersistanceAdaptater,servicePersistenceAdaptater);
    }

}
