package org.step.social.network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.step.social.network.configuration.AppProperties;
import org.step.social.network.model.User;
import org.step.social.network.repository.UserRepository;

import java.util.Arrays;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.step")
@EnableTransactionManagement
@EnableJpaAuditing
@EnableConfigurationProperties(value = {AppProperties.class})
public class SocialNetworkApplication {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public SocialNetworkApplication(UserRepository userRepository,
									PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Bean
	public CommandLineRunner setup() {
		return args -> {
			User first = new User();
			first.setUsername("first");
			first.setPassword(passwordEncoder.encode("first"));
			first.setEnabled(true);
			User second = new User();
			second.setUsername("second");
			second.setPassword(passwordEncoder.encode("second"));
			second.setEnabled(true);
			User third = new User();
			third.setUsername("third");
			third.setPassword(passwordEncoder.encode("third"));
			third.setEnabled(true);

			userRepository.saveAll(Arrays.asList(first, second, third));
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkApplication.class, args);
	}

}
