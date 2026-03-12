package com.example.minibankbackend.transaction.controller;



import com.example.minibankbackend.transaction.dto.TransferRequest;
import com.example.minibankbackend.transaction.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService service;

    public TransferController(TransferService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<?> transfer(Authentication auth, @Valid @RequestBody TransferRequest req) {
        Long userId = Long.valueOf(auth.getName());
        service.transfer(userId, req);
        return ResponseEntity.ok().build();
    }
}
