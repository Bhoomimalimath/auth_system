package com.auth.auth_system.controller;

import com.auth.auth_system.dto.ApiResponse;
import com.auth.auth_system.model.QRSession;
import com.auth.auth_system.service.QRSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/qr")
public class QRController {

    @Autowired
    private QRSessionService qrSessionService;

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<Map<String, String>>> init() {
        String sessionId = qrSessionService.createSession();
        String qrImage = qrSessionService.generateQRCodeBase64(sessionId);
        
        Map<String, String> data = new HashMap<>();
        data.put("sessionId", sessionId);
        data.put("qrCode", "data:image/png;base64," + qrImage);
        
        return ResponseEntity.ok(new ApiResponse<>("QR Session Initialized", data));
    }

    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<String>> approve(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        qrSessionService.approveSession(sessionId, username);
        return ResponseEntity.ok(new ApiResponse<>("Login Approved", "Session verified for user: " + username));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> status(@RequestParam String sessionId) {
        QRSession session = qrSessionService.getStatus(sessionId);
        Map<String, Object> data = new HashMap<>();
        data.put("status", session.getStatus());
        
        if ("VERIFIED".equals(session.getStatus())) {
            String token = qrSessionService.completeLogin(sessionId);
            data.put("token", token);
        }
        
        return ResponseEntity.ok(new ApiResponse<>("Session Status", data));
    }
}
