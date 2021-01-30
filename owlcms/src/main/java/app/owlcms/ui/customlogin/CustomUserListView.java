package app.owlcms.ui.customlogin;

import java.util.List;
import java.util.Collection;

import com.vaadin.flow.router.Route;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.crudui.crud.CrudListener;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.BoxSizing;

import app.owlcms.ui.shared.OwlcmsRouterLayout;
import app.owlcms.data.customlogin.CustomUserRepository;
import app.owlcms.data.customlogin.CustomUser;
import app.owlcms.data.customlogin.CustomRole;
import app.owlcms.ui.crudui.OwlcmsCrudGrid;
import app.owlcms.ui.crudui.OwlcmsCrudFormFactory;
import app.owlcms.ui.crudui.OwlcmsGridLayout;
import app.owlcms.ui.shared.OwlcmsContent;

@Route(value = "customuserlist", layout = OwlcmsRouterLayout.class)
@SuppressWarnings("serial")
public class CustomUserListView extends VerticalLayout implements CrudListener<CustomUser>, OwlcmsContent {

    private final static Logger logger = (Logger) LoggerFactory.getLogger(CustomUserListView.class);

    private OwlcmsCrudFormFactory<CustomUser> crudFormFactory;
    private OwlcmsRouterLayout routerLayout;

    private ComboBox<CustomRole> customRoleFilter = new ComboBox<>();

    public CustomUserListView() {
        addClassName("user-list-view");
        setSizeFull();
        
        setAlignItems(Alignment.CENTER);

        Grid<CustomUser> grid = new Grid<>(CustomUser.class, false);
        grid.addColumn("username").setHeader("Username");
        grid.addColumn("role").setHeader("Role");
        
        OwlcmsCrudGrid<CustomUser> crudGrid = new OwlcmsCrudGrid<>(CustomUser.class, new OwlcmsGridLayout(CustomUser.class),
                crudFormFactory, grid);
        crudGrid.setCrudListener(this);
        crudGrid.setClickRowToUpdate(true);

        customRoleFilter.setPlaceholder("Role");
        customRoleFilter.setItems(CustomRole.findAll());
        customRoleFilter.setClearButtonVisible(true);
        customRoleFilter.addValueChangeListener(e -> {
            crudGrid.refreshGrid();
        });
        customRoleFilter.setWidth("10em");
        crudGrid.getCrudLayout().addFilterComponent(customRoleFilter);

        this.setBoxSizing(BoxSizing.BORDER_BOX);
        this.setSizeFull();
        this.add(crudGrid);
    }

    @Override
    public CustomUser add(CustomUser customUser) {
        crudFormFactory.add(customUser);
        return customUser;
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Preparation_Registration");
    }

    @Override
    public void setRouterLayout(OwlcmsRouterLayout routerLayout) {
        this.routerLayout = routerLayout;
    }

    @Override
    public CustomUser update(CustomUser customuser) {
        return crudFormFactory.update(customuser);
    }

    @Override
    public OwlcmsRouterLayout getRouterLayout() {
        return routerLayout;
    }

    @Override
    public void delete(CustomUser customUser) {
        crudFormFactory.delete(customUser);
        return;
    }

    @Override
    public Collection<CustomUser> findAll() {
        List<CustomUser> all = CustomUserRepository.findFiltered(customRoleFilter.getValue());
        return all;
    }
}
