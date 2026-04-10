package com.viniciusdev.commerceapi.security.authorization;

import com.viniciusdev.commerceapi.database.model.User;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    public boolean isOwner(Long userId, Long ownerId) {
        if (userId == null) return false;
        return userId.equals(ownerId);
    }
}
