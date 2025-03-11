package com.project.service;

import com.project.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "USER-SERVICE", url = "http://localhost:8086/")
@Component
public class UserService {
//public interface UserService {

//	@GetMapping("dbs/admin/get-user/{userId}")
//	ResponseEntity<UserDTO> getUserById(@PathVariable Long userId);
	ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
		return null;
	}
}
