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

    @Override
    public List<MachineEntity> getAllInfoOfMachines(List<String> ipAddress) throws NMapExecutionException, NMapInitializationException {
        List<MachineEntity> machineEntities = null;
        Nmap4j newInfoScan = new Nmap4j(pathNmapExecutale);
        String hosts="";
        for (String ip : ipAddress){
            if(ipAddress.indexOf(ip)==ipAddress.size()-1){
                hosts+=ip;
            }else{
                hosts+=ip+"-";
            }

        }
        System.out.println("host scanned : "+hosts);
        newInfoScan.includeHosts(hosts);
        newInfoScan.addFlags("-O -sS");
        try{
            newInfoScan.execute();
            if(!newInfoScan.hasError()){
                NMapRun run = newInfoScan.getResult();
                List<Host> scannedHosts = run.getHosts();
                for (Host host : scannedHosts){
                    //display host info
                    System.out.println(host.toString());

                    //Default variables
                    List<String> ipAddr = new ArrayList<>();
                    List<String> macAddr =  new ArrayList<>();
                    String hostname="unknown";
                    String os = "unknown";
                    Boolean snmp = false;

                    //check if data exist for each host

                    //Address
                    List<Address> addressHost = host.getAddresses();
                    if(!addressHost.isEmpty()){
                        for (Address addr : addressHost){
                            if(addr.getAddrtype()=="ipv4"){
                                ipAddr.add(addr.toString());
                            }
                            if(addr.getAddrtype()=="mac"){
                                macAddr.add(addr.toString());
                            }
                        }
                    }
                    //hostname
                    if(host.getHostnames()!=null){
                        hostname=host.getHostnames().toString();
                    }
                    //os
                    if(host.getOs()!=null){
                        os=host.getOs().toString();
                    }
                    //snmp
                    List<PortUsed>usedPorts = host.getOs().getPortUseds();
                    ArrayList<Port> ports = host.getPorts().getPorts();
                    Port snmpPort1 = new Port();
                    Port snmpPort2 = new Port();
                    snmpPort1.setPortId(161);
                    snmpPort1.setProtocol("snmp");
                    snmpPort2.setPortId(162);
                    snmpPort2.setProtocol("snmp");
                    if(usedPorts.contains(snmpPort1) || ports.contains(snmpPort1) || usedPorts.contains(snmpPort2) || ports.contains(snmpPort2)){
                        snmp = true;
                    }

                    MachineEntity machineEntity = new MachineEntity(macAddr,ipAddr,hostname,os,snmp);
                    machineEntities.add(machineEntity);

                }//end of for
            }else {
                logger.error(newInfoScan.getExecutionResults().getErrors());
            }
        }catch (NMapExecutionException | NMapInitializationException e){
            throw  new RuntimeException(e);
        }
        return machineEntities ;
    }
}
