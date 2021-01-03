package app.owlcms.data.customlogin;

import app.owlcms.data.AbstractEntity;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.Entity;

@Entity
public class CustomUser extends AbstractEntity {

    private String username;
    private String passwordSalt;
    private String passwordHash;
    private CustomRole role;
    private String activationCode;
    private boolean active;

    private static CustomUser customuser;

    public CustomUser() {
    }

    public CustomUser(String username, String password, CustomRole role) {
        this.username = username;
        this.role = role;
        this.passwordSalt = RandomStringUtils.random(32);
        this.passwordHash = DigestUtils.sha1Hex(password + passwordSalt);
        this.activationCode = RandomStringUtils.randomAlphanumeric(32);
    }

    public boolean checkPassword(String password) {
        return DigestUtils.sha1Hex(password + passwordSalt).equals(passwordHash);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
        if (customuser == null) {
            customuser = CustomUserRepository.findAll().get(0);
        }
        return customuser;
    }

    public static void setCurrent(CustomUser c) {
        customuser = c;
    }
}
