package com.vladimir.ppm.domain;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @ManyToMany(mappedBy = "users")
    private final Set<Group> groups = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private final Set<String> allowedIps = new HashSet<>();

    @OneToOne
    private PwdGenSettings pwdGenSettings;

    public User() {}

    public User(String login, String password, UserStatus status) {
        this.login = login;
        this.password = password;
        this.status = status;
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

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setPwdGenSettings(PwdGenSettings pwdGenSettings) {
        this.pwdGenSettings = pwdGenSettings;
    }

    public boolean isEnabled() {
        return status == UserStatus.ENABLED;
    }
}
