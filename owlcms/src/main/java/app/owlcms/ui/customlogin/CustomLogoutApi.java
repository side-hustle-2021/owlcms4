package app.owlcms.ui.customlogin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@WebServlet("/customlogout")
@SuppressWarnings("serial")
public class CustomLogoutApi extends HttpServlet {

    Logger logger = (Logger) LoggerFactory.getLogger(CustomLogoutApi.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(true);
        SecurityContext authenticationContext = (SecurityContext) session.getAttribute(
                                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        
        if (authenticationContext != null){
            authenticationContext = null;
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, authenticationContext);
        }

        resp.addHeader("Access-Control-Allow-Origin", System.getenv("ACCESS_ALLOW_ORIGIN"));
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        resp.getWriter().print("User logged out");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // get makes no sense on this URL. Standard says there shouldn't be a 405 on a get,
        // but "disallowed" is what makes most sense as a return code.
        resp.sendError(405);
    }
}
