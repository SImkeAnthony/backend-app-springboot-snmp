package be.heh.backendappspringbootsnmp.infra.adaptateur.primary.integration;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.infra.adaptateur.primary.MachineRestController;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MachineRestController.class)
public class MachineRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MachinePortIn machinePortIn;

    private List<MachineEntity> machineEntities=new ArrayList<>();

    @Test
    @Order(1)
    public void getAllMachineEntitiesTest() throws Exception {
        //Records
        machineEntities.clear();
        machineEntities.add(new MachineEntity("machine1.local","windows",true));
        machineEntities.add(new MachineEntity("machine2.local","linux ubuntu",false));
        machineEntities.add(new MachineEntity("machine3.local","oracle",false));

        //Stub
        Mockito.when(machinePortIn.getAllMachineEntities()).thenReturn(machineEntities);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[2].hostname",is("machine3.local")))
                .andExpect(jsonPath("$[1].os",is("linux ubuntu")))
                .andExpect(jsonPath("$[0].snmp",is(true)))
                .andReturn();
    }
    @Test
    @Order(2)
    public void rescanTest() throws Exception {
        //Records
        machineEntities.clear();
        machineEntities.add(new MachineEntity("machine6.local","windows server",true));
        machineEntities.add(new MachineEntity("machine5.local","linux RHEL 7",false));
        machineEntities.add(new MachineEntity("machine4.local","oracle",false));

        //Stub
        Mockito.when(machinePortIn.rescanNetwork()).thenReturn(machineEntities);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/scan")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[2].hostname",is("machine4.local")))
                .andExpect(jsonPath("$[1].os",is("linux RHEL 7")))
                .andExpect(jsonPath("$[0].snmp",is(true)))
                .andReturn();

    }

}
