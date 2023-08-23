package be.heh.backendappspringbootsnmp.domain.service.integration;

import be.heh.backendappspringbootsnmp.domain.entities.*;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpManagerPortOut;
import be.heh.backendappspringbootsnmp.domain.service.MachineService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.testcontainers.shaded.com.google.common.collect.Iterators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

@WebMvcTest(MachineService.class)
public class MachineServiceIntegrationTest {

    private DeviceScannerPortOut deviceScannerPortOut;
    private MachinePortOut machinePortOut;
    private SnmpManagerPortOut snmpManagerPortOut;
    private static List<MachineEntity> registeredMachineEntities = new ArrayList<>();
    private static List<MachineEntity> discoveredMachineEntities = new ArrayList<>();
    @Test
    public void getAllMachineEntitiesTest() throws NMapExecutionException, NMapInitializationException, IOException, InterruptedException {
        //mock each third parties
        deviceScannerPortOut = mock(DeviceScannerPortOut.class);
        machinePortOut = mock(MachinePortOut.class);
        snmpManagerPortOut = mock(SnmpManagerPortOut.class);

        Mockito.when(deviceScannerPortOut.getAllIpOnNetwork("192.168.0.1-255")).thenReturn(List.of("192.168.0.20","192.168.0.30","192.168.0.40"));
        Mockito.doNothing().when(machinePortOut).registerMachineEntities(null);
        Mockito.doNothing().when(machinePortOut).registerMachineEntity(null);

        //first call of getAllMachineEntities
        Mockito.when(machinePortOut.getAllMachineEntities()).thenReturn(registeredMachineEntities);

        Mockito.when(snmpManagerPortOut.getInfoMachineEntities(List.of("192.168.0.20","192.168.0.30","192.168.0.40"))).thenReturn(discoveredMachineEntities);

        //second call of getAllMachineEntities
        registeredMachineEntities.addAll(discoveredMachineEntities);
        Mockito.when(machinePortOut.getAllMachineEntities()).thenReturn(registeredMachineEntities);

        MachineService machineService = new MachineService(machinePortOut,deviceScannerPortOut,snmpManagerPortOut);
        Iterable<MachineEntity> actualMachineEntities = machineService.getAllMachineEntities();

        Assertions.assertEquals(true,actualMachineEntities.iterator().hasNext());
        Assertions.assertEquals(5, Iterators.size(actualMachineEntities.iterator()));
        Assertions.assertEquals("192.168.0.5",Iterators.get(actualMachineEntities.iterator(),0).getInterfaces().get(0).getIpAddress());
        Assertions.assertEquals("Intel Xeon Gold",Iterators.get(actualMachineEntities.iterator(),4).getProcessors().get(0).getReference());
        Assertions.assertEquals(420.0,Iterators.get(actualMachineEntities.iterator(),3).getPersistentStorages().get(0).getUsed());
    }
    @BeforeAll
    public static void completeRegisteredMachineEntities(){
        MachineEntity m1 = new MachineEntity(1L,"pc1.local","windows pro 11",true);
        MachineEntity m2 = new MachineEntity(2L,"pc2.local","linux FreeBSD",true);

        m1.setInterfaces(List.of(new Interface(1L,"00-00-00-00-01","carte Ethernet VEthernet adaptater","192.168.0.5")));
        m2.setInterfaces(List.of(new Interface(2L,"00-00-00-00-02","carte Ethernet VEthernet adaptater","192.168.0.10")));

        m1.setProcessors(List.of(new Processor(1L,"Intel core i9",24,56,5.80)));
        m2.setProcessors(List.of(new Processor(2L,"Intel core i7",20,48,5.40)));

        m1.setPersistentStorages(List.of(new PersistentStorage(1L,"Samsung evo 980",180.0,360.0)));
        m2.setPersistentStorages(List.of(new PersistentStorage(2L,"WD Purple",560.0,420.0)));

        m1.setVolatileStorages(List.of(new VolatileStorage(1L,"Crucial p3",16.0,3200.0,12.0)));
        m2.setVolatileStorages(List.of(new VolatileStorage(2L,"Crucial p7",32.0,5600.0,15.0)));

        m1.setServices(List.of(new Service(1L,"web app","application web React for planning online","3030-5432")));
        m2.setServices(List.of(new Service(2L,"file shared","application for sharing file into department","111-2049")));

        registeredMachineEntities.addAll(List.of(m1,m2));
    }

    @BeforeAll
    public static void completeDiscoveredMachineEntities(){
        MachineEntity m1 = new MachineEntity(3L,"pc3.local","windows pro 11",true);
        MachineEntity m2 = new MachineEntity(4L,"pc4.local","linux FreeBSD",true);
        MachineEntity m3 = new MachineEntity(5L,"pc5.local","linux ubuntu",true);

        m1.setInterfaces(List.of(new Interface(3L,"00-00-00-00-03","carte Ethernet VEthernet adaptater","192.168.0.20")));
        m2.setInterfaces(List.of(new Interface(4L,"00-00-00-00-04","Adaptater VirtualBox Ethernet","192.168.0.30")));
        m3.setInterfaces(List.of(new Interface(5L,"00-00-00-00-05","carte wifi Realtek 2.4","192.168.0.40")));

        m1.setProcessors(List.of(new Processor(3L,"Intel core i9",24,56,5.80)));
        m2.setProcessors(List.of(new Processor(4L,"Intel core i7",20,48,5.40)));
        m3.setProcessors(List.of(new Processor(5L,"Intel Xeon Gold",60,120,3.60)));

        m1.setPersistentStorages(List.of(new PersistentStorage(3L,"Samsung evo 980",180.0,360.0)));
        m2.setPersistentStorages(List.of(new PersistentStorage(4L,"WD Purple",560.0,420.0)));
        m3.setPersistentStorages(List.of(new PersistentStorage(5L,"WD Blue",280.0,700.0)));

        m1.setVolatileStorages(List.of(new VolatileStorage(3L,"Crucial p3",16.0,3200.0,12.0)));
        m2.setVolatileStorages(List.of(new VolatileStorage(4L,"Crucial p7",32.0,5600.0,15.0)));
        m3.setVolatileStorages(List.of(new VolatileStorage(5L,"Crucial p7",64.0,5400.0,18.0)));

        m1.setServices(List.of(new Service(3L,"web app","application web React for planning online","3030-5432")));
        m2.setServices(List.of(new Service(4L,"file shared","application for sharing file into department","111-2049")));
        m3.setServices(List.of(new Service(5L,"connexion-operation","application for connecting operation each other","5263-5264")));

        discoveredMachineEntities.addAll(List.of(m1,m2,m3));
    }
}
