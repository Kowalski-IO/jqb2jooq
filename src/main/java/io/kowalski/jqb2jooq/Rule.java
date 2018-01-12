package io.kowalski.jqb2jooq;

import lombok.Getter;
import lombok.Setter;
import org.jooq.Field;

import java.util.List;

@Getter
@Setter
class Rule implements FilterPart {

    private RuleTarget target;
    private RuleOperator operator;
    private List<Object> parameters;

    Field getField() {
        return target.getField();
    }

    Object getParameter(int index) {
        return parameters.get(index);
    }

}
