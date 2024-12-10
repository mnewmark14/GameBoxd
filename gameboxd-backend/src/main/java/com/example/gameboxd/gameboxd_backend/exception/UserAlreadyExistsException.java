// File: UserAlreadyExistsException.java
package com.example.gameboxd.gameboxd_backend.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
