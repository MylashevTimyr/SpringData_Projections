package org.example.springdataprojections.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springdataprojections.model.Department;
import org.example.springdataprojections.model.Employee;
import org.example.springdataprojections.repository.EmployeeProjection;
import org.example.springdataprojections.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Department sampleDepartment = new Department(1L, "IT Department");
    private final Employee sampleEmployee = new Employee(1L, "John", "Doe", "Developer", "1000.00", sampleDepartment);

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Arrays.asList(sampleEmployee));

        mockMvc.perform(get("/api/employees")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        Mockito.verify(employeeService, Mockito.times(1)).getAllEmployees();
    }

    @Test
    void getEmployeeById_WhenExists_ShouldReturnEmployee() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(sampleEmployee));

        mockMvc.perform(get("/api/employees/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));

        Mockito.verify(employeeService, Mockito.times(1)).getEmployeeById(1L);
    }

    @Test
    void createEmployee_ShouldReturnCreatedEmployee() throws Exception {
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(sampleEmployee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));

        Mockito.verify(employeeService, Mockito.times(1)).createEmployee(any(Employee.class));
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any(Employee.class))).thenReturn(sampleEmployee);

        mockMvc.perform(put("/api/employees/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));

        Mockito.verify(employeeService, Mockito.times(1)).updateEmployee(eq(1L), any(Employee.class));
    }

    @Test
    void deleteEmployee_ShouldInvokeDeleteMethod() throws Exception {
        Mockito.doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/{id}", 1L))
                .andExpect(status().isOk());

        Mockito.verify(employeeService, Mockito.times(1)).deleteEmployee(1L);
    }

    @Test
    void getEmployeesByLastName_ShouldReturnListOfEmployeeProjections() throws Exception {
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

        when(employeeService.getEmployeesByLastName("Doe")).thenReturn(Arrays.asList(projection));

        mockMvc.perform(get("/api/employees/by-lastname")
                        .param("lastName", "Doe")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));

        Mockito.verify(employeeService, Mockito.times(1)).getEmployeesByLastName("Doe");
    }
}
