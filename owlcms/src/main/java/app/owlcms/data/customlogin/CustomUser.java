package app.owlcms.data.customlogin;

import app.owlcms.data.AbstractEntity;
import app.owlcms.data.athlete.Athlete;

import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.Column;

@Entity
public class CustomUser extends AbstractEntity {

    @Column(unique = true)
    private String username;
    private String password;
    private CustomRole role;
    private String activationCode;
    private boolean active;

    private static CustomUser customuser;

    @OneToOne(mappedBy = "customuser", fetch = FetchType.EAGER)
    private Athlete athlete;

    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CustomUser() {
    }

    public CustomUser(String username, String password, CustomRole role) {
        this.username = username;
        this.role = role;
        this.password = passwordEncoder.encode(password);
        this.activationCode = RandomStringUtils.randomAlphanumeric(32);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CustomRole getRole() {
        return role;
    }

    public void setRole(CustomRole role) {
        this.role = role;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static CustomUser getCurrent() {
        return customuser;
    }

    public static void setCurrent(CustomUser c) {
        customuser = c;
    }
}
