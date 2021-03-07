/***
 * Copyright (c) 2009-2020 Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("Non-Profit OSL" 3.0)
 * License text at https://github.com/jflamy/owlcms4/blob/master/LICENSE.txt
 */
package app.owlcms.ui.shared;

import org.slf4j.LoggerFactory;
import org.vaadin.crudui.crud.CrudOperation;

import com.vaadin.flow.component.grid.Grid;

import app.owlcms.data.athlete.Athlete;
import app.owlcms.init.OwlcmsSession;
import app.owlcms.ui.crudui.OwlcmsCrudFormFactory;
import app.owlcms.ui.crudui.OwlcmsCrudGrid;
import app.owlcms.ui.crudui.OwlcmsGridLayout;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * This class attempts to locate the selected athlete from the in the lifting order.
 *
 * <p>
 * This is a workaround for two issues:
 * <li>Why does getValue() return a different object than that in the lifting order (initialization issue?)
 * <li>Why do we have to get the same object anyway (spurious comparison with == instead of getId() or .equals)
 * </p>
 *
 * @author Jean-François Lamy
 */
@SuppressWarnings("serial")
public class AthleteCrudGrid extends OwlcmsCrudGrid<Athlete> {

    final private Logger logger = (Logger) LoggerFactory.getLogger(AthleteCrudGrid.class);
    {
        logger.setLevel(Level.INFO);
    }

    Athlete match = null;

    public AthleteCrudGrid(Class<Athlete> domainType, OwlcmsGridLayout crudLayout,
            OwlcmsCrudFormFactory<Athlete> owlcmsCrudFormFactory, Grid<Athlete> grid) {
        super(domainType, crudLayout, owlcmsCrudFormFactory, grid);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.vaadin.crudui.crud.impl.GridCrud#updateButtonClicked()
     */
    @Override
    protected void updateButtonClicked() {
        Athlete domainObject = grid.asSingleSelect().getValue();
        Athlete sought = domainObject;
        // if available we want the exact object from the lifting order and not a copy
        OwlcmsSession.withFop((fop) -> {
            Long id = sought.getId();
            found: for (Athlete a : fop.getLiftingOrder()) {
                logger.debug("checking for {} : {} {}", id, a, a.getId());
                if (a.getId().equals(id)) {
                    match = a;
                    break found;
                }
            }
        });
        logger.trace("domainObject = {} {}", (domainObject != match ? "!!!!" : ""), domainObject, match);
        if (match != null) {
            domainObject = match;
        }

        // show both an update and a delete button.
        this.showForm(CrudOperation.UPDATE, domainObject, false, savedMessage, null);
    }

    public void showAthleteCard(Athlete athlete){
        this.showForm(CrudOperation.UPDATE, athlete, false, "Item saved", null);
    }
}
