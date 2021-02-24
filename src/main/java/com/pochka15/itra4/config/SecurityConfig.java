package com.pochka15.itra4.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final DataSource datasource;
    private final AuthenticationSuccessHandler authHandler;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(DataSource datasource,
                          AuthenticationSuccessHandler authHandler,
                          PasswordEncoder passwordEncoder) {
        this.datasource = datasource;
        this.authHandler = authHandler;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/css/**", "/js/**");
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security.authorizeRequests()
                .antMatchers("/register").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(authHandler)
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    /**
     * Configure user authentication using the jdbc dataSource
     *
     * @param auth expected to be provided by Spring by default
     * @throws Exception when couldn't authenticate a user
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(datasource)
                .passwordEncoder(passwordEncoder)
                .usersByUsernameQuery("select name, password, is_enabled from user where name=?")
                .authoritiesByUsernameQuery("select u.name, ur.roles from user u inner join user_role ur on u.id = ur.user_id where u.name=?");
    }

}
