package nl.vpro.jcr.criteria.advanced.impl;

import lombok.extern.slf4j.Slf4j;

import org.testng.Assert;
import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.criterion.Order;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;


/**
 * @author Michiel Meeuwissen
 * @since 1.1
 */

@Slf4j
public class AdvancedCriteriaImplTest {

    @Test
    public void testToString() {
        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setBasePath("/")
            .add(Restrictions.eq("MetaData/@mgnl:template", "t-photogallery-sheet"))
            .add(Restrictions.isNotNull("@playlist"))
            .addOrder(Order.desc("@photogalleryDate"));
        Assert.assertEquals(criteria.toString(), "criteria[MetaData/@mgnl:template=t-photogallery-sheet, @playlist not null] order by [@photogalleryDate descending]");


    }
}
