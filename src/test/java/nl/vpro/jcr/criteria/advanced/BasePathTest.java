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

package nl.vpro.jcr.criteria.advanced;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import nl.vpro.jcr.criteria.CriteriaTestUtils;
import nl.vpro.jcr.criteria.query.AdvancedResult;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.ResultIterator;
import nl.vpro.jcr.criteria.query.criterion.Criterion;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;

import static nl.vpro.jcr.criteria.CriteriaTestUtils.getSession;


/**
 * Tests criteria queries with different values of basePath parameter.
 * @author dschivo
 */
@Ignore
public class BasePathTest {

     @BeforeMethod
    public void setup() throws RepositoryException {
        CriteriaTestUtils.setup();
        getSession();
    }
    @AfterMethod
    public void shutdown() {
       CriteriaTestUtils.shutdown();
    }


    /**
     * Passing a null basePath should search the entire repository.
     */
    @Test
    public void testNullBasePath() {
        Collection<String> paths = searchPaths(null, "AdvancedCriteriaImpl");
        Assert.assertNotNull(paths);
        Assert.assertEquals(paths.size(), 3);
        Assert.assertTrue(paths.contains("/Criteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
        Assert.assertTrue(paths.contains("/Criteria/AdvancedCriteriaImpl"));
        Assert.assertTrue(paths.contains("/Criteria/TranslatableCriteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
    }

    /**
     * Passing an empty basePath should search the entire repository.
     */
    @Test
    public void testEmptyBasePath() {
        Collection<String> paths = searchPaths(StringUtils.EMPTY, "AdvancedCriteriaImpl");
        Assert.assertNotNull(paths);
        Assert.assertEquals(paths.size(), 3);
        Assert.assertTrue(paths.contains("/Criteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
        Assert.assertTrue(paths.contains("/Criteria/AdvancedCriteriaImpl"));
        Assert.assertTrue(paths.contains("/Criteria/TranslatableCriteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
    }

    /**
     * Passing an xpath query ending with /* as the basePath should search the children.
     */
    @Test
    public void testSearchXpathBasePathWithSingleSlash() {
        Collection<String> paths = searchPaths("//Criteria/AbstractCriteriaImpl/*", StringUtils.EMPTY);
        Assert.assertNotNull(paths);
        Assert.assertEquals(paths.size(), 2);
        Assert.assertTrue(paths.contains("/Criteria/AbstractCriteriaImpl/AbstractMagnoliaCriteriaImpl"));
        Assert.assertTrue(paths.contains("/Criteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
    }

    /**
     * Passing an xpath query ending with //* as the basePath should search the descendants.
     */
    @Test
    public void testSearchXpathBasePathWithDoubleSlash() {
        Collection<String> paths = searchPaths("//Criteria/TranslatableCriteria//*", "AdvancedCriteriaImpl");
        Assert.assertNotNull(paths);
        Assert.assertEquals(paths.size(), 1);
        Assert.assertTrue(paths.contains("/Criteria/TranslatableCriteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
    }

    /**
     * Passing an handle as the basePath should search the descendants.
     */
    @Test
    public void testSearchHandleBasePath() {
        Collection<String> paths = searchPaths("/Criteria/TranslatableCriteria", "AdvancedCriteriaImpl");
        Assert.assertNotNull(paths);
        Assert.assertEquals(paths.size(), 1);
        Assert.assertTrue(paths.contains("/Criteria/TranslatableCriteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
    }

    /**
     * Passing an handle ending with / as the basePath should search the descendants. This test makes sure that the
     * resulting xpath query does not end with ///*
     */
    @Test
    public void testSearchHandleBasePathWithTrailingSlash() {
        Collection<String> paths = searchPaths("/Criteria/TranslatableCriteria/", "AdvancedCriteriaImpl");
        Assert.assertNotNull(paths);
        Assert.assertEquals(paths.size(), 1);
        Assert.assertTrue(paths.contains("/Criteria/TranslatableCriteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
    }

    private Collection<String> searchPaths(String basePath, String title) {
        Criteria criteria = JCRCriteriaFactory.createCriteria();
        criteria.setBasePath(basePath);
        criteria.add(Restrictions.eq(Criterion.JCR_PRIMARYTYPE, "nt:page"));
        if (!StringUtils.isEmpty(title))
        {
            criteria.add(Restrictions.eq("@title", title));
        }
        AdvancedResult advResult = criteria.execute(CriteriaTestUtils.session);
        ResultIterator<? extends Node> items = advResult.getItems();
        List<String> paths = new ArrayList<String>();
        while (items.hasNext()) {
            paths.add(CriteriaTestUtils.path(items.next()));
        }
        return paths;
    }
}
