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
public class ServicePersistenceAdaptaterTest extends AbstractIntegrationTest{

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    @Sql({"createMachineTable.sql","insertMachine.sql","createServiceTable.sql", "insertService.sql"})
    public void testGetAllServices(){
        ServicePersistenceAdaptater servicePersistenceAdaptater = new ServicePersistenceAdaptater(serviceRepository,new ServiceMapper());

        List<Service> services = servicePersistenceAdaptater.getAllServices();

        assertEquals("service to access to the planning web",services.get(1).getDescription());
        assertEquals("4321",services.get(0).getPort());
    }
}
