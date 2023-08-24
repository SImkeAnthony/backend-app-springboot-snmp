package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.integration;

import be.heh.backendappspringbootsnmp.domain.entities.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.SnmpManagerAdaptater;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpInterfaceManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpMaterialsManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpServiceManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.manager.SnmpSystemManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder.SnmpListener;
import org.javatuples.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.hamcrest.MockitoHamcrest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.*;

import static org.mockito.Mockito.*;

@WebMvcTest(SnmpManagerAdaptater.class)
public class SnmpManagerAdaptaterIntegrationTest {

    private SnmpSystemManager snmpSystemManager;
    private SnmpInterfaceManager snmpInterfaceManager;
    private SnmpMaterialsManager snmpMaterialsManager;
    private SnmpServiceManager snmpServiceManager;

    private SnmpListener snmpListener;
    private static final Map<Integer, Pair<String, Boolean>> requestControllerTest = new HashMap<>();
    private static final List<MachineEntity> machineEntities = new ArrayList<>();

    @Test
    public void getInfoMachineEntitiesTest(){

        List<String> ipAddress = List.of("192.168.0.10","192.168.0.20","192.168.0.30");

        //mocker each third parties

        snmpSystemManager = mock(SnmpSystemManager.class);
        snmpInterfaceManager = mock(SnmpInterfaceManager.class);
        snmpMaterialsManager = mock(SnmpMaterialsManager.class);
        snmpServiceManager = mock(SnmpServiceManager.class);
        snmpListener = mock(SnmpListener.class);

        when(snmpListener.getRequestController()).thenReturn(requestControllerTest);
        when(snmpListener.getMachineEntities()).thenReturn(machineEntities);
        when(snmpSystemManager.getSnmpListener()).thenReturn(snmpListener);

        //simulate function's behavior
        doAnswer(invocation -> {
            snmpSystemManager.getSnmpListener().getMachineEntities().addAll(List.of(
                    new MachineEntity("pc1.local","windows pro 11",true),
                    new MachineEntity("pc2.local","Oracle Server",true),
                    new MachineEntity("pc3.local","Linux ubuntu",true)
            ));
            return null;
        }).when(snmpSystemManager).completeMachineEntitiesWithSystemVariables(ipAddress);

        doAnswer(invocation -> {
            snmpSystemManager.getSnmpListener().getMachineEntities().get(0).setInterfaces(List.of(new Interface("00-00-00-00-00-01","Adaptater WiFi Realtek 2.4","192.168.0.10")));
            snmpSystemManager.getSnmpListener().getMachineEntities().get(1).setInterfaces(List.of(new Interface("00-00-00-00-00-02","Adaptater VEthernet VMWare","192.168.0.20")));
            snmpSystemManager.getSnmpListener().getMachineEntities().get(2).setInterfaces(List.of(new Interface("00-00-00-00-00-03","Adaptater Ethernet","192.168.0.30")));
            return null;
        }).when(snmpInterfaceManager).completeInterfacesForEachMachineEntities(MockitoHamcrest.argThat(PersonalMatcher.sameAsSet(ipAddress)));

        doAnswer(invocation -> {
            //processor
            snmpSystemManager.getSnmpListener().getMachineEntities().get(0).setProcessors(List.of(new Processor("intel core i9",24,56,5.80)));
            snmpSystemManager.getSnmpListener().getMachineEntities().get(1).setProcessors(List.of(new Processor("intel core i7",20,48,5.40)));
            snmpSystemManager.getSnmpListener().getMachineEntities().get(2).setProcessors(List.of(new Processor("intel Xeon Gold",60,120,3.60)));
            //persistent storage
            snmpSystemManager.getSnmpListener().getMachineEntities().get(0).setPersistentStorages(List.of(new PersistentStorage("Samsung EVO 980",180.0,50.0)));
            snmpSystemManager.getSnmpListener().getMachineEntities().get(1).setPersistentStorages(List.of(new PersistentStorage("WD Purple",560.0,430.0)));
            snmpSystemManager.getSnmpListener().getMachineEntities().get(2).setPersistentStorages(List.of(new PersistentStorage("WD Blue",1200.0,790.0)));
            //volatile storage
            snmpSystemManager.getSnmpListener().getMachineEntities().get(0).setVolatileStorages(List.of(new VolatileStorage("Crucial p3",16.0,3200.0,12.0)));
            snmpSystemManager.getSnmpListener().getMachineEntities().get(1).setVolatileStorages(List.of(new VolatileStorage("Crucial p5",32.0,5600.0,16.0)));
            snmpSystemManager.getSnmpListener().getMachineEntities().get(2).setVolatileStorages(List.of(new VolatileStorage("Crucial p7",128.0,2333.0,18.0)));
            return null;
        }).when(snmpMaterialsManager).completeMaterialsForEachMachineEntities(MockitoHamcrest.argThat(PersonalMatcher.sameAsSet(ipAddress)));

        doAnswer(invocation -> {
            snmpSystemManager.getSnmpListener().getMachineEntities().get(0).setServices(List.of(new Service("web app","application web React for planning online","3030-5432")));
            snmpSystemManager.getSnmpListener().getMachineEntities().get(1).setServices(List.of(new Service("file shared","application for sharing file into department","111-2049")));
            snmpSystemManager.getSnmpListener().getMachineEntities().get(2).setServices(List.of(new Service("connexion-operation","application for connecting operation each other","5263-5264")));
            return null;
        }).when(snmpServiceManager).completeServicesForEachMachineEntities(MockitoHamcrest.argThat(PersonalMatcher.sameAsSet(ipAddress)));

        SnmpManagerAdaptater snmpManagerAdaptater = new SnmpManagerAdaptater(snmpSystemManager,snmpInterfaceManager,snmpMaterialsManager,snmpServiceManager);

        List<MachineEntity> actualMachineEntities = snmpManagerAdaptater.getInfoMachineEntities(ipAddress);

        Assertions.assertEquals(3,actualMachineEntities.size());
        verify(snmpInterfaceManager).completeInterfacesForEachMachineEntities(MockitoHamcrest.argThat(PersonalMatcher.sameAsSet(ipAddress)));
        Assertions.assertEquals("192.168.0.30",actualMachineEntities.get(2).getInterfaces().get(0).getIpAddress());
        verify(snmpServiceManager).completeServicesForEachMachineEntities(MockitoHamcrest.argThat(PersonalMatcher.sameAsSet(ipAddress)));
        Assertions.assertEquals("connexion-operation",actualMachineEntities.get(2).getServices().get(0).getName());
        verify(snmpMaterialsManager).completeMaterialsForEachMachineEntities(MockitoHamcrest.argThat(PersonalMatcher.sameAsSet(ipAddress)));
        Assertions.assertEquals("intel core i7",actualMachineEntities.get(1).getProcessors().get(0).getReference());
        verify(snmpSystemManager).completeMachineEntitiesWithSystemVariables(MockitoHamcrest.argThat(PersonalMatcher.sameAsSet(ipAddress)));
        Assertions.assertEquals("pc1.local",actualMachineEntities.get(0).getHostname());
    }
    @BeforeAll
    public static void clearMachineEntities(){
        machineEntities.clear();
    }
    @BeforeAll
    public static void completeRequestControllerTest(){
        requestControllerTest.put(1,new Pair<>("192.168.0.10",true));
        requestControllerTest.put(2,new Pair<>("192.168.0.20",true));
        requestControllerTest.put(3,new Pair<>("192.168.0.30",true));
    }
}
