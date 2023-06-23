package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MOManager;
import be.heh.backendappspringbootsnmp.domain.entities.MOTable;
import be.heh.backendappspringbootsnmp.domain.entities.MOVariable;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.MibBrowser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.javatuples.Pair;
import org.snmp4j.smi.OID;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class OIDPersistanceAdaptater {
    @Getter
    private final MibBrowser mibBrowser;
    @Getter
    @Setter
    private String mibFile;
    @Getter
    private List<MOManager> moManagers = new ArrayList<>();

    public void initMOManagers(){
        getMibBrowser().getNodeIdentity().forEach(nodeIdentity->{
            MOManager moManager = new MOManager(nodeIdentity.getValue0(),nodeIdentity.getValue1());
            moManager.setMoVariables(getMibBrowser().getMOVariablesBySubTree(new OID(moManager.getOidRoot())));
            moManager.setMoTables(getMibBrowser().getMOTableBySubTree(new OID(moManager.getOidRoot())));
        });
    }

    public MOManager getMOManagerByOID(OID oid){
        if(getMoManagers().isEmpty()){
            initMOManagers();
        }
        for (MOManager moManager : getMoManagers()){
            if(oid.toString().contains(moManager.getOidRoot())){
                return moManager;
            }
        }
        return null;
    }
    public String getOIDHostname(){
        for (MOManager moManager : getMoManagers()){
            if(moManager.getName().equals("system")){
                if(!moManager.getMoVariables().isEmpty()){
                    for(MOVariable moVariable : moManager.getMoVariables()){
                        if(moVariable.getName().equals("sysHostname")){
                            return moVariable.getOid();
                        }
                    }
                }
            }
        }
        return "0";
    }
    public Pair<String,String> getOIDNumberIndexOFTable(String tableName){
        for(MOManager moManager: getMoManagers()){
            for(MOTable moTable : moManager.getMoTables()){
                if(moTable.getName().equals(tableName)){
                    return new Pair<>(moTable.getOidNumber(),moTable.getOidIndex());
                }
            }
        }
        return new Pair<>("0","0");
    }

    public List<MOVariable> getColumnOfTable(String tableName){
        for(MOManager moManager : getMoManagers()){
            for(MOTable moTable : moManager.getMoTables()){
                if(moTable.getName().equals(tableName)){
                    return moTable.getColumns();
                }
            }
        }
        return new ArrayList<>();
    }
    public String getOIDByName(String name,MOManager moManager){
        for(MOVariable moVariable:moManager.getMoVariables()){
            if(moVariable.getName().equals(name)){
                return moVariable.getOid();
            }
        }
        return "0";
    }
    public String getOIDColumnByName(String name,MOManager moManager){
        for(MOTable moTable : moManager.getMoTables()){
            for(MOVariable moColumn : moTable.getColumns()){
                if(moColumn.getName().equals(name)){
                    return moTable.getOid();
                }
            }
        }
        return "0";
    }
    public MOManager getMOManagerByName(String name){
        for(MOManager moManager : getMoManagers()){
            if(moManager.getName().equals(name)){
                return moManager;
            }
        }
        return null;
    }
}
