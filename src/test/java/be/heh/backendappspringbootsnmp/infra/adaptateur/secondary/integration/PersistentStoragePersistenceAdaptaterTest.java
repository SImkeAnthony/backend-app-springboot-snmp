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
public class PersistentStoragePersistenceAdaptaterTest extends AbstractIntegrationTest{

    @Autowired
    private PersistentStorageRepository persistentStorageRepository;

    @Test
    public void testGetAllPStorages(){

        PersistentStoragePersistenceAdaptater persistentStoragePersistenceAdaptater = new PersistentStoragePersistenceAdaptater(persistentStorageRepository,new PersistentStorageMapper());
        List<PersistentStorage> persistentStorages = persistentStoragePersistenceAdaptater.getAllPersistentStorages();

        assertEquals("Samsung EVO 970 Pro",persistentStorages.get(2).getReference());
        assertEquals(660,persistentStorages.get(4).getUsed());
        assertEquals(1560,persistentStorages.get(3).getAvailable());
    }
}
