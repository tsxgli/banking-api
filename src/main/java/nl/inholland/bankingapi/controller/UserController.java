package nl.inholland.bankingapi.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.model.dto.*;
import nl.inholland.bankingapi.model.dto.UserPOST_DTO;
import nl.inholland.bankingapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController

@RequestMapping("users")
@Log
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    //GET Returns all the users on the system
    @GetMapping
    public ResponseEntity<Object> getAllUsers(
            //@RequestParam( required = false) Integer offset,
            //@RequestParam( required = false) Integer limit,
            @RequestParam( required = false) String email,
            @RequestParam( required = false) String firstName,
            @RequestParam( required = false) String lastName,
            @RequestParam( required = false) String birthDate,
            @RequestParam( required = false) String postalCode,
            @RequestParam( required = false) String address,
            @RequestParam( required = false) String city,
            @RequestParam( required = false) String phoneNumber,
            @RequestParam( required = false) UserType userType,
            @RequestParam( required = false) String keyword,
            @RequestParam( required = false) String hasAccount,
            @RequestParam( required = false) AccountType excludedAccountType
    ) {
        return ResponseEntity.ok(userService.getAllUsers(keyword, firstName, lastName, hasAccount, email, birthDate, postalCode, address, city, phoneNumber, userType, excludedAccountType));
    }

    //POST Creates a new user
    @PostMapping
    public ResponseEntity registerUser(@RequestBody UserPOST_DTO dto) {
        try {
            return ResponseEntity.status(201).body(
                    userService.registerUser(dto));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    //DELETE Deletes a user of specified id
    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.status(204).body(null);
        } catch (Exception e) {
            throw e;
        }
    }

    //GET Retrieves a user of specified id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    //PUT Updates an existing user
    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable Long id, @RequestBody UserPOST_DTO dto) {
        try {
            return ResponseEntity.status(201).body(userService.updateUser(id, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}