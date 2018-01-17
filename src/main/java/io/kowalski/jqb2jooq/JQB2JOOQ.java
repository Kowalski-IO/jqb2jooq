package io.kowalski.jqb2jooq;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.Condition;

import java.util.Map;

/**
 * JQB2JOOQ is the entry point of the library and handles the conversion
 * of a Json Filter to a JOOQ Condition
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JQB2JOOQ {

    /**
     * parse performs the transformation of filter to condition.
     * @param targetClass must be an Enum that implements {@link RuleTarget}
     * @param jsonFilter is the raw Json String representation of the jQuery QueryBuilder filter deserialized into a Map
     * @return the JOOQ Condition equivalent of the provided Json filter
     */
    public static Condition parse(final Class<? extends RuleTarget> targetClass, final Map<String, Object> jsonFilter) {
        Filter filter = FilterParser.parseJSON(targetClass, jsonFilter);
        return FilterTranslator.translate(filter);
    }

}
