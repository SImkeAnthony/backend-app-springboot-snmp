package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.ServiceJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<ServiceJpa,Long> {
}
