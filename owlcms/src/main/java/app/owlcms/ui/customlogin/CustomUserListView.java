package app.owlcms.ui.customlogin;

import java.util.List;
import java.util.Collection;

import com.vaadin.flow.router.Route;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.crudui.crud.CrudListener;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.notification.Notification;

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
    static {
        logger.setLevel(Level.INFO);
    }

    private OwlcmsCrudFormFactory<CustomUser> crudFormFactory;
    private OwlcmsRouterLayout routerLayout;

    private ComboBox<CustomRole> roleFilter = new ComboBox<>();
    private TextField usernameFilter = new TextField();
    private ComboBox<Boolean> activeFilter = new ComboBox<>();

    public CustomUserListView() {
        addClassName("user-list-view");
        setSizeFull();
        setBoxSizing(BoxSizing.BORDER_BOX);
        setAlignItems(Alignment.CENTER);

        Grid<CustomUser> grid = new Grid<>(CustomUser.class, false);

        OwlcmsCrudGrid<CustomUser> crudGrid = new OwlcmsCrudGrid<>(
            CustomUser.class, new OwlcmsGridLayout(CustomUser.class),
            crudFormFactory, grid
        );

        crudGrid.setCrudListener(this);
        crudGrid.setClickRowToUpdate(true);

        grid.addColumn("username").setHeader("Username");
        grid.addColumn("role").setHeader("Role");
        grid.addColumn(
            new ComponentRenderer<>(
                customuser -> {
                    Checkbox checkbox = new Checkbox();
                    checkbox.setValue(customuser.isActive());                            
                    checkbox.addValueChangeListener(
                        event -> updateCustomUserActive(customuser, crudGrid)
                    );
                    return checkbox;
                }
            )
        ).setHeader("Active");
        
        setUsernameFilter(crudGrid);
        setRoleFilter(crudGrid);
        setActiveFilter(crudGrid);

        grid.addColumn(
            new ComponentRenderer<>(
                customuser -> {
                    Button button = new Button(null, VaadinIcon.TRASH.create());
                    button.addClickListener(
                        event -> deleteCustomUser(customuser, crudGrid)
                    );
                    return button;
                }
            )
        ).setHeader("Delete");

        setClearFilters(crudGrid);
        add(crudGrid);
    }

    public void deleteCustomUser(CustomUser customuser, OwlcmsCrudGrid<CustomUser> crudGrid){
        if (customuser.getUsername().toLowerCase().equals("admin")){
            Notification.show("Cannot delete the 'admin' user.");
            return;
        }
        try {
            CustomUserRepository.delete(customuser);
        }
        catch (Exception e){
            Notification.show("Unable to delete username: " + customuser.getUsername());
            Notification.show("Please make sure the user is not mapped to an athlete.");
            Notification.show("If issue persists please contact system admin.");
            return;
        }
        
        crudGrid.refreshGrid();
        Notification.show("Deleted username: " + customuser.getUsername());
    }

    public void updateCustomUserActive(CustomUser customuser, OwlcmsCrudGrid<CustomUser> crudGrid){
        logger.info("Starting update active status for user: "+ customuser.getUsername());
        if (customuser.getUsername().toLowerCase().equals("admin")){
            Notification.show("Cannot change active status for the 'admin' user.");
            crudGrid.refreshGrid();
            return;
        }
        logger.debug("Updating active status for user: " + customuser.getUsername());
        CustomUserRepository.updateActive(customuser);
        logger.debug("Updated active status for user: " + customuser.getUsername());
        Notification.show(customuser.getUsername() + " set Active: " + customuser.isActive());
    }

    public void setRoleFilter(OwlcmsCrudGrid<CustomUser> crudGrid){
        roleFilter.setPlaceholder("Role");
        roleFilter.setItems(CustomRole.findAll());
        roleFilter.setClearButtonVisible(true);
        roleFilter.addValueChangeListener(e -> {
            crudGrid.refreshGrid();
        });
        roleFilter.setWidth("10em");
        crudGrid.getCrudLayout().addFilterComponent(roleFilter);
    }

    public void setUsernameFilter(OwlcmsCrudGrid<CustomUser> crudGrid){
        usernameFilter.setPlaceholder("Username");
        usernameFilter.setClearButtonVisible(true);
        usernameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        usernameFilter.addValueChangeListener(e -> {
            crudGrid.refreshGrid();
        });
        usernameFilter.setWidth("10em");
        crudGrid.getCrudLayout().addFilterComponent(usernameFilter);
    }

    public void setActiveFilter(OwlcmsCrudGrid<CustomUser> crudGrid){
        activeFilter.setPlaceholder("Active");
        activeFilter.setClearButtonVisible(true);
        activeFilter.setItems(Boolean.TRUE, Boolean.FALSE);
        activeFilter.addValueChangeListener(e -> {
            crudGrid.refreshGrid();
        });
        activeFilter.setWidth("10em");
        crudGrid.getCrudLayout().addFilterComponent(activeFilter);
    }

    public void setClearFilters(OwlcmsCrudGrid<CustomUser> crudGrid){
        Button clearFilters = new Button(null, VaadinIcon.ERASER.create());
        clearFilters.addClickListener(event -> {
            usernameFilter.clear();
            roleFilter.clear();
            activeFilter.clear();
        });
        crudGrid.getCrudLayout().addFilterComponent(clearFilters);
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
        List<CustomUser> all = CustomUserRepository.findFiltered(
            usernameFilter.getValue(), roleFilter.getValue(), 
            activeFilter.getValue());
        return all;
    }
}