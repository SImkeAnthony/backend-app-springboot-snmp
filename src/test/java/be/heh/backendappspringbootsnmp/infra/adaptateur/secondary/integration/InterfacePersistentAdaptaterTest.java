package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.integration;

import be.heh.backendappspringbootsnmp.domain.entities.Interface;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.InterfacePersistenceAdaptater;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.InterfaceMapper;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.InterfaceRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
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
public class InterfacePersistentAdaptaterTest extends AbstractIntegrationTest {
    @Autowired
    private InterfaceRepository interfaceRepository;

    @Test
    public void testGetAllInterfaces(){

        InterfacePersistenceAdaptater interfacePersistenceAdaptater = new InterfacePersistenceAdaptater(interfaceRepository, new InterfaceMapper());

        List<Interface> interfaces = interfacePersistenceAdaptater.getAllInterfaces();

        assertEquals("MediaTek Wi-Fi 6 MT7921 Wireless LAN Card",interfaces.get(1).getDescription());
        assertEquals("192.168.0.16",interfaces.get(4).getIpAddress());
        assertEquals("00-00-00-00-02-02",interfaces.get(5).getMacAddress());
        assertEquals("Hyper-V Virtual Ethernet Adapter",interfaces.get(0).getDescription());
    }
}
