package app.owlcms.ui.athletecard;

import java.time.Duration;
import java.time.LocalDateTime;

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

    public static boolean validatePreviousAttempt(int currentAttempt, Athlete editingAthlete){
        OwlcmsSession.withFop((fop) -> {
            Athlete curAthlete = fop.getCurAthlete();

            if ((currentAttempt == 2 && curAthlete.getSnatch1ActualLift().isEmpty()) ||
                (currentAttempt == 3 && curAthlete.getSnatch2ActualLift().isEmpty()) ||
                (currentAttempt == 4 && curAthlete.getSnatch3ActualLift().isEmpty()) ||
                (currentAttempt == 5 && curAthlete.getCleanJerk1ActualLift().isEmpty()) ||
                (currentAttempt == 6 && curAthlete.getCleanJerk2ActualLift().isEmpty())) {
                RuleViolationException ruleDeclarationNotAllowed = null;
                ruleDeclarationNotAllowed = RuleViolation.declarationNotAllowed();
                throw ruleDeclarationNotAllowed;
            }
        });
        return true;
    }

    public static boolean validateDeclarationTime(Athlete editingAthlete){
        OwlcmsSession.withFop((fop) -> {
            Athlete curAthlete = fop.getCurAthlete();
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime liftAttemptedDateTime = curAthlete.getSnatch1LiftTime();

            Duration durationSinceLift = Duration.between(currentDateTime, liftAttemptedDateTime);

            long durationSeconds = durationSinceLift.getSeconds();

            if (durationSeconds > 60){
                RuleViolationException ruleDeclarationTimeExceeded = null;
                ruleDeclarationTimeExceeded = RuleViolation.declarationTimeExceeded();
                throw ruleDeclarationTimeExceeded;
            }
        });
        return true;
    }
}
