package io.kowalski.jqb2jooq;

import lombok.Getter;

@Getter
enum RuleOperator {

    EQUAL,
    NOT_EQUAL,

    LESS,
    LESS_OR_EQUAL,

    GREATER,
    GREATER_OR_EQUAL,

    IN,
    NOT_IN,

    CONTAINS,
    NOT_CONTAINS,

    BETWEEN,
    NOT_BETWEEN,

    IS_NULL,
    IS_NOT_NULL,

    IS_EMPTY,
    IS_NOT_EMPTY

}
