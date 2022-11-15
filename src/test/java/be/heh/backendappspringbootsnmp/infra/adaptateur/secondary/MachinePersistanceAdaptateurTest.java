package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MachinePersistanceAdaptateurTest extends AbstractIntegrationTest {

    @Autowired
    private MachineRepository machineRepository;
    private MachineMapper machineMapper;
    private MachinePersistanceAdaptateur machinePersistanceAdaptateur;

    @Test
    @Sql({"createMachineTable.sql","insertMachine.sql"})
    public void testGetAllMachineEntities(){
        machineMapper = new MachineMapper();
        machinePersistanceAdaptateur = new MachinePersistanceAdaptateur(machineRepository,machineMapper);
        List<MachineEntity> machineEntities = new ArrayList<>();
        machineEntities = machinePersistanceAdaptateur.getAllMachineEntities();
        assertEquals("192.168.0.1",machineEntities.get(0).ipAdd());
        assertEquals(false,machineEntities.get(1).snmp());
    }
}
