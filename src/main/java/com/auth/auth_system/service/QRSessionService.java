package com.auth.auth_system.service;

import com.auth.auth_system.model.QRSession;
import com.auth.auth_system.model.User;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class QRSessionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthService authService;

    private static final String KEY_PREFIX = "qr:session:";
    private static final long TTL_SECONDS = 120;

    public String createSession() {
        String sessionId = UUID.randomUUID().toString();
        QRSession session = new QRSession("PENDING", null, System.currentTimeMillis());
        redisTemplate.opsForValue().set(KEY_PREFIX + sessionId, session, TTL_SECONDS, TimeUnit.SECONDS);
        return sessionId;
    }

    public void approveSession(String sessionId, String username) {
        QRSession session = (QRSession) redisTemplate.opsForValue().get(KEY_PREFIX + sessionId);
        if (session == null) {
            throw new RuntimeException("Session expired or invalid");
        }
        if (!"PENDING".equals(session.getStatus())) {
            throw new RuntimeException("Session already processed");
        }

        session.setStatus("VERIFIED");
        session.setUsername(username);
        redisTemplate.opsForValue().set(KEY_PREFIX + sessionId, session, TTL_SECONDS, TimeUnit.SECONDS);
    }

    public QRSession getStatus(String sessionId) {
        QRSession session = (QRSession) redisTemplate.opsForValue().get(KEY_PREFIX + sessionId);
        if (session == null) {
            return new QRSession("EXPIRED", null, 0);
        }
        return session;
    }

    public String generateQRCodeBase64(String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 250, 250);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            throw new RuntimeException("Could not generate QR Code", e);
        }
    }

    public String completeLogin(String sessionId) {
        QRSession session = (QRSession) redisTemplate.opsForValue().get(KEY_PREFIX + sessionId);
        if (session != null && "VERIFIED".equals(session.getStatus())) {
            User user = authService.getUserByUsername(session.getUsername());
            String token = tokenService.generateToken(user);
            redisTemplate.delete(KEY_PREFIX + sessionId);
            return token;
        }
        return null;
    }
}
