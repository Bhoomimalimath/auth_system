package com.auth.auth_system.security;

import java.util.Map;
import java.util.Set;

public class RolePermissions {

    public static final Map<Role, Set<Permission>> ROLE_MAP = Map.of(
        Role.USER, Set.of(Permission.READ_PROFILE),
        Role.ADMIN, Set.of(Permission.READ_PROFILE, Permission.UPDATE_PROFILE, Permission.ADMIN_ACCESS)
    );
}
