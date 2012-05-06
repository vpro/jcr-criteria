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
// TEMPORARY COPIED HERE TO AVOID CIRCULAR DEPENDENCY - MuST SPLIT mgnlutils to FIX
package it.openutils.mgnlutils.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;


/**
 * TestNG version of Magnolia RepositoryTestcase. Extend this class and configure it by adding a @RepositoryTestConfiguration
 * annotation.
 * @author fgiust
 * @version $Id$
 */
public abstract class TestNgRepositoryTestcase extends AbstractRepositoryTestcase
{

    @Override
    @BeforeClass
    public void setUp() throws Exception
    {
        super.setUp();
    }

    @Override
    @AfterClass
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

}
