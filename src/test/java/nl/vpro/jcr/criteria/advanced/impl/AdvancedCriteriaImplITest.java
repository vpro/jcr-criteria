package nl.vpro.jcr.criteria.advanced.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.jcr.*;
import javax.jcr.query.Query;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.fs.local.FileUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.query.AdvancedResultItem;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.criterion.MatchMode;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;

import static org.testng.AssertJUnit.*;


/**
 * @author Michiel Meeuwissen
 * @since 1.1
 */

@Slf4j
public class AdvancedCriteriaImplITest {
    Repository repository;
    Path tempDirectory;
    Path tempFile;

    @BeforeMethod
    public void setup() throws IOException {
        // Using jackrabbit memory only seems to be impossible. Sad...
        tempDirectory = Files.createTempDirectory("criteriatest");
        System.setProperty("derby.stream.error.file", new File(tempDirectory.toFile(), "derby.log").toString());
        tempFile = Files.createTempFile("repository", ".xml");
        Files.copy(getClass().getResourceAsStream("/repository.xml"), tempFile, StandardCopyOption.REPLACE_EXISTING);
        FileUtil.delete(tempDirectory.toFile());
        repository = new TransientRepository(tempFile.toFile(), tempDirectory.toFile());;
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


    @Test
    public void testExecuteXPath() throws RepositoryException {
        testExecute(Query.XPATH);
    }
    @Test
    public void testExecuteSQL2() throws RepositoryException {
        testExecute(Query.JCR_SQL2);
    }
    protected void testExecute(String language) throws RepositoryException {
        {
            Session session = getSession();
            Node root = session.getRootNode();
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
                JCRCriteriaFactory.createCriteria()
                    .setBasePath("/")
                    .add(Restrictions.attrLike("a", "a", MatchMode.START))
                //.add(Restrictions.like("hello/jcr:name", "a"))
                    //.addOrderByScore()
                ;


            AdvancedResultImpl result = (AdvancedResultImpl) criteria.execute(getSession(), language);
            for (AdvancedResultItem item : result.getItems()) {
                System.out.println(item);
            }
            assertFalse(result.totalSizeDetermined());
            assertEquals(2, result.getTotalSize());
            assertTrue(result.totalSizeDetermined());
        }
    }

    Session getSession() throws RepositoryException {
        return repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }



}
