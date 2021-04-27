package app.owlcms.ui.athletecard;

import java.time.Duration;
import java.time.LocalDateTime;

import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.athlete.AthleteRepository;
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

    public static boolean validatePreviousAttempt(int currentAttempt){
        OwlcmsSession.withFop((fop) -> {
            Athlete curAthlete = fop.getCurAthlete();

            if ((currentAttempt == 2 && curAthlete.getSnatch1ActualLift().isEmpty()) ||
                (currentAttempt == 3 && curAthlete.getSnatch2ActualLift().isEmpty()) ||
                (currentAttempt == 5 && curAthlete.getCleanJerk1ActualLift().isEmpty()) ||
                (currentAttempt == 6 && curAthlete.getCleanJerk2ActualLift().isEmpty())) {
                RuleViolationException ruleDeclarationNotAllowed = null;
                ruleDeclarationNotAllowed = RuleViolation.declarationNotAllowed();
                throw ruleDeclarationNotAllowed;
            }
        });
        return true;
    }

    public static boolean validateDeclarationTime(int currentAttempt, String currentDeclaration){
        OwlcmsSession.withFop((fop) -> {
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime liftAttemptedDateTime = null;
            String change1 = ""; String change2 = "";
            Athlete curAthlete = fop.getCurAthlete();
            if (currentAttempt-1 != curAthlete.getAttemptsDone()){
                return;
            }

            if (currentAttempt == 2) {
                liftAttemptedDateTime = curAthlete.getSnatch1LiftTime();
                change1 = curAthlete.getSnatch2Change1();
                change2 = curAthlete.getSnatch2Change2();
            }
            else if (currentAttempt == 3){
                liftAttemptedDateTime = curAthlete.getSnatch2LiftTime();
                change1 = curAthlete.getSnatch3Change1();
                change2 = curAthlete.getSnatch3Change2();
            }
            else if (currentAttempt == 5){
                liftAttemptedDateTime = curAthlete.getCleanJerk1LiftTime();
                change1 = curAthlete.getCleanJerk2Change1();
                change2 = curAthlete.getCleanJerk2Change2();
            }
            else if (currentAttempt == 6){
                liftAttemptedDateTime = curAthlete.getCleanJerk2LiftTime();
                change1 = curAthlete.getCleanJerk3Change1();
                change2 = curAthlete.getCleanJerk3Change2();
            }
            
            if (liftAttemptedDateTime == null){
                validatePreviousAttempt(currentAttempt);
            }
            else {
                Duration durationSinceLift = Duration.between(liftAttemptedDateTime, currentDateTime);
                long durationSeconds = durationSinceLift.getSeconds();

                if (durationSeconds > 60 && !currentDeclaration.isEmpty() &&
                    change1.isEmpty() && change2.isEmpty()){
                    setFailedDeclarationValues(currentAttempt, curAthlete);
                    RuleViolationException ruleDeclarationTimeExceeded = null;
                    ruleDeclarationTimeExceeded = RuleViolation.declarationTimeExceeded();
                    throw ruleDeclarationTimeExceeded;
                }
            }
        });
        return true;
    }

    protected static void setFailedDeclarationValues(int currentAttempt, Athlete curAthlete){
        switch (currentAttempt){
            case 2:
                AthleteRepository.updateSnatch2FromAP(curAthlete);
                break;
            case 3:
            AthleteRepository.updateSnatch3FromAP(curAthlete);
                break;
            case 5:
                AthleteRepository.updateCleanJerk2FromAP(curAthlete);
                break;
            case 6:
                AthleteRepository.updateCleanJerk3FromAP(curAthlete);
                break;
        }
    }
}
