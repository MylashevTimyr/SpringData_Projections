package org.example.springdataprojections.service;

import org.example.springdataprojections.model.Department;
import org.example.springdataprojections.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department sampleDepartment;

    @BeforeEach
    void setUp() {
        sampleDepartment = new Department();
        sampleDepartment.setId(1L);
        sampleDepartment.setName("IT Department");
    }

    @Test
    void getAllDepartments_ShouldReturnListOfDepartments() {
        when(departmentRepository.findAll()).thenReturn(Arrays.asList(sampleDepartment));

        List<Department> departments = departmentService.getAllDepartments();

        assertNotNull(departments);
        assertEquals(1, departments.size());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void getDepartmentById_WhenDepartmentExists_ShouldReturnDepartment() {
        when(departmentRepository.findById(1)).thenReturn(Optional.of(sampleDepartment));

        Optional<Department> department = departmentService.getDepartmentById(1L);

        assertTrue(department.isPresent());
        assertEquals(sampleDepartment, department.get());
        verify(departmentRepository, times(1)).findById(1);
    }

    @Test
    void getDepartmentById_WhenDepartmentDoesNotExist_ShouldReturnEmptyOptional() {
        when(departmentRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Department> department = departmentService.getDepartmentById(1L);

        assertFalse(department.isPresent());
        verify(departmentRepository, times(1)).findById(1);
    }

    @Test
    void createDepartment_ShouldReturnCreatedDepartment() {
        when(departmentRepository.save(sampleDepartment)).thenReturn(sampleDepartment);

        Department createdDepartment = departmentService.createDepartment(sampleDepartment);

        assertNotNull(createdDepartment);
        assertEquals(sampleDepartment, createdDepartment);
        verify(departmentRepository, times(1)).save(sampleDepartment);
    }

    @Test
    void updateDepartment_WhenDepartmentExists_ShouldReturnUpdatedDepartment() {
        Department updatedDetails = new Department();
        updatedDetails.setName("HR Department");

        when(departmentRepository.findById(1)).thenReturn(Optional.of(sampleDepartment));
        when(departmentRepository.save(any(Department.class))).thenReturn(updatedDetails);

        Department updatedDepartment = departmentService.updateDepartment(1L, updatedDetails);

        assertNotNull(updatedDepartment);
        assertEquals("HR Department", updatedDepartment.getName());
        verify(departmentRepository, times(1)).findById(1);
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void updateDepartment_WhenDepartmentDoesNotExist_ShouldThrowException() {
        when(departmentRepository.findById(1)).thenReturn(Optional.empty());

        Department updatedDetails = new Department();
        updatedDetails.setName("HR Department");

        assertThrows(RuntimeException.class, () -> departmentService.updateDepartment(1L, updatedDetails));
        verify(departmentRepository, times(1)).findById(1);
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void deleteDepartment_ShouldInvokeDeleteById() {
        doNothing().when(departmentRepository).deleteById(1);

        departmentService.deleteDepartment(1L);

        verify(departmentRepository, times(1)).deleteById(1);
    }
}
