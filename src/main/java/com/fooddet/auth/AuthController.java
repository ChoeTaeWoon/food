package com.fooddet.auth;

import com.fooddet.auth.dto.SignupRequest;
import com.fooddet.auth.dto.LoginRequest;
import com.fooddet.auth.dto.TokenResponse;

import com.fooddet.user.User;
import com.fooddet.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/auth") @RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwt;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiError("EMAIL_TAKEN", "이미 사용 중인 이메일이었다."));
        }
        User u = User.builder()
                .email(req.getEmail())
                .passwordHash(encoder.encode(req.getPassword()))
                .displayName(req.getDisplayName())
                .locale(req.getLocale() == null ? "ko" : req.getLocale())
                .build();
        userRepo.save(u);
        String token = jwt.createAccessToken(u.getId(), u.getEmail());
        return ResponseEntity.ok(new TokenResponse(token, "Bearer", 7 * 24 * 3600));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        var u = userRepo.findByEmail(req.getEmail())
                .orElse(null);
        if (u == null || !encoder.matches(req.getPassword(), u.getPasswordHash())) {
            return ResponseEntity.status(401).body(new ApiError("INVALID_CREDENTIALS", "이메일 또는 비밀번호가 일치하지 않았다."));
        }
        String token = jwt.createAccessToken(u.getId(), u.getEmail());
        return ResponseEntity.ok(new TokenResponse(token, "Bearer", 7 * 24 * 3600));
    }

    record ApiError(String code, String message) {}
}
