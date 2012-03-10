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

package net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion;

import info.magnolia.repository.RepositoryConstants;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author dschivo
 * @version $Id$
 */
public class ConjunctionTest
{

    /**
     * @throws Exception
     */
    @Test
    public void testImplicitConjunction() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/")
            .add(Restrictions.eq("MetaData/@mgnl:template", "t-photogallery-sheet"))
            .add(Restrictions.isNotNull("@playlist"))
            .addOrder(Order.desc("@photogalleryDate"));

        String expectedStmt = "//*"
            + "[((MetaData/@mgnl:template='t-photogallery-sheet') and @playlist)]"
            + " order by @photogalleryDate descending";
        String actualStmt = criteria.toXpathExpression();
        Assert.assertEquals(StringUtils.remove(actualStmt, ' '), StringUtils.remove(expectedStmt, ' '));
    }

    /**
     * @throws Exception
     */
    @Test
    public void testExplicitConjunction() throws Exception
    {
        Junction conjunction = Restrictions
            .conjunction()
            .add(Restrictions.eq("MetaData/@mgnl:template", "t-photogallery-sheet"))
            .add(Restrictions.isNotNull("@playlist"));
        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/")
            .add(conjunction)
            .addOrder(Order.desc("@photogalleryDate"));

        String expectedStmt = "//*"
            + "[(((MetaData/@mgnl:template='t-photogallery-sheet') and @playlist))]"
            + " order by @photogalleryDate descending";
        String actualStmt = criteria.toXpathExpression();
        Assert.assertEquals(StringUtils.remove(actualStmt, ' '), StringUtils.remove(expectedStmt, ' '));
    }

}
