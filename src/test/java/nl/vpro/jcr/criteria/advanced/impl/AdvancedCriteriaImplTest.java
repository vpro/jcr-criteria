package nl.vpro.jcr.criteria.advanced.impl;

import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.criterion.Order;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;

/**
 * @author Michiel Meeuwissen
 * @since 1.1
 */

public class AdvancedCriteriaImplTest {


    @Test
    public void testToString() throws Exception {
        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setBasePath("/")
            .add(Restrictions.eq("MetaData/@mgnl:template", "t-photogallery-sheet"))
            .add(Restrictions.isNotNull("@playlist"))
            .addOrder(Order.desc("@photogalleryDate"));
        System.out.println(criteria.toString());

    }

}
