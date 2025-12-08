package kr.pe.ryudaewan.brano.user.controller;

import jakarta.validation.Valid;
import kr.pe.ryudaewan.brano.user.service.UserService;
import kr.pe.ryudaewan.brano.user.service.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<UserVo>> findUsers() {
        List<UserVo> dbUsers = this.userService.findUsers();

        if (null == dbUsers || dbUsers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dbUsers);
    }

    @GetMapping("/api/user/{uid}")
    public ResponseEntity<UserVo> getUser(@PathVariable Long uid) {
        UserVo dbUser = this.userService.getUser(uid);

        if (null == dbUser) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(dbUser);
    }

    @PutMapping("/api/user")
    public UserVo registerUser(@RequestBody @Valid UserVo user) {
        return this.userService.registerUser(user);
    }

    @PostMapping("/api/user")
    public ResponseEntity<UserVo> modifyUser(@RequestBody @Valid UserVo user) {
        UserVo dbUser = userService.modifyUser(user);

        if (dbUser == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dbUser);
    }

    @DeleteMapping("/api/user/{uid}")
    public ResponseEntity<Integer> deleteUser(@PathVariable Long uid) {
        int cnt = this.userService.eraseUser(uid);

        if (cnt < 1) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(cnt);
    }
}
