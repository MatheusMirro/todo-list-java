package br.com.matheusmirro.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.matheusmirro.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();

        if (servletPath.startsWith("/tasks/")) {
            // Receives the "Authorization" header along with the Base64-encoded
            // authorization
            var authorization = request.getHeader("Authorization");
            // Remove the "Basic" prefix and any leading/trailing spaces
            var authEncoded = authorization.substring("Basic".length()).trim();
            // Converts it into a decoded byte array
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
            // Transform the decoded bytes into a plain String
            var authString = new String(authDecoded);
            // Creates an array of strings by splitting on ':' characters
            String[] credentials = authString.split(":");
            // Get the username at index [0] in the array
            String username = credentials[0];
            // Get the password at index [1] in the array
            String password = credentials[1];

            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401);
            } else {
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    request.setAttribute("IdUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}

// garantir que quando for passado /tasks 'sem a barra no final ele não autorize
// a passagem da task.'
