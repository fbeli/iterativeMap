package com.becb.api.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

                // Para todos: .authorizeRequests().anyRequest().authenticated()
                /* Para o médodo:
                .authorizeRequests()
                .antMatchers("/hello").hasAuthority("HELLO")
                .antMatchers("api/*").hasAuthority("HELLO")
                .antMatchers("/config").hasAuthority("CONFIG")
                .and()*/
                .csrf().disable()// para colocar a auth no método, ver /hello
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/aprovar/*").permitAll()
                    .antMatchers(HttpMethod.GET, "/bloquear/*").permitAll()
                    .antMatchers(HttpMethod.GET, "/user/*").permitAll()
                .and()
                .cors().and()
                .oauth2ResourceServer()
                    .jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var authorities = jwt.getClaimAsStringList("authorities");
            if (authorities == null) {
                authorities = java.util.Collections.emptyList();
            }
            return authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return jwtAuthenticationConverter;
    }
    //@Bean
    /**
     * Quano não se usa chave pública
     */
    /*public JwtDecoder jwtDecoder() {
        *//*quando não se usa chave pública*//*
        var secretKey = new SecretKeySpec("BECBmysecretkey0987654321123456789".getBytes(),"HmacSHA256");


        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }*/



}