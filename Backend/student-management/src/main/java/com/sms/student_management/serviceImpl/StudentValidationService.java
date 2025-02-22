package com.sms.student_management.serviceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sms.student_management.entity.Student;
import com.sms.student_management.exception.DuplicatePhoneNumberException;
import com.sms.student_management.exception.InvalidStudentDataException;
import com.sms.student_management.exception.StudentNotFoundException;
import com.sms.student_management.model.StudentDTO;
import com.sms.student_management.repository.StudentRepository;


@Service
@Transactional(readOnly = true)
public class StudentValidationService {
    private final StudentRepository studentRepository;

    public StudentValidationService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void validateNewStudent(StudentDTO studentDTO) {
        validatePhoneNumber(studentDTO.getPhoneNumber());
        validateAge(studentDTO.getAge());
        validateClassName(studentDTO.getClassName());
    }

    public void validateStudentUpdate(Long id, StudentDTO studentDTO) {
        Student existingStudent = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id));
        
        if (!existingStudent.getPhoneNumber().equals(studentDTO.getPhoneNumber())) {
            validatePhoneNumber(studentDTO.getPhoneNumber());
        }
        validateAge(studentDTO.getAge());
        validateClassName(studentDTO.getClassName());
    }

    private void validatePhoneNumber(String phoneNumber) {
        studentRepository.findByPhoneNumber(phoneNumber)
            .ifPresent(s -> {
                throw new DuplicatePhoneNumberException(phoneNumber);
            });
    }

    private void validateAge(String age) {
        try {
            int ageNum = Integer.parseInt(age);
            if (ageNum < 4 || ageNum > 21) {
                throw new InvalidStudentDataException("Age must be between 4 and 21");
            }
        } catch (NumberFormatException e) {
            throw new InvalidStudentDataException("Invalid age format");
        }
    }

    private void validateClassName(String className) {
        if (!className.matches("^(1st|2nd|3rd|[4-9]th|10th|11th|12th)$")) {
            throw new InvalidStudentDataException("Invalid class name format");
        }
    }

}
