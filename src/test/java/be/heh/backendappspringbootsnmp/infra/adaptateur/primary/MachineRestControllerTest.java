package be.heh.backendappspringbootsnmp.infra.adaptateur.primary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
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
        machineEntities.add(new MachineEntity("02-23-04-05-06-07","192.168.0.15","pc-1","pc",true));
        machineEntities.add(new MachineEntity("02-23-04-05-06-08","192.168.0.1","routeur-1","routeur",false));
        machineEntities.add(new MachineEntity("02-23-04-05-06-09","192.168.0.250","serveur-1","serveur",true));

        //Stub
        Mockito.when(machinePortIn.getAllMachineEntities()).thenReturn(machineEntities);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[2].ipAdd",is("192.168.0.250")))
                .andExpect(jsonPath("$[1].hostName",is("routeur-1")))
                .andExpect(jsonPath("$[0].hostName",is("pc-1")));
    }

}
