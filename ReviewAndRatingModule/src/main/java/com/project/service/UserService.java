package com.project.service;

import com.project.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("USER-MODULE")
@RequestMapping("dbs")
public interface UserService {

	@PostMapping("/admin/get-user/{userId}")
	ResponseEntity<UserDTO> getUserById(@PathVariable long userId);
	
}
