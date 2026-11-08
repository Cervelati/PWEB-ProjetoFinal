package com.rosetas.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public class FirebaseIdentityResolver implements IdentityAuthFilter.IdentityResolver {

    @Override
    public Optional<IdentityAuthFilter.Identity> resolve(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return Optional.empty();
        }
        try {
            FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(header.substring(7));
            return Optional.of(new IdentityAuthFilter.Identity(token.getUid(), token.getEmail()));
        } catch (FirebaseAuthException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
