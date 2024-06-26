package org.jio.orchidbe.configs;

import org.jio.orchidbe.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebMvc
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    //private final CustomOAuth2UserService oauth2UserService;
    @Value("${api.prefix}")
    private String apiPrefix;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)

                .csrf(AbstractHttpConfigurer::disable)

                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/auth/**", apiPrefix),
                                    //healthcheck
                                    String.format("%s/healthcheck/**", apiPrefix),

                                    // pay
                                    String.format("%s/payments/**", apiPrefix),

                                    String.format("%s/actuator/**", apiPrefix),
                                    //swagger
                                    //"/v3/api-docs",
                                    //"/v3/api-docs/**",
                                    "/api-docs",
                                    "/api-docs/**",
                                    "/swagger-resources",
                                    "/swagger-resources/**",
                                    "/configuration/ui",
                                    "/configuration/security",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/webjars/swagger-ui/**",
                                    "/swagger-ui/index.html"


                            )
                            .permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/categories/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/auctions/**", apiPrefix)).permitAll()
//                            .requestMatchers(GET,
//                                    String.format("%s/products/images/*", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/orders/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/order_details/**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/products**", apiPrefix)).authenticated()
                            .requestMatchers(GET,
                                    String.format("%s/hello**", apiPrefix)).authenticated()
                            .requestMatchers(GET,
                                    String.format("%s/products/**", apiPrefix)).permitAll()
//                            .anyRequest().authenticated();
                            .anyRequest().authenticated();
                });


            //http.securityMatcher(String.valueOf(EndpointRequest.toAnyEndpoint()));
        return http.build();
    }
}
