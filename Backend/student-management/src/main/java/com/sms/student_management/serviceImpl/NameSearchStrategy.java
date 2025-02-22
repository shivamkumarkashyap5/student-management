package com.sms.student_management.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sms.student_management.entity.Student;
import com.sms.student_management.repository.StudentRepository;
import com.sms.student_management.service.SearchStrategy;

@Component
public class NameSearchStrategy implements SearchStrategy {
    private final StudentRepository repository;
    
    public NameSearchStrategy(StudentRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public List<Student> search(String criteria) {
        return repository.findByNameContainingIgnoreCase(criteria);
    }
}