package io.kowalski.jqb2jooq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.jooq.tools.reflect.Reflect.on;

final class FilterParser {

    protected static Filter parseJSON(final Class<? extends RuleTarget> targetClass, final Map<String, Object> jsonFilter) {
        RuleSet outerRuleSet = new RuleSet();
        outerRuleSet.setOperator(BooleanOperator.valueOf(jsonFilter.get("condition").toString()));
        outerRuleSet.setRules(parseRaw(targetClass, parseRulesList(jsonFilter)));

        return new Filter(outerRuleSet);
    }

    private static Collection<FilterPart> parseRaw(final Class<? extends RuleTarget> targetClass, List<Map<String, Object>> rules) {
        List<FilterPart> filterParts = new ArrayList<>();

        rules.forEach(rule -> filterParts.add(rule.containsKey("condition")
                ? parseRawRuleSet(targetClass, rule) : parseRawRule(targetClass, rule)));

        return filterParts;
    }

    private static RuleSet parseRawRuleSet(final Class<? extends RuleTarget> targetClass, final Map<String, Object> rawRuleSet) {
        RuleSet ruleSet = new RuleSet();
        ruleSet.setOperator(BooleanOperator.valueOf(rawRuleSet.get("condition").toString()));
        ruleSet.setRules(parseRaw(targetClass, parseRulesList(rawRuleSet)));

        return ruleSet;
    }

    @SuppressWarnings("unchecked")
    private static Rule parseRawRule(final Class<? extends RuleTarget> targetClass, final Map<String, Object> rawRule) {
        String rawOperator = rawRule.get("operator").toString().toUpperCase();
        String rawField = rawRule.get("field").toString().toUpperCase();
        List<Object> rawValues;

        if (rawRule.get("value") instanceof Collection) {
            rawValues = ((List) rawRule.get("value"));
        } else {
            rawValues = new ArrayList<>();
            rawValues.add(rawRule.get("value"));
        }

        Class<? extends Enum> enumClass = on(targetClass).get();
        Enum enumTarget = Enum.valueOf(enumClass, rawField);
        RuleTarget target = on(enumTarget).get();

        Rule rule = new Rule();
        rule.setOperator(RuleOperator.valueOf(rawOperator));
        rule.setTarget(target);
        rule.setParameters(rawValues);
        return rule;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> parseRulesList(final Map<String, Object> jsonFilter) {
        return (List<Map<String, Object>>) jsonFilter.get("rules");
    }

}
