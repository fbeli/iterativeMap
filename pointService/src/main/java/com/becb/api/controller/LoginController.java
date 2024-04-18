package com.becb.api.controller;

import com.becb.api.dto.LoginDto;
import com.becb.api.dto.LoginResponse;
import com.becb.api.exception.UsuarioAlreadyExistsException;
import com.becb.api.security.AuthSecurity;
import com.becb.api.service.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
//@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class LoginController   {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	AuthSecurity authSecurity;

	@Autowired
	AuthorizationService authorizationService;

	@PreAuthorize("hasAuthority('GUIDE')")
	@GetMapping("/hello")
//	@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
	@ResponseBody
	public String hello() {

		return "Hello, "+authSecurity.getName();
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/autorizado")
//	@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
	@ResponseBody
	public String autorizado() {

		return "autorizado, "+authSecurity.getName();
	}

	@GetMapping("/config")
	@ResponseBody
	public String config() {
		return "{ \"nome\": \"Fulano de Tal\", \"idade\": 30, \"endereco\": \"Rua Exemplo, 123\", \"telefone\": \"(00) 1234-5678\" }";
	}

	@PostMapping("/login")
	@ResponseBody
	public LoginResponse login(@RequestBody LoginDto loginDto, HttpServletResponse response)  {

		logger.info("endpoint {}",  authorizationService.toString());

		LoginResponse loginResponse = authorizationService.login(loginDto);
		if(loginResponse.getStatus() != HttpServletResponse.SC_OK){
			response.setStatus(loginResponse.getStatus());
			response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, PUT, POST");
			response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
			response.setHeader("Access-Control-Allow-Credentials", "true");
		}

		return loginResponse;

	}



	@PostMapping("/signin")
	@ResponseBody
	public LoginResponse signin(@RequestBody LoginDto loginDto, HttpServletResponse response)  {

		String id;
		LoginResponse loginResponse = new LoginResponse();

		try {
			id = authorizationService.adicionarUsuario(loginDto);
			if(id == null){
				return internalServerError(response, "Erro to add user.");
			}

			loginResponse = authorizationService.login(loginDto);
		}catch (UsuarioAlreadyExistsException e){
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			loginResponse.setError(e.getMessage());
			loginResponse.setStatus(HttpServletResponse.SC_CONFLICT);

		} catch (Exception e){
			loginResponse =  internalServerError(response, e.getMessage());
		}

		return loginResponse;
	}

	private LoginResponse internalServerError(HttpServletResponse response, String exception ){
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		LoginResponse loginResponse = new LoginResponse();
		loginResponse.setError(exception);
		loginResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return loginResponse;
	}
}