package io.kowalski.jqb2jooq;

import org.jooq.Condition;

import java.util.Map;

public class JQB2JOOQ {

    public static Condition parse(final Class<? extends RuleTarget> targetClass, final Map<String, Object> jsonFilter) {
        Filter filter = FilterParser.parseJSON(targetClass, jsonFilter);
        return FilterTranslator.translate(filter);
    }

}
