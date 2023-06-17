package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.MachineJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<MachineJpaEntity,Long> {

}
