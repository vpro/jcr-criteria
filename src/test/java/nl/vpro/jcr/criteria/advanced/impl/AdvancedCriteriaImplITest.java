package nl.vpro.jcr.criteria.advanced.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.fs.local.FileUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.query.AdvancedResultItem;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.criterion.MatchMode;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;

import static nl.vpro.jcr.criteria.query.JCRCriteriaFactory.builder;
import static org.testng.AssertJUnit.*;


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


    Repository repository;
    Path tempDirectory;
    Path tempFile;
    Session session;
    Node root;



    @BeforeMethod
    public void setup() throws IOException, RepositoryException {
        // Using jackrabbit memory only seems to be impossible. Sad...
        tempDirectory = Files.createTempDirectory("criteriatest");
        System.setProperty("derby.stream.error.file", new File(tempDirectory.toFile(), "derby.log").toString());
        tempFile = Files.createTempFile("repository", ".xml");
        Files.copy(getClass().getResourceAsStream("/repository.xml"), tempFile, StandardCopyOption.REPLACE_EXISTING);
        FileUtil.delete(tempDirectory.toFile());
        repository = new TransientRepository(tempFile.toFile(), tempDirectory.toFile());;
        session = getSession();
        root = session.getRootNode();
    }
    @AfterMethod
    public void shutdown() {
        try {
            FileUtils.deleteDirectory(tempDirectory.toFile());
            Files.deleteIfExists(tempFile);
        } catch (IOException ioe) {
            log.warn(ioe.getMessage(), ioe);
        }
        log.info("Removed " + tempDirectory + " and " + tempFile);
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

        AdvancedResultImpl xpath = directXpath("//element(*, nt:unstructured)[@a='true']");

        log.info("{}", xpath.getItems());
        {
            check(
                builder()
                    .language(language)
                    .type(NodeType.NT_UNSTRUCTURED)
                    .basePath("/")
                    .add(Restrictions.attrEq("a", Boolean.TRUE)),
                2);

                ;
            check(builder().language(language)
                    .type(NodeType.NT_UNSTRUCTURED)
                    .basePath("/")
                    .add(Restrictions.attrIsFalsy("a")),
                2); // hello2, byte2
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
            check(builder().language(language)
                    .type(NodeType.NT_UNSTRUCTURED)
                    .add(Restrictions.isNotNull("@a")),
                3);
            check(
                builder()
                    .language(language)
                    .type(NodeType.NT_UNSTRUCTURED)
                    .add(Restrictions.isNull("@a")),
                1); // goodbye

                ;

        }
    }

    void check(AdvancedCriteriaImpl.Builder builder, int expectedSize) {
        check(builder.build(), expectedSize);
    }
    @SneakyThrows
    void check(Criteria criteria, int expectedSize) {
        AdvancedResultImpl result = (AdvancedResultImpl) criteria.execute(session);
        for (AdvancedResultItem item : result) {
            log.info("{} {}", item.getJCRNode().getPrimaryNodeType().getName(), item);
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

    Session getSession() throws RepositoryException {
        return repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }



}
