package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.InterfaceJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterfaceRepository extends JpaRepository<InterfaceJpa,Long> {
    List<InterfaceJpa> findAllByIdMachine(Long idMachine);
}
