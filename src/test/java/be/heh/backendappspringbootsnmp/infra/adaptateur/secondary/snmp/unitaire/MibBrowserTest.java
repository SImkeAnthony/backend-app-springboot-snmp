package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.unitaire;

import be.heh.backendappspringbootsnmp.domain.entities.MOTable;
import be.heh.backendappspringbootsnmp.domain.entities.MOVariable;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.MibBrowser;
import org.javatuples.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.smi.OID;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Arrays;

@WebMvcTest(MibBrowser.class)
public class MibBrowserTest {

    @MockBean
    private MibBrowser mibBrowser;

    @Test
    public void getNodeIdentityTest(){
        List<Pair<String,String>> nodeIdentity = Arrays.asList(
                new Pair<String,String>("default1","1.2.3.4"),
                new Pair<String,String>("default2","1.2.3.5"),
                new Pair<String,String>("default3","1.2.3.6")
        );

        Mockito.when(mibBrowser.getNodeIdentity()).thenReturn(nodeIdentity);

        List<Pair<String,String>> actualNodeIdentities = mibBrowser.getNodeIdentity();

        Assertions.assertEquals(3,actualNodeIdentities.size());
        Assertions.assertEquals("default3",actualNodeIdentities.get(2).getValue0());
        Assertions.assertEquals("1.2.3.4",actualNodeIdentities.get(0).getValue1());
    }

    @Test
    public void getAllMOVariableTest(){
        List<MOVariable> moVariables = Arrays.asList(
                new MOVariable("default1","1.2.3.4.5.1", MOAccessImpl.ACCESS_READ_ONLY,"desc1"),
                new MOVariable("default2","1.2.3.4.5.2", MOAccessImpl.ACCESS_READ_ONLY,"desc2"),
                new MOVariable("default3","1.2.3.4.5.3", MOAccessImpl.ACCESS_READ_WRITE,"desc3"),
                new MOVariable("default4","1.2.3.4.5.4", MOAccessImpl.ACCESS_WRITE_ONLY,"desc4"),
                new MOVariable("default5","1.2.3.4.5.5", MOAccessImpl.ACCESS_READ_CREATE,"desc5")
        );

        Mockito.when(mibBrowser.getAllMOVariable()).thenReturn(moVariables);

        List<MOVariable> actualMOVariables = mibBrowser.getAllMOVariable();

        Assertions.assertEquals(5,actualMOVariables.size());
        Assertions.assertEquals("default4",actualMOVariables.get(3).getName());
        Assertions.assertEquals("1.2.3.4.5.2",actualMOVariables.get(1).getOid());
        Assertions.assertEquals(MOAccessImpl.ACCESS_WRITE_ONLY,actualMOVariables.get(3).getMoAccess());
        Assertions.assertEquals("desc5",actualMOVariables.get(4).getDescription());
    }

    @Test
    public void getMOVariablesBySubTreeTest(){
        OID oidIdentity = new OID("1.2.3.4.5");

        List<MOVariable> moVariables = Arrays.asList(
                new MOVariable("default1","1.2.3.4.5.1", MOAccessImpl.ACCESS_READ_ONLY,"desc1"),
                new MOVariable("default2","1.2.3.4.5.2", MOAccessImpl.ACCESS_READ_ONLY,"desc2"),
                new MOVariable("default3","1.2.3.4.5.3", MOAccessImpl.ACCESS_READ_WRITE,"desc3"),
                new MOVariable("default4","1.2.3.4.5.4", MOAccessImpl.ACCESS_WRITE_ONLY,"desc4"),
                new MOVariable("default5","1.2.3.4.5.5", MOAccessImpl.ACCESS_READ_CREATE,"desc5")
        );

        Mockito.when(mibBrowser.getMOVariablesBySubTree(oidIdentity)).thenReturn(moVariables);

        List<MOVariable> actualMOVariables = mibBrowser.getMOVariablesBySubTree(oidIdentity);

        Assertions.assertEquals(5,actualMOVariables.size());
        Assertions.assertEquals("default4",actualMOVariables.get(3).getName());
        Assertions.assertEquals("1.2.3.4.5.2",actualMOVariables.get(1).getOid());
        Assertions.assertEquals(MOAccessImpl.ACCESS_WRITE_ONLY,actualMOVariables.get(3).getMoAccess());
        Assertions.assertEquals("desc5",actualMOVariables.get(4).getDescription());

    }

    @Test
    public void getMOTableBySubTreeTest(){
        OID oidIdentity = new OID("1.2.3.4.5");

        List<MOTable> moTables = Arrays.asList(
                new MOTable("table1","1.2.3.4","desc1"),
                new MOTable("table2","1.2.3.6","desc2"),
                new MOTable("table3","1.2.3.8","desc3"),
                new MOTable("table4","1.2.3.10","desc4")
        );

        Mockito.when(mibBrowser.getMOTableBySubTree(oidIdentity)).thenReturn(moTables);

        List<MOTable> actualMOTables = mibBrowser.getMOTableBySubTree(oidIdentity);

        Assertions.assertEquals(4,actualMOTables.size());
        Assertions.assertEquals("table2",actualMOTables.get(1).getName());
        Assertions.assertEquals("1.2.3.8",actualMOTables.get(2).getOid());
        Assertions.assertEquals("desc4",actualMOTables.get(3).getDescription());
    }
}
