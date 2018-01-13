package io.kowalski.jqb2jooq.test;

import io.kowalski.jqb2jooq.RuleTarget;
import org.jooq.Condition;
import org.jooq.Field;

import static io.kowalski.jqb2jooq.test.jooq.Tables.*;

public enum TestFilterTargets implements RuleTarget {

    FULLNAME(EMPLOYEES.FULLNAME),
    DOB(EMPLOYEES.DOB),
    SALARY(PAYROLL.SALARY);

    private final Field field;
    private final Condition[] implicitConditions;

    TestFilterTargets(Field field, Condition... implicitConditions) {
        this.field = field;
        this.implicitConditions = implicitConditions;
    }

    @Override
    public TestFilterTargets parse(String value) {
        return TestFilterTargets.valueOf(value);
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public Condition[] getImplicitConditions() {
        return implicitConditions;
    }

}
