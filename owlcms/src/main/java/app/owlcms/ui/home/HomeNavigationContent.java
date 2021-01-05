/***
 * Copyright (c) 2009-2020 Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("Non-Profit OSL" 3.0)
 * License text at https://github.com/jflamy/owlcms4/blob/master/LICENSE.txt
 */
package app.owlcms.ui.home;

import org.slf4j.LoggerFactory;

import com.github.appreciated.app.layout.component.applayout.AbstractLeftAppLayoutBase;
import com.github.appreciated.css.grid.GridLayoutComponent.AutoFlow;
import com.github.appreciated.css.grid.GridLayoutComponent.Overflow;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.css.grid.sizes.Length;
import com.github.appreciated.css.grid.sizes.MinMax;
import com.github.appreciated.css.grid.sizes.Repeat;
import com.github.appreciated.layout.FlexibleGridLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.Route;

import app.owlcms.components.NavigationPage;
import app.owlcms.i18n.Translator;
import app.owlcms.ui.displayselection.DisplayNavigationContent;
import app.owlcms.ui.lifting.LiftingNavigationContent;
import app.owlcms.ui.preparation.PreparationNavigationContent;
import app.owlcms.ui.results.ResultsNavigationContent;
import app.owlcms.ui.shared.BaseNavigationContent;
import app.owlcms.ui.shared.OwlcmsRouterLayout;
import app.owlcms.utils.DebugUtils;
import app.owlcms.utils.IPInterfaceUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * The Class HomeNavigationContent.
 */
/**
 * @author owlcms
 *
 */
@SuppressWarnings("serial")
@Route(value = "home", layout = OwlcmsRouterLayout.class)
public class HomeNavigationContent extends BaseNavigationContent implements NavigationPage, HasDynamicTitle {

    final private static Logger logger = (Logger) LoggerFactory.getLogger(HomeNavigationContent.class);
    static {
        logger.setLevel(Level.INFO);
    }

    /**
     * Navigation crudGrid.
     *
     * @param items the items
     * @return the flexible crudGrid layout
     */
    public static FlexibleGridLayout navigationGrid(Component... items) {
        FlexibleGridLayout layout = new FlexibleGridLayout();
        layout.withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("300px"), new Flex(1)))
                .withAutoRows(new Length("1fr")).withItems(items).withGap(new Length("2vmin"))
                .withOverflow(Overflow.AUTO).withAutoFlow(AutoFlow.ROW).withMargin(false).withPadding(true)
                .withSpacing(false);
        layout.setSizeUndefined();
        layout.setWidth("80%");
        layout.setBoxSizing(BoxSizing.BORDER_BOX);
        return layout;
    }

    String PREPARE_COMPETITION = Translator.translate("PrepareCompetition");
    String RUN_LIFTING_GROUP = Translator.translate("RunLiftingGroup");
    String START_DISPLAYS = Translator.translate("StartDisplays");
    String RESULT_DOCUMENTS = Translator.translate("ResultDocuments");

    String INFO = Translator.translate("About");

    /**
     * Instantiates a new main navigation content.
     */
    public HomeNavigationContent() {

        VerticalLayout intro = buildIntro();

        Button prepare = new Button(PREPARE_COMPETITION,
                buttonClickEvent -> UI.getCurrent().navigate(PreparationNavigationContent.class));
        Button displays = new Button(START_DISPLAYS,
                buttonClickEvent -> UI.getCurrent().navigate(DisplayNavigationContent.class));
        Button lifting = new Button(RUN_LIFTING_GROUP,
                buttonClickEvent -> UI.getCurrent().navigate(LiftingNavigationContent.class));
        Button documents = new Button(RESULT_DOCUMENTS,
                buttonClickEvent -> UI.getCurrent().navigate(ResultsNavigationContent.class));
        FlexibleGridLayout grid = HomeNavigationContent.navigationGrid(prepare, lifting, displays, documents);

        fillH(intro, this);
        fillH(grid, this);

        DebugUtils.gc();
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
     * @see app.owlcms.ui.parameters.QueryParameterReader#isIgnoreFopFromURL()
     */
    @Override
    public boolean isIgnoreFopFromURL() {
        return true;
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

    /**
     * @see app.owlcms.ui.shared.BaseNavigationContent#createTopBarFopField(java.lang.String, java.lang.String)
     */
    @Override
    protected HorizontalLayout createTopBarFopField(String label, String placeHolder) {
        return null;
    }

    /**
     * @see app.owlcms.ui.shared.BaseNavigationContent#createTopBarGroupField(java.lang.String, java.lang.String)
     */
    @Override
    protected HorizontalLayout createTopBarGroupField(String label, String placeHolder) {
        return null;
    }

    /**
     * @see app.owlcms.ui.shared.BaseNavigationContent#getTitle()
     */
    @Override
    protected String getTitle() {
        return getTranslation("OWLCMS_Top");
    }

    private VerticalLayout buildIntro() {
        VerticalLayout intro = new VerticalLayout();
        IPInterfaceUtils urlFinder = new IPInterfaceUtils();
        addP(intro, getTranslation("SystemURL"));
        for (String url : urlFinder.getRecommended()) {
            intro.add(new Div(new Anchor(url, url)));
        }
        for (String url : urlFinder.getWired()) {
            intro.add(new Div(new Anchor(url, url), new Label(getTranslation("Wired"))));
        }
        for (String url : urlFinder.getWireless()) {
            intro.add(new Div(new Anchor(url, url), new Label(getTranslation("Wireless"))));
        }
        for (String url : urlFinder.getLocalUrl()) {
            intro.add(new Div(new Anchor(url, url), new Label(getTranslation("LocalComputer"))));
        }
        intro.add(new Div());
        intro.add(new Hr());
        addP(intro,
                getTranslation("LeftMenuNavigate")
                        + getTranslation("PrepareCompatition_description", PREPARE_COMPETITION)
                        + getTranslation("RunLiftingGroup_description", RUN_LIFTING_GROUP)
                        + getTranslation("StartDisplays_description", START_DISPLAYS)
                        + getTranslation("CompetitionDocuments_description", RESULT_DOCUMENTS)
                        + getTranslation("SeparateLaptops"));
        intro.getStyle().set("margin-bottom", "-1em");
        return intro;
    }

}