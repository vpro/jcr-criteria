package nl.vpro.jcr.criteria.query;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Michiel Meeuwissen
 * @since ...
 */

public class JCRQueryExceptionTest {
    @Test
    public void test() {
        JCRQueryException e = new JCRQueryException(Criteria.Expression.xpath("xpath"), new Exception());
        assertThat(e.getLanguage()).isEqualTo("xpath");
        assertThat(e.getStatement()).isEqualTo("xpath");
        assertThat(e.getMessage()).isEqualTo("An error occurred while executing a xpath query. Query was 'xpath'. Exception message is null");

    }
}
