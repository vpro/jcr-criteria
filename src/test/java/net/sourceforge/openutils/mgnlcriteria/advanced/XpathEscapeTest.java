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
import info.magnolia.context.MgnlContext;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;

import java.util.Collection;

import javax.jcr.Node;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRQueryException;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Disjunction;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;
import net.sourceforge.openutils.mgnlcriteria.tests.CriteriaTestUtils;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author lbrindisi
 * @version $Id$
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = {
    "/crit-bootstrap/website.00000.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml",
    "/crit-bootstrap/config.server.auditLogging.xml",
    "/crit-bootstrap/config.server.i18n.content.xml" }, security = true)
public class XpathEscapeTest extends TestNgRepositoryTestcase
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
    }

    @Test
    public void testEscapeQuotesForEqRestriction() throws Exception
    {
        String title = "Tallart, Camille d'Hostun, cónte di-";
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.eq("@title", title));
        AdvancedResult advResult = null;
        try
        {
            advResult = criteria.execute();
        }
        catch (JCRQueryException e)
        {
            Assert.fail("Invalid query");
        }
        Assert.assertNotNull(advResult);

        Assert.assertEquals(CriteriaTestUtils.title(advResult.getFirstResult()), title);

        Collection<Node> collection = CriteriaTestUtils.collectCollectionFromResult(advResult);
        Assert.assertEquals(collection.size(), 1);
        for (Node content : collection)
        {
            Assert.assertEquals(CriteriaTestUtils.title(content), title);
        }
    }

    @Test
    public void testEscapeDoubleQuotesForContainsRestriction() throws Exception
    {
        String searchText = "\"Milano\"";
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.contains("@title", searchText));
        try
        {
            criteria.execute();
        }
        catch (JCRQueryException e)
        {
            Assert.fail("Search string not properly escaped. " + e.getMessage());
        }
    }

    @Test
    public void testEscapeInvalidChars() throws Exception
    {
        String searchText = "\"Milano(){}[]+*?^|\\/!";
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.contains("@title", searchText));
        try
        {
            criteria.execute();
        }
        catch (JCRQueryException e)
        {
            Assert.fail("Search string not properly escaped. " + e.getMessage());
        }
    }

    @Test
    public void testEscapeSingleQuotesForContainsRestriction() throws Exception
    {
        String searchText = "Milano'";
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.contains("@title", searchText));
        try
        {
            criteria.execute();
        }
        catch (JCRQueryException e)
        {
            Assert.fail("Search string not properly escaped. " + e.getMessage());
        }
    }

    @Test
    public void testEscapePlusForContainsRestriction() throws Exception
    {
        String searchText = "Milano +";
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.contains("@title", searchText));
        try
        {
            criteria.execute();
        }
        catch (JCRQueryException e)
        {
            Assert.fail("Search string not properly escaped. " + e.getMessage());
        }
    }

    @Test
    public void testNotInPrivatePropertiesNoMagnoliaUser()
    {

        AdvancedResult advResult = CriteriaTestUtils.search("superuser", 1, 200);
        Collection<Node> result = CriteriaTestUtils.collectCollectionFromResult(advResult);

        CriteriaTestUtils.assertNumOfResults(0, result, "superuser");
    }

    @Test
    public void testNotInPrivatePropertiesNoTemplates()
    {
        AdvancedResult advResult = CriteriaTestUtils.search("t-redirect", 1, 200);
        Collection<Node> result = CriteriaTestUtils.collectCollectionFromResult(advResult);

        CriteriaTestUtils.assertNumOfResults(0, result, "t-redirect");
    }

    @Test
    public void testEscapePipes() throws Exception
    {
        String searchText = "giovanni paolo ||";
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.contains("@title", searchText));
        try
        {
            AdvancedResult advResult = criteria.execute();
            CriteriaTestUtils.assertNumOfResults(
                1,
                CriteriaTestUtils.collectCollectionFromResult(advResult),
                searchText);
        }
        catch (JCRQueryException e)
        {
            Assert.fail("Search string not properly escaped. " + e.getMessage());
        }
    }

    @Test
    public void testEqualsSmile()
    {
        String searchText = ":)";

        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.eq("@title", searchText));
        try
        {
            AdvancedResult advResult = criteria.execute();
            CriteriaTestUtils.assertNumOfResults(
                1,
                CriteriaTestUtils.collectCollectionFromResult(advResult),
                searchText);
        }
        catch (JCRQueryException e)
        {
            Assert.fail("Search string not properly escaped. " + e.getMessage());
        }

    }

    @Test
    public void testEscapeUnconventionalKeywords() throws Exception
    {
        String searchText = "(ai)(n)(uk)";

        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.contains("@title", searchText));

        Assert.assertEquals(
            criteria.toXpathExpression(),
            "//*[( (@jcr:primaryType='mgnl:page')  and  ( jcr:contains(@title, '\\(ai\\)\\(n\\)\\(uk\\)') )  )] ");

        try
        {
            AdvancedResult advResult = criteria.execute();
            CriteriaTestUtils.assertNumOfResults(
                0,
                CriteriaTestUtils.collectCollectionFromResult(advResult),
                searchText);
        }
        catch (JCRQueryException e)
        {
            Assert.fail("Search string not properly escaped. " + e.getMessage());
        }
    }

    @Test
    public void testEscapeUnconventionalKeywords0() throws Exception
    {
        String searchText = "(fr: )(n)";
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);

        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.contains("@title", StringUtils.defaultString(searchText))); // serve per il
        // boost!
        disjunction.add(Restrictions.contains(".", StringUtils.defaultString(searchText)));
        criteria.add(disjunction);

        Assert
            .assertEquals(
                criteria.toXpathExpression(),
                "//*[(( ( jcr:contains(@title, '\\(fr\\: \\)\\(n\\)') )  or  ( jcr:contains(., '\\(fr\\: \\)\\(n\\)') ) ) )] ");

        try
        {
            AdvancedResult advResult = criteria.execute();
            CriteriaTestUtils.assertNumOfResults(
                0,
                CriteriaTestUtils.collectCollectionFromResult(advResult),
                searchText);
        }
        catch (JCRQueryException e)
        {
            Assert.fail("Search string not properly escaped. " + e.getMessage());
        }
    }

    @Test
    public void testDoubleColumn()
    {

        String searchText = ":";

        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.contains("@title", searchText));

        Assert.assertEquals(
            criteria.toXpathExpression(),
            "//*[( (@jcr:primaryType='mgnl:page')  and  ( jcr:contains(@title, '\\:') )  )] ");

        AdvancedResult advResult = criteria.execute();
        Collection<Node> result = CriteriaTestUtils.collectCollectionFromResult(advResult);

        CriteriaTestUtils.assertNumOfResults(0, result, searchText);
    }

    @Test
    public void testParenthesis()
    {

        String searchText = ")";

        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.contains("@title", searchText));

        Assert.assertEquals(criteria.toXpathExpression(), "//*[( ( jcr:contains(@title, '\\)') )  )] ");

        AdvancedResult advResult = criteria.execute();
        Collection<Node> result = CriteriaTestUtils.collectCollectionFromResult(advResult);

        CriteriaTestUtils.assertNumOfResults(0, result, searchText);
    }

    @Test
    public void testSmileSeparated()
    {

        String searchText = ": )";

        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.contains("@title", searchText));

        // TODO: doesn't work with jackrabbit 2, to be fixed
        Assert.assertEquals(criteria.toXpathExpression(), "//*[( ( jcr:contains(@title, '\\: \\)') )  )] ");

        AdvancedResult advResult = criteria.execute();
        Collection<Node> result = CriteriaTestUtils.collectCollectionFromResult(advResult);

        CriteriaTestUtils.assertNumOfResults(0, result, searchText);
    }

    @Test
    public void testEscapeOrKeyword() throws Exception
    {
        String searchText = "OR SONO";
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.contains("@title", searchText));

        Assert.assertEquals(
            criteria.toXpathExpression(),
            "//*[( (@jcr:primaryType='mgnl:page')  and  ( jcr:contains(@title, '\"OR\" SONO') )  )] ");

        AdvancedResult advResult = criteria.execute();
        CriteriaTestUtils.assertNumOfResults(1, CriteriaTestUtils.collectCollectionFromResult(advResult), searchText);

    }

    @Test
    public void testEscapeOrKeywordWithEqual() throws Exception
    {
        String searchText = "OR SONO";
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.eq("@title", searchText));

        Assert.assertEquals(
            criteria.toXpathExpression(),
            "//*[( (@jcr:primaryType='mgnl:page')  and  (@title='OR SONO')  )] ");

        AdvancedResult advResult = criteria.execute();
        CriteriaTestUtils.assertNumOfResults(1, CriteriaTestUtils.collectCollectionFromResult(advResult), searchText);

    }

    @Test
    public void testEscapeAndKeyword() throws Exception
    {
        String searchText = "AND ME";
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.add(Restrictions.contains("@title", searchText));

        Assert.assertEquals(
            criteria.toXpathExpression(),
            "//*[( (@jcr:primaryType='mgnl:page')  and  ( jcr:contains(@title, 'AND ME') )  )] ");

        AdvancedResult advResult = criteria.execute();
        CriteriaTestUtils.assertNumOfResults(0, CriteriaTestUtils.collectCollectionFromResult(advResult), searchText);

    }

    public void testSmile()
    {

        String searchText = ":)";

        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.contains("@title", searchText));

        // the space is added as a workaround
        Assert.assertEquals(criteria.toXpathExpression(), "//*[( ( jcr:contains(@title, '\\: \\)') )  )] ");

        AdvancedResult advResult = criteria.execute();
        Collection<Node> result = CriteriaTestUtils.collectCollectionFromResult(advResult);

        CriteriaTestUtils.assertNumOfResults(0, result, searchText);
    }

}
