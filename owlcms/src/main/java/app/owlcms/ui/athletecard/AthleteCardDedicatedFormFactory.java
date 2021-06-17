/***
 * Copyright (c) 2009-2020 Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("Non-Profit OSL" 3.0)
 * License text at https://github.com/jflamy/owlcms4/blob/master/LICENSE.txt
 */

package app.owlcms.ui.athletecard;

import org.vaadin.crudui.crud.CrudOperation;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToDoubleConverter;

import app.owlcms.components.fields.ValidationUtils;
import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.competition.Competition;
import app.owlcms.i18n.Translator;
import app.owlcms.ui.lifting.AthleteCardFormFactory;
import app.owlcms.ui.shared.IAthleteEditing;

@SuppressWarnings("serial")
public class AthleteCardDedicatedFormFactory extends AthleteCardFormFactory {

    TextField snatch2Declaration;
    TextField snatch3Declaration;
    TextField cj1Declaration;
    TextField cj2Declaration;
    TextField cj3Declaration;

    public AthleteCardDedicatedFormFactory(Class<Athlete> domainType, IAthleteEditing origin) {
        super(domainType, origin);
    }

    /**
     * @param operation
     * @param operation
     * @param gridLayout
     */
    @Override
     protected void bindGridFields(CrudOperation operation) {
        binder = buildBinder(null, getEditedAthlete());

        TextField snatch1Declaration = createPositiveWeightField(DECLARATION, SNATCH1);
        binder.forField(snatch1Declaration)
                .withValidator(
                        ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateSnatch1Declaration(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getSnatch1Declaration, Athlete::setSnatch1Declaration);
        atRowAndColumn(gridLayout, snatch1Declaration, DECLARATION, SNATCH1);

        TextField snatch1Change1 = createPositiveWeightField(CHANGE1, SNATCH1);
        binder.forField(snatch1Change1)
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateNextAthleteChangeTime(v)))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateDistinctChange1(1, v)))
                .withValidator(ValidationUtils.checkUsingException(v -> getEditedAthlete().validateSnatch1Change1(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getSnatch1Change1, Athlete::setSnatch1Change1);
        atRowAndColumn(gridLayout, snatch1Change1, CHANGE1, SNATCH1);

        TextField snatch1Change2 = createPositiveWeightField(CHANGE2, SNATCH1);
        binder.forField(snatch1Change2)
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateNextAthleteChangeTime(v)))
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateDistinctChange2(1, v)))
                .withValidator(ValidationUtils.checkUsingException(v -> getEditedAthlete().validateSnatch1Change2(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getSnatch1Change2, Athlete::setSnatch1Change2);
        atRowAndColumn(gridLayout, snatch1Change2, CHANGE2, SNATCH1);

        snatch1ActualLift = createActualWeightField(ACTUAL, SNATCH1);
        binder.forField(snatch1ActualLift)
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateSnatch1ActualLift(v)))
                .withValidator(ValidationUtils.checkUsingException(v -> setAutomaticProgressions(getEditedAthlete())))
                .withValidationStatusHandler(status -> setActualLiftStyle(status))
                .bind(Athlete::getSnatch1ActualLift, Athlete::setSnatch1ActualLift);
        atRowAndColumn(gridLayout, snatch1ActualLift, ACTUAL, SNATCH1);
        snatch1ActualLift.setReadOnly(true);

        snatch2AutomaticProgression = new TextField();
        snatch2AutomaticProgression.setReadOnly(true);
        snatch2AutomaticProgression.setTabIndex(-1);
        binder.forField(snatch2AutomaticProgression).bind(Athlete::getSnatch2AutomaticProgression,
                Athlete::setSnatch2AutomaticProgression);
        atRowAndColumn(gridLayout, snatch2AutomaticProgression, AUTOMATIC, SNATCH2);

        snatch2Declaration = createPositiveWeightField(DECLARATION, SNATCH2);
        binder.forField(snatch2Declaration)
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateSnatch2Declaration(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getSnatch2Declaration, Athlete::setSnatch2Declaration);
        atRowAndColumn(gridLayout, snatch2Declaration, DECLARATION, SNATCH2);

        TextField snatch2Change1 = createPositiveWeightField(CHANGE1, SNATCH2);
        binder.forField(snatch2Change1)
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateNextAthleteChangeTime(v)))
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateDistinctChange1(2, v)))
                .withValidator(ValidationUtils.checkUsingException(v -> getEditedAthlete().validateSnatch2Change1(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getSnatch2Change1, Athlete::setSnatch2Change1);
        atRowAndColumn(gridLayout, snatch2Change1, CHANGE1, SNATCH2);

        TextField snatch2Change2 = createPositiveWeightField(CHANGE2, SNATCH2);
        binder.forField(snatch2Change2)
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateNextAthleteChangeTime(v)))
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateDistinctChange2(2, v)))
                .withValidator(ValidationUtils.checkUsingException(v -> getEditedAthlete().validateSnatch2Change2(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getSnatch2Change2, Athlete::setSnatch2Change2);
        atRowAndColumn(gridLayout, snatch2Change2, CHANGE2, SNATCH2);

        snatch2ActualLift = createActualWeightField(ACTUAL, SNATCH2);
        binder.forField(snatch2ActualLift)
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateSnatch2ActualLift(v)))
                .withValidator(ValidationUtils.checkUsingException(v -> setAutomaticProgressions(getEditedAthlete())))
                .withValidationStatusHandler(status -> setActualLiftStyle(status))
                .bind(Athlete::getSnatch2ActualLift, Athlete::setSnatch2ActualLift);
        atRowAndColumn(gridLayout, snatch2ActualLift, ACTUAL, SNATCH2);
        snatch2ActualLift.setReadOnly(true);

        snatch3AutomaticProgression = new TextField();
        snatch3AutomaticProgression.setReadOnly(true);
        snatch3AutomaticProgression.setTabIndex(-1);
        binder.forField(snatch3AutomaticProgression).bind(Athlete::getSnatch3AutomaticProgression,
                Athlete::setSnatch3AutomaticProgression);
        atRowAndColumn(gridLayout, snatch3AutomaticProgression, AUTOMATIC, SNATCH3);

        snatch3Declaration = createPositiveWeightField(DECLARATION, SNATCH3);
        binder.forField(snatch3Declaration)
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateSnatch3Declaration(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getSnatch3Declaration, Athlete::setSnatch3Declaration);
        atRowAndColumn(gridLayout, snatch3Declaration, DECLARATION, SNATCH3);

        TextField snatch3Change1 = createPositiveWeightField(CHANGE1, SNATCH3);
        binder.forField(snatch3Change1)
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateNextAthleteChangeTime(v)))
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateDistinctChange1(3, v)))
                .withValidator(ValidationUtils.checkUsingException(v -> getEditedAthlete().validateSnatch3Change1(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getSnatch3Change1, Athlete::setSnatch3Change1);
        atRowAndColumn(gridLayout, snatch3Change1, CHANGE1, SNATCH3);

        TextField snatch3Change2 = createPositiveWeightField(CHANGE2, SNATCH3);
        binder.forField(snatch3Change2)
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateNextAthleteChangeTime(v)))
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateDistinctChange2(3, v)))
                .withValidator(ValidationUtils.checkUsingException(v -> getEditedAthlete().validateSnatch3Change2(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getSnatch3Change2, Athlete::setSnatch3Change2);
        atRowAndColumn(gridLayout, snatch3Change2, CHANGE2, SNATCH3);

        snatch3ActualLift = createActualWeightField(ACTUAL, SNATCH3);
        binder.forField(snatch3ActualLift)
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateSnatch3ActualLift(v)))
                .withValidationStatusHandler(status -> setActualLiftStyle(status))
                .bind(Athlete::getSnatch3ActualLift, Athlete::setSnatch3ActualLift);
        atRowAndColumn(gridLayout, snatch3ActualLift, ACTUAL, SNATCH3);
        snatch3ActualLift.setReadOnly(true);

        cj1Declaration = createPositiveWeightField(DECLARATION, CJ1);
        binder.forField(cj1Declaration)
                .withValidator(
                        ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateCleanJerk1Declaration(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getCleanJerk1Declaration, Athlete::setCleanJerk1Declaration);
        atRowAndColumn(gridLayout, cj1Declaration, DECLARATION, CJ1);

        TextField cj1Change1 = createPositiveWeightField(CHANGE1, CJ1);
        binder.forField(cj1Change1)
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateNextAthleteChangeTime(v)))
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateDistinctChange1(4, v)))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateCleanJerk1Change1(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getCleanJerk1Change1, Athlete::setCleanJerk1Change1);
        atRowAndColumn(gridLayout, cj1Change1, CHANGE1, CJ1);

        TextField cj1Change2 = createPositiveWeightField(CHANGE2, CJ1);
        binder.forField(cj1Change2)
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateNextAthleteChangeTime(v)))
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateDistinctChange2(4, v)))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateCleanJerk1Change2(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getCleanJerk1Change2, Athlete::setCleanJerk1Change2);
        atRowAndColumn(gridLayout, cj1Change2, CHANGE2, CJ1);

        cj1ActualLift = createActualWeightField(ACTUAL, CJ1);
        binder.forField(cj1ActualLift)
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateCleanJerk1ActualLift(v)))
                .withValidator(ValidationUtils.checkUsingException(v -> setAutomaticProgressions(getEditedAthlete())))
                .withValidationStatusHandler(status -> setActualLiftStyle(status))
                .bind(Athlete::getCleanJerk1ActualLift, Athlete::setCleanJerk1ActualLift);
        atRowAndColumn(gridLayout, cj1ActualLift, ACTUAL, CJ1);
        cj1ActualLift.setReadOnly(true);

        cj2AutomaticProgression = new TextField();
        cj2AutomaticProgression.setReadOnly(true);
        cj2AutomaticProgression.setTabIndex(-1);
        binder.forField(cj2AutomaticProgression).bind(Athlete::getCleanJerk2AutomaticProgression,
                Athlete::setCleanJerk2AutomaticProgression);
        atRowAndColumn(gridLayout, cj2AutomaticProgression, AUTOMATIC, CJ2);

        cj2Declaration = createPositiveWeightField(DECLARATION, CJ2);
        binder.forField(cj2Declaration)
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateCleanJerk2Declaration(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getCleanJerk2Declaration, Athlete::setCleanJerk2Declaration);
        atRowAndColumn(gridLayout, cj2Declaration, DECLARATION, CJ2);

        TextField cj2Change1 = createPositiveWeightField(CHANGE1, CJ2);
        binder.forField(cj2Change1)
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateNextAthleteChangeTime(v)))
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateDistinctChange1(5, v)))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateCleanJerk2Change1(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getCleanJerk2Change1, Athlete::setCleanJerk2Change1);
        atRowAndColumn(gridLayout, cj2Change1, CHANGE1, CJ2);

        TextField cj2Change2 = createPositiveWeightField(CHANGE2, CJ2);
        binder.forField(cj2Change2)
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateNextAthleteChangeTime(v)))
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateDistinctChange2(5, v)))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateCleanJerk2Change2(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getCleanJerk2Change2, Athlete::setCleanJerk2Change2);
        atRowAndColumn(gridLayout, cj2Change2, CHANGE2, CJ2);

        cj2ActualLift = createActualWeightField(ACTUAL, CJ2);
        binder.forField(cj2ActualLift)
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateCleanJerk2ActualLift(v)))
                .withValidator(ValidationUtils.checkUsingException(v -> setAutomaticProgressions(getEditedAthlete())))
                .withValidationStatusHandler(status -> setActualLiftStyle(status))
                .bind(Athlete::getCleanJerk2ActualLift, Athlete::setCleanJerk2ActualLift);
        atRowAndColumn(gridLayout, cj2ActualLift, ACTUAL, CJ2);
        cj2ActualLift.setReadOnly(true);

        cj3AutomaticProgression = new TextField();
        cj3AutomaticProgression.setReadOnly(true);
        cj3AutomaticProgression.setTabIndex(-1);
        binder.forField(cj3AutomaticProgression).bind(Athlete::getCleanJerk3AutomaticProgression,
                Athlete::setCleanJerk3AutomaticProgression);
        atRowAndColumn(gridLayout, cj3AutomaticProgression, AUTOMATIC, CJ3);

        cj3Declaration = createPositiveWeightField(DECLARATION, CJ3);
        binder.forField(cj3Declaration)
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateCleanJerk3Declaration(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getCleanJerk3Declaration, Athlete::setCleanJerk3Declaration);
        atRowAndColumn(gridLayout, cj3Declaration, DECLARATION, CJ3);

        TextField cj3Change1 = createPositiveWeightField(CHANGE1, CJ3);
        binder.forField(cj3Change1)
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateNextAthleteChangeTime(v)))
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateDistinctChange1(6, v)))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateCleanJerk3Change1(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getCleanJerk3Change1, Athlete::setCleanJerk3Change1);
        atRowAndColumn(gridLayout, cj3Change1, CHANGE1, CJ3);

        TextField cj3Change2 = createPositiveWeightField(CHANGE2, CJ3);
        binder.forField(cj3Change2)
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateNextAthleteChangeTime(v)))
                .withValidator(
                    ValidationUtils.checkUsingException(v -> AthleteCardRules.validateWobRule(v, getEditedAthlete())))
                .withValidator(ValidationUtils.checkUsingException(v -> AthleteCardRules.validateDistinctChange2(6, v)))
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateCleanJerk3Change2(v)))
                .withValidationStatusHandler(status -> {
                }).bind(Athlete::getCleanJerk3Change2, Athlete::setCleanJerk3Change2);
        atRowAndColumn(gridLayout, cj3Change2, CHANGE2, CJ3);

        cj3ActualLift = createActualWeightField(ACTUAL, CJ3);
        binder.forField(cj3ActualLift)
                .withValidator(
                        ValidationUtils.checkUsingException(v -> getEditedAthlete().validateCleanJerk3ActualLift(v)))
                .withValidationStatusHandler(status -> setActualLiftStyle(status))
                .bind(Athlete::getCleanJerk3ActualLift, Athlete::setCleanJerk3ActualLift);
        atRowAndColumn(gridLayout, cj3ActualLift, ACTUAL, CJ3);
        cj3ActualLift.setReadOnly(true);

        if (Competition.getCurrent().isCustomScore()) {
            TextField custom = createPositiveWeightField(SCORE, CJ3);
            binder.forField(custom)
                    .withConverter(new StringToDoubleConverter(0.0D, Translator.translate("NumberExpected")))
                    .bind(Athlete::getCustomScoreComputed, Athlete::setCustomScore);
            atRowAndColumn(gridLayout, custom, SCORE, CJ3);
        }

        // use setBean so that changes are immediately reflected to the working copy
        // otherwise the changes are only visible in the fields, and the validation
        // routines in the
        // Athlete class don't work
        binder.setBean(getEditedAthlete());
        setAutomaticDeclarations();
        setAllReadOnly();
        setFocus(getEditedAthlete());
    }

    protected void setAllReadOnly(){
        for (int row = 0; row < textfields.length; row++){
            for (int col = 0; col < textfields[row].length; col++) {
                if (textfields[row][col] == null){
                    continue;
                }
                textfields[row][col].setReadOnly(true);
            }
        }
    }

    @Override
    protected void setFocus(Athlete a) {
        boolean breakSearch = false;
        int targetRow = ACTUAL + 1;
        int targetCol = CJ3 + 1;

        // figure out whether we are searching for snatch or CJ
        int rightCol;
        int leftCol;
        if (a.getAttemptsDone() >= 3) {
            rightCol = CJ3;
            leftCol = CJ1;
        } else {
            rightCol = SNATCH3;
            leftCol = SNATCH1;
        }

        for (int col=leftCol; col<=rightCol && !breakSearch; col++){
            if (!textfields[ACTUAL-1][col - 1].isEmpty()){
                continue;
            }
            else if (col > leftCol && textfields[ACTUAL-1][col - 2].isEmpty()){
                breakSearch = true;
            }
            for (int row=AUTOMATIC+1; row<ACTUAL && !breakSearch; row++) {
                boolean empty = textfields[row - 1][col - 1].isEmpty();
                if(empty){
                    breakSearch = true;
                    targetRow = row - 1;
                    targetCol = col - 1;
                }
            }
        }

        if (targetCol <= CJ3 && targetRow <= ACTUAL) {
            // a suitable empty cell was found, set focus
            textfields[targetRow][targetCol].setAutofocus(true);
            textfields[targetRow][targetCol].setAutoselect(true);
            textfields[targetRow][targetCol].setReadOnly(false);
        }
    }

    /**
     * @see app.owlcms.ui.crudui.OwlcmsCrudFormFactory#buildFooter(org.vaadin.crudui.crud.CrudOperation,
     *      java.lang.Object, com.vaadin.flow.component.ComponentEventListener,
     *      com.vaadin.flow.component.ComponentEventListener, com.vaadin.flow.component.ComponentEventListener)
     */
    @Override
    public Component buildFooter(CrudOperation operation, Athlete unused,
            ComponentEventListener<ClickEvent<Button>> cancelButtonClickListener,
            ComponentEventListener<ClickEvent<Button>> unused2, ComponentEventListener<ClickEvent<Button>> unused3,
            boolean shortcutEnter,
            Button... buttons) {
        ComponentEventListener<ClickEvent<Button>> postOperationCallBack = (e) -> {
        };
        Button operationButton = null;
        if (operation == CrudOperation.UPDATE) {
            operationButton = buildOperationButton(CrudOperation.UPDATE, originalAthlete, postOperationCallBack);
        } else if (operation == CrudOperation.ADD) {
            operationButton = buildOperationButton(CrudOperation.ADD, originalAthlete, postOperationCallBack);
        }
        Button cancelButton = buildCancelButton(cancelButtonClickListener);

        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setWidth("100%");
        footerLayout.setSpacing(true);
        footerLayout.setPadding(false);

        Label spacer = new Label();

        footerLayout.add(spacer, operationTrigger);

        if (cancelButton != null) {
            footerLayout.add(cancelButton);
        }

        if (operationButton != null) {
            footerLayout.add(operationButton);
            if (operation == CrudOperation.UPDATE && shortcutEnter) {
                ShortcutRegistration reg = operationButton.addClickShortcut(Key.ENTER);
                reg.allowBrowserDefault();
            }
        }
        footerLayout.setFlexGrow(1.0, spacer);
        return footerLayout;
    }

    protected void setAutomaticDeclarations(){
        if (!snatch1ActualLift.getValue().isEmpty() && snatch2Declaration.getValue().isEmpty()) {
            snatch2Declaration.setValue(snatch2AutomaticProgression.getValue());
        }
        
        if (!snatch2ActualLift.getValue().isEmpty() && snatch3Declaration.getValue().isEmpty()) {
            snatch3Declaration.setValue(snatch3AutomaticProgression.getValue());
        }

        if (!cj1ActualLift.getValue().isEmpty() && cj2Declaration.getValue().isEmpty()) {
            cj2Declaration.setValue(cj2AutomaticProgression.getValue());
        }

        if (!cj2ActualLift.getValue().isEmpty() && cj3Declaration.getValue().isEmpty()) {
            cj3Declaration.setValue(cj3AutomaticProgression.getValue());
        }
    }
}
