package org.example.springdataprojections.repository;

import org.example.springdataprojections.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}
