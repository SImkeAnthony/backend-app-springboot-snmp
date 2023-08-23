package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.unitaire;

import be.heh.backendappspringbootsnmp.domain.entities.MOManager;
import be.heh.backendappspringbootsnmp.domain.entities.MOVariable;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.OIDPersistenceAdaptater;
import org.javatuples.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.smi.OID;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(OIDPersistenceAdaptater.class)
public class OIDPersistenceAdaptaterTest {
    @MockBean
    private OIDPersistenceAdaptater oidPersistenceAdaptater;

    @Test
    public void getMOManagerByOIDTest(){
        OID oid = new OID("1.2.3.4.5.6.7.8.9");
        MOManager moManager = new MOManager("default","1.2.3");

        Mockito.when(oidPersistenceAdaptater.getMOManagerByOID(oid)).thenReturn(moManager);

        MOManager actualMOManager = oidPersistenceAdaptater.getMOManagerByOID(oid);

        Assertions.assertEquals("1.2.3",actualMOManager.getOidRoot());
        Assertions.assertEquals("default",actualMOManager.getName());
    }

    @Test
    public void getOIDHostnameTest(){
        String oidHostname = "1.3.4.12.6.13.2.4";

        Mockito.when(oidPersistenceAdaptater.getOIDHostname()).thenReturn(oidHostname);

        String actualOIDHostname = oidPersistenceAdaptater.getOIDHostname();

        Assertions.assertEquals("1.3.4.12.6.13.2.4",actualOIDHostname);
    }

    @Test
    public void getOidNumberIndexOfTableTest(){
        String tableName = "defaultTable";
        Pair<String,String> numberIndex = new Pair<>("1.2.3.4","1.2.3.4.1");

        Mockito.when(oidPersistenceAdaptater.getOidNumberIndexOfTable(tableName)).thenReturn(numberIndex);

        Pair<String,String> actualNumberIndex = oidPersistenceAdaptater.getOidNumberIndexOfTable(tableName);

        Assertions.assertEquals("1.2.3.4",actualNumberIndex.getValue0());
        Assertions.assertEquals("1.2.3.4.1",actualNumberIndex.getValue1());
    }

    @Test
    public void getColumnOfTableTest(){
        String tableName = "defaultTable";

        List<MOVariable> columns = Arrays.asList(
                new MOVariable("name1","1.2.3.4.1.1", MOAccessImpl.ACCESS_READ_ONLY,"desc1"),
                new MOVariable("name2","1.2.3.4.1.2", MOAccessImpl.ACCESS_READ_ONLY,"desc2"),
                new MOVariable("name3","1.2.3.4.1.3", MOAccessImpl.ACCESS_READ_ONLY,"desc3")
        );

        Mockito.when(oidPersistenceAdaptater.getColumnOfTable(tableName)).thenReturn(columns);

        List<MOVariable> actualColumns = oidPersistenceAdaptater.getColumnOfTable(tableName);

        Assertions.assertEquals(MOAccessImpl.ACCESS_READ_ONLY,actualColumns.get(2).getMoAccess());
        Assertions.assertEquals("desc1",actualColumns.get(0).getDescription());
        Assertions.assertEquals(3,actualColumns.size());
        Assertions.assertEquals("name2",actualColumns.get(1).getName());
        Assertions.assertEquals("1.2.3.4.1.2",actualColumns.get(1).getOid());
    }

    @Test
    public void getOIDByNameTest(){
        String name = "defaultName";
        MOManager moManager = new MOManager("defaultName","1.2.3.4");

        String oid = "1.2.3.4.1.2";

        Mockito.when(oidPersistenceAdaptater.getOIDByName(name,moManager)).thenReturn(oid);

        String actualOID = oidPersistenceAdaptater.getOIDByName(name,moManager);

        Assertions.assertEquals("1.2.3.4.1.2",actualOID);
    }

    @Test
    public void getOIDColumnByNameTest(){
        String columnName = "defaultName";
        MOManager moManager = new MOManager("defaultName","1.2.3.4");

        String oid = "1.2.3.4.1.2";

        Mockito.when(oidPersistenceAdaptater.getOIDColumnByName(columnName,moManager)).thenReturn(oid);

        String actualOID = oidPersistenceAdaptater.getOIDColumnByName(columnName,moManager);

        Assertions.assertEquals("1.2.3.4.1.2",actualOID);
    }

    @Test
    public void getMOManagerByNameTest(){
        String name = "defaultName";

        MOManager moManager = new MOManager("defaultName","1.2.3.4");

        Mockito.when(oidPersistenceAdaptater.getMOManagerByName(name)).thenReturn(moManager);

        MOManager actualMOManager = oidPersistenceAdaptater.getMOManagerByName(name);

        Assertions.assertEquals("defaultName",actualMOManager.getName());
        Assertions.assertEquals("1.2.3.4",actualMOManager.getOidRoot());
    }

}
