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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation for configuring a Repository test case.
 * @author carlo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface RepositoryTestConfiguration {

    /**
     * Path for magnolia repository config file. Defaults to "/test-repositories.xml".
     */
    String repositoryConfig() default "/test-repositories.xml";

    /**
     * Path for jackrabbit configuration file. Defaults to "/repo-conf/jackrabbit-memory-search.xml"
     */
    String jackrabbitRepositoryConfig() default "/repo-conf/jackrabbit-memory-search.xml";

    /**
     * A list of files to bootstrap.
     */
    String[] bootstrapFiles() default {};

    /**
     * A list of directory (classpath paths) to bootstrap.
     */
    String bootstrapDirectory() default "";

    /**
     * magnolia.properties location. Defaults to "/test-magnolia.properties".
     */
    String magnoliaProperties() default "/test-magnolia.properties";

    /**
     * Autostart repositories. Defaults to true.
     */
    boolean autostart() default true;

    /**
     * Quiet (shut up logging during startup). Defaults to true.
     */
    boolean quiet() default true;

    /**
     * Name of modules that should be registered and started. Configuration should exist in the repository for these
     * modules.
     */
    ModuleConfiguration[] startModules() default {};

}
