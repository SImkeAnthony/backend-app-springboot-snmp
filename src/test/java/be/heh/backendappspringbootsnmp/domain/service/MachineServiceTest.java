package be.heh.backendappspringbootsnmp.domain.service;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

public class MachineServiceTest {
    @Test
    public void getAllMachineEntitiesTestEmptyList(){
        MachineService machineService = new MachineService();

        machineService.setIpAddress(new ArrayList<>());
        machineService.setDiscoverMachineEntities(new ArrayList<>());
        machineService.setRegisterMachineEntities(new ArrayList<>());

        Iterable<MachineEntity> machineEntities = machineService.getAllMachineEntities();

        Assertions.assertFalse(machineEntities.iterator().hasNext());
    }

    @Test void getAllMachineEntitiesTestNotEmptyList(){
        MachineService machineService = new MachineService();

        machineService.setIpAddress(Arrays.asList("192.168.0.12","192.168.0.15"));
        machineService.setDiscoverMachineEntities(List.of(new MachineEntity[]{
                new MachineEntity("pc1.local", "os1", false),
                new MachineEntity("pc2.local", "os2", true),
                new MachineEntity("pc3.local", "os3", false)
        }));
        machineService.setRegisterMachineEntities(List.of(new MachineEntity[]{
                new MachineEntity("pc4.local","os4",true),
                new MachineEntity("pc5.local","os5",false)
        }));

        Iterable<MachineEntity> machineEntities = machineService.getAllMachineEntities();

        Assertions.assertEquals(2, StreamSupport.stream(machineEntities.spliterator(),false).toList().size());
        Assertions.assertEquals("192.168.0.12",machineService.getIpAddress().get(0));
        Assertions.assertEquals(3,machineService.getDiscoverMachineEntities().size());
    }
}
