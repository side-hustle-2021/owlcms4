package app.owlcms.ui.athletecard;

import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.athlete.RuleViolation;
import app.owlcms.data.athlete.RuleViolationException;
import app.owlcms.init.OwlcmsSession;

public class AthleteCardRules {

    public static boolean validateWobRule(String requestedWeight, Athlete editingAthlete){
        OwlcmsSession.withFop((fop) -> {
            Athlete curAthlete = fop.getCurAthlete();
            int curWeight = fop.getCurWeight();
            if (
                (Athlete.zeroIfInvalid(requestedWeight) < curWeight) && 
                (!(editingAthlete.getUsername().equals(curAthlete.getUsername())
                ))
            ) {
                RuleViolationException ruleWobViolated = null;
                ruleWobViolated = RuleViolation.wobViolated(
                    requestedWeight, Integer.toString(curWeight)
                );
                throw ruleWobViolated;
            }
        });
        return true;
    }
    
}
