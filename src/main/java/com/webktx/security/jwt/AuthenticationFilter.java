package com.webktx.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.webktx.entity.Role;
import com.webktx.entity.User;
import com.webktx.repository.impl.UserDetailsServiceImpl;
import com.webktx.repository.impl.UserRepositoryImpl;
import com.webktx.service.CustomRoleService;

public class AuthenticationFilter extends OncePerRequestFilter {
	@Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    UserRepositoryImpl userRepositoryImpl;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private JSONObject object;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = CustomRoleService.getTokenFromRequest(request);
            UserDetails userDetails = null;
            if (token != null && jwtUtils.validateJwtToken(token)) {
                String username = jwtUtils.getUserNameFromJwtToken(token);
                try {
                	userDetails = userDetailsService.loadUserByUsername(username);
				} catch (Exception e) {
					logger.error("",e);
				}
            }else {
            	// if jwtUtils.validateJwtToken(token) == false => account is google login
            	object = GoogleUtils.parseJwt(token);
            	// If exist: load data
            	// else: add
            	if(!userRepositoryImpl.checkExistingUserByUsername(object.getString("email"))) {
            		User user = new User();
            		user.setUsername(object.getString("email"));
            		user.setEmail(object.getString("email"));
            		user.setFullName(object.getString("name"));
            		Role role = new Role();
            		role.setRoleId(2);
            		user.setRole(role);
            		userRepositoryImpl.add(user);
            	}
            	userDetails = userDetailsService.loadUserByUsername(object.getString("email"));
            }
            // Store user infomation
            if(userDetails!=null) {
            	UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            			userDetails, null, userDetails.getAuthorities());
            	authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            	SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }
}
