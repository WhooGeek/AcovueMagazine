package com.AcovueMagazine.User.Controller;

import com.AcovueMagazine.User.Dto.CreateUserRequest;
import com.AcovueMagazine.User.Security.Dto.UserInfoResponse;
import com.AcovueMagazine.User.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

//    회원가입 기능
    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@RequestBody CreateUserRequest newUser){

        userService.createUser(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserInfoResponse> getUserInfoById(@PathVariable("id") Long id){

        UserInfoResponse userInfoResponse = userService.getUserInfoById(id);

        return ResponseEntity.ok(userInfoResponse);
    }
}
