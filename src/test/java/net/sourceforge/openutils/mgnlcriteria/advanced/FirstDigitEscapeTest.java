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

import info.magnolia.cms.core.MgnlNodeType;
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
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;
import net.sourceforge.openutils.mgnlcriteria.tests.CriteriaTestUtils;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author dschivo
 * @version $Id$
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = {
    "/crit-bootstrap/website.myproject.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml",
    "/crit-bootstrap/config.server.auditLogging.xml",
    "/crit-bootstrap/config.server.i18n.content.xml" })
public class FirstDigitEscapeTest extends TestNgRepositoryTestcase
{

    /**
     * {@inheritDoc}
     */
    @Override
    @BeforeClass
    public void setUp() throws Exception
    {

        super.setUp();

        MgnlContext.getJCRSession(RepositoryConstants.WEBSITE).save();

        // info.magnolia.cms.security.SecurityTest.setUp()
        final SecuritySupportImpl sec = new SecuritySupportImpl();
        SystemUserManager systemUserManager = new SystemUserManager();
        systemUserManager.setRealmName(Realm.REALM_SYSTEM.getName());
        sec.addUserManager(Realm.REALM_SYSTEM.getName(), systemUserManager);
        sec.setRoleManager(new MgnlRoleManager());
        ComponentsTestUtil.setInstance(SecuritySupport.class, sec);
    }

    @Test
    public void testEscape() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("//myproject/Sport/F1/0a67369b-8cc6-43d8-b2d3-c07b12a2ed5f/versions/*");
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_COMPONENT));
        criteria.addOrder(Order.desc("@jcr:created"));

        AdvancedResult advResult = criteria.execute();

        ResultIterator< ? extends Node> resultIterator = advResult.getItems();
        Assert.assertTrue(resultIterator.hasNext());
        Assert.assertEquals(CriteriaTestUtils.name(resultIterator.next()), "ceb55065-e6cd-451a-8ce0-7e495e7e8fbc");
    }

    @Test
    public void testEscapeHyphen() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("//myproject/Sport/F1/-0a67369b-8cc6-43d8-b2d3-c07b12a2ed5f/versions/*");
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_COMPONENT));
        criteria.addOrder(Order.desc("@jcr:created"));

        AdvancedResult advResult = criteria.execute();

        ResultIterator< ? extends Node> resultIterator = advResult.getItems();
        Assert.assertFalse(resultIterator.hasNext());
    }

    @Test
    public void testEscapeParentheses() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/myproject/Sport/F1/0a67369b-8cc6-43d8-b2d3-(c07b12a2ed5f)");
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_COMPONENT));
        criteria.addOrder(Order.desc("@jcr:created"));

        AdvancedResult advResult = criteria.execute();

        ResultIterator< ? extends Node> resultIterator = advResult.getItems();
        Assert.assertFalse(resultIterator.hasNext());
    }

}
