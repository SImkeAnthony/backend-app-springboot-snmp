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
public class ProcessorPersistenceAdaptaterTest extends AbstractIntegrationTest{

    @Autowired
    private ProcessorRepository processorRepository;

    @Test
    public void testGetAllProcessors(){

        ProcessorPersistenceAdaptater processorPersistenceAdaptater = new ProcessorPersistenceAdaptater(processorRepository,new ProcessorMapper());

        List<Processor> processors = processorPersistenceAdaptater.getAllProcessors();

        assertEquals("Intel Xeon gold 6438N",processors.get(1).getReference());
        assertEquals(64,processors.get(3).getVCore());
        assertEquals(3.2,processors.get(2).getFrequency());
    }
}
