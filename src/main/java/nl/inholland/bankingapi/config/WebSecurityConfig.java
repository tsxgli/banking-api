package nl.inholland.bankingapi.config;

import nl.inholland.bankingapi.filter.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
//sets up the authentication and authorization mechanisms, defines security rules, and registers various filters
public class WebSecurityConfig extends WebSecurityConfiguration {

    private final JwtTokenFilter jwtTokenFilter;

    public WebSecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // We need to do this to allow POST requests
        httpSecurity.csrf().disable();
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.cors();

        httpSecurity.authorizeHttpRequests()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
//                .requestMatchers(("/users/**")).permitAll()
                .requestMatchers(("/users")).permitAll()
                .requestMatchers(("/auth/**")).permitAll()
                .requestMatchers(("/transactions")).permitAll()
                .anyRequest().authenticated();

        httpSecurity.headers().frameOptions().disable();

        // We ensure our own filter is executed before the framework runs its own authentication filter code
        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
