package be.heh.backendappspringbootsnmp.infra.adaptateur.primary.integration;

import be.heh.backendappspringbootsnmp.domain.entities.Interface;
import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.entities.Service;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.infra.adaptateur.primary.MachineRestController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    public void testGetAllMachineEntities() throws Exception {

        //Records
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

}
