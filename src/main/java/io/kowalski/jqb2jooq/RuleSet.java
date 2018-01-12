package io.kowalski.jqb2jooq;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
class RuleSet implements FilterPart {

    private BooleanOperator operator;
    private Collection<FilterPart> rules;

}
