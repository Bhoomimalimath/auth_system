package com.auth.auth_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QRSession implements Serializable {
    private String status; // PENDING, VERIFIED, EXPIRED
    private String username;
    private long createdAt;
}
