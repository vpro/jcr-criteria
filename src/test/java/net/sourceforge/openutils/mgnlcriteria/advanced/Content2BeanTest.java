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
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
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

import java.util.Map;

import javax.jcr.Node;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIterator;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;
import net.sourceforge.openutils.mgnlcriteria.tests.CriteriaTestUtils;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Tests for Content2Bean transformation
 * @author fgiust
 * @version $Id$
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = {
    "/crit-bootstrap/website.contains.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml",
    "/crit-bootstrap/config.server.auditLogging.xml",
    "/crit-bootstrap/config.server.i18n.content.xml" })
public class Content2BeanTest extends TestNgRepositoryTestcase
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

        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());

        // info.magnolia.cms.security.SecurityTest.setUp()
        final SecuritySupportImpl sec = new SecuritySupportImpl();
        sec.addUserManager(Realm.REALM_SYSTEM.getName(), new SystemUserManager());
        sec.setRoleManager(new MgnlRoleManager());
        ComponentsTestUtil.setInstance(SecuritySupport.class, sec);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoremAndIpsum() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.contains("@title", "lorem ipsum"));
        criteria.addOrder(Order.desc("@jcr:score"));

        AdvancedResult advResult = criteria.execute();
        Assert.assertNotNull(advResult);
        Assert.assertEquals(advResult.getTotalSize(), 1);
        ResultIterator< ? extends Node> items = advResult.getItems();
        Node item = items.next();
        Assert.assertEquals(CriteriaTestUtils.title(item), "lorem ipsum");
        Assert.assertEquals(CriteriaTestUtils.path(item), "/contains/lorem-ipsum");

        // this is also a Map!
        Assert.assertEquals(((Map<String, Object>) item).get("title"), "lorem ipsum");
        Assert.assertEquals(((Map<String, Object>) item).get("text"), "ohoh");
        Assert.assertEquals(((Map<String, Object>) item).get("number").toString(), "5");
        Assert.assertEquals(((Map<String, Object>) item).get("handle"), "/contains/lorem-ipsum");

        ResultIterator<Page> itemsTransformed = advResult.getItems(Page.class);
        Assert.assertNotNull(itemsTransformed);
        Page page = itemsTransformed.next();
        Assert.assertEquals(page.getTitle(), "lorem ipsum");
        Assert.assertEquals(page.getText(), "ohoh");
        Assert.assertEquals(page.getNumber(), 5);

    }

    public static class Page
    {

        private String title;

        private String text;

        private int number;

        /**
         * Returns the title.
         * @return the title
         */
        public String getTitle()
        {
            return title;
        }

        /**
         * Sets the title.
         * @param title the title to set
         */
        public void setTitle(String title)
        {
            this.title = title;
        }

        /**
         * Returns the text.
         * @return the text
         */
        public String getText()
        {
            return text;
        }

        /**
         * Sets the text.
         * @param text the text to set
         */
        public void setText(String text)
        {
            this.text = text;
        }

        /**
         * Returns the number.
         * @return the number
         */
        public int getNumber()
        {
            return number;
        }

        /**
         * Sets the number.
         * @param number the number to set
         */
        public void setNumber(int number)
        {
            this.number = number;
        }
    }
}
