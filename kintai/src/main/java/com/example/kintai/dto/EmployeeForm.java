package com.example.kintai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmployeeForm {

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Email
    private String email;

    @Size(max = 50)
    private String department;

    public EmployeeForm() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}

