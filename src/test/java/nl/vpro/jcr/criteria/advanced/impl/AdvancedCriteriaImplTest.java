package nl.vpro.jcr.criteria.advanced.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.jcr.*;

import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.fs.local.FileUtil;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.query.AdvancedResultItem;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.criterion.MatchMode;
import nl.vpro.jcr.criteria.query.criterion.Order;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;

import static org.testng.AssertJUnit.*;


/**
 * @author Michiel Meeuwissen
 * @since 1.1
 */

public class AdvancedCriteriaImplTest {
    Repository repository;
    Path tempDirectory;
    Path tempFile;

    @BeforeSuite
    public void setup() throws RepositoryException, IOException {
        // Using jackrabbit memory only seems to be impossible. Sad...

        tempDirectory = Files.createTempDirectory("criteriatest");
        tempFile = Files.createTempFile("repository", ".xml");
        Files.copy(getClass().getResourceAsStream("/repository.xml"), tempFile, StandardCopyOption.REPLACE_EXISTING);
        FileUtil.delete(tempDirectory.toFile());
        TransientRepository tr = new TransientRepository(tempFile.toFile(), tempDirectory.toFile());

        repository = tr;
    }
    @AfterSuite
    public void shutdown() throws IOException {
        Files.delete(tempDirectory);
        Files.delete(tempFile);
    }
    @Test
    public void testToString() throws Exception {
        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setBasePath("/")
            .add(Restrictions.eq("MetaData/@mgnl:template", "t-photogallery-sheet"))
            .add(Restrictions.isNotNull("@playlist"))
            .addOrder(Order.desc("@photogalleryDate"));
        Assert.assertEquals(criteria.toString(), "criteria[MetaData/@mgnl:template=t-photogallery-sheet, @playlist not null] order by [@photogalleryDate descending]");


    }

    @Test
    public void testExecute() throws RepositoryException {
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


            AdvancedResultImpl result = (AdvancedResultImpl) criteria.execute(getSession());
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
