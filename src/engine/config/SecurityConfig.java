package engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .csrf((AbstractHttpConfigurer::disable))
                .csrf(cfg -> cfg.disable()).headers(cfg -> cfg.frameOptions().disable())
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(HttpMethod.POST,"/actuator/shutdown").permitAll()
                        .antMatchers(HttpMethod.POST,"/api/quizzes").authenticated()
                        .antMatchers(HttpMethod.POST,"/api/quizzes/**").authenticated()
                        .antMatchers(HttpMethod.DELETE,"/api/quizzes/*").authenticated()
                        .antMatchers(HttpMethod.GET,"/api/quizzes").authenticated()
                        .antMatchers(HttpMethod.GET,"/api/quizzes/*").authenticated()
                        .antMatchers(HttpMethod.POST,"/api/register").permitAll()
                        .antMatchers(HttpMethod.GET,"/api/users/*").permitAll()
                        .antMatchers(HttpMethod.DELETE,"/api/users/*").permitAll()
                        .antMatchers(HttpMethod.GET,"/api/users").permitAll()
                        .antMatchers("/h2-console/**").permitAll()

                        .anyRequest().denyAll()
                );


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
