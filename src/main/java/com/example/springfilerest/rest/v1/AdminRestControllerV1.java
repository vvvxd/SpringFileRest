package com.example.springfilerest.rest.v1;


import com.example.springfilerest.dto.RepresentationBuilder;
import com.example.springfilerest.dto.UpdateUserDto;
import com.example.springfilerest.dto.UserRegistrationDto;
import com.example.springfilerest.dto.OperationResultOk;
import com.example.springfilerest.model.Event;
import com.example.springfilerest.model.Status;
import com.example.springfilerest.model.User;
import com.example.springfilerest.service.EventService;
import com.example.springfilerest.service.UserService;
import com.example.springfilerest.util.ControllerUtils;
import com.example.springfilerest.util.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/")
public class AdminRestControllerV1<AccountService> {

    private final ErrorResponse errorResponse;
    private final UserService userService;

    private final EventService eventService;

    @Autowired
    public AdminRestControllerV1(
            ErrorResponse errorResponse,
            UserService userService,
            EventService eventService) {

        this.errorResponse = errorResponse;
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping(value = "users")
    public ResponseEntity getAllUsers() {
        List<User> allUsers = userService.getAll();
        return new ResponseEntity(allUsers, HttpStatus.OK);
    }

    @GetMapping(value = "users/{id}")
    public ResponseEntity getUserById(@PathVariable(name = "id") Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity("User with id - " + id + " not found.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @GetMapping(value = "users/status/{status}")
    public ResponseEntity getUsersByStatus(@PathVariable(name = "status") String status) {
        if (!ControllerUtils.isStatusValid(status)) {
            return new ResponseEntity("Application doesn't supply status - " + status, HttpStatus.BAD_REQUEST);
        }
        Status requiredStatus = Status.valueOf(status.toUpperCase());
        List<User> usersWithConcreteStatus = userService.getAll()
                .stream()
                .filter(user -> user.getStatus().equals(requiredStatus))
                .collect(Collectors.toList());
        return new ResponseEntity(usersWithConcreteStatus, HttpStatus.OK);
    }

    @PostMapping("users/add")
    public ResponseEntity addUser(@RequestBody UserRegistrationDto userRegistrationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(errorResponse.registrationResponse(bindingResult), HttpStatus.BAD_REQUEST);
        }
        User user = userRegistrationDto.dtoToUser();
        userService.register(user);
        return new ResponseEntity(RepresentationBuilder.createResponseForAdmin(user), HttpStatus.OK);
    }

    @PutMapping("users/{id}")
    public ResponseEntity updateUser(@RequestBody UpdateUserDto userDto, BindingResult bindingResult,
                                     @PathVariable("id") Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity("Not any user was found with such id", HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(errorResponse.updateResponse(bindingResult), HttpStatus.BAD_REQUEST);
        }
        ControllerUtils.updateUser(user, userDto);
        userService.update(user);
        return new ResponseEntity(new OperationResultOk(), HttpStatus.OK);
    }

    @DeleteMapping("users/{id}")

    public ResponseEntity deleteUser(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity("Not any user was found with such id", HttpStatus.NOT_FOUND);
        }
        user.setStatus(Status.DELETED);
        userService.updateStatus(user);
        return new ResponseEntity(new OperationResultOk(), HttpStatus.OK);
    }

    @GetMapping(value = "users/{user_id}/events/{id}")
    public ResponseEntity getEventByUserId(@PathVariable("user_id") Long userId, @PathVariable("id") Long eventId) {
        User user = userService.findById(userId);
        if (user == null) {
            return new ResponseEntity("User with id - " + userId + " not found.", HttpStatus.NOT_FOUND);
        }

        Event event = eventService.findById(eventId, user);
        if (event == null) {
            return new ResponseEntity("No any events was found.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(event, HttpStatus.OK);
    }

    @GetMapping(value = "users/{id}/events")
    public ResponseEntity getEventsByUserId(@PathVariable("id") Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return new ResponseEntity("User with id - " + userId + " not found.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(user.getEvents(), HttpStatus.OK);
    }

}
