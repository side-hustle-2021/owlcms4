package app.owlcms.ui.athletecard;

import java.time.Duration;
import java.time.LocalDateTime;

import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.athlete.AthleteRepository;
import app.owlcms.data.athlete.RuleViolation;
import app.owlcms.data.athlete.RuleViolationException;
import app.owlcms.data.customlogin.CustomUserRepository;
import app.owlcms.init.OwlcmsSession;

public class AthleteCardRules {

    public static boolean validateWobRule(String requestedWeight, Athlete editingAthlete){
        OwlcmsSession.withFop((fop) -> {
            int curWeight = fop.getCurWeight();
            if (Athlete.zeroIfInvalid(requestedWeight) < curWeight)
            {
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

    public static boolean validateDistinctChange1(
        int currentAttempt, String currentChange1, Athlete editingAthlete
    ){
        OwlcmsSession.withFop((fop) -> {
            switch (currentAttempt){
                case 1:
                    if (editingAthlete.getSnatch1Declaration().equals(currentChange1)) {
                        validateDistinctChange1(-1, "", editingAthlete);
                    }
                    break;
                case 2:
                    if (editingAthlete.getSnatch2AutomaticProgression().equals(currentChange1) ||
                        editingAthlete.getSnatch2Declaration().equals(currentChange1)) {
                        validateDistinctChange1(-1, "", editingAthlete);
                    }
                    break;
                case 3:
                    if (editingAthlete.getSnatch3AutomaticProgression().equals(currentChange1) ||
                        editingAthlete.getSnatch3Declaration().equals(currentChange1)) {
                        validateDistinctChange1(-1, "", editingAthlete);
                    }
                    break;
                case 4:
                    if (editingAthlete.getCleanJerk1Declaration().equals(currentChange1)) {
                        validateDistinctChange1(-1, "", editingAthlete);
                    }
                    break;
                case 5:
                    if (editingAthlete.getCleanJerk2AutomaticProgression().equals(currentChange1) ||
                        editingAthlete.getCleanJerk2Declaration().equals(currentChange1)) {
                        validateDistinctChange1(-1, "", editingAthlete);
                    }
                    break;
                case 6:
                    if (editingAthlete.getCleanJerk3AutomaticProgression().equals(currentChange1) ||
                        editingAthlete.getCleanJerk3Declaration().equals(currentChange1)) {
                        validateDistinctChange1(-1, "", editingAthlete);
                    }
                    break;
                default:
                    RuleViolationException ruleChange1MustBeUnique = null;
                    ruleChange1MustBeUnique = RuleViolation.uniqueChange1Violation();
                    throw ruleChange1MustBeUnique;
            }
        });
        return true;
    }

    public static boolean validateDistinctChange2(
        int currentAttempt, String currentChange2, Athlete editingAthlete
    ){
        OwlcmsSession.withFop((fop) -> {

            switch (currentAttempt){
                case 1:
                    if (editingAthlete.getSnatch1Declaration().equals(currentChange2) || 
                        editingAthlete.getSnatch1Change1().equals(currentChange2)) {
                        validateDistinctChange2(-1, "", editingAthlete);
                    }
                    break;
                case 2:
                    if (editingAthlete.getSnatch2AutomaticProgression().equals(currentChange2) ||
                        editingAthlete.getSnatch2Declaration().equals(currentChange2) ||
                        editingAthlete.getSnatch2Change1().equals(currentChange2)) {
                        validateDistinctChange2(-1, "", editingAthlete);
                    }
                    break;
                case 3:
                    if (editingAthlete.getSnatch3AutomaticProgression().equals(currentChange2) ||
                        editingAthlete.getSnatch3Declaration().equals(currentChange2) ||
                        editingAthlete.getSnatch3Change1().equals(currentChange2)) {
                        validateDistinctChange2(-1, "", editingAthlete);
                    }
                    break;

                case 4:
                    if (editingAthlete.getCleanJerk1Declaration().equals(currentChange2) ||
                        editingAthlete.getCleanJerk1Change1().equals(currentChange2)) {
                        validateDistinctChange2(-1, "", editingAthlete);
                    }
                    break;
                case 5:
                    if (editingAthlete.getCleanJerk2AutomaticProgression().equals(currentChange2) ||
                        editingAthlete.getCleanJerk2Declaration().equals(currentChange2) ||
                        editingAthlete.getCleanJerk2Change1().equals(currentChange2)) {
                        validateDistinctChange2(-1, "", editingAthlete);
                    }
                    break;
                case 6:
                    if (editingAthlete.getCleanJerk3AutomaticProgression().equals(currentChange2) ||
                        editingAthlete.getCleanJerk3Declaration().equals(currentChange2) ||
                        editingAthlete.getCleanJerk3Change1().equals(currentChange2)) {
                        validateDistinctChange2(-1, "", editingAthlete);
                    }
                    break;
                default:
                    RuleViolationException ruleChange2MustBeUnique = null;
                    ruleChange2MustBeUnique = RuleViolation.uniqueChange2Violation();
                    throw ruleChange2MustBeUnique;
            }
        });
        return true;
    }

    public static boolean validateNextAthleteChangeTime(String currentChange, Athlete editedAthlete){
        OwlcmsSession.withFop((fop) -> {
            Athlete clockOwner = fop.getClockOwner();

            if ((clockOwner != null) && 
                (clockOwner.getUsername().equals(editedAthlete.getUsername()))
            ){
                fop.getAthleteTimer().stop();
                Integer timeRemaining = fop.getAthleteTimer().getTimeRemaining();
                fop.getAthleteTimer().start();
                
                if (timeRemaining < 30000){
                    RuleViolationException ruleChangeTimeExceeded = null;
                    ruleChangeTimeExceeded = RuleViolation.changeTimeExceeded();
                    throw ruleChangeTimeExceeded;
                }
            }
        });
        return true;
    }

    public static boolean validateChangeForAttempt(int currentAttempt, Athlete editedAthlete){
        Athlete fetchedAthlete = AthleteRepository.getAthleteByUsername(
            CustomUserRepository.getByUsername(editedAthlete.getUsername())
        );

        if (currentAttempt <= fetchedAthlete.getAttemptsDone()){
            RuleViolationException ruleCannotProcessChange = null;
            ruleCannotProcessChange = RuleViolation.cannotProcessChange();
            throw ruleCannotProcessChange;
        }
        return true;
    }
}
