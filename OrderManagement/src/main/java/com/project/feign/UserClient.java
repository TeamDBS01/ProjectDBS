package com.project.feign;


import com.project.dto.UserCreditDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.dto.UserDTO;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name="USER-SERVICE")
public interface UserClient {

	@GetMapping("dbs/user/admin/get-user/{userId}")
	ResponseEntity<UserDTO> getUserById(@PathVariable Long userId);

	@PutMapping("dbs/user/admin/debit-credits/{userId}/{amount}")
	ResponseEntity<UserCreditDTO> debitCredits(@PathVariable Long userId,@PathVariable Double amount);

	@GetMapping("dbs/user/admin/get-user-credits/{userId}")
	UserCreditDTO getUserCredit(@PathVariable Long userId);

	@PutMapping("dbs/user/admin/add-credits/{userId}/{amount}")
	ResponseEntity<UserCreditDTO> addCredits(@PathVariable Long userId, @PathVariable Double amount);
}


