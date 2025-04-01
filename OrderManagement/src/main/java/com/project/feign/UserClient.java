package com.project.feign;


import com.project.dto.UserCreditDTO;
import com.project.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * Feign client interface for interacting with the USER-SERVICE.
 * This interface defines methods to retrieve user details, update user credits, and perform credit operations.
 */
@FeignClient(name="USER-SERVICE")
public interface UserClient {

	/**
	 * Retrieves a user by their ID.
	 *
	 * @param userId The ID of the user to retrieve.
	 * @return A ResponseEntity containing the UserDTO representing the user, or null if not found.
	 */
	@GetMapping("dbs/user/admin/get-user/{userId}")
	ResponseEntity<UserDTO> getUserById(@PathVariable Long userId);

	/**
	 * Debits credits from a user's account.
	 *
	 * @param userId The ID of the user.
	 * @param amount The amount of credits to debit.
	 * @return A ResponseEntity containing the UserCreditDTO with updated credit information.
	 */
	@PutMapping("dbs/user/admin/debit-credits/{userId}/{amount}")
	ResponseEntity<UserCreditDTO> debitCredits(@PathVariable Long userId,@PathVariable Double amount);


	/**
	 * Retrieves the credit information for a user.
	 *
	 * @param userId The ID of the user.
	 * @return The UserCreditDTO representing the user's credit information.
	 */
	@GetMapping("dbs/user/admin/get-user-credits/{userId}")
	UserCreditDTO getUserCredit(@PathVariable Long userId);

	/**
	 * Adds credits to a user's account.
	 *
	 * @param userId The ID of the user.
	 * @param amount The amount of credits to add.
	 * @return A ResponseEntity containing the UserCreditDTO with updated credit information.
	 */
	@PutMapping("dbs/user/admin/add-credits/{userId}/{amount}")
	ResponseEntity<UserCreditDTO> addCredits(@PathVariable Long userId, @PathVariable Double amount);
}


