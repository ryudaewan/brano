package kr.pe.ryudaewan.brano.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Profile("local")
@Configuration
@EnableWebSecurity
public class SecurityConfigLocal {
    private final String[] permitAllUrls = {"/favicon.ico", "/**/*.html", "/swagger-ui/**", "/api-docs/**"};

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(this.permitAllUrls).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        //.ignoringRequestMatchers(PathRequest.toH2Console()) // h2-console 은 CSRF 비활성화
                        .disable()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
        ;

        return http.build();
    }

}
