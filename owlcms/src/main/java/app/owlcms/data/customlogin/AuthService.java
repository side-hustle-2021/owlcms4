package app.owlcms.data.customlogin;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@SuppressWarnings("serial")
public class AuthService implements UserDetailsService{

    public class AuthException extends Exception {
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        CustomUser customuser = CustomUserRepository.getByUsername(username);
        if (customuser == null) {
            throw new UsernameNotFoundException(username);
        }
        UserDetails user = User.withUsername(customuser.getUsername())
                            .password(customuser.getPassword())
                            .authorities(customuser.getRole().toString()).build();

        return user;
    }

    public static CustomUser register(String username, String password, CustomRole role) {
        CustomUser user = CustomUserRepository.save(new CustomUser(username, password, role));
        System.out.println("http://localhost:8080/customactivate?code=" + user.getActivationCode());
        return user;
    }

    public static void activate(String activationCode) throws Exception {
        CustomUser user = CustomUserRepository.getByActivationCode(activationCode);
        if (user != null) {
            user.setActive(true);
            CustomUserRepository.save(user);
        } else {
            throw new Exception();
        }
    }
}

