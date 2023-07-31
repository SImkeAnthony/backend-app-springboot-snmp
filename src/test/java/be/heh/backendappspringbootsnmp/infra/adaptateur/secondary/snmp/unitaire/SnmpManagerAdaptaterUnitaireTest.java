package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.unitaire;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.SnmpManagerAdaptater;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(SnmpManagerAdaptater.class)
public class SnmpManagerAdaptaterUnitaireTest {

    @MockBean
    private SnmpManagerAdaptater snmpManagerAdaptater;

    @Test
    public void getInfoMachineEntitiesTest(){
        List<String> ipAddresses = Arrays.asList("192.168.0.1","192.168.0.2");

        List<MachineEntity> machineEntities = Arrays.asList(
                new MachineEntity("pc1.local","windows server 2000",true),
                new MachineEntity("pc2.local","windows pro 11",true)
        );

        Mockito.when(snmpManagerAdaptater.getInfoMachineEntities(ipAddresses)).thenReturn(machineEntities);

        List<MachineEntity> actualMachineEntities = snmpManagerAdaptater.getInfoMachineEntities(ipAddresses);

        Assertions.assertEquals(2,actualMachineEntities.size());
        Assertions.assertEquals(true,actualMachineEntities.get(0).getSnmp());
        Assertions.assertEquals("pc2.local",actualMachineEntities.get(1).getHostname());

    }
}
