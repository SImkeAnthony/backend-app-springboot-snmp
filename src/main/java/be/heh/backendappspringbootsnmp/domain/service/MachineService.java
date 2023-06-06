package be.heh.backendappspringbootsnmp.domain.service;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.in.MachinePortIn;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import be.heh.backendappspringbootsnmp.domain.port.out.SnmpManagerPortOut;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MachineService implements MachinePortIn {
    @Getter
    private final MachinePortOut machinePortOut;

    @Getter
    private final DeviceScannerPortOut deviceScannerPortOut;

    @Getter
    private final SnmpManagerPortOut snmpManagerPortOut;
    @Getter
    @Setter
    private List<MachineEntity>registerMachineEntities=new ArrayList<>();
    @Getter
    @Setter
    private List<MachineEntity>discoverMachineEntities=new ArrayList<>();
    @Getter
    @Setter
    private List<String> ipAddress =new ArrayList<>();
    @Getter
    @Setter
    private boolean managed = false;

    @Override
    public Iterable<MachineEntity> getAllMachineEntities()  {
        try{
            if(getIpAddress().isEmpty()){
                //scan network
                setIpAddress(getDeviceScannerPortOut().getAllIpOnNetwork("192.168.0.1-254"));
            }
            //get infos
            completeListMachineEntities();
            manageList();
            setRegisterMachineEntities(machinePortOut.getAllMachineEntities());
        } catch (IOException | NMapInitializationException | NMapExecutionException e) {
            System.out.println("Error scanning ip : "+e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Error interrupt : "+e.getMessage());
        }
        return ()->getRegisterMachineEntities().iterator();
    }

    private void completeListMachineEntities() throws IOException, InterruptedException {
        if(getRegisterMachineEntities().isEmpty()){
            setRegisterMachineEntities(getMachinePortOut().getAllMachineEntities());
        }
        if(getDiscoverMachineEntities().isEmpty()){
            setDiscoverMachineEntities(getSnmpManagerPortOut().getInfoMachineEntities(getIpAddress()));
        }
    }
    private void manageList(){
        if(!isManaged()){
            if(getRegisterMachineEntities().isEmpty()){
                getMachinePortOut().registerMachineEntities(getDiscoverMachineEntities());
            }else{
                for (MachineEntity discoverEntity : getDiscoverMachineEntities()){
                    if(!Objects.equals(discoverEntity.getHostname(),"unknown") && !alreadyRegisterByHostName(discoverEntity)){
                        getMachinePortOut().registerMachineEntity(discoverEntity);
                    }else if (!discoverEntity.getMacAddr().contains("unknown") && !alreadyRegisterByMacAddr(discoverEntity)){
                        getMachinePortOut().registerMachineEntity(discoverEntity);
                    }
                }
            }
        }
        setManaged(true);
    }
    private boolean alreadyRegisterByHostName(MachineEntity machineEntity){
        for(MachineEntity registerEntity : getRegisterMachineEntities()){
            if(Objects.equals(registerEntity.getHostname(),machineEntity.getHostname())){
                return true;
            }
        }
        return false;
    }
    private boolean alreadyRegisterByMacAddr(MachineEntity machineEntity){
        for(MachineEntity registerEntity : getRegisterMachineEntities()){
            if(!Collections.disjoint(registerEntity.getMacAddr(),machineEntity.getMacAddr())){
                if(Objects.equals(registerEntity.toString(),machineEntity.toString())){
                    return true;
                }else{
                    //update list address
                    Set<String> updateMacAddresses = new HashSet<>(registerEntity.getMacAddr());
                    updateMacAddresses.addAll(machineEntity.getMacAddr());
                    Set<String> updateIpAddresses = new HashSet<>(registerEntity.getIpAddr());
                    updateIpAddresses.addAll(machineEntity.getIpAddr());

                    //update entity
                    getMachinePortOut().updateMachineEntity(new MachineEntity(
                            updateMacAddresses.stream().toList(),
                            updateIpAddresses.stream().toList(),
                            machineEntity.getHostname(),
                            machineEntity.getOs(),
                            machineEntity.getSnmp()
                    ));
                }
            }
        }
        return false;
    }

}
