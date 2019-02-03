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

package nl.vpro.jcr.criteria;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

import javax.jcr.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.fs.local.FileUtil;
import org.testng.Assert;

import nl.vpro.jcr.criteria.query.AdvancedResult;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.ResultIterator;
import nl.vpro.jcr.criteria.query.criterion.Disjunction;
import nl.vpro.jcr.criteria.query.criterion.Order;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;


/**
 * Utility methods used in unit tests.
 * @author fgiust
 */
@Slf4j
public class CriteriaTestUtils {

    static Repository repository;
    static Path tempDirectory;
    static Path tempFile;
    public static Session session;
    public static Node root;


    @SneakyThrows
    public static void setup() {
         // Using jackrabbit memory only seems to be impossible. Sad...
        tempDirectory = Files.createTempDirectory("criteriatest");
        System.setProperty("derby.stream.error.file", new File(tempDirectory.toFile(), "derby.log").toString());
        tempFile = Files.createTempFile("repository", ".xml");
        Files.copy(CriteriaTestUtils.class.getResourceAsStream("/repository.xml"), tempFile, StandardCopyOption.REPLACE_EXISTING);
        FileUtil.delete(tempDirectory.toFile());
        repository = new TransientRepository(tempFile.toFile(), tempDirectory.toFile());;
        session = getSession();
        root = session.getRootNode();
    }

    public static  void shutdown() {
        try {
            FileUtils.deleteDirectory(tempDirectory.toFile());
            Files.deleteIfExists(tempFile);
        } catch (IOException ioe) {
            log.warn(ioe.getMessage(), ioe);
        }
        log.info("Removed " + tempDirectory + " and " + tempFile);
    }

    public static Session getSession() throws RepositoryException {
        return repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }


    public static void assertNumOfResults(int expected, Collection<Node> result, String search)
    {
        if (result.size() != expected)
        {
            List<String> titles = new ArrayList<String>();
            for (Node content : result)
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

    public static void assertUnsortedResults(String[] expected, Collection<Node> result, String search)
    {
        Arrays.sort(expected);

        List<String> titles = new ArrayList<String>();

        for (Node content : result)
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

    public static void assertSortedResults(String[] expected, Collection<Node> result, String search)
    {
        List<String> titles = new ArrayList<String>();
        for (Node content : result)
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

    public static AdvancedResult search(String searchText, int page, int itemsPerPage) {
        return search(searchText, StringUtils.EMPTY, "WEBSITE", false, page, itemsPerPage);
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

        return criteria.execute(session);
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
            .addOrder(Order.desc("@jcr:score"))
            .add(Restrictions.eq("@jcr:primaryType", "nt:page"));

        if (startnode != null)
        {
            criteria.setBasePath(startnode);
        }
        return criteria;
    }

    public static Collection<Node> collectCollectionFromResult(AdvancedResult result)
    {

        ResultIterator<? extends Node> items = result.getItems();
        ArrayList<Node> list = new ArrayList<Node>();

        CollectionUtils.addAll(list, items);
        return list;
    }

    @SneakyThrows
    public static String title(Node item) {
        return item.getProperty("title").getString();
    }

    @SneakyThrows
    public static String name(Node item) {
        return item.getName();
    }

    @SneakyThrows
    public static String path(Node item) {
        return item.getPath();
    }
}
