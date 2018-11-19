package io.kowalski.jqb2jooq;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.jooq.tools.reflect.Reflect.on;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FilterParser {

    static Filter parseJSON(final RuleTargetBuilder ruleTargetBuilder, final Map<String, Object> jsonFilter) {
        RuleSet outerRuleSet = new RuleSet();
        outerRuleSet.setOperator(BooleanOperator.valueOf(jsonFilter.get("condition").toString()));
        outerRuleSet.setRules(parseRaw(ruleTargetBuilder, parseRulesList(jsonFilter)));

        return new Filter(outerRuleSet);
    }

    private static Collection<FilterPart> parseRaw(final RuleTargetBuilder ruleTargetBuilder, List<Map<String, Object>> rules) {
        List<FilterPart> filterParts = new ArrayList<>();

        rules.forEach(rule -> filterParts.add(rule.containsKey("condition")
                ? parseRawRuleSet(ruleTargetBuilder, rule) : parseRawRule(ruleTargetBuilder, rule)));

        return filterParts;
    }

    private static RuleSet parseRawRuleSet(final RuleTargetBuilder targetClass, final Map<String, Object> rawRuleSet) {
        RuleSet ruleSet = new RuleSet();
        ruleSet.setOperator(BooleanOperator.valueOf(rawRuleSet.get("condition").toString()));
        ruleSet.setRules(parseRaw(targetClass, parseRulesList(rawRuleSet)));

        return ruleSet;
    }

    @SuppressWarnings("unchecked")
    private static Rule parseRawRule(final RuleTargetBuilder ruleTargetBuilder, final Map<String, Object> rawRule) {
        List<Object> rawValues;

        if (rawRule.get("value") instanceof Collection) {
            rawValues = ((List) rawRule.get("value"));
        } else {
            rawValues = new ArrayList<>();
            rawValues.add(rawRule.get("value"));
        }

        Rule rule = new Rule();

        String rawOperator = rawRule.get("operator").toString().toUpperCase();
        rule.setOperator(RuleOperator.valueOf(rawOperator));

        String rawField = rawRule.get("field").toString();
        RuleTarget target = ruleTargetBuilder.build(rawField);
        rule.setTarget(target);

        rule.setParameters(rawValues);

        return rule;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> parseRulesList(final Map<String, Object> jsonFilter) {
        return (List<Map<String, Object>>) jsonFilter.get("rules");
    }

}
