package com.example.kintai.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.kintai.model.Employee;
import com.example.kintai.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }
    @Override
    public List<Employee> searchByName(String keyword) {
        return employeeRepository.findByNameContainingIgnoreCase(keyword);
    }
    @Override
    public boolean existsByEmail(String email) {
        return employeeRepository.existsByEmail(email);
}
}
    
