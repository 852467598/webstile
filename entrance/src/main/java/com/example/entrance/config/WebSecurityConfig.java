package com.example.entrance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.ELRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ProjectName: webstile
 * @Package: com.example.entrance.config
 * @ClassName: webSecurityConfig
 * @Author: 陈凯
 * @Description: ${description}
 * @Date: 2019/6/20 10:26
 * @Version: 1.0
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public WebSecurityConfig(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring().antMatchers("/favicon.png**", "/script/**", "/img/**", "/css/**", "/js/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests().antMatchers("/login**").permitAll()
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login").successForwardUrl("/")
                .and().rememberMe()
                .and().csrf().disable().headers().frameOptions().sameOrigin()
                .and().exceptionHandling().authenticationEntryPoint(new CustomLoginUrlAuthenticationEntryPoint("/login"))
                .and().sessionManagement().invalidSessionUrl("/login");

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    public static class CustomLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

        private static final RequestMatcher REQUEST_MATCHER = new ELRequestMatcher("hasHeader('X-Requested-With','XMLHttpRequest')");

        CustomLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
            super(loginFormUrl);
        }

        static boolean isAjaxRequest(HttpServletRequest request) {
            return REQUEST_MATCHER.matches(request);
        }

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            if (isAjaxRequest(request)) {
                SecurityContext context = SecurityContextHolder.getContext();
                Authentication authentication = context.getAuthentication();
                if (authentication == null) {//未登录
                    response.addHeader("sessionstatus", "timeout");
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                }
            } else {
                super.commence(request, response, authException);
            }
        }
    }
}
