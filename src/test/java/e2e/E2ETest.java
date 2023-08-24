package e2e;

import be.heh.backendappspringbootsnmp.BackendAppSpringbootSnmpApplication;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;

/**
 * This is an end-to-end test
 * Ensure that this test is running only in a development environment
 * Before running this test, ensure you ran a single agent SNMP freestanding (available here : https://github.com/SImkeAnthony/agent-snmp.git)
 * And ensure you ran an instance of PostgreSQl container via Docker (command : docker run -d --name=postgres -p 5432:5432 -e POSTGRES_PASSWORD=root postgres:latest)
 * Otherwise this test will fail
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,classes = BackendAppSpringbootSnmpApplication.class)
public class E2ETest {
    @LocalServerPort
    private int port;
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
    @Test
    public void getAllMachineEntitiesTest(){
        Response response = when().get("/");
        response.then().statusCode(200);

        //system
        Assertions.assertEquals("generic.hostname.local",response.jsonPath().getString("[0].hostname"));
        Assertions.assertEquals("generic os by generic society",response.jsonPath().getString("[0].os"));
        Assertions.assertTrue(response.jsonPath().getBoolean("[0].snmp"));

        //interface
        List<Map<String, ?>> interfaces = response.jsonPath().getList("[0].interfaces");
        Assertions.assertEquals(2, interfaces.size());
        Assertions.assertEquals("intel R wifi 2.4 gHz", interfaces.get(0).get("description"));
        Assertions.assertEquals("56-65-03-a3-c6-d6", interfaces.get(0).get("macAddress"));

        //processor
        List<Map<String, ?>> processors = response.jsonPath().getList("[0].processors");
        Assertions.assertEquals(2,processors.size());
        Assertions.assertEquals(5.9,Double.parseDouble(processors.get(0).get("frequency").toString()),0.01);
        Assertions.assertEquals(36,processors.get(1).get("vcore"));

        //persistentStorages
        List<Map<String, ?>> persistentStorages = response.jsonPath().getList("[0].persistentStorages");
        Assertions.assertEquals(2,persistentStorages.size());
        Assertions.assertEquals(180.0,Double.parseDouble(persistentStorages.get(0).get("available").toString()),0.01);
        Assertions.assertEquals("WD purple 2T0",persistentStorages.get(1).get("reference"));

        //volatileStorages
        List<Map<String, ?>> volatileStorages = response.jsonPath().getList("[0].volatileStorages");
        Assertions.assertEquals(2,volatileStorages.size());
        Assertions.assertEquals("Crucial RAM 1",volatileStorages.get(0).get("reference"));
        Assertions.assertEquals(16.0,Double.parseDouble(volatileStorages.get(1).get("latency").toString()),0.01);

        //Services
        List<Map<String, ?>> services = response.jsonPath().getList("[0].services");
        Assertions.assertEquals(2,services.size());
        Assertions.assertEquals("this is a site web to access on planing platform",services.get(0).get("description"));
        Assertions.assertEquals("5432",services.get(1).get("port"));
    }
}
