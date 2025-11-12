//package com.example.foodapp.user;
package com.fooddet.user;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=180)
    private String email;

    @Column(nullable=false)
    private String passwordHash;

    @Column(length=80)
    private String displayName;

    @Column(length=10)
    private String locale;

    @Column(nullable=false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (locale == null) locale = "ko";
    }
}
