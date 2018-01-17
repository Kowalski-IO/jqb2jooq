package io.kowalski.jqb2jooq;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.Condition;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FilterTranslator {

    static Condition translate(Filter filter) {
        return parseRuleSet(filter.getRuleSet());
    }

    private static Condition parseRuleSet(RuleSet ruleSet) {
        Condition condition = null;

        for (FilterPart filterPart : ruleSet.getRules()) {
            Condition parsed;

            if (filterPart instanceof RuleSet) {
                parsed = parseRuleSet((RuleSet) filterPart);
            } else {
                parsed = parseRule((Rule) filterPart);
            }

            condition = appendCondition(condition, parsed, ruleSet.getOperator());
        }

        return condition;
    }

    @SuppressWarnings("unchecked")
    private static Condition parseRule(Rule rule) {
        Condition condition = null;
        switch (rule.getOperator()) {
            case EQUAL:
                condition = rule.getField().eq(rule.getParameter(0));
                break;
            case NOT_EQUAL:
                condition = rule.getField().ne(rule.getParameter(0));
                break;
            case LESS:
                condition = rule.getField().lt(rule.getParameter(0));
                break;
            case LESS_OR_EQUAL:
                condition = rule.getField().le(rule.getParameter(0));
                break;
            case GREATER:
                condition = rule.getField().gt(rule.getParameter(0));
                break;
            case GREATER_OR_EQUAL:
                condition = rule.getField().ge(rule.getParameter(0));
                break;
            case IN:
                condition = rule.getField().in(rule.getParameters());
                break;
            case NOT_IN:
                condition = rule.getField().notIn(rule.getParameters());
                break;
            case CONTAINS:
                condition = rule.getField().contains(rule.getParameter(0));
                break;
            case NOT_CONTAINS:
                condition = rule.getField().notContains(rule.getParameter(0));
                break;
            case BETWEEN:
                condition = rule.getField().between(rule.getParameter(0), rule.getParameter(1));
                break;
            case NOT_BETWEEN:
                condition = rule.getField().notBetween(rule.getParameter(0), rule.getParameter(1));
                break;
            case IS_NULL:
                condition = rule.getField().isNull();
                break;
            case IS_NOT_NULL:
                condition = rule.getField().isNotNull();
                break;
            case IS_EMPTY:
                condition = rule.getField().length().eq(0);
                break;
            case IS_NOT_EMPTY:
                condition = rule.getField().length().gt(0);
                break;
        }

        if (rule.getTarget().getImplicitConditions() != null && rule.getTarget().getImplicitConditions().length > 0) {
            for (Condition implicitCondition : rule.getTarget().getImplicitConditions()) {
                condition = condition.and(implicitCondition);
            }
        }

        return condition;
    }

    private static Condition appendCondition(Condition initial, Condition addition, BooleanOperator operator) {
        if (initial == null) {
            return addition;
        }

        return operator.equals(BooleanOperator.OR) ? initial.or(addition) : initial.and(addition);
    }

}
