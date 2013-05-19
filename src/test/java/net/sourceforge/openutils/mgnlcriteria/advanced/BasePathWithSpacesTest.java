/**
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

package net.sourceforge.openutils.mgnlcriteria.advanced;

import info.magnolia.cms.security.MgnlRoleManager;
import info.magnolia.cms.security.Realm;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SecuritySupportImpl;
import info.magnolia.cms.security.SystemUserManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;

import javax.jcr.Node;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIterator;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Criterion;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author dschivo
 * @version $Id$
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = {
    "/crit-bootstrap/website.Lorem ipsum dolor sit amet.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml" })
public class BasePathWithSpacesTest extends TestNgRepositoryTestcase
{

    /**
     * {@inheritDoc}
     */
    @Override
    @BeforeClass
    public void setUp() throws Exception
    {
        super.setUp();

        // Nodes in this workspace:
        // - Lorem ipsum dolor sit amet
        // --- consectetur adipisici elit
        MgnlContext.getJCRSession(RepositoryConstants.WEBSITE).save();

        // info.magnolia.cms.security.SecurityTest.setUp()
        final SecuritySupportImpl sec = new SecuritySupportImpl();
        sec.addUserManager(Realm.REALM_SYSTEM.getName(), new SystemUserManager());
        sec.setRoleManager(new MgnlRoleManager());
        ComponentsTestUtil.setInstance(SecuritySupport.class, sec);
    }

    /**
     * @throws Exception
     */
    @Test
    public void test() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/Lorem ipsum dolor sit amet");
        criteria.add(Restrictions.eq(Criterion.JCR_PRIMARYTYPE, "mgnl:content"));
        AdvancedResult advResult = criteria.execute();
        ResultIterator<? extends Node> items = advResult.getItems();
        Assert.assertTrue(items.hasNext());
        Assert.assertEquals(items.next().getName(), "consectetur adipisici elit");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testWithParams() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("//*[prop1='A' and prop2='B']/Lorem ipsum dolor sit amet");
        criteria.add(Restrictions.eq(Criterion.JCR_PRIMARYTYPE, "mgnl:content"));
        // criteria.addOrder(Order.desc("@jcr:score"));
        AdvancedResult advResult = criteria.execute();

        // if not escaped properly it will crash
        Assert.assertEquals(advResult.getItems().getSize(), 0);
        Assert.assertEquals(advResult.getTotalSize(), 0);

    }

}
