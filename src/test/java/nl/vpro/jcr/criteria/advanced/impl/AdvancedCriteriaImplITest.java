package nl.vpro.jcr.criteria.advanced.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import javax.jcr.query.Query;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.CriteriaTestUtils;
import nl.vpro.jcr.criteria.query.AdvancedResultItem;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.criterion.MatchMode;
import nl.vpro.jcr.criteria.query.criterion.Order;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;

import static nl.vpro.jcr.criteria.CriteriaTestUtils.*;
import static nl.vpro.jcr.criteria.query.JCRCriteriaFactory.builder;
import static nl.vpro.jcr.criteria.query.criterion.Restrictions.attr;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;


/**
 * @author Michiel Meeuwissen
 * @since 1.1
 */

@Slf4j
public class AdvancedCriteriaImplITest {


   @DataProvider(name = "language")
   public static Object[][] language() {
      return new Object[][] {{Query.XPATH}, {Query.JCR_SQL2}, {""}};
   }






    @BeforeMethod
    public void setup() throws RepositoryException {
        CriteriaTestUtils.setup();
        Session session = getSession();

        { // define a
            NodeTypeManager nodeTypeManager = session.getWorkspace().getNodeTypeManager();
            NodeTypeTemplate a  = nodeTypeManager.createNodeTypeTemplate();
            a.setName("a");
            {
                PropertyDefinitionTemplate media  = nodeTypeManager.createPropertyDefinitionTemplate();
                media.setName("media");
                media.setRequiredType(PropertyType.STRING);
                a.getPropertyDefinitionTemplates().add(media);
            }
            {
                PropertyDefinitionTemplate longType   = nodeTypeManager.createPropertyDefinitionTemplate();
                longType.setName("long");
                longType.setRequiredType(PropertyType.LONG);
                a.getPropertyDefinitionTemplates().add(longType);
            }
            {
                PropertyDefinitionTemplate dateType   = nodeTypeManager.createPropertyDefinitionTemplate();
                dateType.setName("date");
                dateType.setRequiredType(PropertyType.DATE);
                a.getPropertyDefinitionTemplates().add(dateType);
            }
            a.setQueryable(true);
            nodeTypeManager.registerNodeType(a, true);
        }
        { // define b

            NodeTypeManager nodeTypeManager = session.getWorkspace().getNodeTypeManager();
            NodeTypeTemplate b  = nodeTypeManager.createNodeTypeTemplate();
            b.setName("b");
            PropertyDefinitionTemplate media  = nodeTypeManager.createPropertyDefinitionTemplate();
            media.setName("media");
            media.setRequiredType(PropertyType.STRING);
            b.getPropertyDefinitionTemplates().add(media);
            b.setQueryable(true);
            nodeTypeManager.registerNodeType(b, true);
        }

    }
    @AfterMethod
    public void shutdown() {
       CriteriaTestUtils.shutdown();
    }


    @Test(dataProvider = "language")
    public void start(String language) throws RepositoryException {
        {
            Node hello = root.addNode("hello");
            hello.setProperty("a", "a1");
            Node hello2 = root.addNode("hello2");
            hello2.setProperty("a", "b");
            Node goodbye = root.addNode("bye");
            goodbye.setProperty("a", "a2");
            session.save();
        }
        {
            Criteria criteria =
                builder()
                    .language(language)
                    .basePath("/")
                    .add(Restrictions.attrLike("a", "a", MatchMode.START))
                    .build()
                ;
            check(criteria, 2);
        }
    }

    @Test(dataProvider = "language")
    public void booleanMatch(String language) throws RepositoryException {
        {
            Node hello = root.addNode("hello");
            hello.setProperty("a", Boolean.TRUE);
            Node hello2 = root.addNode("hello2");
            hello2.setProperty("a", Boolean.FALSE);
            Node goodbye = root.addNode("bye");
            goodbye.setProperty("a", Boolean.TRUE);
            Node goodbye2 = root.addNode("bye2");
            session.save();
        }



        {
            check(
                builder()
                    .type(NodeType.NT_UNSTRUCTURED)
                    .basePath("/")
                    .add(Restrictions.attrEq("a", Boolean.TRUE)),
                language,
                2);

                ;
            check(builder()
                    .type(NodeType.NT_UNSTRUCTURED)
                    .basePath("/")
                    .add(Restrictions.attrIsFalsy("a")),
                language,
                3); // hello2, byte2, root
        }
    }

    @Test(dataProvider = "language")
    public void isNull(String language) throws RepositoryException {
        {
            Node hello = root.addNode("hello");
            hello.setProperty("a", Boolean.TRUE);
            Node hello2 = root.addNode("hello2");
            hello2.setProperty("a", Boolean.FALSE);
            Node goodbye = root.addNode("bye");
            goodbye.setProperty("a", Boolean.TRUE);
            Node goodbye2 = root.addNode("bye2");
            session.save();
        }
        {
            check(builder()
                    .type(NodeType.NT_UNSTRUCTURED)
                    .add(Restrictions.isNotNull("@a")),
                language, 3);
            check(
                builder()
                    .type(NodeType.NT_UNSTRUCTURED)
                    .add(Restrictions.isNull("@a")),
                language,
                2); // goodbye and root

                ;

        }
    }

    @Test(dataProvider = "language")
    public void betweenLong(String language) throws RepositoryException {
        {
            for (long i = 0; i < 10; i++) {
                Node n = root.addNode("n" + i);
                n.setPrimaryType("a");
                n.setProperty("long", i);
            }

            session.save();
        }
        {
            check(builder()
                    .type("a")
                    .asc(attr("long"))
                    .add(Restrictions.between(attr("long"), 4, 8)),
                language,
                5); // 4, 5, 6, 7, 9
        }
    }

    @Test(dataProvider = "language")
    public void betweenDates(String language) throws RepositoryException {
        {
            for (long i = 0; i < 10; i++) {
                Node n = root.addNode("n" + i);
                LocalDateTime dateTime = LocalDate.of(2019, 1, 1).plusDays(i).atStartOfDay();
                n.setPrimaryType("a");
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(dateTime.getYear(), dateTime.getMonthValue() - 1, dateTime.getDayOfMonth(),
                    dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond());
                n.setProperty("date", calendar);
            }

            session.save();
        }
        {
            check(builder()
                    .type("a")
                    .asc(attr("date"))
                    .add(
                        Restrictions.between(attr("date"),
                            LocalDate.of(2019, 1, 5),
                            LocalDate.of(2019, 1, 8))
                    ), language, 4);

        }
    }

    @Test(dataProvider = "language")
    public void withBasePath(String language) throws RepositoryException {
       Node node1;
        {
            node1 = root.addNode("node1");

            Node node2 = node1.addNode("node2");

            Node node2_1 = node2.addNode("node2_1");

            Node node3 = root.addNode("node3");

            session.save();
        }

        AdvancedCriteriaImpl.Builder criteria = builder()
            .basePath(node1.getPath())
            .order(Order.desc("@jcr:score"));


        check(criteria, language,2);

    }

    void check(AdvancedCriteriaImpl.Builder builder, String language,  int expectedSize) {
        check(builder.language(language).build(), expectedSize);
    }
    @SneakyThrows
    void check(Criteria criteria, int expectedSize) {
        AdvancedResultImpl result = (AdvancedResultImpl) criteria.execute(session);
        for (AdvancedResultItem item : result) {
            boolean isUnstructured = item.getJCRNode().isNodeType(NodeType.NT_UNSTRUCTURED);
            log.info("{} {} (is unstructured: {})", item.getJCRNode().getPrimaryNodeType().getName(), item, isUnstructured);
        }
        assertFalse(result.totalSizeDetermined());
        assertEquals(expectedSize, result.getTotalSize());
        assertTrue(result.totalSizeDetermined());

    }


    AdvancedResultImpl directXpath(String expression) {
        return QueryExecutorHelper.execute(
            Criteria.Expression.xpath(expression),
            () -> {
                throw new UnsupportedOperationException();

            },
            session,
            null,
            0,
            null,
            false);

    }



}
