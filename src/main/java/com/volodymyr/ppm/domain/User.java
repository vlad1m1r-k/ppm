package com.volodymyr.ppm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String password;
    private String tfaCode;
    private Date lastTfaDate;
    
    @Enumerated(EnumType.STRING)
    private UserTfaStatus tfaStatus = UserTfaStatus.DISABLED;
    
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean changePwdOnNextLogon = false;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @ManyToMany(mappedBy = "users")
    private final Set<Group> groups = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private final Set<String> allowedIps = new HashSet<>();

    @OneToOne
    private PwdGenSettings pwdGenSettings;
    
    @OneToOne
    private UserAuthData authData;

    public User() {}

    public User(String login, String password, UserStatus status, Boolean changePwdOnNextLogon) {
        this.login = login;
        this.password = password;
        this.status = status;
        this.changePwdOnNextLogon = changePwdOnNextLogon;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
    
    public Boolean isHaveToChangePwd() {
		return changePwdOnNextLogon;
	}

	public Set<Group> getGroups() {
        return groups;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Set<String> getAllowedIps() {
        return allowedIps;
    }

    public PwdGenSettings getPwdGenSettings() {
        return pwdGenSettings;
    }
    
    public Date getLastTfaDate() {
    	return lastTfaDate;
    }
    
    public String getTfaCode() {
    	return tfaCode;
    }
    
    public UserTfaStatus getTfaStatus() {
    	return tfaStatus;
    }
    
    public UserAuthData getUserAuthData() {
    	return authData == null ? new UserAuthData() : authData;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setChangePwdOnNextLogon(Boolean changePwdOnNextLogon) {
		this.changePwdOnNextLogon = changePwdOnNextLogon;
	}

	public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setPwdGenSettings(PwdGenSettings pwdGenSettings) {
        this.pwdGenSettings = pwdGenSettings;
    }
    
    public void setLastTfaDate(Date lastTfaDate) {
    	this.lastTfaDate = lastTfaDate;
    }
    
    public void setTfaCode(String tfaCode) {
    	this.tfaCode = tfaCode;
    }
    
    public void setTfaStatus(UserTfaStatus tfaStatus) {
    	this.tfaStatus = tfaStatus;
    }
    
    public void setUserAuthData(UserAuthData authData) {
    	this.authData = authData;
    }

    public boolean isEnabled() {
        return status == UserStatus.ENABLED;
    }
    
    public boolean isTfaEnabled() {
		return tfaStatus != UserTfaStatus.DISABLED;
	}
    
    public boolean isTfaSetup() {
    	return tfaStatus == UserTfaStatus.INPROGRESS;
    }
}
