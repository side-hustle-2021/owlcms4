/***
 * Copyright (c) 2009-2020 Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("Non-Profit OSL" 3.0)
 * License text at https://github.com/jflamy/owlcms4/blob/master/LICENSE.txt
 */
package app.owlcms.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import app.owlcms.data.agegroup.AgeGroup;
import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.athlete.Gender;
import app.owlcms.data.category.AgeDivision;
import app.owlcms.data.category.Category;

public class AthleteTest {

    private static Athlete athlete;

    @Before
    public void setupTest() {
        athlete = new Athlete();
        athlete.setLastName("Strong");
        athlete.setFirstName("Paul");
        athlete.setGender(Gender.M);
        athlete.setBodyWeight(68.5);
        athlete.setSnatch1Declaration("60");
        athlete.setCleanJerk1Declaration("80");
        athlete.setYearOfBirth(1900);
        Category registrationCategory = new Category(0L, 67.0, 73.0, Gender.M, true, 0, 0, 348,
                new AgeGroup("SR", true, 15, 999, Gender.M, AgeDivision.IWF));
        athlete.setCategory(registrationCategory);
    }

    /**
     * Test method for {@link org.concordiainternational.competition.data.Athlete#getTotal()}.
     */
    @Test
    public void testGetTotalBombOut() {
        athlete.setSnatch1ActualLift("-60");
        athlete.setSnatch2ActualLift("-60");
        athlete.setSnatch3ActualLift("-60");
        athlete.setCleanJerk1ActualLift("-80");
        athlete.setCleanJerk2ActualLift("-80");
        athlete.setCleanJerk3ActualLift("-80");
        assertEquals("total with full bomb out", 0, (long) athlete.getTotal());
    }

    /**
     * Test method for {@link org.concordiainternational.competition.data.Athlete#getTotal()}.
     */
    @Test
    public void testGetTotalHappyPath() {
        athlete.setSnatch1ActualLift("60");
        athlete.setSnatch2ActualLift("61");
        athlete.setSnatch3ActualLift("62");
        athlete.setCleanJerk1ActualLift("80");
        athlete.setCleanJerk2ActualLift("81");
        athlete.setCleanJerk3ActualLift("82");
        assertEquals("total with all values", 144, (long) athlete.getTotal());
        assertEquals("robi score", 53.33D, athlete.getRobi(), 0.005);
    }

    /**
     * Test method for {@link org.concordiainternational.competition.data.Athlete#getTotal()}.
     */
    @Test
    public void testGetTotalNoCleanJerkData() {
        athlete.setSnatch1ActualLift("60");
        athlete.setSnatch2ActualLift("61");
        athlete.setSnatch3ActualLift("62");
        athlete.setCleanJerk1ActualLift(null);
        athlete.setCleanJerk2ActualLift(null);
        athlete.setCleanJerk3ActualLift(null);
        assertEquals("total with no clean and jerk results", 0, (long) athlete.getTotal());
    }

    /**
     * Test method for {@link org.concordiainternational.competition.data.Athlete#getTotal()}.
     */
    @Test
    public void testGetTotalNoData() {
        assertEquals("total without any results", 0, (long) athlete.getTotal());
    }

    /**
     * Test method for {@link org.concordiainternational.competition.data.Athlete#getTotal()}.
     */
    @Test
    public void testGetTotalNoSnatchData() {
        athlete.setSnatch1ActualLift(null);
        athlete.setSnatch2ActualLift(null);
        athlete.setSnatch3ActualLift(null);
        athlete.setCleanJerk1ActualLift("80");
        athlete.setCleanJerk2ActualLift("81");
        athlete.setCleanJerk3ActualLift("82");
        assertEquals("total with no snatch results", 0L, (long) athlete.getTotal());
    }

    /**
     * Test method for {@link org.concordiainternational.competition.data.Athlete#getTotal()}.
     */
    @Test
    public void testGetTotalPartialData() {
        athlete.setSnatch1ActualLift("60");
        athlete.setSnatch2ActualLift("");
        athlete.setSnatch3ActualLift(null);
        athlete.setCleanJerk1ActualLift("-80");
        athlete.setCleanJerk2ActualLift("-");
        athlete.setCleanJerk3ActualLift(null);
        assertEquals("total with failed clean and jerk results", 0, (long) athlete.getTotal());
    }

    /**
     * Test method for {@link org.concordiainternational.competition.data.Athlete#getTotal()}.
     */
    @Test
    public void testGetTotalSnatchBombOut() {
        athlete.setSnatch1ActualLift("-60");
        athlete.setSnatch2ActualLift("-60");
        athlete.setSnatch3ActualLift("-60");
        athlete.setCleanJerk1ActualLift("80");
        athlete.setCleanJerk2ActualLift("81");
        athlete.setCleanJerk3ActualLift("-");
        assertEquals("total with snatch bomb out", 0, (long) athlete.getTotal());
    }

}
