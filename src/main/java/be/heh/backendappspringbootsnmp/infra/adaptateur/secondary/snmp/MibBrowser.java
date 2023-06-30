package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp;

import be.heh.backendappspringbootsnmp.domain.entities.MOTable;
import be.heh.backendappspringbootsnmp.domain.entities.MOVariable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.snmp4j.agent.MOAccess;
import org.snmp4j.agent.mo.MOTableSubIndex;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.SMIConstants;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MibBrowser {

    @Setter
    @Getter
    private JSONObject jsonObject = new JSONObject();
    @Getter
    private final Map<String, MOAccess> access;
    @Getter
    @Setter
    private String mibFileName;

    public void reinitialize(String newMibFile){
        try{
            setMibFileName(newMibFile);
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(getMibFileName());
            String jsonContent = new String(inputStream.readAllBytes(),StandardCharsets.ISO_8859_1);
            setJsonObject(new JSONObject(jsonContent));
        }catch (Exception e){
            System.err.println("Error mapping io : "+e.getMessage());
        }
    }

    public List<Pair<String,String>> getNodeIdentity(){
        List<Pair<String,String>> nodeIdentities = new ArrayList<>();
        Iterator<String> keys = getJsonObject().keys();
        while (keys.hasNext()){
            JSONObject mo = getJsonObject().getJSONObject(keys.next());
            if(mo.has("class")){
                if(mo.get("class").equals("objectidentity")){
                    nodeIdentities.add(new Pair<>(mo.get("name").toString(),mo.get("oid").toString()));
                }
            }
        }
        return nodeIdentities;
    }

    public List<MOVariable> getAllMOVariable(){
        List<MOVariable> moVariables = new ArrayList<>();
        Iterator<String> keys = getJsonObject().keys();
        while (keys.hasNext()){
            JSONObject mo = getJsonObject().getJSONObject(keys.next());
            if(mo.has("class")){
                if(mo.get("class").equals("objecttype") && (mo.get("nodetype").equals("scalar")) || (mo.get("nodetye").equals("column"))){
                    //manage here
                    MOVariable moVariable = new MOVariable(mo.get("name").toString(),mo.get("oid").toString(), getAccess().get(mo.get("maxaccess")),mo.get("description").toString());
                    moVariables.add(moVariable);
                }
            }
        }
        return moVariables;
    }
    public List<MOVariable> getMOVariablesBySubTree(OID oidIdentity){
        List<MOVariable> moVariables = new ArrayList<>();
        Iterator<String> keys = getJsonObject().keys();
        while (keys.hasNext()){
            JSONObject mo = getJsonObject().getJSONObject(keys.next());
            if(mo.has("class")){
                if(mo.get("class").equals("objecttype") && mo.get("nodetype").equals("scalar") && mo.get("oid").toString().contains(oidIdentity.toString())){
                    //manage here
                    MOVariable moVariable = new MOVariable(mo.get("name").toString(),mo.get("oid").toString(), getAccess().get(mo.get("maxaccess")),mo.get("description").toString());
                    moVariables.add(moVariable);
                }
            }
        }
        return moVariables;
    }
    public List<MOTable> getMOTableBySubTree(OID oidIdentity){
        List<MOTable> moTables = new ArrayList<>();
        MOTableSubIndex[] moTableSubIndexes = new MOTableSubIndex[]{
            new MOTableSubIndex(SMIConstants.SYNTAX_INTEGER)
        };
        Iterator<String> keys = getJsonObject().keys();
        while (keys.hasNext()){
            JSONObject mo = getJsonObject().getJSONObject(keys.next());
            if(mo.has("class")){
                if(mo.get("class").equals("objecttype") && mo.get("nodetype").equals("table") && mo.get("oid").toString().contains(oidIdentity.toString())){
                    MOTable moTable = new MOTable(mo.get("name").toString(),mo.get("oid").toString(),mo.get("description").toString());
                    moTable.setColumns(getColumnBySubTree(getOIDEntryByOIDTable(new OID(mo.get("oid").toString()))));
                    moTable.setOidNumber(getOIDNumberOfTable(new OID(mo.get("oid").toString())));
                    moTable.setOidIndex(getOIDIndexOfTable(getOIDEntryByOIDTable(new OID(mo.get("oid").toString()))));
                    moTables.add(moTable);
                }
            }
        }
        return moTables;
    }

    private String getOIDNumberOfTable(OID oidTable){
        Iterator<String> keys = getJsonObject().keys();
        String[] oidList = oidTable.toString().split("\\.");
        int last = Integer.parseInt(oidList[oidList.length-1])-1;
        oidList[oidList.length-1] = String.valueOf(last);
        String oidNumber = String.join(".",oidList);
        while (keys.hasNext()){
            JSONObject mo = getJsonObject().getJSONObject(keys.next());
            if(mo.has("class")){
                if(mo.get("class").equals("objecttype") && mo.get("nodetype").equals("scalar") && mo.get("oid").toString().equals(oidNumber)){
                    return mo.get("oid").toString();
                }
            }
        }
        return "0";
    }

    private String getOIDIndexOfTable(OID oidEntryTable){
        Iterator<String> keys = getJsonObject().keys();
        while (keys.hasNext()){
            JSONObject mo = getJsonObject().getJSONObject(keys.next());
            if(mo.has("class")){
                if(mo.get("class").equals("objecttype") && mo.get("nodetype").equals("column") && mo.get("oid").toString().contains(oidEntryTable.toString()+".1")){
                    return mo.get("oid").toString();
                }
            }
        }
        return "0";
    }
    private OID getOIDEntryByOIDTable(OID oidTable){
        Iterator<String> keys = getJsonObject().keys();
        int colId = 1;
        while (keys.hasNext()){
            JSONObject mo = getJsonObject().getJSONObject(keys.next());
            if(mo.has("class")){
                if(mo.get("class").equals("objecttype") && mo.get("nodetype").equals("row") && mo.get("oid").toString().contains(oidTable.toString())){
                    return new OID(mo.get("oid").toString());
                }
            }
        }
        return new OID("");
    }

    private List<MOVariable> getColumnBySubTree(OID oidIdentity){
        List<MOVariable> moColumns = new ArrayList<>();
        Iterator<String> keys = getJsonObject().keys();
        while (keys.hasNext()){
            JSONObject mo = getJsonObject().getJSONObject(keys.next());
            if(mo.has("class")){
                if(mo.get("class").equals("objecttype") && mo.get("nodetype").equals("column") && mo.get("oid").toString().contains(oidIdentity.toString())){
                if(mo.get("class").equals("objecttype") && mo.get("nodetype").equals("column") && mo.get("oid").toString().contains(oidIdentity.toString()))
                    moColumns.add(new MOVariable(
                            mo.get("name").toString(),
                            mo.get("oid").toString(),
                            getAccess().get(mo.get("maxaccess")),
                            mo.get("description").toString()
                    ));
                }
            }
        }
        return moColumns;
    }
}
