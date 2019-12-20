package app.controller;

import app.auth.TokenProvider;
import app.data.ResponseMessage;
import app.data.UserCredentials;
import app.services.UserService;
import app.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users/")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
	@PostMapping(value = "/register")
	public ResponseEntity<ResponseMessage> createUser(@RequestBody User newUser) {
		if (newUser.getUsername() == null || newUser.getPassword() == null ||
				newUser.getPassword().trim().equals("") || newUser.getUsername().trim().equals("")) {
			logger.error("username or pass is null");
			return new ResponseEntity<>(new ResponseMessage("Username or password is null"), HttpStatus.BAD_REQUEST);
		}

		if (userService.find(newUser.getUsername()) != null) {
			logger.error("username Already exist " + newUser.getUsername());
			return new ResponseEntity<>(new ResponseMessage("User already exists"), HttpStatus.CONFLICT);
		}
		newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
		userService.save(newUser);

		return new ResponseEntity<>(new ResponseMessage("User successfully created"), HttpStatus.OK);
	}

	@CrossOrigin
	@PostMapping("/login")
	public ResponseEntity<ResponseMessage> user(@RequestBody UserCredentials data) {
		if (data.getUsername() == null || data.getPassword() == null) {
			logger.error("username or pass is null");
			return new ResponseEntity<>(new ResponseMessage("Username or password is null"), HttpStatus.BAD_REQUEST);
		}

		try {
			String username = data.getUsername();
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
			String token = tokenProvider.createToken(username);
			return new ResponseEntity<>(new ResponseMessage(token), HttpStatus.OK);
		} catch (AuthenticationException e) {
			return new ResponseEntity<>(new ResponseMessage("Wrong user or password"), HttpStatus.UNAUTHORIZED);
		}
	}

	@CrossOrigin
	@PostMapping(value = "/logout")
	public ResponseEntity<ResponseMessage> logout(Principal user) {
		try {
			userService.invalidateToken(user.getName());
			return new ResponseEntity<>(new ResponseMessage("logout successful"), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}


	
	
}
