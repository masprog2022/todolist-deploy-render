package com.masprogtech.task.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.masprogtech.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    private final IUserRepository userRepository;

    public FilterTaskAuth(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var servletPath = request.getServletPath();

        if(servletPath.startsWith("/api/tasks/")){

            // Pegar a autentiticação (usuário e senha)
            var authorization = request.getHeader("Authorization");

            var authEncoded = authorization.substring("Basic".length()).trim();

            byte[] authDecode = Base64.getDecoder().decode(authEncoded);

            var authString =  new String(authDecode);

            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            System.out.println("Authorization");
            System.out.println(username);
            System.out.println(password);

            // validar usuario

            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401);
            }else{

                var passwordVerify =  BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

                if(passwordVerify.verified){
                    request.setAttribute("userId", user.getId());
                    filterChain.doFilter(request, response);
                }else{
                    response.sendError(401);
                }
            }
        }else{
            filterChain.doFilter(request, response);
        }


    }
}
