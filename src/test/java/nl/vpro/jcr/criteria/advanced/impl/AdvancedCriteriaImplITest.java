package nl.vpro.jcr.criteria.advanced.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.CriteriaTestUtils;
import nl.vpro.jcr.criteria.query.AdvancedResult;
import nl.vpro.jcr.criteria.query.AdvancedResultItem;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.ExecutableQuery;
import nl.vpro.jcr.criteria.query.criterion.MatchMode;
import nl.vpro.jcr.criteria.query.criterion.Op;
import nl.vpro.jcr.criteria.query.criterion.Order;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;
import nl.vpro.jcr.criteria.query.impl.Column;

import static javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED;
import static nl.vpro.jcr.criteria.CriteriaTestUtils.*;
import static nl.vpro.jcr.criteria.query.JCRCriteriaFactory.builder;
import static nl.vpro.jcr.criteria.query.criterion.Restrictions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;


/**
 * @author Michiel Meeuwissen
 * @since 1.1
 */

@Slf4j
public class AdvancedCriteriaImplITest {


    @SuppressWarnings("deprecation")
    @DataProvider(name = "language")
    public static Object[][] language() {
        return new Object[][] {{Query.XPATH}, {Query.JCR_SQL2}, {""}};
    }


    @DataProvider(name = "sql2only")
    public static Object[][] sql2only() {
        return new Object[][] {{Query.JCR_SQL2}, {""}};
    }





    @BeforeMethod
    public void setup() throws RepositoryException {
        CriteriaTestUtils.setup();
        getSession();
        CriteriaTestUtils.defineA();
        CriteriaTestUtils.defineB();
    }

    @AfterMethod
    public void shutdown() {
       CriteriaTestUtils.shutdown();
    }

    @Test(dataProvider = "language")
    public void like(String language) throws RepositoryException {
        {
            Node n1 = root.addNode("n1");
            n1.setProperty("a", "a1"); // a at start
            Node n2 = root.addNode("n2");
            n2.setProperty("a", "ba"); // at at begin
            Node n3 = root.addNode("n3");
            n3.setProperty("a", "a2"); // another a at start

            Node n4 = root.addNode("n4");
            n4.setProperty("a", "b a c"); // a, but neither at start, of at end
            Node n5 = root.addNode("n5");
            n5.setProperty("a", "b c d"); //no a at all
            session.save();
        }
        {
            Criteria criteria =
                builder()
                    .basePath("/")
                    .column(Column.ALL)
                    //.column(Column.EXCERPT) TODO Does't work
                    .add(Restrictions.attrLike("a", "a", MatchMode.START))
                    .build()
                ;
            AdvancedResult result = check(criteria, language, 2);
            for (AdvancedResultItem item : result) {
                log.info("{}", item.getExcerpt());
            }
        }
        {
            Criteria criteria =
                builder()
                    .basePath("/")
                    .column(Column.ALL)
                    .add(Restrictions.attrLike("a", "a"))
                    .build()
                ;
            AdvancedResult result = check(criteria, language, 4);
            for (AdvancedResultItem item : result) {
                log.info("{}", item.getExcerpt());
            }
        }
        {
            Criteria criteria =
                builder()
                    .basePath("/")
                    .column(Column.ALL)
                    .add(Restrictions.attrLike("a", "a", MatchMode.END))
                    .build()
                ;
            AdvancedResult result = check(criteria, language, 1);
            for (AdvancedResultItem item : result) {
                log.info("{}", item.getExcerpt());
            }
        }
        {
            Criteria criteria =
                builder()
                    .basePath("/")
                    .column(Column.ALL)
                    .add(Restrictions.attrLike("a", "b % c", MatchMode.NONE))
                    .build()
                ;
            AdvancedResult result = check(criteria, language, 1);
            for (AdvancedResultItem item : result) {
                log.info("{}", item.getExcerpt());
            }
            assertThat(result.getFirstResult().getHandle()).isEqualTo("/n4");
        }
    }


    @Test(dataProvider = "language")
    public void uuidMatch(String language) throws RepositoryException {
        String helloId;
        {
            Node hello = root.addNode("hello");
            hello.setProperty("a", "04df24e2-adc0-491b-8573-40a1155665d4");
            hello.addMixin("mix:referenceable");
            Node hello2 = root.addNode("hello2");
            hello2.setProperty("a", "16e86c92-71ce-4779-9a0d-6d59dabc7398");
            Node goodbye = root.addNode("bye");
            goodbye.setProperty("a", "a2");
            session.save();
            helloId = hello.getIdentifier();
        }
        log.info("{}", helloId);
        showSession();
        {
            Criteria criteria =
                builder()
                    .basePath("/")
                    .add(Restrictions.attrEq("a", UUID.fromString("16e86c92-71ce-4779-9a0d-6d59dabc7398")))
                    .build()
                ;
            check(criteria, language, 1);
        }
         {
            Criteria criteria =
                builder()
                    .basePath("/")
                    .add(Restrictions.attrEq("jcr:uuid", UUID.fromString(helloId)))
                    .build()
                ;
            check(criteria, language, 1);
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
                    .type(NT_UNSTRUCTURED)
                    .basePath("/")
                    .add(attrIsTrue("a")),
                language,
                2);


            check(builder()
                    .type(NT_UNSTRUCTURED)
                    .basePath("/")
                    .add(attrIsFalsy("a")),
                language,
                3); // hello2, byte2, root

             check(builder()
                    .type(NT_UNSTRUCTURED)
                    .basePath("/")
                    .add(ne(attr("a"), Boolean.FALSE)),
                language,
                4); // root, hello, byte, bye2
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
                    .type(NT_UNSTRUCTURED)
                    .add(Restrictions.has("@a")),
                language, 3);
            check(
                builder()
                    .type(NT_UNSTRUCTURED)
                    .add(Restrictions.hasnt("@a")),
                language,
                2); // goodbye and root


        }
    }


    @Test(dataProvider = "language")
    public void betweenStringsAndEq(String language) throws RepositoryException {
        {
            for (long i = 0; i < 100; i++) {
                Node n = root.addNode("n" + i);
                LocalDateTime dateTime = LocalDate.of(2019, 1, 1).plusDays(i).atStartOfDay();
                n.setPrimaryType("a");
                n.setProperty("title", String.format("title%02d", i));
            }

            session.save();
        }
        {
            check(builder()
                .type("a")
                .asc(attr("title"))
                .add(
                    between(attr("title"),
                        "title20",
                        "title25")
                ), language, 6);

        }
        {
            check(builder()
                .type("a")
                .asc(attr("title"))
                .add(
                    ne(attr("title"),
                        "title24")
                ), language, 99);

        }
         {
            check(builder()
                .type("a")
                .asc(attr("title"))
                .add(
                    attrEq("title", "title24")
                ), language, 1);

        }
    }


    @Test(dataProvider = "language")
    public void betweenAndEqLong(String language) throws RepositoryException {
        {
            for (long i = 0; i < 10; i++) {
                Node n = root.addNode("n" + i);
                n.setPrimaryType("a");
                n.setProperty("long", i);
            }

            session.save();
        }
        {
            AdvancedResult result = check(builder()
                    .fromUnstructured()
                    .type("a")
                    .desc(attr("long"))
                    .add(between(attr("long"), 4, false, 8, true)),
                language,
                4); // 5, 6, 7, 8

            List<Long> longs = result.stream().map(n -> longProp(n, "long")).collect(Collectors.toList());
            assertThat(longs).containsExactly(8L, 7L, 6L, 5L);
        }
        {
            AdvancedResult result = check(builder()
                .fromUnstructured()
                .type("a")
                .desc(attr("long"))
                .add(attrEq("long", 4)),
                language,
            1);
            assertThat(result.getFirstResult().getProperty("long").getLong()).isEqualTo(4);
        }
        {
            AdvancedResult result = check(builder().fromUnstructured()
                    .type("a")
                    .desc(attr("long"))
                    .add(le(attr("long"), 4)),
                language,
                5);
            assertThat(result.getFirstResult().getProperty("long").getLong()).isEqualTo(4);
        }
        {
            AdvancedResult result = check(builder().fromUnstructured()
                    .type("a")
                    .desc(attr("long"))
                    .add(lt(attr("long"), 4)),
                language,
                4);
            assertThat(result.getFirstResult().getProperty("long").getLong()).isEqualTo(3);
        }
        {
            AdvancedResult result = check(builder().fromUnstructured()
                    .type("a")
                    .asc(attr("long"))
                    .add(ne(attr("long"), 4)),
                language,
                9);
            assertThat(result.getFirstResult().getProperty("long").getLong()).isEqualTo(0);
        }
         {
            AdvancedResult result = check(builder().fromUnstructured()
                    .type("a")
                    .asc(attr("long"))
                    .add(ge(attr("long"), 4)),
                language,
                6);
            assertThat(result.getFirstResult().getProperty("long").getLong()).isEqualTo(4);
        }
         {
            AdvancedResult result = check(builder().fromUnstructured()
                    .type("a")
                    .asc(attr("long"))
                    .add(gt(attr("long"), 4)),
                language,
                5);
            assertThat(result.getFirstResult().getProperty("long").getLong()).isEqualTo(5);
        }
    }

    @Test(dataProvider = "language")
    public void betweenDatesAndEq(String language) throws RepositoryException {
        {
            for (long i = 0; i < 10; i++) {
                Node n = root.addNode("n" + i);
                LocalDateTime dateTime = LocalDate.of(2019, 1, 1).plusDays(i).atStartOfDay();
                n.setPrimaryType("a");
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.setTimeZone(TimeZone.getTimeZone("Asia/Tashkent"));
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
                .timeZone(ZoneId.of("Asia/Tashkent"))
                .add(
                    between(attr("date"),
                        LocalDate.of(2019, 1, 5),
                        LocalDate.of(2019, 1, 8))
                ), language, 4);

        }
        {
            check(builder()
                .type("a")
                .asc(attr("date"))
                .timeZone(ZoneId.of("Asia/Tashkent"))
                .add(
                    attrEq("date",
                        LocalDate.of(2019, 1, 6))
                ), language, 1);

        }
    }



    @Test(dataProvider = "language")
    public void betweenLocalDatesAndInstancesAndFindFirst(String language) throws RepositoryException {
        {
            for (long i = 0; i < 10; i++) {
                Node n = root.addNode("n" + i);
                LocalDateTime dateTime = LocalDate.of(2019, 1, 1)
                    .atStartOfDay().plusMinutes(10 * i);
                n.setPrimaryType("a");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("Asia/Tashkent"));
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
                .timeZone(ZoneId.of("Asia/Tashkent"))
                .add(
                    between(attr("date"),
                        LocalDateTime.of(2019, 1, 1, 0, 12),
                        LocalDateTime.of(2019, 1, 1, 0, 50))
                ), language, 4); // 20, 30, 40, 50

        }
        {
            check(builder()
                .type("a")
                .asc(attr("date"))
                .timeZone(ZoneId.of("Asia/Tashkent"))
                .add(
                    attrEq("date", LocalDateTime.of(2019, 1, 1, 0, 30))
                ), language, 1);

        }
        {
          check(builder()
                .type("a")
                .asc(attr("date"))
                .timeZone(ZoneId.of("Asia/Tashkent"))
                .add(
                    ne(attr("date"), LocalDateTime.of(2019, 1, 1, 0, 30))
                ), language, 9);

        }
        {
            check(builder()
                .type("a")
                .asc(attr("date"))
                .add(
                    between(attr("date"),
                        LocalDateTime.of(2019, 1, 1, 0, 12).atZone(ZoneId.of("Asia/Tashkent")).toInstant(),
                        LocalDateTime.of(2019, 1, 1, 0, 50).atZone(ZoneId.of("Asia/Tashkent")).toInstant())
                ), language, 4); // 20, 30, 40, 50

        }
        {
            check(builder()
                .type("a")
                .asc(attr("date"))
                .add(
                    attrEq("date", LocalDateTime.of(2019, 1, 1, 0, 30).atZone(ZoneId.of("Asia/Tashkent")).toInstant())
                ), language, 1);

        }
        {
            check(builder()
                .type("a")
                .asc(attr("date"))
                .add(
                    ne(attr("date"), LocalDateTime.of(2019, 1, 1, 0, 30).atZone(ZoneId.of("Asia/Tashkent")).toInstant())
                ), language, 9);

        }
        {
            check(builder()
                .type("a")
                .asc(attr("date"))
                .add(
                    between(attr("date"),
                        GregorianCalendar.from(LocalDateTime.of(2019, 1, 1, 0, 12).atZone(ZoneId.of("Asia/Tashkent"))),
                        GregorianCalendar.from(LocalDateTime.of(2019, 1, 1, 0, 50).atZone(ZoneId.of("Asia/Tashkent"))))
                ), language, 4); // 20, 30, 40, 50

        }
        {
            check(builder()
                .type("a")
                .asc(attr("date"))
                .add(
                    attrEq("date", GregorianCalendar.from(LocalDateTime.of(2019, 1, 1, 0, 30).atZone(ZoneId.of("Asia/Tashkent"))))
                ), language, 1);

        }
        {
            check(builder()
                .type("a")
                .asc(attr("date"))
                .add(
                    ne(attr("date"), GregorianCalendar.from(LocalDateTime.of(2019, 1, 1, 0, 30).atZone(ZoneId.of("Asia/Tashkent"))))
                ), language, 9);

        }
        {
            Optional<Node> firstNode = builder().type("a").asc(attr("date"))
                .build().findFirst(session);
            assertThat(firstNode.get().getPath()).isEqualTo("/n0");
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
            .score();


        check(criteria, language,2);

    }



    @Test(dataProvider = "language")
    public void in(String language) throws RepositoryException {

        {
            Node node1 = root.addNode("node1");
            node1.setProperty("a", "a");

            Node node2 = node1.addNode("node2");
            node2.setProperty("a", "x y b c z");

            Node node2_1 = node2.addNode("node2_1");
            node2_1.setProperty("a", "c");

            Node node3 = root.addNode("node3");
            node3.setProperty("a", "d");

            session.save();
        }

        AdvancedCriteriaImpl.Builder criteria = builder()
            .add(Restrictions.in(attr("a"), "b", "c"))
            .add(Order.SCORE);


        AdvancedResult result = check(criteria, language, 2);
        Iterator<AdvancedResultItem> i = result.iterator(); ;
        AdvancedResultItem item1 = i.next();
        AdvancedResultItem item2 = i.next();
        assertThat(i.hasNext()).isFalse();
        try {
            i.next();
            fail("Should have thrown exception");
        } catch(NoSuchElementException ignored) {

        }
        assertThat(item1.getScore()).isGreaterThan(item2.getScore());
        assertThat(item1.getHandle()).isEqualTo("/node1/node2");
        assertThat(item2.getHandle()).isEqualTo("/node1/node2/node2_1");
    }


    @Test(dataProvider = "sql2only") // TODO support XPATH too?
    public void isChild(String language) throws RepositoryException {

        Node node2;
        {
            Node node1 = root.addNode("node1");
            node2 = node1.addNode("node2");
            Node node2_1 = node2.addNode("node2_1");
            Node node2_2 = node2.addNode("node2_2");
            Node node2_1_1 = node2_1.addNode("node2_1_1");
            Node node3 = root.addNode("node3");
            session.save();
        }

        AdvancedCriteriaImpl.Builder criteria = builder()
            .add(Restrictions.isChildOf(node2))
            .order(Order.desc("@jcr:score"));


        check(criteria, language,2); // node 2_1 and 2_2 but not 2_1_1
    }


    @Test(dataProvider = "sql2only")
    public void isDescendantOrSame(String language) throws RepositoryException {

        Node node2;
        {
            Node node1 = root.addNode("node1");
            node2 = node1.addNode("node'&2");
            Node node2_1 = node2.addNode("node2_1");
            Node node2_2 = node2.addNode("node2_2");
            Node node2_1_1 = node2_1.addNode("node2_1_1");
            Node node3 = root.addNode("node3");
            session.save();
        }

        AdvancedCriteriaImpl.Builder criteria = builder()
            .add(Restrictions.isDescendantOf(node2))
            .score();


        check(criteria, language,3); // node 2_1 and 2_2 and also  2_1_1



        AdvancedCriteriaImpl.Builder criteriaOrSame = builder()
            .add(Restrictions.or(
                Restrictions.isDescendantOf(node2),
                Restrictions.isSame(node2))
            )
            .score();

        check(criteriaOrSame, language,4); // node 2_1 and 2_2 and also  2_1_1, and node2 itself




    }


    @Test(dataProvider = "language")
    @SneakyThrows
    public void pagingAndWrapping(String language) {
        {
            for (int i = 0; i < 100; i++) {
                Node n = root.addNode("node" + i);
                n.setProperty("integer", i);
            }
            session.save();
        }


        ExecutableQuery query = builder()
            .fromUnstructured()
            .paging(10, 2)
            .asc("@integer")
            .build();


        AdvancedResult result = query.execute(session, language);
        assertThat(result.getItemsPerPage()).isEqualTo(10);
        assertThat(result.getTotalSize()).isEqualTo(101); // those 100 plus root.
        List<String> test = new ArrayList<>();
        double[] score = new double[1];
        score[0] = -1d;
        result.getItems(row -> {
            try {
                if (score[0] > 0) {
                    assertThat(score[0]).isEqualTo(row.getScore());
                }
                score[0] = row.getScore();
                return row.getNode().getPath();
            } catch(Exception e) {
                return e.getMessage();
            }

        }).forEachRemaining((c) -> test.add((String) c));
        assertThat(test).containsExactly(
            "/node9",
            "/node10",
            "/node11",
            "/node12",
            "/node13",
            "/node14",
            "/node15",
            "/node16",
            "/node17",
            "/node18");
        assertThat(result.getPage()).isEqualTo(2);
        assertThat(result.getNumberOfPages()).isEqualTo(11);
    }

    @Test(dataProvider = "language")
    @SneakyThrows
    public void not(String language) {
        {
            for (int i = 0; i < 10; i++) {
                Node n = root.addNode("node" + i);
                n.setProperty("integer", i);
            }
            session.save();
        }
        check(builder()
            .fromUnstructured()
            .add(Restrictions.isNotNull(attr("integer")))
            .add(Restrictions.not(Restrictions.eq(attr("integer"), 5)))
            .asc(attr("integer")), language, 9);

    }

    @Test(dataProvider = "language")
    @SneakyThrows
    public void nodeTypes(String language) {
         {
            for (int i = 0; i < 10; i++) {
                Node n = root.addNode("node" + i);
                n.setPrimaryType(new String[]{"a", "b", NT_UNSTRUCTURED}[i % 3]);
            }
             session.save();
         }
         check(builder()
             .type("a")
             ,language, 4);

         check(builder()
             .add(hasNodeType("a", "b"))
             ,language, 7);

    }


    @Test(dataProvider = "language")
    @SneakyThrows
    public void op(String language) {
        {
            for (int i = 0; i < 10; i++) {
                Node n = root.addNode("node" + i);
                n.setPrimaryType("a");
                n.setProperty("long", i);
                n.setProperty("title", "abcdefghijklm".substring(i, i + 2));
            }
            session.save();
        }


        check(builder()
                .add(Restrictions.attrOp(Op.EQ, "long", 5))
             ,language, 1);

        {
            AdvancedResult check = check(builder()
                    .add(attrOp(Op.LT, "title", "d"))
                    .desc(attr("title"))
                , language, 3);
            assertThat(check.getFirstResult().getTitle()).isEqualTo("cd");
        }
        {
            AdvancedResult check = check(builder()
                .add(attrOp(Op.LT, "title", "a"))
                .desc(attr("title"))
                , language, 0);
            assertThat(check.getFirstResult()).isNull();
        }

    }

     @Test(dataProvider = "language")
    @SneakyThrows
    public void localPaging(String language) {
         {
            for (int i = 0; i < 50; i++) {
                Node n = root.addNode("node" + i);
                n.setPrimaryType("a");
                n.setProperty("long", i);
                n.setProperty("media", "abcdefghijklmopqrstuvw".substring(i % 20, (i % 20) + 2));
            }
             session.save();
         }


         AdvancedResult result = check(builder()
                 .paging(10, 2)
                 .type("a")
                 .order(Order.desc(attr("long")))
                 .forcePagingWithDocumentOrder(true)
             ,language, 50);
         assertThat(result).hasSize(10);
         assertThat(result.getNumberOfPages()).isEqualTo(5);
         assertThat(result.getItems().getPosition()).isEqualTo(0);



    }


    AdvancedResult check(AdvancedCriteriaImpl.Builder builder, String language,  int expectedSize) {
        return check(builder.build(), language, expectedSize);
    }
    @SneakyThrows
    AdvancedResult  check(Criteria criteria, String language, int expectedSize) {
        AdvancedResultImpl result = (AdvancedResultImpl) criteria.execute(session, language);
        for (AdvancedResultItem item : result) {
            boolean isUnstructured = item.isNodeType(NT_UNSTRUCTURED);
            log.info("{} {} (is unstructured: {})", item.getPrimaryNodeType().getName(), item, isUnstructured);
        }
        assertFalse(result.totalSizeDetermined());
        assertEquals(expectedSize, result.getTotalSize());
        assertTrue(result.totalSizeDetermined());
        return result;

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
     AdvancedResultImpl directSql(String expression) {
        return QueryExecutorHelper.execute(
            Criteria.Expression.sql2(expression),
            () -> {
                throw new UnsupportedOperationException();

            },
            session,
            null,
            0,
            null,
            false);

    }


    @SneakyThrows
    long longProp(Node node, String name) {
        return node.getProperty(name).getLong();
    }


    @SneakyThrows
    protected void showSession() {
        AtomicLong count = new AtomicLong(0);
        showSession(session.getRootNode(), count);
        log.info("Found {} unstructured", count.get());
    }
    @SneakyThrows
    protected void showSession(Node node, AtomicLong count) {

        boolean isUnstructured =  node.isNodeType(NT_UNSTRUCTURED);
        log.info("{} {} {}", node.getPrimaryNodeType().getName(), node, isUnstructured);
        if (isUnstructured) {
            count.incrementAndGet();
        }
        NodeIterator n = node.getNodes();

        while(n.hasNext()) {
            Node next = n.nextNode();
          /*  PropertyIterator properties = next.getProperties("jcr:*");
            while(properties.hasNext()) {
                log.info("{}", properties.nextProperty().getName());
            }*/
            showSession(next, count);
        }
    }
}
