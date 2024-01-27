package com.becb.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthSecurity {




        public Authentication getAuthentication() {
            return SecurityContextHolder.getContext().getAuthentication();
        }

        public Long getUsuarioId() {
            Jwt jwt = (Jwt) getAuthentication().getPrincipal();

            return jwt.getClaim("usuario_id");
        }
    public String getName() {
        Jwt jwt = (Jwt) getAuthentication().getPrincipal();

        return jwt.getClaim("nome_completo");
    }


}
