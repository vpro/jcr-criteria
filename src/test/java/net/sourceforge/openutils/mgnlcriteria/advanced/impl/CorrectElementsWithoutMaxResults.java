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

/**
 * 
 */
package net.sourceforge.openutils.mgnlcriteria.advanced.impl;

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
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;
import net.sourceforge.openutils.mgnlcriteria.tests.CriteriaTestUtils;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author carlo
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = {
    "/crit-bootstrap/website.pets.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml",
    "/crit-bootstrap/config.server.auditLogging.xml",
    "/crit-bootstrap/config.server.i18n.content.xml" })
public class CorrectElementsWithoutMaxResults extends TestNgRepositoryTestcase
{

    @Override
    @BeforeClass
    public void setUp() throws Exception
    {
        super.setUp();
        // Nodes in this workspace:
        // - pets (title=Pets)
        // --- cats (title=Cats)
        // ----- 1 (title=Leo, petType=cat, birthDate=2000-09-07)
        // ----- 7 (title=Samantha, petType=cat, birthDate=1995-09-04)
        // ----- 8 (title=Max, petType=cat, birthDate=1995-09-04)
        // ----- 13 (title=Sly, petType=cat, birthDate=2002-06-08)
        // --- dogs (title=Dogs)
        // ----- 3 (title=Rosy, petType=dog, birthDate=2001-04-17)
        // ----- 4 (title=Jewel, petType=dog, birthDate=2000-03-07)
        // ----- 10 (title=Mulligan, petType=dog, birthDate=1997-02-24)
        // ----- 12 (title=Lucky, petType=dog, birthDate=2000-06-24)
        // --- lizards (title=Lizards)
        // ----- 5 (title=Iggy, petType=lizard, birthDate=2000-11-30)
        // --- snakes (title=Snakes)
        // ----- 6 (title=George, petType=snake, birthDate=2000-01-20)
        // --- birds (title=Birds)
        // ----- 9 (title=Lucky, petType=bird, birthDate=1999-08-06)
        // ----- 11 (title=Freddy, petType=bird, birthDate=2000-03-09)
        // --- hamsters (title=Hamsters)
        // ----- 2 (title=Basil, petType=hamster, birthDate=2002-08-06)
        // ************************************************************
        // total 13 pets
        MgnlContext.getJCRSession(RepositoryConstants.WEBSITE).save();

        // info.magnolia.cms.security.SecurityTest.setUp()
        final SecuritySupportImpl sec = new SecuritySupportImpl();
        sec.addUserManager(Realm.REALM_SYSTEM.getName(), new SystemUserManager());
        sec.setRoleManager(new MgnlRoleManager());
        ComponentsTestUtil.setInstance(SecuritySupport.class, sec);
    }

    @Test
    public void otherResultWithMaxResultAsMaxInt() throws Exception
    {
        Criteria criteria = getAllPetWithDocumentOrder();

        criteria.setFirstResult(9);
        criteria.setMaxResults(Integer.MAX_VALUE);

        CriteriaTestUtils.assertNumOfResults(
            4,
            CriteriaTestUtils.collectCollectionFromResult(criteria.execute()),
            "all pets, offset:9");
        assertMissing4MissingPets(criteria);
    }

    @Test
    public void otherResultWithMaxResultAsOffset() throws Exception
    {
        Criteria criteria = getAllPetWithDocumentOrder();

        criteria.setFirstResult(9);
        criteria.setMaxResults(9);

        CriteriaTestUtils.assertNumOfResults(
            4,
            CriteriaTestUtils.collectCollectionFromResult(criteria.execute()),
            "all pets, offset:9");
        assertMissing4MissingPets(criteria);
    }

    @Test
    public void otherResultWithMaxResultAsLesserThanOffset() throws Exception
    {
        Criteria criteria = getAllPetWithDocumentOrder();

        criteria.setFirstResult(9);
        criteria.setMaxResults(8);

        CriteriaTestUtils.assertNumOfResults(
            4,
            CriteriaTestUtils.collectCollectionFromResult(criteria.execute()),
            "all pets, offset:9, maxResult: 8");
        assertMissing4MissingPets(criteria);
    }

    @Test
    public void otherResultWithMaxResultAsGreaterThanOffset() throws Exception
    {
        Criteria criteria = getAllPetWithDocumentOrder();

        criteria.setFirstResult(9);
        criteria.setMaxResults(10);

        CriteriaTestUtils.assertNumOfResults(
            4,
            CriteriaTestUtils.collectCollectionFromResult(criteria.execute()),
            "all pets, offset:9, maxResult: 10");
        assertMissing4MissingPets(criteria);
    }

    /**
     * Control test
     * @throws Exception
     */
    @Test
    public void otherResultWithPagination() throws Exception
    {
        Criteria criteria = getAllPetWithDocumentOrder();
        criteria.setPaging(9, 2);
        CriteriaTestUtils.assertNumOfResults(
            4,
            CriteriaTestUtils.collectCollectionFromResult(criteria.execute()),
            "all pets, 2nd page, pagesize: 9");
        assertMissing4MissingPets(criteria);
    }

    @Test
    public void otherResultWithOnlyOffset() throws Exception
    {
        Criteria criteria = getAllPetWithDocumentOrder();
        criteria.setFirstResult(3);
        CriteriaTestUtils.assertNumOfResults(
            10,
            CriteriaTestUtils.collectCollectionFromResult(criteria.execute()),
            "All pet, offset = 3");

    }

    /**
     * @param criteria
     */
    private void assertMissing4MissingPets(Criteria criteria)
    {
        CriteriaTestUtils.assertSortedResults(
            new String[]{"George", "Lucky", "Freddy", "Basil" },
            CriteriaTestUtils.collectCollectionFromResult(criteria.execute()),
            "all missing pets, sorted");
    }

    /**
     * @return
     */
    private Criteria getAllPetWithDocumentOrder()
    {
        return JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/jcr:root/pets/*/*")
            .add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE))
            .setForcePagingWithDocumentOrder(true);
    }

}
