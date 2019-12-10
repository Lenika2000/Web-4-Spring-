package app.services;

import app.repositories.UserRepository;
import app.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return find(username);
	}

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User save(User user) {
		return userRepository.saveAndFlush(user);
	}

	public User find(String userName) {
		return userRepository.findOneByUsername(userName);
	}

	public void invalidateToken(String username) {
		User user = userRepository.findOneByUsername(username);
		user.setAuthToken(null);
		userRepository.save(user);
	}

	public User findByAuthToken(String token) {
		return userRepository.findByAuthTokenEquals(token);
	}

}
