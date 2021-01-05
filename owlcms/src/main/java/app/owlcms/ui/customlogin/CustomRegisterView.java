package app.owlcms.ui.customlogin;

import app.owlcms.data.customlogin.AuthService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.component.UI;

import app.owlcms.data.customlogin.CustomUser;
import app.owlcms.data.customlogin.CustomRole;
import app.owlcms.data.customlogin.CustomUserRepository;
import app.owlcms.ui.shared.OwlcmsRouterLayout;
import app.owlcms.ui.shared.BaseNavigationContent;
import app.owlcms.components.NavigationPage;

@Route(value = "customregister", layout = OwlcmsRouterLayout.class)
@SuppressWarnings("serial")
public class CustomRegisterView extends BaseNavigationContent implements NavigationPage, HasDynamicTitle {

    private final static Logger logger = (Logger) LoggerFactory.getLogger(CustomRegisterView.class);

    public CustomRegisterView() {

        addClassName("register-view");
        setSizeFull();
        
        setAlignItems(Alignment.CENTER);

        TextField username = new TextField("Username");
        username.setMinLength(1);

        PasswordField password1 = new PasswordField("Password");
        PasswordField password2 = new PasswordField("Confirm password");

        Select<CustomRole> role = new Select<>();
        role.setItems(CustomRole.values());
        role.setLabel("Role");

        add(
                new H2("Register"),
                username,
                role,
                password1,
                password2,
                new Button("Save", event -> register(
                        username.getValue(),
                        password1.getValue(),
                        password2.getValue(),
                        role.getValue()
                ))
        );
    }

    private void register(String username, String password1, String password2, CustomRole role) {

        if (username.trim().isEmpty()) {
            showNotification("Enter a username");
        } else if (role == null) {
            showNotification("Select a Role");
        } else if (password1.isEmpty()) {
            showNotification("Enter a password");
        } else if (!password1.equals(password2)) {
            showNotification("Passwords don't match");
        } else if (CustomUserRepository.getByUsername(username) != null ){
            showNotification("Username already exists");
        } else {
            CustomUser user = AuthService.register(username, password1, role);
            try {
                AuthService.activate(user.getActivationCode());    
            } catch (Exception e) {
                logger.error("Invalid link. User not found");
            }
            showNotification("User Registered successfully.");
        }
    }

    void showNotification(String notificationText){
        Notification.show(notificationText, 3000, Notification.Position.TOP_CENTER);
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public UI getLocationUI() {
        return this.locationUI;
    }

    /**
     * @see com.vaadin.flow.router.HasDynamicTitle#getPageTitle()
     */
    @Override
    public String getPageTitle() {
        return getTranslation("RegisterUser");
    }

    /**
     * @see app.owlcms.ui.shared.BaseNavigationContent#getTitle()
     */
    @Override
    protected String getTitle() {
        return getTranslation("RegisterUser");
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void setLocationUI(UI locationUI) {
        this.locationUI = locationUI;
    }
}
