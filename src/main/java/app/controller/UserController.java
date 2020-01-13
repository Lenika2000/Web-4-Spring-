package app.controller;

import app.authentication.TokenProvider;
import app.data.ResponseMessage;
import app.data.User;
import app.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users/")
public class UserController {

	private final UserService userService;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;

	public UserController(UserService userService, AuthenticationManager authenticationManager, TokenProvider tokenProvider, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}


	@CrossOrigin
	@PostMapping("register")
	public ResponseEntity<ResponseMessage> createUser(@RequestBody app.entities.User newUser) {
		if (newUser.getUsername() == null || newUser.getPassword() == null ||
				newUser.getPassword().trim().equals("") || newUser.getUsername().trim().equals("")) {

			return new ResponseEntity<>(new ResponseMessage("Не найдены логин или пароль"), HttpStatus.BAD_REQUEST);
		}

		if (userService.find(newUser.getUsername()) != null) {

			return new ResponseEntity<>(new ResponseMessage("Пользователь уже существует"), HttpStatus.CONFLICT);
		}
		newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
		userService.save(newUser);

		return new ResponseEntity<>(new ResponseMessage("Пользователь успешно создан"), HttpStatus.OK);
	}

	@CrossOrigin
	@PostMapping("login")
	public ResponseEntity<ResponseMessage> user(@RequestBody User data) {
		if (data.getUsername() == null || data.getPassword() == null) {

			return new ResponseEntity<>(new ResponseMessage("Не найдены логин или пароль"), HttpStatus.BAD_REQUEST);
		}

		try {
			String username = data.getUsername();
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
			String token = tokenProvider.createToken(username);
			return new ResponseEntity<>(new ResponseMessage(token), HttpStatus.OK);
		} catch (AuthenticationException e) {
			return new ResponseEntity<>(new ResponseMessage("Неверный логин или пароль"), HttpStatus.UNAUTHORIZED);
		}
	}

	@CrossOrigin
	@PostMapping(value = "logout")
	public ResponseEntity<ResponseMessage> logout(Principal user) {
		try {
			userService.invalidateToken(user.getName());
			return new ResponseEntity<>(new ResponseMessage("Выход успешно осуществлен"), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}


	
	
}
