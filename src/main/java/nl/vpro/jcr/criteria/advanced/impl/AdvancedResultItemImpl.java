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

package nl.vpro.jcr.criteria.advanced.impl;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Row;

import nl.vpro.jcr.criteria.query.AdvancedResultItem;
import nl.vpro.jcr.utils.JcrNodeWrapper;


/**
 * @author fgiust
 * @author Michiel Meeuwisen
 */
@Slf4j
public class AdvancedResultItemImpl extends JcrNodeWrapper implements AdvancedResultItem {

    private final Row row;

    public AdvancedResultItemImpl(Row row)  {
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
            log.warn("Error getting excerpt for " + this.getHandle(), e);
            return null;
        }

        if (excerptValue != null) {
            try {
                return excerptValue.getString();
            } catch (RepositoryException e){
                log.warn("Error getting excerpt for " + this.getHandle(), e);
                return null;
            }
        }

        return null;
    }

    @Override
    public double getScore() {
        try {
            return row.getScore();
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public double getScore(String selector) {
        if (selector == null) {
            return getScore();
        }
        try {
            return row.getScore(selector);
        } catch (RepositoryException e) {
            log.warn("unable to extract score from {} using selector {}", row, selector);
        }

        return 0;
    }

    @Override
    @SneakyThrows
    public String getTitle() {
        if (hasProperty("title")) {
            return getProperty("title").getString();
        }
        return null;
    }

    @Override
    @SneakyThrows
    public String getHandle() {
        return getPath();
    }

    @Override
    @SneakyThrows
    protected Node getNode() {
        return row.getNode();
    }
    @Override
    public String toString() {
        return "row:" + getHandle() + " " + getTitle();
    }
}
