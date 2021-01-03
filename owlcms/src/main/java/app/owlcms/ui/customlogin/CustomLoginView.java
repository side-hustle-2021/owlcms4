package app.owlcms.ui.customlogin;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.router.RouterLink;

import app.owlcms.data.customlogin.CustomUserRepository;
import app.owlcms.data.customlogin.CustomUser;
import java.util.List;

@Route("login")
@PageTitle("Login | Vaadin CRM")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@SuppressWarnings("serial")
public class CustomLoginView extends VerticalLayout implements BeforeEnterObserver {

    LoginForm login = new LoginForm();

    public CustomLoginView() {
        List<CustomUser> allusers = CustomUserRepository.findAll();
        CustomUser zerouser = allusers.get(0);
        String zerusername = zerouser.getUsername();


        addClassName("login-view");
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        login.setAction("login");

        add(
            new H1("OWLCMS Login"),
            login,
            new RouterLink("Register", CustomRegisterView.class)
        );
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation()
        .getQueryParameters()
        .getParameters()
        .containsKey("error")) {
            login.setError(true);
        }
    }
}
