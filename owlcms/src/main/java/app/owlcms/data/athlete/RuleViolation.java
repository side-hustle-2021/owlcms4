/***
 * Copyright (c) 2009-2020 Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("Non-Profit OSL" 3.0)
 * License text at https://github.com/jflamy/owlcms4/blob/master/LICENSE.txt
 */
package app.owlcms.data.athlete;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 * The Class RuleViolation.
 */
public class RuleViolation {

    /** The Constant logger. */
    final static Logger logger = (Logger) LoggerFactory.getLogger(RuleViolation.class);

    // public static RuleViolationException change1ValueTooSmall(Object... objs)
    // {
    // return new RuleViolationException(("RuleViolation.change1ValueTooSmall"),
    // objs);
    // }
    //
    // public static RuleViolationException change2ValueTooSmall(Object... objs)
    // {
    // return new RuleViolationException(("RuleViolation.change2ValueTooSmall"),
    // objs);
    // }
    /**
     * Declaration value too small.
     *
     * @param objs the objs
     * @return the rule violation exception
     */
    //
    public static RuleViolationException declarationValueTooSmall(Object... objs) {
        return new RuleViolationException(("RuleViolation.declarationValueTooSmall"), objs);
    }

    //
    // public static RuleViolationException liftValueTooSmall(Object... objs) {
    // return new RuleViolationException(("RuleViolation.liftValueTooSmall"), objs);
    // }

    /**
     * Declared changes not ok.
     *
     * @param objs the objs
     * @return the rule violation exception
     */
    public static RuleViolationException declaredChangesNotOk(Object... objs) {
        return new RuleViolationException(("RuleViolation.declaredChangesNotOk"), objs);
    }

    /**
     * Lift value below progression.
     *
     * @param curLift              the cur lift
     * @param actualLift           the actual lift
     * @param automaticProgression the automatic progression
     * @return the rule violation exception
     */
    public static RuleViolationException liftValueBelowProgression(int curLift, String actualLift,
            int automaticProgression) {
        return new RuleViolationException(("RuleViolation.liftValueBelowProgression"), curLift, actualLift,
                automaticProgression);
    }

    /**
     * Lift value not what was requested.
     *
     * @param objs the objs
     * @return the rule violation exception
     */
    public static RuleViolationException liftValueNotWhatWasRequested(Object... objs) {
        return new RuleViolationException(("RuleViolation.liftValueNotWhatWasRequested"), objs);
    }

    /**
     * Rule 15 20 violated.
     *
     * @param objs the objs
     * @return the rule violation exception
     */
    public static RuleViolationException rule15_20Violated(Object... objs) {
        return new RuleViolationException("RuleViolation.rule15_20Violated", objs);
    }

    public static RuleViolationException wobViolated(Object... objs) {
        return new RuleViolationException("RuleViolation.wobViolated", objs);
    }

//	public static RuleViolationException declarationValueRequired(int curLift) {
//		 return new RuleViolationException("RuleViolation.declarationRequired");
//	}

}
