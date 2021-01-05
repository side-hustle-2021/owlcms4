package app.owlcms.ui.home;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.component.UI;
import com.github.appreciated.app.layout.component.applayout.AbstractLeftAppLayoutBase;
import com.vaadin.flow.component.html.Label;

import app.owlcms.ui.shared.OwlcmsRouterLayout;
import app.owlcms.ui.shared.BaseNavigationContent;
import app.owlcms.components.NavigationPage;

@SuppressWarnings("serial")
@Route(value = "", layout = OwlcmsRouterLayout.class)
public class DefaultNavigationContent extends BaseNavigationContent implements NavigationPage, HasDynamicTitle{

    public DefaultNavigationContent(){
        
        addClassName("default-view");
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        add(
            new H1("Welcome to OWLCMS")
        );
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
        return getTranslation("OWLCMS_Home");
    }

    /**
     * @see app.owlcms.ui.shared.BaseNavigationContent#getTitle()
     */
    @Override
    protected String getTitle() {
        return getTranslation("OWLCMS_Top");
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void setLocationUI(UI locationUI) {
        this.locationUI = locationUI;
    }

    /**
     * The left part of the top bar.
     *
     * @see app.owlcms.ui.shared.BaseNavigationContent#configureTopBarTitle(java.lang.String)
     */
    @Override
    protected void configureTopBarTitle(String topBarTitle) {
        AbstractLeftAppLayoutBase appLayout = getAppLayout();
        appLayout.getTitleWrapper().getElement().getStyle().set("flex", "0 1 40em");
        Label label = new Label(getTitle());
        appLayout.setTitleComponent(label);
    }
}
