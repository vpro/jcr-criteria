package nl.vpro.jcr.criteria.advanced.impl;

import javax.jcr.*;

import org.apache.jackrabbit.commons.JcrUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.query.AdvancedResultItem;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.criterion.Order;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;


/**
 * @author Michiel Meeuwissen
 * @since 1.1
 */

public class AdvancedCriteriaImplTest {
    Repository repository;


    @BeforeSuite
    public void setup() throws RepositoryException {
        repository = JcrUtils.getRepository();
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
    public void testList() throws RepositoryException {
        {
            Session session = getSession();
            Node root = session.getRootNode();
            Node hello = root.addNode("hello");
            hello.setProperty("a", "a");
            session.save();
        }
        {
            Criteria criteria =
                JCRCriteriaFactory.createCriteria()
                    .setBasePath("/")
                    .add(Restrictions.eq("hello/@a", "a"))
                    .addOrderByScore()
                ;


            AdvancedResultImpl result = (AdvancedResultImpl) criteria.execute(getSession());
            for (AdvancedResultItem item : result.getItems()) {
                System.out.println(item);
            }
            assertFalse(result.totalSizeDetermined());
            assertEquals(1, result.getTotalSize());
            assertTrue(result.totalSizeDetermined());
        }
    }

    Session getSession() throws RepositoryException {
        return repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }



}
