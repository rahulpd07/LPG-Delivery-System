package com.pbg.lpg_delivery.model.entity;


import com.pbg.lpg_delivery.common.Role;
import com.pbg.lpg_delivery.model.request.SignupRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;

    private String phoneNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderEntity> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentEntity> payments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FeedbackEntity> feedbacks;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreated(){
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    public static UserEntity mapToEntity(SignupRequest signupRequest, PasswordEncoder passwordEncoder){
        return UserEntity.builder()
               .username(signupRequest.username())
               .password(passwordEncoder.encode(signupRequest.password()))
               .email(signupRequest.email())
               .phoneNumber(signupRequest.phoneNumber())
               .address(signupRequest.address())
               .role(Role.CUSTOMER)
               .build();
    }

    public static UserEntity mapToEntityForUser(SignupRequest signupRequest, PasswordEncoder passwordEncoder){
        return UserEntity.builder()
                .username(signupRequest.username())
                .password(passwordEncoder.encode(signupRequest.password()))
                .email(signupRequest.email())
                .phoneNumber(signupRequest.phoneNumber())
                .address(signupRequest.address())
                .role(signupRequest.role())
                .build();
    }
}



