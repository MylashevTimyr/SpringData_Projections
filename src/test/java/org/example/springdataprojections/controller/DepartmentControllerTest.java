package org.example.springdataprojections.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springdataprojections.model.Department;
import org.example.springdataprojections.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Department sampleDepartment = new Department(1L, "IT Department");

    @Test
    void getAllDepartments_ShouldReturnListOfDepartments() throws Exception {
        when(departmentService.getAllDepartments()).thenReturn(Arrays.asList(sampleDepartment));

        mockMvc.perform(get("/api/departments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("IT Department"));

        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    void getDepartmentById_WhenExists_ShouldReturnDepartment() throws Exception {
        when(departmentService.getDepartmentById(1L)).thenReturn(Optional.of(sampleDepartment));

        mockMvc.perform(get("/api/departments/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("IT Department"));

        verify(departmentService, times(1)).getDepartmentById(1L);
    }

    @Test
    void createDepartment_ShouldReturnCreatedDepartment() throws Exception {
        when(departmentService.createDepartment(any(Department.class))).thenReturn(sampleDepartment);

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDepartment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("IT Department"));

        verify(departmentService, times(1)).createDepartment(any(Department.class));
    }

    @Test
    void updateDepartment_ShouldReturnUpdatedDepartment() throws Exception {
        when(departmentService.updateDepartment(eq(1L), any(Department.class))).thenReturn(sampleDepartment);

        mockMvc.perform(put("/api/departments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDepartment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("IT Department"));

        verify(departmentService, times(1)).updateDepartment(eq(1L), any(Department.class));
    }

    @Test
    void deleteDepartment_ShouldInvokeDeleteMethod() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/departments/{id}", 1L))
                .andExpect(status().isOk());

        verify(departmentService, times(1)).deleteDepartment(1L);
    }
}
