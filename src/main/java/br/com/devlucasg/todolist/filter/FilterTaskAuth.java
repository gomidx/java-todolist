package br.com.devlucasg.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.devlucasg.todolist.user.IUserRepository;
import br.com.devlucasg.todolist.user.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    private UserModel user;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
            var servletPath = request.getServletPath();

            if (servletPath.startsWith("/tasks/")) {
                var auth = request.getHeader("Authorization");
    
                if (!this.isAuthorized(auth)) {
                    response.sendError(401);
                } else {
                    request.setAttribute("userId", this.user.getUserId());
                }
            }

            filterChain.doFilter(request, response);
    }

    private Boolean isAuthorized(String auth) {
        var credentials = this.decodeAuth(auth);

        var userIsValid = this.validateUser(credentials);

        if (!userIsValid) {
            return false;
        }

        return true;
    }

    private String[] decodeAuth(String basicAuth) {
        byte[] authBase64 = Base64.getDecoder().decode(basicAuth.substring("Basic ".length()).trim());

        var authString = new String(authBase64);

        return authString.split(":");
    }

    private Boolean validateUser(String[] credentials) {
        String username = credentials[0];        
        String password = credentials[1];

        var user = this.userRepository.findByUsername(username);

        if (user == null) {
            return false;
        }

        var verifyPassword = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

        if (!verifyPassword.verified) {
            return false;
        }

        this.user = user;

        return true;
    }
    
}
