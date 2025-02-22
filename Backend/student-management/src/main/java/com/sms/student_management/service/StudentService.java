package com.sms.student_management.service;

import java.util.List;

import com.sms.student_management.model.StudentDTO;

public interface StudentService {
    List<StudentDTO> getAllStudents();
    StudentDTO getStudentById(Long id);
    StudentDTO createStudent(StudentDTO studentDTO);
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    String deleteStudent(Long id);
    List<StudentDTO> searchStudentsByName(String name);
}
