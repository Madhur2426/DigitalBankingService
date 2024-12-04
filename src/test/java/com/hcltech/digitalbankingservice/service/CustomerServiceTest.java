package com.hcltech.digitalbankingservice.service;

import com.hcltech.digitalbankingservice.dao.CustomerDaoService;
import com.hcltech.digitalbankingservice.dto.CustomerDto;
import com.hcltech.digitalbankingservice.exception.CustomerNotFoundException;
import com.hcltech.digitalbankingservice.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

    @Mock
    private CustomerDaoService customerDaoService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("John");

        CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName("John");
        customerDto.setAadharNumber("12345687");

        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(customerDaoService.save(any(Customer.class))).thenReturn(customer);

        Optional<CustomerDto> result = customerService.save(customerDto);
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void testFindCustomerById() {
        Customer customer = new Customer();
        customer.setId(1L);

        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);

        // Mocking ModelMapper behavior
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(customerDaoService.findCustomerById(1L)).thenReturn(Optional.of(customer));

        Optional<CustomerDto> result = customerService.findCustomerById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testUpdateCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");

        CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName("John");

        // Mocking ModelMapper behavior
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(customerDaoService.findCustomerById(anyLong())).thenReturn(Optional.of(customer));
        when(customerDaoService.update(any(Customer.class))).thenReturn(customer);

        Optional<CustomerDto> result = customerService.update(1L, customerDto);
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void testDeleteCustomer() {
        when(customerDaoService.findCustomerById(anyLong())).thenReturn(Optional.of(new Customer()));
        customerService.delete(1L);

        verify(customerDaoService, times(1)).delete(anyLong());
    }
    @Test
    void testFindCustomerById_CustomerNotFound() {
        when(customerDaoService.findCustomerById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.findCustomerById(1L);
        });

        assertEquals("Customer with ID 1 not found.", exception.getMessage());
    }


}
