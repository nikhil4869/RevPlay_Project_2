package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;


@Configuration
public class SecurityConfig {
	
	private final JwtFilter jwtFilter;

	public SecurityConfig(JwtFilter jwtFilter) {
	    this.jwtFilter = jwtFilter;
	}


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	    http
	        .csrf(csrf -> csrf.disable())
	        .sessionManagement(session ->
	                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/auth/**").permitAll()
	                .requestMatchers("/test-error").permitAll()
<<<<<<< HEAD
	                .requestMatchers("/artist/**").hasRole("ARTIST")
	                .requestMatchers("/user/**").hasRole("USER")
=======

	                // PUBLIC DISCOVERY
	                .requestMatchers(HttpMethod.GET, "/albums/**").permitAll()
	                .requestMatchers(HttpMethod.GET, "/songs/**").permitAll()
	                .requestMatchers(HttpMethod.GET, "/artist/*").permitAll()

	                // SEARCH → USER ONLY
	                .requestMatchers("/search/**").hasRole("USER")

	                

	                // ARTIST ONLY ACTIONS
	                .requestMatchers(HttpMethod.POST, "/albums/**").hasRole("ARTIST")
	                .requestMatchers(HttpMethod.PUT, "/albums/**").hasRole("ARTIST")
	                .requestMatchers(HttpMethod.DELETE, "/albums/**").hasRole("ARTIST")
	                
	             // ARTIST ONLY actions
	                .requestMatchers(org.springframework.http.HttpMethod.POST, "/songs/upload").hasRole("ARTIST")
	                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/songs/**").hasRole("ARTIST")
	                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/songs/**").hasRole("ARTIST")

	                .requestMatchers("/artist/**").hasRole("ARTIST")
	                .requestMatchers("/user/**").hasRole("USER")
	                .requestMatchers("/favorites/**").hasRole("USER")
	                .requestMatchers("/history/**").hasRole("USER")
	                .requestMatchers("/player/**").hasRole("USER")
	                .requestMatchers("/audio/**").permitAll()
	                .requestMatchers("/analytics/**").hasRole("ARTIST")
	                // PUBLIC playlist viewing
	                .requestMatchers(HttpMethod.GET, "/playlists/public/**").permitAll()
	                // USER playlist features
	                .requestMatchers("/playlists/**").hasRole("USER")
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
	                .anyRequest().authenticated()
	        );


	    http.addFilterBefore(jwtFilter,
	            UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
