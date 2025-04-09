package com.project.repotest;//package com.project.repotest;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import com.project.models.User;
//import com.project.repositories.UserRepository;
//import com.project.services.UserService;
//
//import java.util.Optional;
//
// class UserRepositoryTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private UserService userService;
//
//    @BeforeEach
//     void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//     void testFindByEmailSuccess() {
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setName("Test User");
//
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
//
//        Optional<User> result = userRepository.findByEmail("test@example.com");
//
//        assertTrue(result.isPresent());
//        assertEquals("test@example.com", result.get().getEmail());
//        assertEquals("Test User", result.get().getName());
//    }
//
//    @Test
//     void testFindByEmailNotFound() {
//        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
//
//        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");
//
//        assertFalse(result.isPresent());
//    }
//
//    @Test
//    void testFindByEmailException() {
//        when(userRepository.findByEmail("error@example.com")).thenThrow(new RuntimeException("Database error"));
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userRepository.findByEmail("error@example.com");
//        });
//
//        assertEquals("Database error", exception.getMessage());
//    }
//
//    @Test
//     void testExistsByEmailSuccess() {
//        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
//
//        boolean exists = userRepository.existsByEmail("test@example.com");
//
//        assertTrue(exists);
//    }
//
//    @Test
//     void testExistsByEmailNotFound() {
//        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);
//
//        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
//
//        assertFalse(exists);
//    }
//
//    @Test
//     void testExistsByEmailException() {
//        when(userRepository.existsByEmail("error@example.com")).thenThrow(new RuntimeException("Database error"));
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userRepository.existsByEmail("error@example.com");
//        });
//
//        assertEquals("Database error", exception.getMessage());
//    }
//
//    @Test
//    void testSaveUserSuccess() {
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setName("Test User");
//
//        when(userRepository.save(user)).thenReturn(user);
//
//        User savedUser = userRepository.save(user);
//
//        assertNotNull(savedUser);
//        assertEquals("test@example.com", savedUser.getEmail());
//        assertEquals("Test User", savedUser.getName());
//    }
//
//    @Test
//     void testSaveUserException() {
//        User user = new User();
//        user.setEmail("error@example.com");
//        user.setName("Error User");
//
//        when(userRepository.save(user)).thenThrow(new RuntimeException("Database error"));
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userRepository.save(user);
//        });
//
//        assertEquals("Database error", exception.getMessage());
//    }
//
//    @Test
//    void testFindByIdSuccess() {
//        User user = new User();
//        user.setUserId(1L);
//        user.setEmail("test@example.com");
//        user.setName("Test User");
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//
//        Optional<User> result = userRepository.findById(1L);
//
//        assertTrue(result.isPresent());
//        assertEquals(1L, result.get().getUserId());
//        assertEquals("test@example.com", result.get().getEmail());
//        assertEquals("Test User", result.get().getName());
//    }
//
//    @Test
//     void testFindByIdNotFound() {
//        when(userRepository.findById(999L)).thenReturn(Optional.empty());
//
//        Optional<User> result = userRepository.findById(999L);
//
//        assertFalse(result.isPresent());
//    }
//
//    @Test
//    void testFindByIdException() {
//        when(userRepository.findById(999L)).thenThrow(new RuntimeException("Database error"));
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userRepository.findById(999L);
//        });
//
//        assertEquals("Database error", exception.getMessage());
//    }
//
//    @Test
//     void testDeleteByIdSuccess() {
//        doNothing().when(userRepository).deleteById(1L);
//
//        userRepository.deleteById(1L);
//
//        verify(userRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    void testDeleteByIdException() {
//        doThrow(new RuntimeException("Database error")).when(userRepository).deleteById(999L);
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userRepository.deleteById(999L);
//        });
//
//        assertEquals("Database error", exception.getMessage());
//    }
//}
