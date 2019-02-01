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

package nl.vpro.jcr.criteria.query.xpath;


import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

/**
 * A simple XPATH <tt>SELECT</tt> statement
 * @author Federico Grilli
 */
public class XPathSelect {

    @Getter
    @Setter
    private String type;

    private String root;

    private String predicate;

    private String orderByClause;

    private int guesstimatedBufferSize = 20;

    /**
     * Construct an XPATH <tt>SELECT</tt> statement from the given clauses
     */
    public String toStatementString() {
        StringBuilder buf = new StringBuilder(guesstimatedBufferSize);
        String r = root == null ? "//*" : root;



        if (r.startsWith("//")) {
            r = r.substring(2);
            buf.append("//");
        } else if (r.startsWith("/")) {
            r = r.substring(1);
            buf.append('/');

        }

        if (type != null) {
            buf.append("element(");
        }
        buf.append(r);

        if (type != null) {
            buf
                .append(", ")
                .append(type.replace("{http://www.jcp.org/jcr/nt/1.0}", "nt:"))
                .append(")");
        }

        if (StringUtils.isNotEmpty(predicate)) {
            buf.append("[")
                .append(predicate)
                .append("]");
        }

        if (StringUtils.isNotEmpty(orderByClause)) {
            buf.append("order by").append(orderByClause);
        }

        return buf.toString();
    }

    public XPathSelect setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
        this.guesstimatedBufferSize += orderByClause.length();
        return this;
    }

    /**
     * Sets the selectClause.
     * @param root the root path for the jcr query
     */
    public XPathSelect setRoot(String root) {
        this.root = root;
        this.guesstimatedBufferSize += root.length();
        return this;
    }

    /**
     * Sets the predicate.
     * @param predicate The predicate to set
     */
    public XPathSelect setPredicate(String predicate) {
        this.predicate = predicate;
        this.guesstimatedBufferSize += predicate.length();
        return this;
    }
}
