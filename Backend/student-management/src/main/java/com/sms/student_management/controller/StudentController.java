package com.sms.student_management.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sms.student_management.exception.StudentNotFoundException;
import com.sms.student_management.exception.StudentOperationException;
import com.sms.student_management.model.StudentDTO;
import com.sms.student_management.service.StudentService;

@CrossOrigin("*")
@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        try {
            return ResponseEntity.ok(studentService.getAllStudents());
        } catch (StudentOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(studentService.getStudentById(id));
        } catch (StudentNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        try {
            StudentDTO created = studentService.createStudent(studentDTO);
            return ResponseEntity.created(URI.create("/api/students/" + created.getId()))
                .body(created);
        } catch (StudentOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {
        try {
            return ResponseEntity.ok(studentService.updateStudent(id, studentDTO));
        } catch (StudentNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (StudentOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        try {
            String response = studentService.deleteStudent(id);
            return ResponseEntity.ok(response);
        } catch (StudentNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (StudentOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchStudents(@RequestParam String name) {
        try {
            return ResponseEntity.ok(studentService.searchStudentsByName(name));
        } catch (StudentNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (StudentOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
