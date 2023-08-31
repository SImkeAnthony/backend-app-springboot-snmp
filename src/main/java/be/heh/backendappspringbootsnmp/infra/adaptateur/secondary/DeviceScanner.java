package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;
import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import lombok.Getter;
import org.nmap4j.Nmap4j;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.nmap4j.data.NMapRun;
import org.nmap4j.data.host.Address;
import org.nmap4j.data.nmaprun.Host;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
@Component
public class DeviceScanner implements DeviceScannerPortOut {
    @Value("${nmap.path}")
    private String path;
    @Value("${nmap.hosts}")
    private String hostRange;
    @Getter
    private Nmap4j nmap4j;

    @Override
    public List<String> getAllIpOnNetwork(String ipRange) {
        nmap4j = new Nmap4j(path);
        List<String>ipAddress=new ArrayList<>();
        if (ipRange.isEmpty()) {
            nmap4j.includeHosts(hostRange);
        } else {
            nmap4j.includeHosts(ipRange);
        }
        nmap4j.addFlags("-sP"); //not right flags correct this later
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
                System.err.println("Error execute nmap scan : "+nmap4j.getExecutionResults().getErrors());
            }
        }catch (NMapExecutionException | NMapInitializationException | NullPointerException e){
            throw  new RuntimeException(e);
        }
        return ipAddress ;
    }
    @Override
    public MachineEntity getAllInfoOfMachineEntity(MachineEntity machineEntity,List<String> ipAddress) {
        Nmap4j scanOs = new Nmap4j(path);
        Nmap4j scanHostname = new Nmap4j(path);
        String hosts=String.join("-",ipAddress);
        scanOs.includeHosts(hosts);
        scanHostname.includeHosts(hosts);
        //Get variables
        Boolean snmp = machineEntity.getSnmp();
        machineEntity.setOs(getOs(scanOs));
        machineEntity.setHostname(getHostname(scanHostname));
        return machineEntity;
    }

    private String getOs(Nmap4j scan){
        scan.addFlags("-sP"); //not right flags correct this later
        try{
            scan.execute();
            if(!scan.hasError()){
                NMapRun run = scan.getResult();
                if(run==null){
                    System.err.println("Error execute nmap scan : result is null");
                    return "unknown";
                }else{
                    ArrayList<Host> hosts = run.getHosts();
                    String os = "unknown";
                    if(!hosts.isEmpty()){
                        if(!hosts.get(0).getOs().toString().isEmpty()){
                            System.out.println("os : "+hosts.get(0).getOs().toString());
                            os = hosts.get(0).getOs().toString();
                        }

                    }
                    return os;
                }
            }else {
                System.err.println("Error execute nmap scan : "+nmap4j.getExecutionResults().getErrors());
                return "unknown";
            }
        }catch (NMapExecutionException | NMapInitializationException | NullPointerException e){
            System.err.println("Error execute nmap scan : "+e.getMessage());
            return "unknown";
        }
    }
    private String getHostname(Nmap4j scan){
        scan.addFlags("-sP");
        try{
            scan.execute();
            if(!scan.hasError()){
                NMapRun run = scan.getResult();
                if(run==null){
                    System.err.println("Error execute nmap scan : result is null");
                    return "unknown";
                }else {
                    ArrayList<Host> hosts = run.getHosts();
                    String hostname = "unknown";
                    if(!hosts.isEmpty()){
                        if(!hosts.get(0).getHostnames().getHostname().getName().isEmpty()){
                            hostname = hosts.get(0).getHostnames().getHostname().getName();
                        }
                    }
                    return hostname;
                }
            }else {
                System.err.println("Error execute nmap scan : "+nmap4j.getExecutionResults().getErrors());
                return "unknown";
            }
        }catch (NMapExecutionException | NMapInitializationException | NullPointerException e){
            System.err.println("Error execute nmap scan : "+e.getMessage());
            return "unknown";
        }
    }
}
