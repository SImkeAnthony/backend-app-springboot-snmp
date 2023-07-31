package be.heh.backendappspringbootsnmp.infra.adaptateur.primary.unitaire;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.infra.adaptateur.primary.MachineRestController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(MachineRestController.class)
public class MachineRestControllerUnitaireTest {

    @MockBean
    private MachineRestController machineRestController;
    @Test
    public void getAllMachineEntitiesTest() throws IOException, NMapExecutionException, NMapInitializationException {

        List<MachineEntity> machineEntities = Arrays.asList(
                new MachineEntity("pc1.local","windows server 2011",true),
                new MachineEntity("pc2.local","windows pro 10",true),
                new MachineEntity("pc3.local","oracle",false),
                new MachineEntity("pc4.local","linux RHEL 8",true),
                new MachineEntity("pc5.local","linux ubuntu",false)
        );

        Mockito.when(machineRestController.getAllMachineEntities()).thenReturn(machineEntities);

        List<MachineEntity> actualMachineEntities = (List<MachineEntity>) machineRestController.getAllMachineEntities();

        Assertions.assertEquals(5,actualMachineEntities.size());
        Assertions.assertEquals("pc3.local",actualMachineEntities.get(2).getHostname());
        Assertions.assertEquals(false,actualMachineEntities.get(4).getSnmp());
        Assertions.assertEquals("windows server 2011",actualMachineEntities.get(0).getOs());

    }

    @Test
    public void rescanNetworkTest() throws IOException, NMapExecutionException, NMapInitializationException {

        List<MachineEntity> machineEntities = Arrays.asList(
                new MachineEntity("pc1.local","windows server 2011",true),
                new MachineEntity("pc2.local","windows pro 10",true),
                new MachineEntity("pc3.local","oracle",false),
                new MachineEntity("pc4.local","linux RHEL 8",true),
                new MachineEntity("pc5.local","linux ubuntu",false)
        );

        Mockito.when(machineRestController.rescanNetwork()).thenReturn(machineEntities);

        List<MachineEntity> actualMachineEntities = (List<MachineEntity>) machineRestController.rescanNetwork();

        Assertions.assertEquals(5,actualMachineEntities.size());
        Assertions.assertEquals("pc3.local",actualMachineEntities.get(2).getHostname());
        Assertions.assertEquals(false,actualMachineEntities.get(4).getSnmp());
        Assertions.assertEquals("windows server 2011",actualMachineEntities.get(0).getOs());

    }

}
