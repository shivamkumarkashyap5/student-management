package com.sms.student_management.exception;

public class StudentOperationException extends RuntimeException {
    public StudentOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StudentOperationException(String msg){
        super(msg);
    }
}