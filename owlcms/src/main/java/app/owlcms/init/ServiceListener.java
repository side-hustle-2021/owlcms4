/***
 * Copyright (c) 2009-2020 Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("Non-Profit OSL" 3.0)
 * License text at https://github.com/jflamy/owlcms4/blob/master/LICENSE.txt
 */
package app.owlcms.init;

import org.slf4j.LoggerFactory;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;

import app.owlcms.utils.LoggerUtils;
import ch.qos.logback.classic.Logger;

import app.owlcms.security.SecurityUtils;
import com.vaadin.flow.router.BeforeEnterEvent;
import app.owlcms.ui.login.CustomLoginView;
import com.vaadin.flow.component.UI;

/**
 * Automatic configuration at startup of the various listeners for sessions, etc.
 *
 * The fully qualified name of this class (app.owlcms.ui.uiEvents.ServiceListener) must appear on single line in file
 * src/main/resources/META-INF/services/com.vaadin.flow.server.VaadinServiceInitListener
 *
 * @author owlcms
 *
 */
@SuppressWarnings("serial")
public class ServiceListener implements VaadinServiceInitListener {
    private static Logger logger = (Logger) LoggerFactory.getLogger(ServiceListener.class);

    /**
     * Instantiates a new service listener.
     */
    public ServiceListener() {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.flow.server.VaadinServiceInitListener#serviceInit(com.vaadin.flow. server.ServiceInitEvent)
     */
    @Override
    public void serviceInit(ServiceInitEvent event) {
        logger.debug("Vaadin Service Startup Configuration. {} {}", event.toString(), LoggerUtils.whereFrom());
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });

        event.getSource().addSessionInitListener(sessionInitEvent -> {
            sessionInit(sessionInitEvent);
        });
    }

    /**
     * Reroutes the user if they're not authorized to access the view.
     *
     * @param event
     *            before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {
        if (!CustomLoginView.class.equals(event.getNavigationTarget())
            && !SecurityUtils.isUserLoggedIn()) {
            event.rerouteTo(CustomLoginView.class);
        }
    }

    // session init listener will be called whenever a VaadinSession is created
    // (which holds the http session and all the browser pages (UIs) under
    // the same session.
    private void sessionInit(SessionInitEvent sessionInitEvent) {
        VaadinSession session = sessionInitEvent.getSession();

        // override noisy Jetty error handler.
        session.setErrorHandler(new JettyErrorHandler());

//		// ignore browser-specific settings based on configuration
//		session.setLocale(Locale.ENGLISH);
//
    }

}
