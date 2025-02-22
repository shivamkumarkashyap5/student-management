package com.sms.student_management.serviceImpl;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.sms.student_management.entity.Student;
import com.sms.student_management.exception.StudentNotFoundException;
import com.sms.student_management.exception.StudentOperationException;
import com.sms.student_management.model.StudentDTO;
import com.sms.student_management.repository.StudentRepository;
import com.sms.student_management.service.StudentService;

import lombok.extern.slf4j.Slf4j;

@Service
@CacheConfig(cacheNames = "students")
@Slf4j
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private StudentValidationService validationService;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "students")
    public List<StudentDTO> getAllStudents() {
        log.info("Fetching all students");
        try {
            List<Student> students = studentRepository.findAll();
            if (students.isEmpty()) {
                throw new StudentOperationException("No students found");
            }
            return students.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching students: {}", e.getMessage());
            throw new StudentOperationException("Failed to retrieve students", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "students", key = "#id")
    public StudentDTO getStudentById(Long id) {
        log.info("Fetching student with id: {}", id);
        return studentRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found"));
    }

    @Override
    @Transactional
    @CachePut(key = "#result.id")
    public StudentDTO createStudent(StudentDTO studentDTO) {
        log.info("Creating new student");
        try {
            validationService.validateNewStudent(studentDTO);
            Student student = convertToEntity(studentDTO);
            Student savedStudent = studentRepository.save(student);
            return convertToDTO(savedStudent);
        } catch (Exception e) {
            log.error("Error creating student: {}", e.getMessage());
            throw new StudentOperationException("Failed to create student", e);
        }
    }

    @Override
    @Transactional
    @CachePut(key = "#id")
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        log.info("Updating student with id: {}", id);
        try {
            Student existingStudent = studentRepository.findById(id)
                    .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found"));

            validationService.validateStudentUpdate(id, studentDTO);

            modelMapper.map(studentDTO, existingStudent);
            existingStudent.setId(id);
            Student updatedStudent = studentRepository.save(existingStudent);
            return convertToDTO(updatedStudent);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConcurrentModificationException("Student was updated by another user");
        } catch (Exception e) {
            log.error("Error updating student: {}", e.getMessage());
            throw new StudentOperationException("Failed to update student", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public String deleteStudent(Long id) {
        log.info("Deleting student with id: {}", id);
        try {
            if (!studentRepository.existsById(id)) {
                throw new StudentNotFoundException("Student with ID " + id + " not found");
            }
            studentRepository.deleteById(id);
            return "Student Deleted Successfully with ID: " + id;
        } catch (Exception e) {
            log.error("Error deleting student: {}", e.getMessage());
            throw new StudentOperationException("Failed to delete student", e);
        }
    }

    @Override
    public List<StudentDTO> searchStudentsByName(String name) {
        try {
            List<Student> students = studentRepository.findByNameContainingIgnoreCase(name);
            if (CollectionUtils.isEmpty(students)) {
                throw new StudentNotFoundException("No students found with name: " + name);
            }
            return students.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching students: {}", e.getMessage());
            throw new StudentOperationException("Failed to search students", e);
        }
    }

    private StudentDTO convertToDTO(Student student) {
        return modelMapper.map(student, StudentDTO.class);
    }

    private Student convertToEntity(StudentDTO studentDTO) {
        return modelMapper.map(studentDTO, Student.class);
    }
}
