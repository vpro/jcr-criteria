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

package nl.vpro.jcr.criteria.advanced.impl;


import nl.vpro.jcr.criteria.query.AdvancedResultItem;

import java.lang.reflect.InvocationTargetException;

import javax.jcr.*;
import javax.jcr.query.Row;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class AdvancedResultItemImpl extends DelegateNodeWrapper implements AdvancedResultItem {

    private final Row row;

    private static final Logger LOG = LoggerFactory.getLogger(AdvancedResultItemImpl.class);

    /**

     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     */
    public AdvancedResultItemImpl(Row row, Item item) throws RepositoryException, AccessDeniedException {
        super(new I18nNodeWrapper((Node) item));
        this.row = row;
    }

    @Override
    public String getExcerpt() {

        return getExcerpt(".");
    }

    @Override
    public String getExcerpt(String selector) {

        Value excerptValue;
        try {
            excerptValue = row.getValue("rep:excerpt(" + selector + ")");
        } catch (RepositoryException e){
            LOG.warn("Error getting excerpt for " + this.getHandle(), e);
            return null;
        }

        if (excerptValue != null) {
            try {
                return excerptValue.getString();
            } catch (RepositoryException e){
                LOG.warn("Error getting excerpt for " + this.getHandle(), e);
                return null;
            }
        }

        return null;
    }

    @Override
    public double getScore() {
        try {
            return (Double) PropertyUtils.getSimpleProperty(row, "score");
        } catch (IllegalAccessException | InvocationTargetException e){
            LOG.warn("Error getting score for {}", this.getHandle(), e);
        } catch (NoSuchMethodException e) {
            LOG
                .error("Unsupported version of jackrabbit detected, you need at least 1.6.x or a jcr 2.0 compliant version");
        }

        return 0;
    }

    @Override
    public double getScore(String selector) {
        if (selector == null) {
            try {
                return row.getScore();
            }catch (RepositoryException e) {
                LOG.warn("unable to extract score from {}", row);
            }
        }
        try {
            return row.getScore(selector);
        } catch (RepositoryException e) {
            LOG.warn("unable to extract score from {} using selector {}", row, selector);
        }

        return 0;
    }

    @Override
    public String getTitle() {
        try {
            if (hasProperty("title")) {
                return getProperty("title").getString();
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public String getHandle() {
        try {
            return getPath();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Node getJCRNode() {
        return this;
    }

}
