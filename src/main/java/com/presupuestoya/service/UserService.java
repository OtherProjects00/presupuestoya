package com.presupuestoya.service;

import com.presupuestoya.domain.AppUser;
import com.presupuestoya.domain.BusinessProfile;
import com.presupuestoya.repository.AppUserRepository;
import com.presupuestoya.repository.BusinessProfileRepository;
import com.presupuestoya.web.form.RegisterForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {

    private final AppUserRepository users;
    private final BusinessProfileRepository profiles;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository users, BusinessProfileRepository profiles, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.profiles = profiles;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser register(RegisterForm form) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        String email = form.getEmail().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese email.");
        }

        AppUser user = new AppUser(
                email,
                passwordEncoder.encode(form.getPassword()),
                form.getDisplayName().trim());
        user = users.save(user);

        BusinessProfile profile = new BusinessProfile(user, user.getDisplayName());
        profiles.save(profile);
        return user;
    }
}
