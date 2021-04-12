package app.owlcms.ui.athletecard;

import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.athlete.RuleViolation;
import app.owlcms.data.athlete.RuleViolationException;
import app.owlcms.init.OwlcmsSession;

public class AthleteCardRules {

    public static boolean validateWobRule(String requestedWeight){
        OwlcmsSession.withFop((fop) -> {
            int curWeight = fop.getCurWeight();
            if (Athlete.zeroIfInvalid(requestedWeight) < curWeight) {
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
