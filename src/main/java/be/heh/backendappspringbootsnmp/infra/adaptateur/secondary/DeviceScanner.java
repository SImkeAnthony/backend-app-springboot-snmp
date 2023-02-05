package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;
import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.nmap4j.Nmap4j;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.nmap4j.data.NMapRun;
import org.nmap4j.data.host.Address;
import org.nmap4j.data.nmaprun.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class DeviceScanner implements DeviceScannerPortOut {
    private final String pathNmapExecutale="C:\\Program Files (x86)\\Nmap";
    @Getter
    private Nmap4j nmap4j = new Nmap4j(pathNmapExecutale);
    @Getter
    private Logger logger = LoggerFactory.getLogger(DeviceScanner.class);

    @Override
    public List<String> getAllIpOnNetwork(String ipRange) {
        List<String>ipAddress=new ArrayList<>();
        nmap4j.includeHosts(ipRange);
        nmap4j.addFlags("-sP");
        try{
            nmap4j.execute();
            if(!nmap4j.hasError()){
                NMapRun run = nmap4j.getResult();
                ArrayList<Host> hosts = run.getHosts();
                for(Host host : hosts){
                    List<Address> hostAddresses = host.getAddresses();
                    for(Address addrr : hostAddresses){
                        if(Objects.equals(addrr.getAddrtype(), "ipv4")){
                            ipAddress.add(addrr.getAddr());
                        }
                    }
                }
            }else {
                logger.error(nmap4j.getExecutionResults().getErrors());
            }
        }catch (NMapExecutionException | NMapInitializationException e){
            throw  new RuntimeException(e);
        }
        return ipAddress ;
    }
}
