package app.owlcms.ui.customlogin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import app.owlcms.data.customlogin.CustomUserRepository;
import app.owlcms.data.customlogin.CustomUser;

@WebServlet("/customauthenticateapi")
@SuppressWarnings("serial")
public class CustomAuthenticateApi extends HttpServlet{

    Logger logger = (Logger) LoggerFactory.getLogger(CustomAuthenticateApi.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // get makes no sense on this URL. Standard says there shouldn't be a 405 on a get,
        // but "disallowed" is what makes most sense as a return code.
        resp.sendError(405);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String authorizationToken = "";
        String username = "";
        String password = "";

        resp.addHeader("Access-Control-Allow-Origin", System.getenv("ACCESS_ALLOW_ORIGIN"));
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        
        try{
            username = req.getParameter("username");
            password = req.getParameter("password");
            authorizationToken = req.getParameter("Authorization");
        }
        catch (Exception e){
            resp.setStatus(422);
            resp.getWriter().print("Invalid request body");
            return;
        }

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) 
                || StringUtils.isEmpty(authorizationToken)){
            resp.setStatus(422);
            resp.getWriter().print("Username, password or authorization token cannot be empty");
            return;
        }

        String configuredApiToken = System.getenv("REGISTER_API_TOKEN");
        if (StringUtils.isEmpty(configuredApiToken)){
            resp.setStatus(500);
            resp.getWriter().print("Missing configurations. Please contact system admin");
            return;
        }

        if (!authorizationToken.equals(configuredApiToken)){
            resp.setStatus(417);
            resp.getWriter().print("Authorization Failed");
            return;
        }

        CustomUser customuser = CustomUserRepository.getByUsername(username);

        if (customuser == null){
            resp.setStatus(417);
            resp.getWriter().print("User not found");
            return;
        }
        
        try{

            HttpSession session = req.getSession(true);
            SecurityContext authenticationContext = (SecurityContext) session.getAttribute(
                                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            
            if (authenticationContext != null){
                Authentication existingAuthentication = authenticationContext.getAuthentication();
                if (existingAuthentication.isAuthenticated()){
                    resp.getWriter().print("User already logged in");
                    return;
                }
            }

            UserDetails customuserdetails = User.withUsername(username)
                            .password(password)
                            .authorities(customuser.getRole().toString()).build();

            Authentication authentication =  new UsernamePasswordAuthenticationToken(customuserdetails, 
                            password, customuserdetails.getAuthorities());
            logger.debug("Logging in with {}", authentication.getPrincipal());

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
            resp.getWriter().print("User "+ username + " successfully logged in.");
            resp.setHeader("Set-Cookie", resp.getHeader("Set-Cookie") + "; SameSite=None; Secure; ");
        }
        catch (Exception e){
            resp.setStatus(500);
            resp.getWriter().print("Unable to authenticate user. Please contact system admin.");
            return;
        }
    }
}
