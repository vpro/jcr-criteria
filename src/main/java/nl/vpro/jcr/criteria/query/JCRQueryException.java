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

package nl.vpro.jcr.criteria.query;

import lombok.experimental.Delegate;

/**
 * Runtime exception wrapper for jcr checked exceptions.
 * @author fgrilli
 */
public class JCRQueryException extends RuntimeException {

    private static final long serialVersionUID = -8737641628360563743L;

    @Delegate
    private Criteria.Expression expression;

    public JCRQueryException(Criteria.Expression expression, Throwable cause) {
        super("An error occurred while executing a " + expression.getLanguage() + " query. Query was '"
            + expression.statement
            + "'. Exception message is "
            + cause.getMessage(), cause);
        this.expression = expression;
    }


}
