package app.config;

import app.auth.TokenProvider;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configurable
@EnableWebSecurity
public class WebConfig extends WebSecurityConfigurerAdapter {

	private final TokenProvider tokenProvider;

	public WebConfig(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	//включаем cors-запросы
	@Bean
	public WebMvcConfigurer corsConfigurer() {
	    return new WebMvcConfigurerAdapter() {
	        @Override
	        public void addCorsMappings(CorsRegistry registry) {
	            registry.addMapping("/**").allowedOrigins("http://localhost:4200");
	          
	        }
	    };
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				// Выключем стандартную аутентификацию спринга логин:пароль с каждым запросом
				.httpBasic().disable()
				.csrf().disable()
				//выключаем сессию
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				//для запросов get и post с url /api/points/* требуется аутентификация
				.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/api/points/**").authenticated()
				.antMatchers(HttpMethod.POST, "/api/points/**").authenticated()
				//для остальных запросов аутентификация не требуется
				.anyRequest().permitAll()
				.and()
				//применяем систему аутентификации
				.apply(new SecurityConfigurer(tokenProvider));
	}


}
