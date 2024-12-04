package org.example.springdataprojections.service;

import org.example.springdataprojections.model.Department;
import org.example.springdataprojections.model.Employee;
import org.example.springdataprojections.repository.DepartmentRepository;
import org.example.springdataprojections.repository.EmployeeProjection;
import org.example.springdataprojections.repository.EmployeeRepository;
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
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee sampleEmployee;
    private Department sampleDepartment;

    @BeforeEach
    void setUp() {
        sampleDepartment = new Department(1L, "IT Department");
        sampleEmployee = new Employee(1L, "John", "Doe", "Developer", "1000.00", sampleDepartment);
    }

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(sampleEmployee));

        List<Employee> employees = employeeService.getAllEmployees();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void getEmployeeById_WhenExists_ShouldReturnEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));

        Optional<Employee> employee = employeeService.getEmployeeById(1L);

        assertTrue(employee.isPresent());
        assertEquals(sampleEmployee, employee.get());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getEmployeeById_WhenDoesNotExist_ShouldReturnEmptyOptional() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Employee> employee = employeeService.getEmployeeById(1L);

        assertFalse(employee.isPresent());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void createEmployee_ShouldReturnCreatedEmployee() {
        when(employeeRepository.save(sampleEmployee)).thenReturn(sampleEmployee);

        Employee createdEmployee = employeeService.createEmployee(sampleEmployee);

        assertNotNull(createdEmployee);
        assertEquals(sampleEmployee, createdEmployee);
        verify(employeeRepository, times(1)).save(sampleEmployee);
    }

    @Test
    void updateEmployee_WhenExists_ShouldReturnUpdatedEmployee() {
        Employee updatedDetails = new Employee();
        updatedDetails.setFirstName("Jane");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedDetails);

        Employee updatedEmployee = employeeService.updateEmployee(1L, updatedDetails);

        assertNotNull(updatedEmployee);
        assertEquals("Jane", updatedEmployee.getFirstName());
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void updateEmployee_WhenDoesNotExist_ShouldThrowException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        Employee updatedDetails = new Employee();
        updatedDetails.setFirstName("Jane");

        assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(1L, updatedDetails));
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_ShouldInvokeDeleteById() {
        doNothing().when(employeeRepository).deleteById(1L);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void getEmployeesByLastName_ShouldReturnListOfEmployeeProjections() {
        EmployeeProjection projection = new EmployeeProjection() {
            @Override
            public String getFirstName() {
                return "John";
            }

            @Override
            public String getLastName() {
                return "Doe";
            }

            @Override
            public String getPosition() {
                return "Developer";
            }

            @Override
            public String getDepartmentName() {
                return "IT Department";
            }
        };

        when(employeeRepository.findByLastName("Doe")).thenReturn(Arrays.asList(projection));

        List<EmployeeProjection> projections = employeeService.getEmployeesByLastName("Doe");

        assertNotNull(projections);
        assertEquals(1, projections.size());
        verify(employeeRepository, times(1)).findByLastName("Doe");
    }
}
