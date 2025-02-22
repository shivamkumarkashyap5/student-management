package com.sms.student_management.exception;

public class DuplicatePhoneNumberException extends RuntimeException {
    public DuplicatePhoneNumberException(String phoneNumber) {
        super("Student with phone number " + phoneNumber + " already exists");
    }
}