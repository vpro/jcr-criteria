package nl.vpro.jcr.criteria.advanced.impl;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

import org.testng.annotations.*;

import nl.vpro.jcr.criteria.CriteriaTestUtils;
import nl.vpro.jcr.criteria.query.AdvancedResult;

import static nl.vpro.jcr.criteria.CriteriaTestUtils.*;
import static nl.vpro.jcr.criteria.query.JCRCriteriaFactory.builder;


/**
 * @author Michiel Meeuwissen
 * @since 1.1
 */

@Slf4j
public class PerformancelITest {


    @SuppressWarnings("deprecation")
    @DataProvider(name = "language")
    public static Object[] language() {
        return new Object[]{"", Query.XPATH, Query.JCR_SQL2};
    }




    int desired = 10;

    @BeforeClass
    public void setup() throws RepositoryException {
        CriteriaTestUtils.setup();
        getSession();
        CriteriaTestUtils.defineA();
        CriteriaTestUtils.defineB();
        long count = 0;
        for (int i = 0; i < desired - 1; i++) {
            Node n1 = root.addNode("n" + i);
            n1.setProperty("a", "a1"); // a at start
            n1.setProperty("long", i);
            count++;
            for (int j = 0; j < desired; j++) {
                Node n2 = n1.addNode("n" + i + "_" + j);
                n2.setProperty("a", "a1"); // a at start
                n2.setProperty("long", i * desired + j);
                count++;
                for (int k = 0; k < 10; k++) {
                    Node n3= n2.addNode("n" + i + "_" + j + "_" + k);
                    n3.setProperty("a", "a1");
                    n3.setProperty("long", (i * desired+ j) * desired + k);
                    count++;
                }
            }
        }
        log.info("Created {} nodes", count);

        session.save();
        log.info("Saved {} nodes", count);
    }

    @AfterClass
    public void shutdown() {
       CriteriaTestUtils.shutdown();
    }

    @Test(dataProvider = "language")
    public void run(String language) {
        int page = 1;
        while(true) {
            AdvancedCriteriaImpl.Builder criteria = builder()
                .basePath("/n" + (desired / 2))
                .paging(10, page++)
                .score();

            AdvancedResult execute = criteria.build().execute(session, language);
            long start = System.nanoTime();
            log.info("{}", execute.getTotalSize());
            log.info("Took {}", Duration.ofNanos(System.nanoTime() - start));
            if (execute.getItems().getSize()== 0) {
                break;
            }
        }


    }
}
