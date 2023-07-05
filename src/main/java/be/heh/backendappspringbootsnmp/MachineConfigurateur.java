package be.heh.backendappspringbootsnmp;

import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpManagerPortOut;
import be.heh.backendappspringbootsnmp.domain.service.MachineService;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.MibBrowser;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpInterfaceManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpMaterialsManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpServiceManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpSystemManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.SnmpListener;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.DeviceScanner;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.SnmpManagerAdaptater;
import org.snmp4j.agent.MOAccess;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories
public class MachineConfigurateur {
    // -------------------- //
    //      Repository      //
    // -------------------- //
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
    // ---------------- //
    //      Mapper      //
    // ---------------- //
    private MachineMapper machineMapper=new MachineMapper();
    private InterfaceMapper interfaceMapper = new InterfaceMapper();
    private ProcessorMapper processorMapper = new ProcessorMapper();
    private PersistentStorageMapper persistentStorageMapper = new PersistentStorageMapper();
    private VolatileStorageMapper volatileStorageMapper = new VolatileStorageMapper();
    private ServiceMapper serviceMapper = new ServiceMapper();
    // ----------------- //
    //      Adaptater    //
    // ----------------- //
    private MachinePersistenceAdaptater machinePersistenceAdaptater;
    private InterfacePersistenceAdaptater interfacePersistenceAdaptater;
    private ProcessorPersistenceAdaptater processorPersistenceAdaptater;
    private PersistentStoragePersistenceAdaptater persistentStoragePersistenceAdaptater;
    private VolatileStoragePersistenceAdaptater volatileStoragePersistenceAdaptater;
    private ServicePersistenceAdaptater servicePersistenceAdaptater;
    private DeviceScanner deviseScanner;
    private OIDPersistenceAdaptater oidPersistenceAdaptater;
    // ------------------ //
    //      Manager       //
    // ------------------ //
    private SnmpSystemManager snmpSystemManager;
    private SnmpInterfaceManager snmpInterfaceManager;
    private SnmpMaterialsManager snmpMaterialsManager;
    private SnmpServiceManager snmpServiceManager;
    // -------------------- //
    //      Component       //
    // -------------------- //
    private MibBrowser mibBrowser;
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
        interfacePersistenceAdaptater = new InterfacePersistenceAdaptater(interfaceRepository,interfaceMapper);
        processorPersistenceAdaptater = new ProcessorPersistenceAdaptater(processorRepository,processorMapper);
        persistentStoragePersistenceAdaptater = new PersistentStoragePersistenceAdaptater(persistentStorageRepository,persistentStorageMapper);
        volatileStoragePersistenceAdaptater = new VolatileStoragePersistenceAdaptater(volatileStorageRepository,volatileStorageMapper);
        servicePersistenceAdaptater = new ServicePersistenceAdaptater(serviceRepository,serviceMapper);
        machinePersistenceAdaptater = new MachinePersistenceAdaptater(machineRepository,machineMapper, interfacePersistenceAdaptater, processorPersistenceAdaptater, persistentStoragePersistenceAdaptater, volatileStoragePersistenceAdaptater,servicePersistenceAdaptater);
        deviseScanner = new DeviceScanner();
        return new MachineService(machinePersistenceAdaptater,deviseScanner,getSnmpManagerPortOut());
    }

    @Bean
    public DeviceScannerPortOut getDeviceScannerPortOut(){return new DeviceScanner();}

    @Bean
    public SnmpManagerPortOut getSnmpManagerPortOut(){
        completeAccess();
        mibBrowser = new MibBrowser(access);
        oidPersistenceAdaptater = new OIDPersistenceAdaptater(mibBrowser);
        oidPersistenceAdaptater.setMibFile("personal-mib.json");
        oidPersistenceAdaptater.initMOManagers();
        snmpListener = new SnmpListener(oidPersistenceAdaptater);
        snmpSystemManager = new SnmpSystemManager(snmpListener);
        snmpInterfaceManager = new SnmpInterfaceManager(snmpListener);
        snmpMaterialsManager = new SnmpMaterialsManager(snmpListener);
        snmpServiceManager = new SnmpServiceManager(snmpListener);
        return new SnmpManagerAdaptater(snmpSystemManager,snmpInterfaceManager,snmpMaterialsManager,snmpServiceManager);
    }

    @Bean
    public MachinePortOut getMachinePortOut(){
        interfacePersistenceAdaptater = new InterfacePersistenceAdaptater(interfaceRepository,interfaceMapper);
        processorPersistenceAdaptater = new ProcessorPersistenceAdaptater(processorRepository,processorMapper);
        persistentStoragePersistenceAdaptater = new PersistentStoragePersistenceAdaptater(persistentStorageRepository,persistentStorageMapper);
        volatileStoragePersistenceAdaptater = new VolatileStoragePersistenceAdaptater(volatileStorageRepository,volatileStorageMapper);
        servicePersistenceAdaptater = new ServicePersistenceAdaptater(serviceRepository,serviceMapper);
        return new MachinePersistenceAdaptater(machineRepository,machineMapper, interfacePersistenceAdaptater, processorPersistenceAdaptater, persistentStoragePersistenceAdaptater, volatileStoragePersistenceAdaptater,servicePersistenceAdaptater);
    }

}
