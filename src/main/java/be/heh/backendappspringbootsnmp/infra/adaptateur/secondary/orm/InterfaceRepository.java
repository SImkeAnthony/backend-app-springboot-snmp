package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.InterfaceJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterfaceRepository extends JpaRepository<InterfaceJpa,Long> {
}
