/***
 * Copyright (c) 2009-2020 Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("Non-Profit OSL" 3.0)
 * License text at https://github.com/jflamy/owlcms4/blob/master/LICENSE.txt
 */
package app.owlcms.ui.shared;

import java.util.function.Consumer;

import org.slf4j.LoggerFactory;

import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.applayout.AppLayout;
import com.github.appreciated.app.layout.component.applayout.LeftLayouts;
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.items.LeftClickableItem;
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;

import app.owlcms.i18n.Translator;
import app.owlcms.init.OwlcmsFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import app.owlcms.security.SecurityUtils;

/**
 * OwlcmsRouterLayout.
 */
@SuppressWarnings({ "serial", "rawtypes" })
@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@CssImport(value = "./styles/shared-styles.css")
@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
@JsModule("@polymer/iron-icon/iron-icon.js")
@JsModule("@polymer/iron-icons/iron-icons.js")
@JsModule("@polymer/iron-icons/av-icons.js")
@JsModule("@polymer/iron-icons/hardware-icons.js")
@JsModule("@polymer/iron-icons/maps-icons.js")
@JsModule("@polymer/iron-icons/social-icons.js")
@JsModule("@polymer/iron-icons/places-icons.js")

public class OwlcmsRouterLayout extends AppLayoutRouterLayout implements PageConfigurator {

    /**
     * The Class BehaviourSelector.
     */
    class BehaviourSelector extends Dialog {
        /**
         * Instantiates a new behaviour selector.
         *
         * @param current  the current
         * @param consumer the consumer
         */
        @SuppressWarnings("unchecked")
        public BehaviourSelector(Class<? extends AppLayout> current, Consumer<Class<? extends AppLayout>> consumer) {
            VerticalLayout layout = new VerticalLayout();
            add(layout);
            RadioButtonGroup<Class<? extends AppLayout>> group = new RadioButtonGroup<>();
            group.getStyle().set("display", "flex");
            group.getStyle().set("flexDirection", "column");
            group.setItems(LeftLayouts.Left.class, LeftLayouts.LeftOverlay.class, LeftLayouts.LeftResponsive.class,
                    LeftLayouts.LeftHybrid.class, LeftLayouts.LeftHybridSmall.class,
                    LeftLayouts.LeftResponsiveHybrid.class, LeftLayouts.LeftResponsiveOverlay.class,
                    LeftLayouts.LeftResponsiveSmall.class);
            group.setValue(current);
            layout.add(group);
            group.addValueChangeListener(singleSelectionEvent -> {
                consumer.accept(singleSelectionEvent.getValue());
                close();
            });
        }
    }

    final private Logger logger = (Logger) LoggerFactory.getLogger(OwlcmsRouterLayout.class);

    {
        logger.setLevel(Level.INFO);
    }

    private Class<? extends AppLayout> variant;

    private HasElement layoutComponentContent;
    String PREPARE_COMPETITION = Translator.translate("PrepareCompetition");
    String RUN_LIFTING_GROUP = Translator.translate("RunLiftingGroup");
    String START_DISPLAYS = Translator.translate("StartDisplays");
    String RESULT_DOCUMENTS = Translator.translate("ResultDocuments");
    String INFO = Translator.translate("About");
    String PREFERENCES = Translator.translate("Preferences");

    String DOCUMENTATION = Translator.translate("Documentation_Menu");

    @SuppressWarnings("unchecked")
    public OwlcmsRouterLayout() {
        try {
            OwlcmsFactory.getInitializationLatch().await();
        } catch (InterruptedException e) {
        }
        init(getLayoutConfiguration(variant));
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        settings.addInlineWithContents("<link rel=\"icon\" href=\"./frontend/images/owlcms.ico\">",
                InitialPageSettings.WrapMode.NONE);
    }

    /**
     * @return the layoutComponentContent
     */
    public HasElement getLayoutComponentContent() {
        return layoutComponentContent;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.github.appreciated.app.layout.router.AppLayoutRouterLayoutBase#
     * showRouterLayoutContent(com.vaadin.flow.component.HasElement)
     */
    @Override
    public void showRouterLayoutContent(HasElement content) {
        logger.debug("showRouterLayoutContent {}", content.getClass().getSimpleName());
        ((AppLayoutAware) content).setRouterLayout(this);
        super.showRouterLayoutContent(content);
        this.setLayoutComponentContent(content);
    }

    /**
     * @param variant
     * @return
     */
    protected AppLayout getLayoutConfiguration(Class<? extends AppLayout> variant) {
        if (variant == null) {
            variant = LeftLayouts.LeftResponsive.class;
        }

        String userrole = SecurityUtils.loggerInUserRole();

        LeftAppMenuBuilder leftmenulayout;
        //Roles: ADMIN, ATHLETE, REFEREE, ORGANIZER
        if (userrole.equals("ADMIN")){
            leftmenulayout = getAdminLayoutMenu();
        }
        else if(userrole.equals("PUBLIC")){
            leftmenulayout = getPublicLayoutMenu();
        }
        else if(userrole.equals("ATHLETE")){
            leftmenulayout = getAthleteLayoutMenu();
        }
        else if(userrole.equals("REFEREE")){
            leftmenulayout = getRefereeLayoutMenu();
        }
        else if(userrole.equals("ORGANIZER")){
            leftmenulayout = getOrganizerLayoutMenu();
        }
        else{
            leftmenulayout = LeftAppMenuBuilder.get().add();
        }
        
        AppLayout appLayout = AppLayoutBuilder.get(variant).withTitle(getTranslation("OWLCMS_Top"))
                .withIcon("/frontend/images/logo.png").withAppBar(AppBarBuilder.get().build())
                .withAppMenu(leftmenulayout.build())
                .build();

        return appLayout;
    }

    protected LeftAppMenuBuilder getAdminLayoutMenu(){
        return LeftAppMenuBuilder.get()
        .add(new LeftClickableItem("Home", new Icon("icons:home"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/home','_self')")))
        .add(new LeftClickableItem("Prepare Competition", new Icon("social:group-add"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/preparation','_self')")))
        .add(new LeftClickableItem("Run Lifting Group", new Icon("places:fitness-center"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/lifting','_self')")))
        .add(new LeftClickableItem("Start Displays", new Icon("hardware:desktop-windows"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/displays','_self')")))
        .add(new LeftClickableItem("Result Documents", new Icon("maps:local-printshop"),
            clickEvent -> UI.getCurrent().getPage()
                .executeJs("window.open('/results','_self')")))
        .add(new LeftClickableItem("About", new Icon("icons:info-outline"),
            clickEvent -> UI.getCurrent().getPage()
                .executeJs("window.open('/info','_self')")))
        .add(new LeftClickableItem("Register User", new Icon("social:person-add"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/customregister','_self')")))
        .add(new LeftClickableItem("User List", new Icon("social:people"),
            clickEvent -> UI.getCurrent().getPage()
                .executeJs("window.open('/customuserlist','_self')")))
        .add(new LeftClickableItem("Logout", new Icon("icons:exit-to-app"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/logout','_self')")));
    }

    protected LeftAppMenuBuilder getAthleteLayoutMenu(){
        return LeftAppMenuBuilder.get()
        .add(new LeftClickableItem("Start Displays", new Icon("hardware:desktop-windows"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/displays','_self')")))
        .add(new LeftClickableItem("Athlete Card", new Icon("hardware:keyboard"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/athletecard','_self')")))
        .add(new LeftClickableItem("Logout", new Icon("icons:exit-to-app"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/logout','_self')")));
    }

    protected LeftAppMenuBuilder getRefereeLayoutMenu(){
        return LeftAppMenuBuilder.get()
        .add(new LeftClickableItem("Start Displays", new Icon("hardware:desktop-windows"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/displays','_self')")))
        .add(new LeftClickableItem("Logout", new Icon("icons:exit-to-app"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/logout','_self')")));
    }

    protected LeftAppMenuBuilder getPublicLayoutMenu(){
        return LeftAppMenuBuilder.get()
        .add(new LeftClickableItem("Start Displays", new Icon("hardware:desktop-windows"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/displays','_self')")))
        .add(new LeftClickableItem("Logout", new Icon("icons:exit-to-app"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/logout','_self')")));
    }

    protected LeftAppMenuBuilder getOrganizerLayoutMenu(){
        return LeftAppMenuBuilder.get()
        .add(new LeftClickableItem("Prepare Competition", new Icon("social:group-add"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/preparation','_self')")))
        .add(new LeftClickableItem("Run Lifting Group", new Icon("places:fitness-center"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/lifting','_self')")))
        .add(new LeftClickableItem("Start Displays", new Icon("hardware:desktop-windows"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/displays','_self')")))
        .add(new LeftClickableItem("Result Documents", new Icon("maps:local-printshop"),
            clickEvent -> UI.getCurrent().getPage()
                .executeJs("window.open('/results','_self')")))
        .add(new LeftClickableItem("Logout", new Icon("icons:exit-to-app"),
            clickEvent -> UI.getCurrent().getPage()
                    .executeJs("window.open('/logout','_self')")));
    }
    
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        // crude workaround -- randomly getting "dark" due to multiple themes detected in app.
        getElement().executeJs("document.querySelector('html').setAttribute('theme', 'light');");
        super.onAttach(attachEvent);
    }

    @SuppressWarnings("unused")
    private void openModeSelector(Class<? extends AppLayout> variant) {
        new BehaviourSelector(variant, this::setDrawerVariant).open();
    }

    // .add(new LeftClickableItem(DOCUMENTATION, VaadinIcon.COG.create(),
    // clickEvent -> openModeSelector(this.variant))
    @SuppressWarnings("unchecked")
    private void setDrawerVariant(Class<? extends AppLayout> variant) {
        this.variant = variant;
        init(getLayoutConfiguration(variant));
    }

    private void setLayoutComponentContent(HasElement layoutContent) {
        this.layoutComponentContent = layoutContent;
    }
}
