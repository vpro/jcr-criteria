package nl.vpro.jcr.criteria.query.criterion;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.advanced.impl.AdvancedCriteriaImpl;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class BetweenExpressionTest {

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testUnknown() {
        new BetweenExpression("bla", new Unknown(), true, new Unknown(), false).toXPathString(new AdvancedCriteriaImpl());
    }

    @Test
    public void testToString() {
        assertThat(new BetweenExpression("bla", 100, true, 999, false).toString()).isEqualTo("bla between [100, 999>");
    }


    static class Unknown implements Comparable<Unknown> {

        @Override
        public int compareTo(BetweenExpressionTest.@NonNull Unknown o) {
            return 0;

        }
    }
}
