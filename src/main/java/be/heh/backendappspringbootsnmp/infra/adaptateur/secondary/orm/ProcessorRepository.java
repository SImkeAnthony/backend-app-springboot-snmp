package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.ProcessorJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessorRepository extends JpaRepository<ProcessorJpa,Long> {
}
