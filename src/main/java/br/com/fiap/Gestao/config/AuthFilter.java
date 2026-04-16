package br.com.fiap.Gestao.config;

import br.com.fiap.Gestao.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter{

    @Autowired
    private TokenService tokenService;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/v1/login")
                || path.startsWith("/api/v1/usuarios")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var header = request.getHeader("Authorization");
        if(header == null){
            filterChain.doFilter(request, response);
            return;
        }

        if(!header.startsWith("Bearer ")){
            response.setStatus(401);
            response.getWriter().write("""
                {"message": "Header deve iniciar com Bearer"}
            """);
            return;
        }

        var jwt = header.replace("Bearer ", "");

        var user = tokenService.getUsuarioFromToken(jwt);

        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);

    }

}
