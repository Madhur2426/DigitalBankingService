package com.hcltech.digitalbankingservice.controller;

import com.hcltech.digitalbankingservice.dto.CustomerDto;
import com.hcltech.digitalbankingservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    void testGetAllCustomers() throws Exception {
        mockMvc.perform(get("/api/customer/admin/all"))
                .andExpect(status().isOk());

        verify(customerService, times(1)).getAll();
    }

    @Test
    void testDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/customer/1"))
                .andExpect(status().isOk());

        verify(customerService, times(1)).delete(1L);
    }

    @Test
    void testUpdateCustomer() throws Exception {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName("John");

        when(customerService.update(anyLong(), any(CustomerDto.class))).thenReturn(Optional.of(customerDto));

        mockMvc.perform(put("/api/customer/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(customerService, times(1)).update(anyLong(), any(CustomerDto.class));
    }

    @Test
    void testGetCustomerById() throws Exception {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName("John");

        when(customerService.findCustomerById(anyLong())).thenReturn(Optional.of(customerDto));

        mockMvc.perform(get("/api/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(customerService, times(1)).findCustomerById(anyLong());
    }
}
