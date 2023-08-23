package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.integration;

import be.heh.backendappspringbootsnmp.domain.entities.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
/**
 * This test is a complete integration test for the data persistence because they use all secondary adaptater used for this !
 * The individual test for each module of persistence can be found at the same level of this test.
 */

public class MachinePersistenceAdaptaterTest extends AbstractIntegrationTest {

     /* Repository */
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

    /* Adaptater */
    private MachinePersistenceAdaptater machinePersistenceAdaptater;

    @Test
    public void testGetAllMachineEntities(){
        List<MachineEntity> machineEntities = machinePersistenceAdaptater.getAllMachineEntities();

        assertEquals("pc.1.local",machineEntities.get(0).getHostname());
        assertEquals("cisco os 2214",machineEntities.get(1).getOs());

        assertEquals("00-00-00-00-01-01",machineEntities.get(1).getInterfaces().get(0).getMacAddress());
        assertEquals("192.168.0.10",machineEntities.get(2).getInterfaces().get(1).getIpAddress());

        assertEquals(3.6,machineEntities.get(0).getProcessors().get(0).getFrequency());
        assertEquals("Intel Xeon gold 6438N",machineEntities.get(1).getProcessors().get(1).getReference());

        assertEquals("WD Purple",machineEntities.get(1).getPersistentStorages().get(1).getReference());
        assertEquals(1440.0,machineEntities.get(2).getPersistentStorages().get(0).getUsed());

        assertEquals("Crucial Pro",machineEntities.get(2).getVolatileStorages().get(0).getReference());
        assertEquals(4,machineEntities.get(1).getVolatileStorages().get(1).getLatency());

        assertEquals("data collection service",machineEntities.get(1).getServices().get(1).getName());
        assertEquals("4321",machineEntities.get(2).getServices().get(0).getPort());

    }

    @BeforeEach
    public void initializeComponent(){
        /* Initialize Mapper */
        MachineMapper machineMapper = new MachineMapper();
        InterfaceMapper interfaceMapper = new InterfaceMapper();
        ProcessorMapper processorMapper = new ProcessorMapper();
        PersistentStorageMapper persistentStorageMapper = new PersistentStorageMapper();
        VolatileStorageMapper volatileStorageMapper = new VolatileStorageMapper();
        ServiceMapper serviceMapper = new ServiceMapper();

        /* Initialize Adaptater */
        InterfacePersistenceAdaptater interfacePersistenceAdaptater = new InterfacePersistenceAdaptater(interfaceRepository, interfaceMapper);
        ProcessorPersistenceAdaptater processorPersistenceAdaptater = new ProcessorPersistenceAdaptater(processorRepository, processorMapper);
        PersistentStoragePersistenceAdaptater persistentStoragePersistenceAdaptater = new PersistentStoragePersistenceAdaptater(persistentStorageRepository, persistentStorageMapper);
        VolatileStoragePersistenceAdaptater volatileStoragePersistenceAdaptater = new VolatileStoragePersistenceAdaptater(volatileStorageRepository, volatileStorageMapper);
        ServicePersistenceAdaptater servicePersistenceAdaptater = new ServicePersistenceAdaptater(serviceRepository, serviceMapper);
        machinePersistenceAdaptater = new MachinePersistenceAdaptater(
                machineRepository,
                machineMapper,
                interfacePersistenceAdaptater,
                processorPersistenceAdaptater,
                persistentStoragePersistenceAdaptater,
                volatileStoragePersistenceAdaptater,
                servicePersistenceAdaptater
        );
    }
}
