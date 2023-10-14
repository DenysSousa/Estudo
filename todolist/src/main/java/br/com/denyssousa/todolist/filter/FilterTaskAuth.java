package br.com.denyssousa.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.denyssousa.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

	private String UserName;
	private String Password;

	@Autowired
	IUserRepository userRepository;

	public void autenticacao(String authorization) {
		// remove o tipo de autenticação, deixando apenas o base64
		var authBase64 = authorization.substring("Basic".length()).trim();

		// Faz o decode do 64 e transorma em bites
		byte[] auth = Base64.getDecoder().decode(authBase64);

		// transforma os bites em string
		var authString = new String(auth);

		// quebra as credenciais em um array
		String[] credentials = authString.split(":");

		UserName = credentials[0];
		Password = credentials[1];
	};

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (!request.getServletPath().contains("/users")) {

			autenticacao(request.getHeader("Authorization"));

			var user = this.userRepository.findByUsername(UserName);

			if (user == null) {
				response.sendError(401);
			} else {
				if (!BCrypt.verifyer().verify(Password.toCharArray(), user.getPassword().toCharArray()).verified) {
					response.sendError(401);
				}
				request.setAttribute("idUser", user.getId());
			}
		}

		filterChain.doFilter(request, response);

	}

}
