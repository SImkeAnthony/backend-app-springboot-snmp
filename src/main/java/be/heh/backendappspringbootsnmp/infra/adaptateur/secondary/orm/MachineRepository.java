package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<MachineJpaEntity,Long> {

}
