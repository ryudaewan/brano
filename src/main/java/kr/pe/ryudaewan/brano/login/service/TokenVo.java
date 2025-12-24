package kr.pe.ryudaewan.brano.login.service;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
@RedisHash(value = "tokens", timeToLive = 3600)
public class TokenVo implements Serializable {
    @Id
    private String token;
    private String userId;
    private String username;
    private List<String> roles;

    public boolean isValid() {
        return true;  // TTL로 자동 만료
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(SimpleGrantedAuthority::new).toList();
    }
}
