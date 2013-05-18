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

package net.sourceforge.openutils.mgnlcriteria.tests;

import info.magnolia.repository.RepositoryConstants;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIterator;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Disjunction;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;


/**
 * Utility methods used in unit tests.
 * @author fgiust
 * @version $Id$
 */
public class CriteriaTestUtils
{

    public static void assertNumOfResults(int expected, Collection<AdvancedResultItem> result, String search)
    {
        if (result.size() != expected)
        {
            List<String> titles = new ArrayList<String>();
            for (AdvancedResultItem content : result)
            {
                titles.add(title(content));
            }

            Assert.fail("Wrong result when searching for \""
                + search
                + "\", expected "
                + expected
                + " results, found "
                + result.size()
                + ". Pages found: "
                + ArrayUtils.toString(titles));
        }
    }

    public static void assertUnsortedResults(String[] expected, Collection<AdvancedResultItem> result, String search)
    {
        Arrays.sort(expected);

        List<String> titles = new ArrayList<String>();

        for (AdvancedResultItem content : result)
        {
            titles.add(title(content));
        }
        Collections.sort(titles);

        if (result.size() != expected.length)
        {
            Assert.fail("Wrong result when searching for \""
                + search
                + "\", expected "
                + expected.length
                + " results, found "
                + result.size()
                + ". Pages found: "
                + ArrayUtils.toString(titles));
        }

        Assert.assertEquals(arrayToString(titles.toArray()), arrayToString(expected), "Wrong result searching for \""
            + search
            + "\"");

    }

    public static void assertSortedResults(String[] expected, Collection<AdvancedResultItem> result, String search)
    {
        List<String> titles = new ArrayList<String>();
        for (AdvancedResultItem content : result)
        {
            titles.add(title(content));
        }

        if (result.size() != expected.length)
        {
            Assert.fail("Wrong result when searching for \""
                + search
                + "\", expected "
                + expected.length
                + " results, found "
                + result.size()
                + ". Pages found: "
                + ArrayUtils.toString(titles));
        }

        Assert.assertEquals(arrayToString(titles.toArray()), arrayToString(expected), "Wrong order searching for \""
            + search
            + "\"");

    }

    private static String arrayToString(Object[] array)
    {
        StringWriter writer = new StringWriter();
        for (Object string : array)
        {
            writer.append(string.toString());
            writer.append("\n");
        }
        return writer.toString();
    }

    public static AdvancedResult search(String searchText, int page, int itemsPerPage)
    {
        return search(searchText, StringUtils.EMPTY, RepositoryConstants.WEBSITE, false, page, itemsPerPage);
    }

    public static AdvancedResult search(String searchText, String path, String repository, boolean titleOnly, int page,
        int itemsPerPage)
    {
        if (StringUtils.isBlank(searchText))
        {
            return AdvancedResult.EMPTY_RESULT;
        }

        Criteria criteria = createCriteria(repository, path);

        if (titleOnly)
        {
            criteria.add(Restrictions.contains("@title", StringUtils.defaultString(searchText)));
        }
        else
        {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.contains("@title", StringUtils.defaultString(searchText))); // serve per il
            // boost!
            disjunction.add(Restrictions.contains(".", StringUtils.defaultString(searchText)));
            criteria.add(disjunction);
        }

        if (itemsPerPage >= 0)
        {
            criteria.setMaxResults(itemsPerPage);
        }
        if (page > 1)
        {
            criteria.setFirstResult((page - 1) * itemsPerPage);
        }

        return criteria.execute();
    }

    /**
     * @param repo
     * @param startnode
     * @return
     */
    public static Criteria createCriteria(String repo, String startnode)
    {
        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(repo)
            .addOrder(Order.desc("@jcr:score"))
            .add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));

        if (startnode != null)
        {
            criteria.setBasePath(startnode);
        }
        return criteria;
    }

    public static Collection<AdvancedResultItem> collectCollectionFromResult(AdvancedResult result)
    {

        ResultIterator<AdvancedResultItem> items = result.getItems();
        ArrayList<AdvancedResultItem> list = new ArrayList<AdvancedResultItem>();

        CollectionUtils.addAll(list, items);
        return list;
    }

    public static String title(AdvancedResultItem item)
    {
        return item.getTitle();
    }

    public static String name(AdvancedResultItem item)
    {
        return item.getName();
    }

    public static String path(AdvancedResultItem item)
    {
        return item.getHandle();
    }
}
