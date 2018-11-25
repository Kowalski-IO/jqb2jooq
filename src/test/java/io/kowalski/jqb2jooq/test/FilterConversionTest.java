package io.kowalski.jqb2jooq.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.kowalski.jqb2jooq.JQB2JOOQ;
import io.kowalski.jqb2jooq.RuleTargetBuilder;
import io.kowalski.jqb2jooq.test.jooq.tables.pojos.Employees;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.kowalski.jqb2jooq.test.jooq.Tables.EMPLOYEES;
import static io.kowalski.jqb2jooq.test.jooq.Tables.PAYROLL;

/**
 * Convert pre-produced jQuery QueryBuilder Json Filters to JOOQ Conditions and use them against
 * a fake employee / salary schema in H2.
 */
public class FilterConversionTest {

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();

    private static final String FULLNAME_EQUALS_FILTER = "{\"condition\":\"AND\",\"rules\":[{\"id\":\"FULLNAME\",\"field\":\"FULLNAME\",\"type\":\"string\",\"input\":\"text\",\"operator\":\"equal\",\"value\":\"Isaac Fulmer\"}],\"valid\":true}";
    private static final String DOB_BETWEEN_FILTER = "{\"condition\":\"OR\",\"rules\":[{\"id\":\"dob\",\"field\":\"dob\",\"type\":\"date\",\"input\":\"text\",\"operator\":\"between\",\"value\":[\"1982-01-01\",\"1988-04-11\"]}],\"valid\":true}";
    private static final String SALARY_LESS_OR_EQUAL_FILTER = "{\"condition\":\"AND\",\"rules\":[{\"id\":\"salary\",\"field\":\"salary\",\"type\":\"double\",\"input\":\"number\",\"operator\":\"less_or_equal\",\"value\":\"37000\"}],\"valid\":true}";
    private static final String NESTED_DOB_HOURLY_FILTER = "{\"condition\":\"OR\",\"rules\":[{\"id\":\"dob\",\"field\":\"dob\",\"type\":\"date\",\"input\":\"text\",\"operator\":\"greater\",\"value\":\"1985-01-01\"},{\"condition\":\"AND\",\"rules\":[{\"id\":\"hourly\",\"field\":\"hourly\",\"type\":\"integer\",\"input\":\"number\",\"operator\":\"less\",\"value\":\"20\"},{\"id\":\"hourly\",\"field\":\"hourly\",\"type\":\"integer\",\"input\":\"number\",\"operator\":\"not_equal\",\"value\":\"15\"}]}],\"valid\":true}";
    private static final String GRAB_BAG_FILTER = "{\"condition\":\"AND\",\"rules\":[{\"id\":\"salary\",\"field\":\"salary\",\"type\":\"double\",\"input\":\"number\",\"operator\":\"greater_or_equal\",\"value\":\"10\"},{\"id\":\"food\",\"field\":\"food\",\"type\":\"string\",\"input\":\"text\",\"operator\":\"in\",\"value\":\"chicken\"},{\"id\":\"food\",\"field\":\"food\",\"type\":\"string\",\"input\":\"text\",\"operator\":\"not_in\",\"value\":\"roast beef\"},{\"id\":\"food\",\"field\":\"food\",\"type\":\"string\",\"input\":\"text\",\"operator\":\"contains\",\"value\":\"a\"},{\"id\":\"food\",\"field\":\"food\",\"type\":\"string\",\"input\":\"text\",\"operator\":\"not_contains\",\"value\":\"z\"},{\"id\":\"salary\",\"field\":\"salary\",\"type\":\"double\",\"input\":\"number\",\"operator\":\"not_between\",\"value\":[\"20000\",\"30000\"]},{\"id\":\"food\",\"field\":\"food\",\"type\":\"string\",\"input\":\"text\",\"operator\":\"is_null\",\"value\":null},{\"id\":\"food\",\"field\":\"food\",\"type\":\"string\",\"input\":\"text\",\"operator\":\"is_not_null\",\"value\":null},{\"id\":\"food\",\"field\":\"food\",\"type\":\"string\",\"input\":\"text\",\"operator\":\"is_empty\",\"value\":null},{\"id\":\"food\",\"field\":\"food\",\"type\":\"string\",\"input\":\"text\",\"operator\":\"is_not_empty\",\"value\":null}],\"valid\":true}";

    private static HikariDataSource dataSource;

    @BeforeClass
    public static void init() {
        final HikariConfig config = new HikariConfig();
        config.setPoolName("H2_EMBEDDED");
        config.setJdbcUrl("jdbc:h2:file:./target/testdb");
        dataSource = new HikariDataSource(config);
    }

    @Test
    public void sanityCheck() {
        assert dataSource != null && !dataSource.isClosed();
        assert DSL.using(dataSource, SQLDialect.H2).selectCount().from(EMPLOYEES).fetchOne(0, int.class) == 8;
    }

    @Test
    public void nameLike() {
        Map<String, Object> filter = jsonToMap(FULLNAME_EQUALS_FILTER);
        Condition condition = JQB2JOOQ.parse(ruleTargetBuilder, filter);

        try (DSLContext dsl = DSL.using(dataSource, SQLDialect.H2)) {
            List<Employees> employees = dsl.select().from(EMPLOYEES)
                    .where(condition).fetchInto(Employees.class);

            assert employees.size() == 1;
            assert employees.get(0).getId().equals(UUID.fromString("7293357b-8c09-4662-8400-c7fb73d8ab1c"));
        }
    }

    @Test
    public void dobBetween() {
        Map<String, Object> filter = jsonToMap(DOB_BETWEEN_FILTER);
        Condition condition = JQB2JOOQ.parse(ruleTargetBuilder, filter);

        try (DSLContext dsl = DSL.using(dataSource, SQLDialect.H2)) {
            List<Employees> employees = dsl.select().from(EMPLOYEES)
                    .where(condition).fetchInto(Employees.class);

            assert employees.size() == 4;
        }
    }

    @Test
    public void salaryLessThanOrEqual() {
        Map<String, Object> filter = jsonToMap(SALARY_LESS_OR_EQUAL_FILTER);
        Condition condition = JQB2JOOQ.parse(ruleTargetBuilder, filter);

        try (DSLContext dsl = DSL.using(dataSource, SQLDialect.H2)) {
            List<Employees> employees = dsl.select().from(EMPLOYEES)
                    .innerJoin(PAYROLL).on(EMPLOYEES.ID.eq(PAYROLL.EMPLOYEE_ID))
                    .where(condition).fetchInto(Employees.class);

            assert employees.size() == 3;
        }
    }

    @Test
    public void nestedDOBHourly() {
        Map<String, Object> filter = jsonToMap(NESTED_DOB_HOURLY_FILTER);
        Condition condition = JQB2JOOQ.parse(ruleTargetBuilder, filter);

        try (DSLContext dsl = DSL.using(dataSource, SQLDialect.H2)) {
            List<Employees> employees = dsl.select().from(EMPLOYEES)
                    .innerJoin(PAYROLL).on(EMPLOYEES.ID.eq(PAYROLL.EMPLOYEE_ID))
                    .where(condition).fetchInto(Employees.class);

            assert employees.size() == 3;
        }
    }

    @Test
    public void lazyCompleteTestCoverageGrabBag() {
        Map<String, Object> filter = jsonToMap(GRAB_BAG_FILTER);
        Condition condition = JQB2JOOQ.parse(ruleTargetBuilder, filter);

        try (DSLContext dsl = DSL.using(dataSource, SQLDialect.H2)) {
            List<Employees> employees = dsl.select().from(EMPLOYEES)
                    .innerJoin(PAYROLL).on(EMPLOYEES.ID.eq(PAYROLL.EMPLOYEE_ID))
                    .where(condition).fetchInto(Employees.class);

            assert employees.size() == 0;
        }
    }

    private static Map<String, Object> jsonToMap(String json) {
        return GSON.fromJson(json, MAP_TYPE);
    }

    private static RuleTargetBuilder ruleTargetBuilder = name -> TestFilterTargets.valueOf(name.toUpperCase());
}
