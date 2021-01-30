package app.owlcms.ui.customlogin;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.component.UI;
import java.util.List;
import com.vaadin.flow.component.grid.Grid;

import app.owlcms.ui.shared.OwlcmsRouterLayout;
import app.owlcms.ui.shared.BaseNavigationContent;
import app.owlcms.components.NavigationPage;
import app.owlcms.data.customlogin.CustomUserRepository;
import app.owlcms.data.customlogin.CustomUser;


@Route(value = "customuserlist", layout = OwlcmsRouterLayout.class)
@SuppressWarnings("serial")
public class CustomUserListView extends BaseNavigationContent implements NavigationPage, HasDynamicTitle {

    private final static Logger logger = (Logger) LoggerFactory.getLogger(CustomUserListView.class);

    public CustomUserListView() {

        addClassName("register-view");
        setSizeFull();
        
        setAlignItems(Alignment.CENTER);
        
        List<CustomUser> customUserList = CustomUserRepository.findAll();
        Grid<CustomUser> grid = new Grid<>(CustomUser.class);
        grid.setItems(customUserList);

        grid.removeColumnByKey("id");
        grid.removeColumnByKey("password");
        grid.removeColumnByKey("activationCode");
        grid.setColumns("username", "role", "active");

        add(
                new H2("User list"),
                grid
        );
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
