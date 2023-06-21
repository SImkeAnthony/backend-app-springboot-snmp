package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MOManager;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.MibBrowser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
}
