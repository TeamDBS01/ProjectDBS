package com.project.feign;

import com.project.dto.UserDTO;
import com.project.exception.ServiceUnavailableException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;



@Component
public class UserClientFallback implements UserClient {

    @Override
    public ResponseEntity<UserDTO> getUserById(Long userId) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("User Service is Not Available");
    }
}