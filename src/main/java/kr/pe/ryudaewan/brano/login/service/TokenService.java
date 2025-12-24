package kr.pe.ryudaewan.brano.login.service;

import kr.pe.ryudaewan.brano.login.dao.TokenDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {
    private final TokenDao tokenRepository;

    public String createToken(String userId, String username, List<String> roles) {
        String token = generateRandomToken();
        TokenVo info = new TokenVo();
        info.setToken(token);
        info.setUserId(userId);
        info.setUsername(username);
        info.setRoles(roles);

        tokenRepository.save(info);  // TTL 1시간 자동
        return token;
    }

    public TokenVo validateToken(String token) {
        Optional<TokenVo> optional = tokenRepository.findById(token);

        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public void invalidateToken(String token) {
        this.tokenRepository.deleteById(token);
    }

    private String generateRandomToken() {
        byte[] buffer = new byte[32];
        new Random().nextBytes(buffer);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);
    }
}
