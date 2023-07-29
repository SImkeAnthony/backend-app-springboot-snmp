package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.integration;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.*;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

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

    /* Mapper */
    private MachineMapper machineMapper;
    private InterfaceMapper interfaceMapper;
    private ProcessorMapper processorMapper;
    private PersistentStorageMapper persistentStorageMapper;
    private VolatileStorageMapper volatileStorageMapper;
    private ServiceMapper serviceMapper;

    /* Adaptater */
    private MachinePersistenceAdaptater machinePersistenceAdaptater;
    private InterfacePersistenceAdaptater interfacePersistenceAdaptater;
    private ProcessorPersistenceAdaptater processorPersistenceAdaptater;
    private PersistentStoragePersistenceAdaptater persistentStoragePersistenceAdaptater;
    private VolatileStoragePersistenceAdaptater volatileStoragePersistenceAdaptater;
    private ServicePersistenceAdaptater servicePersistenceAdaptater;

    @Test
    @Sql({"createMachineTable.sql",
            "insertMachine.sql",
            "createInterfaceTable.sql",
            "insertInterface.sql",
            "createProcessorTable.sql",
            "insertProcessor.sql",
            "createPersistentStorageTable.sql",
            "insertPersistentStorage.sql",
            "createVolatileStorageTable.sql",
            "insertVolatileStorage.sql",
            "createServiceTable.sql",
            "insertService.sql"})
    public void testGetAllMachineEntities(){
        initializeComponent();

    }

    private void initializeComponent(){
        /* Initialize Mapper */
        machineMapper = new MachineMapper();
        interfaceMapper = new InterfaceMapper();
        processorMapper = new ProcessorMapper();
        persistentStorageMapper = new PersistentStorageMapper();
        volatileStorageMapper = new VolatileStorageMapper();
        serviceMapper = new ServiceMapper();

        /* Initialize Adaptater */
        interfacePersistenceAdaptater = new InterfacePersistenceAdaptater(interfaceRepository,interfaceMapper);
        processorPersistenceAdaptater = new ProcessorPersistenceAdaptater(processorRepository,processorMapper);
        persistentStoragePersistenceAdaptater = new PersistentStoragePersistenceAdaptater(persistentStorageRepository,persistentStorageMapper);
        volatileStoragePersistenceAdaptater = new VolatileStoragePersistenceAdaptater(volatileStorageRepository,volatileStorageMapper);
        servicePersistenceAdaptater = new ServicePersistenceAdaptater(serviceRepository,serviceMapper);
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
