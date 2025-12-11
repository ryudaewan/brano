package kr.pe.ryudaewan.brano.message.controller;

import jakarta.validation.Valid;
import kr.pe.ryudaewan.brano.message.service.MessageService;
import kr.pe.ryudaewan.brano.message.service.MessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<MessageVo>> findMessages() {
        List<MessageVo> dbUsers = this.messageService.findMessages();

        if (null == dbUsers || dbUsers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dbUsers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageVo> getMessage(@PathVariable Long uid) {
        MessageVo dbUser = this.messageService.getMessage(uid);

        if (null == dbUser) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(dbUser);
    }

    @PostMapping
    public MessageVo registerMessage(@RequestBody @Valid MessageVo user) {
        return this.messageService.registerMessage(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageVo> modifyMessage(@PathVariable Long id, @RequestBody @Valid MessageVo user) {
        MessageVo dbUser = messageService.modifyMessage(id, user);

        if (dbUser == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dbUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteUser(@PathVariable Long id) {
        int cnt = this.messageService.eraseMessage(id);

        if (cnt < 1) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(cnt);
    }
}
