package com.sms.student_management.service;

import java.util.List;

import com.sms.student_management.entity.Student;

public interface SearchStrategy {
    List<Student> search(String criteria);
}
