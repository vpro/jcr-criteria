package nl.vpro.jcr.criteria.query;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.CriteriaTestUtils;

import static nl.vpro.jcr.criteria.CriteriaTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class DirectJcrQueryTest {

    @BeforeMethod
    public void setup() throws RepositoryException {
        CriteriaTestUtils.setup();
        getSession();
        CriteriaTestUtils.defineA();
        for (long i = 0; i < 100; i++) {
            Node n = root.addNode("n" + i);
            n.setPrimaryType("a");
            n.setProperty("long", i);
            n.setProperty("title", "title " + i);
        }

        session.save();
    }

    @AfterMethod
    public void shutdown() {
       CriteriaTestUtils.shutdown();
    }


    @Test
    public void paging() {
        DirectJcrQuery directJcrQuery = new DirectJcrQuery(Criteria.Expression.sql2("select * from [a] order by [long]"));
        directJcrQuery.setPaging(10, 5);
        AdvancedResult execute = directJcrQuery.execute(session);
        assertThat(execute.getPage()).isEqualTo(5);
        assertThat(execute.getItems().next().getTitle()).isEqualTo("title 40");
    }

}
