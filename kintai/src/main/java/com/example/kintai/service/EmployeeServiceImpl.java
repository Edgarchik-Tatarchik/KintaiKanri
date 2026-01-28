package com.example.kintai.service;

import com.example.kintai.model.Employee;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final List<Employee> employees = List.of(
        new Employee(1L, "Test User"),
        new Employee(2L, "Demo User")
    );

    @Override
    public List<Employee> findAll() {
        return employees;
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return employees.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    @Override
    public Employee save(Employee employee) {
        return employee;
    }

    @Override
    public void deleteById(Long id) {
        // no-op for now
    }
}
    
