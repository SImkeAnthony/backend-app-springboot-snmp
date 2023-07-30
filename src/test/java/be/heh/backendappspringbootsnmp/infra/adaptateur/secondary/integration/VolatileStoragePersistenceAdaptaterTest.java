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
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class VolatileStoragePersistenceAdaptaterTest extends AbstractIntegrationTest{

    @Autowired
    private VolatileStorageRepository volatileStorageRepository;

    @Test
    @Sql({"createMachineTable.sql","insertMachine.sql","createVolatileStorageTable.sql", "insertVolatileStorage.sql"})
    public void testGetAllVStorages(){
        VolatileStoragePersistenceAdaptater volatileStoragePersistenceAdaptater = new VolatileStoragePersistenceAdaptater(volatileStorageRepository,new VolatileStorageMapper());

        List<VolatileStorage> volatileStorages = volatileStoragePersistenceAdaptater.getAllVolatileStorages();

        assertEquals("Crucial Pro",volatileStorages.get(5).getReference());
        assertEquals(4,volatileStorages.get(1).getLatency());
    }

}
