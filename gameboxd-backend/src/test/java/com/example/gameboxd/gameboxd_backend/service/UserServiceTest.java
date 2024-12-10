package com.example.gameboxd.gameboxd_backend.service;

import com.example.gameboxd.gameboxd_backend.dto.users.UserRegistrationDTO;
import com.example.gameboxd.gameboxd_backend.exception.InvalidCredentialsException;
import com.example.gameboxd.gameboxd_backend.exception.UserAlreadyExistsException;
import com.example.gameboxd.gameboxd_backend.model.User;
import com.example.gameboxd.gameboxd_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class) // Use Mockito's extension
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserRegistrationDTO registrationDTO;

    @Mock
    private CustomListService customListService;

    @BeforeEach
    void setUp() {
        // Initialize mocks and test data
        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("testuser");
        registrationDTO.setEmail("test@example.com");
        registrationDTO.setPassword("password123");
        registrationDTO.setAvatarUrl("http://example.com/avatar.png");
        registrationDTO.setBio("Test bio");
    }

    @Test
    void registerUser_Success() {
        // Arrange
        when(userRepository.findByUsername(registrationDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registrationDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationDTO.getPassword())).thenReturn("hashedPassword");

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .username(registrationDTO.getUsername())
                .email(registrationDTO.getEmail())
                .passwordHash("hashedPassword")
                .avatarUrl(registrationDTO.getAvatarUrl())
                .bio(registrationDTO.getBio())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.registerUser(registrationDTO);

        // Assert
        assertNotNull(result);
        assertEquals(registrationDTO.getUsername(), result.getUsername());
        assertEquals(registrationDTO.getEmail(), result.getEmail());
        assertEquals("hashedPassword", result.getPasswordHash());
        verify(userRepository, times(1)).findByUsername(registrationDTO.getUsername());
        verify(userRepository, times(1)).findByEmail(registrationDTO.getEmail());
        verify(passwordEncoder, times(1)).encode(registrationDTO.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_UsernameAlreadyExists() {
        // Arrange
        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .username(registrationDTO.getUsername())
                .email("existing@example.com")
                .passwordHash("hashedPassword")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername(registrationDTO.getUsername())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(registrationDTO);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(registrationDTO.getUsername());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        // Arrange
        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .username("existinguser")
                .email(registrationDTO.getEmail())
                .passwordHash("hashedPassword")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername(registrationDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registrationDTO.getEmail())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(registrationDTO);
        });

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(registrationDTO.getUsername());
        verify(userRepository, times(1)).findByEmail(registrationDTO.getEmail());
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void authenticateUser_Success() {
        // Arrange
        String username = "testuser";
        String rawPassword = "password123";
        String hashedPassword = "hashedPassword";

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .email("test@example.com")
                .passwordHash(hashedPassword)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);

        // Act
        User result = userService.authenticateUser(username, rawPassword);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(rawPassword, hashedPassword);
    }

    @Test
    void authenticateUser_UserNotFound() {
        // Arrange
        String username = "nonexistent";
        String rawPassword = "password123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            userService.authenticateUser(username, rawPassword);
        });

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(0)).matches(anyString(), anyString());
    }

    @Test
    void authenticateUser_PasswordMismatch() {
        // Arrange
        String username = "testuser";
        String rawPassword = "wrongpassword";
        String hashedPassword = "hashedPassword";

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .email("test@example.com")
                .passwordHash(hashedPassword)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            userService.authenticateUser(username, rawPassword);
        });

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(rawPassword, hashedPassword);
    }
}
