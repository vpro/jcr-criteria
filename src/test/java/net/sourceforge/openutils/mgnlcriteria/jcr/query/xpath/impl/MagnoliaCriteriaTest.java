/**
 *
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2011, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcriteria.jcr.query.xpath.impl;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Junction;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author fgiust
 * @version $Id$
 */
public class MagnoliaCriteriaTest
{

    @Test
    public void testSimple()
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("//site//*");

        Junction conjunction = Restrictions.conjunction();
        criteria.add(conjunction);
        conjunction.add(Restrictions.eq("@property", "test"));
        conjunction.add(Restrictions.eq("@anotherproperty", "anothertest"));

        String xpathExpression = criteria.toXpathExpression();
        Assert.assertEquals(
            "//site//*[(( (@property='test')  and  (@anotherproperty='anothertest') ) )] ",
            xpathExpression);

    }

    @Test
    public void testBooleanProperty()
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("//site//*");

        Junction conjunction = Restrictions.disjunction();
        criteria.add(conjunction);
        conjunction.add(Restrictions.eq("@property", Boolean.FALSE));
        conjunction.add(Restrictions.eq("@anotherproperty", Boolean.TRUE));

        String xpathExpression = criteria.toXpathExpression();
        Assert.assertEquals(
            "//site//*[(( ((@property=false) or not(@property )) or  (@anotherproperty=true) ) )] ",
            xpathExpression);

    }

    /**
     * Test for CRIT-3
     */
    @Test
    public void testEmptyConjuntion()
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("//site//*");

        criteria.add(Restrictions.eq("@property", "test"));

        Junction conjunction = Restrictions.conjunction();
        criteria.add(conjunction);

        String xpathExpression = criteria.toXpathExpression();
        Assert.assertEquals("//site//*[( (@property='test')  )] ", xpathExpression);

    }

    /**
     * Test for CRIT-37
     */
    @Test
    public void testEscapeComma()
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("/one/two/3three/fo,ur/");
        criteria.add(Restrictions.eq("@property", "test"));
        String xpathExpression = criteria.toXpathExpression();

        Assert.assertEquals(xpathExpression, "//one/two/_x0033_three/fo_x002c_ur//*[( (@property='test')  )] ");
    }

    /**
     * Test for CRIT-40
     */
    @Test
    public void testDontEscapeBasePathWithParenthesis()
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("/path/with(paren,thesis)/test");
        criteria.add(Restrictions.eq("@property", "test"));
        String xpathExpression = criteria.toXpathExpression();

        Assert.assertEquals(
            xpathExpression,
            "//path/with_x0028_paren_x002c_thesis_x0029_/test//*[( (@property='test')  )] ");
    }

    /**
     * Test for CRIT-40
     */
    @Test
    public void testDontEscapeExpressions()
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("/jcr:root///element(* , mgnl:media)");
        criteria.add(Restrictions.eq("@property", "test"));
        String xpathExpression = criteria.toXpathExpression();

        Assert.assertEquals(xpathExpression, "/jcr:root///element(* , mgnl:media)[( (@property='test')  )] ");
    }

    /**
     * Test for CRIT-40
     */
    @Test
    public void testDontEscapeExpressions2()
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setBasePath("/jcr:root//*[@jcr:uuid='xxxx-xxxx']//*");
        criteria.add(Restrictions.eq("@property", "test"));
        String xpathExpression = criteria.toXpathExpression();

        Assert.assertEquals(xpathExpression, "/jcr:root//*[@jcr:uuid='xxxx-xxxx']//*[( (@property='test')  )] ");
    }
}
