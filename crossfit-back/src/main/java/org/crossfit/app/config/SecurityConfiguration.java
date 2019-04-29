package org.crossfit.app.config;

import javax.inject.Inject;

import org.crossfit.app.security.AccessCardAuthenticationFilter;
import org.crossfit.app.security.AjaxAuthenticationFailureHandler;
import org.crossfit.app.security.AjaxAuthenticationSuccessHandler;
import org.crossfit.app.security.AjaxLogoutSuccessHandler;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.security.Http401UnauthorizedEntryPoint;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Inject
    private Environment env;

    @Inject
    private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

    @Inject
    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Inject
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

    @Inject
    private Http401UnauthorizedEntryPoint authenticationEntryPoint;

    @Inject
    private UserDetailsService userDetailsService;

    @Inject
    private RememberMeServices rememberMeServices;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
	        .antMatchers("/index.html")
	        .antMatchers("/scripts/**/*.{js,html}")
            .antMatchers("/bower_components/**")
            .antMatchers("/i18n/**")
            .antMatchers("/front-app/**")
            .antMatchers("/assets/**");
    }
    
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // you USUALLY want this
        // likely you should limit this to specific origins
        config.addAllowedOrigin("*"); 
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
           //.ignoringAntMatchers("/websocket/**", "/card/**")
        //.and()
           // .addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
        .and()
            .rememberMe()
            .rememberMeServices(rememberMeServices)
            .rememberMeParameter("remember-me")
            .key(env.getProperty("security.rememberme.key"))
        .and()
            .formLogin()
            .loginProcessingUrl("/api/authentication")
            .successHandler(ajaxAuthenticationSuccessHandler)
            .failureHandler(ajaxAuthenticationFailureHandler)
            .usernameParameter("j_username")
            .passwordParameter("j_password")
            .permitAll()
        .and()
            .logout()
            .logoutUrl("/api/logout")
            .logoutSuccessHandler(ajaxLogoutSuccessHandler)
            .deleteCookies("JSESSIONID")
            .permitAll()
        .and()
            .headers()
            .frameOptions()
            .disable()
        .and()
        	.addFilterBefore(
        			new AccessCardAuthenticationFilter(env.getProperty("security.access.card.token")), 
        			BasicAuthenticationFilter.class)
        	.authorizeRequests()
        	.antMatchers("/card/**").hasAuthority(AuthoritiesConstants.ACCESS_CARD)
        .and()
            .authorizeRequests()
            .antMatchers("/recover/**").permitAll()
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/api/version").authenticated()
            .antMatchers("/api/account/**").authenticated()
            .antMatchers("/api/boxs/current").authenticated()
            .antMatchers("/api/boxs/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api/membershipTypes/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api/bills/**", "/api/bills.csv").hasAnyAuthority(AuthoritiesConstants.COMPTABLE, AuthoritiesConstants.ADMIN)
            
            .antMatchers(HttpMethod.GET, "/api/contractmodels/**").hasAnyAuthority(AuthoritiesConstants.COMPTABLE, AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)
            .antMatchers(HttpMethod.GET, "/api/members/**").hasAnyAuthority(AuthoritiesConstants.COMPTABLE, AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)
            .antMatchers(HttpMethod.GET, "/api/products").hasAnyAuthority(AuthoritiesConstants.COMPTABLE, AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)
            .antMatchers(HttpMethod.GET, "/api/memberships").hasAnyAuthority(AuthoritiesConstants.COMPTABLE, AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)
            
            .antMatchers(HttpMethod.GET, "/api/bookings/**").authenticated()
            .antMatchers(HttpMethod.POST, "/api/bookings/**").authenticated()
            .antMatchers(HttpMethod.DELETE, "/api/bookings/**").authenticated()
            .antMatchers(HttpMethod.GET, "/api/timeSlots/**").authenticated()
            .antMatchers("/api/wod/**").authenticated()
            .antMatchers("/api/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)
            .antMatchers("/private/planning/**", "/ws/**", "/api/events/**").hasAnyAuthority(AuthoritiesConstants.COACH,AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)
            .antMatchers("/private/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)
            .antMatchers("/protected/**").authenticated()
            .antMatchers("/public/**").permitAll()
            .antMatchers("/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/stats/**").hasAnyAuthority(AuthoritiesConstants.DIRECTOR, AuthoritiesConstants.ADMIN);

    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
