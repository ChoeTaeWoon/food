package com.fooddet.user;

import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor
public class MeController {
    private final UserRepository repo;

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        Long uid = (Long) auth.getPrincipal();
        var u = repo.findById(uid).orElseThrow();
        return ResponseEntity.ok(new MeResponse(u.getId(), u.getEmail(), u.getDisplayName(), u.getLocale()));
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMe(Authentication auth,
                                      @RequestBody UpdateMeRequest req) {
        Long uid = (Long) auth.getPrincipal();
        var u = repo.findById(uid).orElseThrow();
        if (req.displayName != null) u.setDisplayName(req.displayName());
        if (req.locale != null) u.setLocale(req.locale());
        repo.save(u);
        return ResponseEntity.ok(new MeResponse(u.getId(), u.getEmail(), u.getDisplayName(), u.getLocale()));
    }

    record MeResponse(Long id, String email, String displayName, String locale) {}
    record UpdateMeRequest(@Size(max=80) String displayName, @Size(max=10) String locale) {}
}
