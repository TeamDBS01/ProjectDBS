package com.project.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.dto.UserDTO;
//import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name="USER-SERVICE")
public interface UserClient {
	@GetMapping("admin/get-user/{userId}")
	ResponseEntity<UserDTO> getUserById(@PathVariable Long userId);
}

////@FeignClient(name="USER-SERVICE")
//@Component
//public class UserClient {
//
////	@GetMapping("admin/get-user/{userId}")
//	public ResponseEntity<UserDTO> getUserById(Long userId){
//		return null;
//	}
//}
