package app.owlcms.ui.athletecard;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.Route;
import ch.qos.logback.classic.Logger;

import org.apache.poi.ss.formula.functions.T;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.crudui.crud.FindAllCrudOperationListener;

import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Location;

import java.util.Collections;

import com.google.common.eventbus.Subscribe;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;

import app.owlcms.ui.shared.OwlcmsRouterLayout;
import app.owlcms.uievents.UIEvent;
import app.owlcms.utils.LoggerUtils;
import app.owlcms.ui.crudui.OwlcmsCrudFormFactory;
import app.owlcms.ui.crudui.OwlcmsCrudGrid;
import app.owlcms.ui.crudui.OwlcmsGridLayout;
import app.owlcms.ui.lifting.AthleteCardFormFactory;
import app.owlcms.ui.lifting.UIEventProcessor;
import app.owlcms.ui.shared.AthleteCrudGrid;
import app.owlcms.ui.shared.AthleteGridContent;
import app.owlcms.ui.shared.BaseNavigationContent;
import app.owlcms.ui.shared.IAthleteEditing;
import app.owlcms.components.NavigationPage;
import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.athlete.AthleteRepository;
import app.owlcms.data.customlogin.CustomUser;
import app.owlcms.data.customlogin.CustomUserRepository;

@Route(value = "athletecard", layout = OwlcmsRouterLayout.class)
@SuppressWarnings("serial")
public class AthleteCardView extends BaseNavigationContent implements NavigationPage, HasDynamicTitle, IAthleteEditing {

    private final static Logger logger = (Logger) LoggerFactory.getLogger(AthleteCardView.class);
    protected OwlcmsGridLayout owlcmsGridLayout;
    protected AthleteCrudGrid crudGrid;

    public AthleteCardView() {
        addClassName("athlete-card-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username = "";
        if (principal instanceof UserDetails) {
          username = ((UserDetails)principal).getUsername();
        } else {
          username = principal.toString();
        }

        owlcmsGridLayout = new OwlcmsGridLayout(Athlete.class);
        IAthleteEditing origin = null;
        // AthleteGridContent athleteGridContent = new AthleteGridContent();
        OwlcmsCrudFormFactory<Athlete> owlcmsCrudFormFactory = new AthleteCardFormFactory(Athlete.class, this);
        Grid<Athlete> grid = new Grid<>(Athlete.class, false);

        crudGrid = new AthleteCrudGrid(Athlete.class, owlcmsGridLayout, owlcmsCrudFormFactory, grid);

        CustomUser customuser = CustomUserRepository.getByUsername(username);
        Athlete athlete = AthleteRepository.getAthleteByUsername(customuser);

        if (athlete == null){
            add (
                new H2("No Athlete found with username: " + username)
            );
        }
        else {
            crudGrid.showAthleteCard(athlete);
        }
    }

    @Subscribe
    public void slaveUpdateGrid(UIEvent.LiftingOrderUpdated e) {
        if (crudGrid == null) {
            return;
        }
        logger.debug("{} {}", e.getOrigin(), LoggerUtils.whereFrom());
        UIEventProcessor.uiAccess(crudGrid, uiEventBus, e, () -> {
            crudGrid.refreshGrid();
        });
    }

    /**
     * @see app.owlcms.ui.shared.IAthleteEditing#closeDialog()
     */
    @Override
    public void closeDialog() {
        owlcmsGridLayout.hideForm();
        crudGrid.getGrid().asSingleSelect().clear();
    }

    @Override
    public OwlcmsCrudGrid<?> getEditingGrid() {
        return crudGrid;
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
        return "Athlete Card";
    }

    /**
     * @see app.owlcms.ui.shared.BaseNavigationContent#getTitle()
     */
    @Override
    protected String getTitle() {
        return "Athlete Card";
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
