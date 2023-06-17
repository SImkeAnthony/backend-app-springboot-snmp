package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.MachineMapper;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.MachineRepository;
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
public class MachinePersistanceAdaptaterTest extends AbstractIntegrationTest {

    @Autowired
    private MachineRepository machineRepository;
    private MachineMapper machineMapper;
    private MachinePersistanceAdaptater machinePersistanceAdaptateur;

    @Test
    @Sql({"createMachineTable.sql","insertMachine.sql"})
    public void testGetAllMachineEntities(){

    }
}
