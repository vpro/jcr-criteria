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

package nl.vpro.jcr.criteria.query.criterion;

import org.testng.Assert;
import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;


/**
 * @author dschivo
 */
public class ConjunctionTest {

    /**
     */
    @Test
    public void testImplicitConjunction() {
        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setBasePath("/")
            .add(Restrictions.eq("MetaData/@mgnl:template", "t-photogallery-sheet"))
            .add(Restrictions.isNotNull("@playlist"))
            .addOrder(Order.desc("@photogalleryDate"));

        String expectedStmt = "//*[MetaData/@mgnl:template='t-photogallery-sheet' and @playlist] order by @photogalleryDate descending";
        String actualStmt = criteria.toXpathExpression().getStatement();
        Assert.assertEquals(actualStmt, expectedStmt);
    }

    /**
     */
    @Test
    public void testExplicitConjunction() {
        Junction conjunction = Restrictions
            .conjunction()
            .add(Restrictions.eq("MetaData/@mgnl:template", "t-photogallery-sheet"))
            .add(Restrictions.isNotNull("@playlist"));
        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setBasePath("/")
            .add(conjunction)
            .addOrder(Order.desc("@photogalleryDate"));

        String expectedStmt = "//*[(MetaData/@mgnl:template='t-photogallery-sheet' and @playlist)] order by @photogalleryDate descending";
        String actualStmt = criteria.toXpathExpression().getStatement();
        Assert.assertEquals(actualStmt, expectedStmt);
    }

}
