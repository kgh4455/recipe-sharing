package com.project.config;

import com.project.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getIsVerified(); // 이메일 인증 여부 기반으로 설정
    }

    // 👉 추가적으로 user 정보 꺼내고 싶을 때
    public Long getId() {
        return user.getId();
    }

    public String getName() {
        return user.getName();
    }

    public String getRole() {
        return user.getRole();
    }
    

    // ✅ 이거 추가
    public User getUser() {
        return user;
    }
}