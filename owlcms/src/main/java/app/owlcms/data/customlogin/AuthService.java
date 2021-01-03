package app.owlcms.data.customlogin;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public class AuthException extends Exception {

    }

    public static void register(String username, String password) {
        CustomUser user = CustomUserRepository.save(new CustomUser(username, password, CustomRole.USER));
        System.out.println("http://localhost:8080/customactivate?code=" + user.getActivationCode());
    }

    public void activate(String activationCode) throws AuthException {
        CustomUser user = CustomUserRepository.getByActivationCode(activationCode);
        if (user != null) {
            user.setActive(true);
            CustomUserRepository.save(user);
        } else {
            throw new AuthException();
        }
    }
}

