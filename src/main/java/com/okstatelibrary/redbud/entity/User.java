package com.okstatelibrary.redbud.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "user")
public class User implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false, updatable = false)
	private Long userId;

	private String username;

	private String password;

	private String firstName;

	private String lastName;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	private boolean enabled;

	private boolean deleted;

	private int roleId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User{" + "userId=" + userId + ", username='" + username + '\'' + ", password='" + password + '\''
				+ ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", email='" + email + '\''
				+ '}';
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = new HashSet<>();

		// userRoles.forEach(ur -> authorities.add(new
		// Authority(ur.getRole().getName())));

		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Transient
	@ElementCollection(targetClass = Integer.class)
	private List<Integer> authorized;

	@Transient
	public List<Integer> getAuthorized() {

		List<Integer> authorizedList = new ArrayList<Integer>();

		// 1 - Execution
		// 2 - Report
		// 3 - Report Module - Inventor Loans

		if (this.roleId == 1) // ADMIN
		{
			Integer[] sourceArray = { 1, 2, 3 };
			authorizedList = Arrays.asList(sourceArray);

		} else if (this.roleId == 2) // STAFF
		{
			Integer[] sourceArray = { 2 };
			authorizedList = Arrays.asList(sourceArray);
		} else if (this.roleId == 3) // OKS-OSU-STAFF
		{
			Integer[] sourceArray = { 2, 3 };
			authorizedList = Arrays.asList(sourceArray);
		}

		return authorizedList;
	}

	public void setAuthorized(List<Integer> authorized) {
		this.authorized = authorized;
	}

}