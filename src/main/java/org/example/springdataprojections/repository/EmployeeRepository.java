package org.example.springdataprojections.repository;

import org.example.springdataprojections.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<EmployeeProjection> findByLastName(String lastName);
}
