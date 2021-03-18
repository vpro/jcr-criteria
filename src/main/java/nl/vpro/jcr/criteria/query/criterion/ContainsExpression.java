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

package nl.vpro.jcr.criteria.query.criterion;

import lombok.EqualsAndHashCode;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRQueryException;
import nl.vpro.jcr.criteria.query.sql2.Condition;
import nl.vpro.jcr.criteria.query.sql2.ContainsCondition;


/**
 * @author Michiel Meeuwissen
 * @since 2.8
 */
@EqualsAndHashCode(callSuper = true)
public class ContainsExpression extends BaseCriterion {

    private static final long serialVersionUID = 0L;

    private final String propertyName;
    private final CharSequence value;

    public ContainsExpression(@NonNull  String propertyName, @NonNull  CharSequence value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public String toXPathString(Criteria criteria) throws JCRQueryException {
       StringBuilder fragment = new StringBuilder();
        fragment.append(" (")
            .append("jcr:contains").append("(")
            .append(propertyName)
            .append(", '")
            .append(value)
            .append("') ) ");
        log.debug("xpathString is {} ", fragment);
        return fragment.toString();
    }

    @Override
    public Condition toSQLCondition(Criteria criteria) {
        return new ContainsCondition(propertyName, value);
    }


}
