/*
 *
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2013, Openmind S.r.l. http://www.openmindonline.it
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nl.vpro.jcr.criteria.query.impl;

import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.criterion.Junction;
import nl.vpro.jcr.criteria.query.criterion.Order;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;

import static org.testng.Assert.assertEquals;


/**
 * @author fgiust
 * @author Michiel Meeuwissen
 */
@SuppressWarnings("deprecation")
public class CriteriaTest {

    @Test
    public void testWithAbsoluteNodePath() {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("/site");
        assertEquals(criteria.toXpathExpression(), "/jcr:root/site//*");
        assertEquals(criteria.toSql2Expression(), "SELECT * from [nt:base] as a WHERE ISCHILDNODE(a, '/site')");
    }

    @Test
    public void testXPathEscaping() {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("/3voor12/nieuws");
        assertEquals( criteria.toXpathExpression(), "/jcr:root/_x0033_voor12/nieuws//*");
        assertEquals( criteria.toSql2Expression(), "SELECT * from [nt:base] as a WHERE ISCHILDNODE(a, '/3voor12/nieuws");
    }

    @Test
    public void testSimple() {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("/site");

        Junction conjunction = Restrictions.conjunction();
        criteria.add(conjunction);
        conjunction.add(Restrictions.eq("@property", "test"));
        conjunction.add(Restrictions.eq("@anotherproperty", "anothertest"));

        assertEquals(criteria.toXpathExpression(), "/jcr:root/site//*[(( (@property='test')  and  (@anotherproperty='anothertest') ) )] ");
        assertEquals(criteria.toSql2Expression(), "/jcr:root/site//*[(( (@property='test')  and  (@anotherproperty='anothertest') ) )] ");
    }

    @Test
    public void testBooleanProperty() {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("/site");

        Junction conjunction = Restrictions.disjunction();
        criteria.add(conjunction);
        conjunction.add(Restrictions.eq("@property", Boolean.FALSE));
        conjunction.add(Restrictions.eq("@anotherproperty", Boolean.TRUE));

        String xpathExpression = criteria.toXpathExpression();
        assertEquals(xpathExpression, "/jcr:root/site//*[(( ((@property=false) or not(@property )) or  (@anotherproperty=true) ) )] ");
    }

    /**
     * Test for CRIT-3
     */
    @Test
    public void testEmptyConjuntion() {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("/site");

        criteria.add(Restrictions.eq("@property", "test"));

        Junction conjunction = Restrictions.conjunction();
        criteria.add(conjunction);

        String xpathExpression = criteria.toXpathExpression();
        assertEquals(xpathExpression, "/jcr:root/site//*[( (@property='test')  )] ");
    }

    /**
     * Test for CRIT-37
     */
    @Test
    public void testEscapeComma() {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("/one/two/3three/fo,ur/");
        criteria.add(Restrictions.eq("@property", "test"));
        String xpathExpression = criteria.toXpathExpression();

        assertEquals(xpathExpression, "/jcr:root/one/two/_x0033_three/fo_x002c_ur//*[( (@property='test')  )] ");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBasePathShouldBeValidNodePath() {
        JCRCriteriaFactory.createCriteria().setBasePath("//site//*");
    }

    @Test
    public void testEquals() {
        Criteria criteria1 = JCRCriteriaFactory.createCriteria()
                .setBasePath("/site")
                .setPaging(10, 5)
                .add(Restrictions.eq("@property", "test"))
                .addOrder(Order.desc("@date"));

        Criteria criteria2 = JCRCriteriaFactory.createCriteria()
                .setBasePath("/site")
                .setPaging(10, 5)
                .add(Restrictions.eq("@property", "test"))
                .addOrder(Order.desc("@date"));

        assertEquals(criteria1, criteria2);
    }

    @Test
    public void testHashCode() {
        Criteria criteria1 = JCRCriteriaFactory.createCriteria()
                .setBasePath("/site")
                .setPaging(10, 5)
                .add(Restrictions.eq("@property", "test"))
                .addOrder(Order.desc("@date"));

        Criteria criteria2 = JCRCriteriaFactory.createCriteria()
                .setBasePath("/site")
                .setPaging(10, 5)
                .add(Restrictions.eq("@property", "test"))
                .addOrder(Order.desc("@date"));

        assertEquals(criteria1.hashCode(), criteria2.hashCode());
    }
}
