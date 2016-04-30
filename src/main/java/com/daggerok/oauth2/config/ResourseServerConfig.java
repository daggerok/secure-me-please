package com.daggerok.oauth2.config;

import com.daggerok.oauth2.config.userdetails.AppUserDetails;
import com.daggerok.oauth2.config.userdetails.Role;
import com.daggerok.oauth2.config.userdetails.User;
import com.daggerok.oauth2.config.userdetails.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import static java.util.Arrays.asList;

/**
 * Created by mak on 4/30/16.
 */
@Configuration
@EnableResourceServer // protecting this app resources
@EnableAutoConfiguration(exclude = DaoAuthenticationConfigurer.class) // needs to avoid this error:
// BeanCreationException: Error creating bean with name 'resourseServerConfig': Injection of autowired dependencies failed; nested exception is java.lang.IllegalStateException:
// Cannot apply org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer@709f0202 to already built object
public class ResourseServerConfig {

    @Autowired
    @SneakyThrows
    public void authenticationManager(AuthenticationManagerBuilder authenticationManagerBuilder,
                                      UserRepository userRepository) {

        if (userRepository.count() < 1) { // using this user
            userRepository.save(User.of("max", "pass", asList(Role.of("USER"), Role.of("ADMIN"))));
        }
        authenticationManagerBuilder.userDetailsService(userDetailsService(userRepository));
    }

    UserDetailsService userDetailsService(final UserRepository userRepository) {
        return username -> new AppUserDetails(userRepository.findByUsername(username));
    }
}
