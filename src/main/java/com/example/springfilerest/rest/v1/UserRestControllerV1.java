package com.example.springfilerest.rest.v1;


import com.example.springfilerest.dto.RepresentationBuilder;
import com.example.springfilerest.dto.UpdateUserDto;
import com.example.springfilerest.dto.OperationResultOk;
import com.example.springfilerest.model.*;
import com.example.springfilerest.security.jwt.JwtUser;
import com.example.springfilerest.service.EventService;
import com.example.springfilerest.service.FileService;
import com.example.springfilerest.service.UserService;
import com.example.springfilerest.util.ControllerUtils;
import com.example.springfilerest.util.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/")
public class UserRestControllerV1 {

    private final UserService userService;
    private final FileService fileService;
    private final ErrorResponse errorResponse;
    private final EventService eventService;

    @Autowired
    public UserRestControllerV1(UserService userService,
                                FileService fileService,
                                ErrorResponse errorResponse,
                                EventService eventService) {
        this.userService = userService;
        this.fileService = fileService;
        this.errorResponse = errorResponse;
        this.eventService = eventService;
    }

    @GetMapping("me")
    public ResponseEntity getLoggedInUser(@AuthenticationPrincipal JwtUser userDetails) {
        User user = userService.findById(userDetails.getId());
        if (user == null) {
            return new ResponseEntity("User - " + userDetails.getUsername() + " not found.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
        Role role_admin = user.getRoles()
                .stream()
                .filter(r -> r.getName().equals("ROLE_ADMIN"))
                .findAny().orElse(null);

        if (role_admin == null) {
            return new ResponseEntity(RepresentationBuilder.createResponseForUser(user), HttpStatus.OK);
        }
        return new ResponseEntity(RepresentationBuilder.createResponseForAdmin(user), HttpStatus.OK);
    }

    @DeleteMapping("me")
    public ResponseEntity deleteLoggedInUser(@AuthenticationPrincipal JwtUser userDetails) {
        if (ControllerUtils.isAdmin(userDetails)) {
            return new ResponseEntity("Admin cannot be deleted", HttpStatus.SERVICE_UNAVAILABLE);
        }
        User user = userService.findById(userDetails.getId());
        user.setStatus(Status.DELETED);
        userService.update(user);
        return new ResponseEntity(new OperationResultOk(), HttpStatus.OK);
    }

    @PutMapping("me")
    public ResponseEntity updateLoggedInUser(@RequestBody UpdateUserDto userDto,
                                             BindingResult bindingResult,
                                             @AuthenticationPrincipal JwtUser userDetails) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(errorResponse.updateResponse(bindingResult), HttpStatus.BAD_REQUEST);
        }
        User user = userService.findById(userDetails.getId());
        ControllerUtils.updateUser(user, userDto);
        userService.update(user);
        return new ResponseEntity(new OperationResultOk(), HttpStatus.OK);
    }

    @GetMapping("me/events")
    public ResponseEntity getEvents(@AuthenticationPrincipal JwtUser userDetails) {
        User user = userService.findById(userDetails.getId());
        List<Event> events = user.getEvents();
        return new ResponseEntity(events, HttpStatus.OK);
    }

    @GetMapping("me/events/{id}")
    public ResponseEntity getEventById(@PathVariable("id") Long eventId,
                                       @AuthenticationPrincipal JwtUser userDetails) {
        User user = userService.findById(userDetails.getId());
        Event event = eventService.findById(eventId, user);
        if (event == null) {
            return new ResponseEntity("No any event with id - " + eventId + " was found.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(event, HttpStatus.OK);
    }

    @GetMapping("me/files")
    public ResponseEntity getFiles(@AuthenticationPrincipal JwtUser userDetails) {
        User user = userService.findById(userDetails.getId());
        List<File> files = fileService.getFilesFromS3Bucket(user);
        return new ResponseEntity(files, HttpStatus.OK);
    }

    @GetMapping("me/files/status/{status}")
    public ResponseEntity getFilesByStatus(@PathVariable("status") String status,
                                           @AuthenticationPrincipal JwtUser userDetails) {
        if (!ControllerUtils.isStatusValid(status)) {
            return new ResponseEntity("Status is not correct.", HttpStatus.BAD_REQUEST);
        }
        User user = userService.findById(userDetails.getId());
        List<File> files = fileService.getFilesFromDb(user, Status.valueOf(status.toUpperCase()));
        return new ResponseEntity(files, HttpStatus.OK);
    }

    @GetMapping("me/files/{id}")
    public ResponseEntity getFileById(@PathVariable("id") Long id) {
        File file = fileService.getById(id);
        if (file == null) {
            return new ResponseEntity("No any file with id - " + id + " was found.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(file, HttpStatus.OK);
    }

    @GetMapping("me/files/download/{filename}")
    public ResponseEntity downloadFile(@PathVariable("filename") String filename,
                                       @AuthenticationPrincipal JwtUser userDetails) {
        User user = userService.findById(userDetails.getId());
        if (user == null) {
            return new ResponseEntity("Something went wrong.", HttpStatus.SERVICE_UNAVAILABLE);
        }
        return fileService.downloadFileFromS3(filename, user);
    }

}
