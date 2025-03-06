package com.project.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.dto.UserDTO;

//@FeignClient("USER-MODULE")
@RequestMapping("dbs")
public interface UserService {

	@PostMapping("user/{userId}")
	UserDTO getUserById(@PathVariable long userId);
	
}
