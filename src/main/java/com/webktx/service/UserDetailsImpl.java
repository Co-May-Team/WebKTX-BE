package com.webktx.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.webktx.model.OptionModel;
import com.webktx.model.RoleDetailModel;
import com.webktx.model.UserModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsImpl.class);

    private Integer id;
    private String username;
    private String email;
    private String password;
    private String avatar;
    private String fullname;
    private RoleDetailModel roles;
    private Collection<? extends GrantedAuthority> authorities;
	

	public static UserDetailsImpl build(UserModel userModel) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		RoleDetailModel roleDetailModel = null;
		try {
			roleDetailModel = userModel.getRole();
			for (OptionModel op : roleDetailModel.getOptions()) {
				authorities.add(new SimpleGrantedAuthority(op.getName()));
			}

			LOGGER.info(authorities.toString());
		} catch (Exception e) {
			LOGGER.error("Have error at build(): ", e);
		}

		return new UserDetailsImpl(userModel.getId(), userModel.getUsername(), userModel.getEmail(), userModel.getPassword(),userModel.getAvatar(),
				userModel.getFullName(),
				roleDetailModel, authorities);
	}

	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    

	@Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

}
