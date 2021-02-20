package app.owlcms.ui.customlogin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

import ch.qos.logback.classic.Logger;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import app.owlcms.data.customlogin.CustomRole;
import app.owlcms.data.customlogin.AuthService;

@WebServlet("/customregisterapi")
@SuppressWarnings("serial")
public class CustomRegisterApi extends HttpServlet {

    Logger logger = (Logger) LoggerFactory.getLogger(CustomRegisterApi.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // get makes no sense on this URL. Standard says there shouldn't be a 405 on a get,
        // but "disallowed" is what makes most sense as a return code.
        resp.sendError(405);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String authorizationToken = "";
        String username = "";
        String password = "";
        CustomRole role = CustomRole.valueOf("PUBLIC");

        try{
            String body = req.getReader().lines().collect(Collectors.joining());
            JSONObject jsonObject = new JSONObject(body);
            username = jsonObject.getString("username");
            password = jsonObject.getString("password");
            role = CustomRole.valueOf(jsonObject.getString("role").toUpperCase());
            authorizationToken = req.getHeader("Authorization");
        }
        catch (Exception e){
            resp.setStatus(422);
            resp.getWriter().print("Invalid request body");
            return;
        }

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(authorizationToken) || 
                StringUtils.isEmpty(password)){
            resp.setStatus(422);
            resp.getWriter().print("Username, role, password or authorization token cannot be empty");
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

        String respMessage = "";
        try{
            respMessage = CustomRegisterView.registerValidation(
                                username, password, password, role);
            if (respMessage != null){
                throw new Exception(respMessage);
            }
            AuthService.register(username, password, role);
        }
        catch (Exception e){
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
            return;
        }

        resp.setStatus(200);
        resp.getWriter().print("User Registered successfully.");
        return;
    }
}