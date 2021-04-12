package app.owlcms.ui.athletecard;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.vaadin.flow.router.HasDynamicTitle;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.vaadin.flow.component.grid.Grid;

import app.owlcms.ui.shared.OwlcmsRouterLayout;
import app.owlcms.utils.LoggerUtils;
import app.owlcms.ui.crudui.OwlcmsCrudFormFactory;
import app.owlcms.ui.crudui.OwlcmsGridLayout;
import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.athlete.AthleteRepository;
import app.owlcms.data.customlogin.CustomUser;
import app.owlcms.data.customlogin.CustomUserRepository;
import app.owlcms.fieldofplay.FieldOfPlay;
import app.owlcms.init.OwlcmsSession;

@Route(value = "athletecard", layout = OwlcmsRouterLayout.class)
@SuppressWarnings("serial")
public class AthleteCardView extends AthleteCardGridContent implements HasDynamicTitle {

    private final static Logger logger = (Logger) LoggerFactory.getLogger(AthleteCardView.class);
    protected OwlcmsGridLayout owlcmsGridLayout;
    protected AthleteCrudDedicatedGrid crudGrid;
    protected CustomUser customuser;
    protected Athlete athlete;
    protected String username;

    public AthleteCardView() {
        super();
        setTopBarTitle("Athlete Card");

        addClassName("athlete-card-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        
        setupAthleteCrudGrid();
    }

    protected void setupAthleteCrudGrid(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
          this.username = ((UserDetails)principal).getUsername();
        } else {
          this.username = principal.toString();
        }

        owlcmsGridLayout = new OwlcmsGridLayout(Athlete.class);
        OwlcmsCrudFormFactory<Athlete> owlcmsCrudFormFactory = new AthleteCardDedicatedFormFactory(Athlete.class, this);
        Grid<Athlete> grid = new Grid<>(Athlete.class, false);

        this.crudGrid = new AthleteCrudDedicatedGrid(Athlete.class, owlcmsGridLayout, owlcmsCrudFormFactory, grid);

        this.customuser = CustomUserRepository.getByUsername(this.username);
        this.athlete = AthleteRepository.getAthleteByUsername(this.customuser);
    }

    @Override
    public Collection<Athlete> findAll() {
        FieldOfPlay fop = OwlcmsSession.getFop();
        if (fop != null) {
            logger.trace("findAll {} {} {}", fop.getName(), fop.getGroup() == null ? null : fop.getGroup().getName(),
                    LoggerUtils.whereFrom());
            return AthleteRepository.getAthleteListByUser(this.customuser);
        } else {
            // no field of play, no group, empty list
            logger.debug("findAll fop==null");
            return ImmutableList.of();
        }
    }

    @Override
    public String getPageTitle() {
        return "Athlete Card";
    }

    /**
     * @see app.owlcms.ui.shared.AthleteGridContent#announcerButtons(com.vaadin.flow.component.orderedlayout.HorizontalLayout)
     */
    @Override
    protected HorizontalLayout announcerButtons(FlexLayout announcerBar) {
        HorizontalLayout buttons = new HorizontalLayout();
        return buttons;
    }

    /**
     * @see app.owlcms.ui.shared.AthleteGridContent#createTopBar()
     */
    @Override
    protected void createTopBar() {
       
    }
}
