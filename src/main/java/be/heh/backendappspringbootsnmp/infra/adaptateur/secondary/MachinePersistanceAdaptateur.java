package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.MachineJpaEntity;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.MachineRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
public class MachinePersistanceAdaptateur implements MachinePortOut {

    private MachineRepository machineRepository;
    private MachineMapper machineMapper;
    @Getter
    @Setter
    private List<MachineEntity> machineEntities = new ArrayList<>();
    @Getter
    @Setter
    private List<MachineJpaEntity> machineJpaEntities = new ArrayList<>();
    @Override
    public List<MachineEntity> getAllMachineEntities() {
        setMachineJpaEntities(machineRepository.findAll());
        return machineMapper.mapMachineJpaToDomain(getMachineJpaEntities());
    }

    @Override
    public void registerMachineEntities(List<MachineEntity> machineEntities) {

    }
}
