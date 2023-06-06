package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;
import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.javatuples.Pair;
import org.nmap4j.Nmap4j;
import org.nmap4j.core.nmap.ExecutionResults;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.nmap4j.data.NMapRun;
import org.nmap4j.data.host.Address;
import org.nmap4j.data.host.os.PortUsed;
import org.nmap4j.data.host.ports.Port;
import org.nmap4j.data.nmaprun.Host;
import org.nmap4j.data.nmaprun.host.ports.port.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RequiredArgsConstructor
public class DeviceScanner implements DeviceScannerPortOut {
    private final String pathNmapExecutale="C:\\Program Files (x86)\\Nmap";
    @Getter
    private Nmap4j nmap4j = new Nmap4j(pathNmapExecutale);

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
                System.err.println("Error execute nmap scan : "+nmap4j.getExecutionResults().getErrors());
            }
        }catch (NMapExecutionException | NMapInitializationException e){
            throw  new RuntimeException(e);
        }
        return ipAddress ;
    }
    @Override
    public MachineEntity getAllInfoOfMachineEntity(MachineEntity machineEntity) {
        Nmap4j newInfoScan = new Nmap4j(pathNmapExecutale);
        String hosts=String.join("-",machineEntity.getIpAddr());
        newInfoScan.includeHosts(hosts);

        //Get variables
        List<String> ipAddr = machineEntity.getIpAddr();
        Boolean snmp = false;
        String hostname = getHostname(newInfoScan);
        Pair<List<String>,String> pair = getMAcOs(newInfoScan);
        List<String> macAddr = pair.getValue0();
        String os = pair.getValue1();
        return new MachineEntity(ipAddr,macAddr,hostname,os,snmp);
    }

    private Pair<List<String>,String> getMAcOs(Nmap4j scan){
        scan.addFlags("-sS -PR -O");
        try{
            scan.execute();
            if(!scan.hasError()){
                NMapRun run = scan.getResult();
                ArrayList<Host> hosts = run.getHosts();
                for(Host host : hosts){
                    System.out.println("MacOs : "+host);
                }
                return new Pair<>(new ArrayList<>(),"");
            }else {
                System.err.println("Error execute nmap scan : "+nmap4j.getExecutionResults().getErrors());
                List<String> macAddr = new ArrayList<>();
                macAddr.add("unknown");
                return new Pair<>(macAddr,"unknown");
            }
        }catch (NMapExecutionException | NMapInitializationException e){
            System.err.println("Error execute nmap scan : "+e.getMessage());
            return new Pair<>(new ArrayList<>(),"unknown");
        }
    }
    private String getHostname(Nmap4j scan){
        scan.addFlags("-Sl");
        try{
            scan.execute();
            if(!scan.hasError()){
                NMapRun run = scan.getResult();
                ArrayList<Host> hosts = run.getHosts();
                for(Host host : hosts){
                    System.out.println("Hostname : "+host);
                }
                return "some os";
            }else {
                System.err.println("Error execute nmap scan : "+nmap4j.getExecutionResults().getErrors());
                return "unknown";
            }
        }catch (NMapExecutionException | NMapInitializationException e){
            System.err.println("Error execute nmap scan : "+e.getMessage());
            return "unknown";
        }
    }
}
