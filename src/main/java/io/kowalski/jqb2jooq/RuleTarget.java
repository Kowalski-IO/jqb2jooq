package io.kowalski.jqb2jooq;

import org.jooq.Condition;
import org.jooq.Field;

/**
 * This represents a template for an Enum to be defined against your model.
 * <p>
 * The constants of your Enum are to mirror the ids of the values within
 * your jQuery QueryBuilder configuration. These will map to a JOOQ Field (preferably
 * provided by auto-generated code). You then may provide an optional var-args collection
 * of Implicit Conditions which will automatically be applied to the user supplied condition.
 * <p>
 * i.e. if a table within your schema holds a variety of types (perhaps made distinct by some discriminator column)
 * you can add in implicit conditions that will be automatically applied to the aforementioned condition (via an AND).
 */
public interface RuleTarget {

    // Getter Contract
    Field getField();
    Condition[] getImplicitConditions();
}
