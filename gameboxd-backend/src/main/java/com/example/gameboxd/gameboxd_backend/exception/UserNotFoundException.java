// File: UserNotFoundException.java
package com.example.gameboxd.gameboxd_backend.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
