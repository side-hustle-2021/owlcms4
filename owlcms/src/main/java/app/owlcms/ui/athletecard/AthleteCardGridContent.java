/***
 * Copyright (c) 2009-2020 Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("Non-Profit OSL" 3.0)
 * License text at https://github.com/jflamy/owlcms4/blob/master/LICENSE.txt
 */

package app.owlcms.ui.athletecard;

import app.owlcms.data.athlete.Athlete;
import app.owlcms.ui.crudui.OwlcmsCrudFormFactory;
import app.owlcms.ui.shared.AthleteGridContent;

/**
 * Class AthleteGridContent.
 *
 * Initialization order is - content class is created - wrapping app layout is created if not present - this content is
 * inserted in the app layout slot
 *
 */
@SuppressWarnings("serial")
public abstract class AthleteCardGridContent extends AthleteGridContent {
    private AthleteCardDedicatedFormFactory athleteEditingFormFactory;

    /**
     * Define the form used to edit a given athlete.
     *
     * @return the form factory that will create the actual form on demand
     */
    @Override
    protected OwlcmsCrudFormFactory<Athlete> createFormFactory() {
        athleteEditingFormFactory = new AthleteCardDedicatedFormFactory(Athlete.class, this);
        return athleteEditingFormFactory;
    }
}
