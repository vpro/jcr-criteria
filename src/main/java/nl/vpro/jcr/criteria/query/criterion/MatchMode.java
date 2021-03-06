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

/**
 * Represents an strategy for matching strings using "like".
 * @author Federico Grilli
 */
public enum MatchMode {

    /**
     * Match the start of the string to the pattern
     */
    START {

        @Override
        public String toMatchString(String pattern) {
            return pattern + '%';
        }
    },

    /**
     * Match the end of the string to the pattern
     */
    END {

        @Override
        public String toMatchString(String pattern) {
            return '%' + pattern;
        }
    },

    /**
     * Match the pattern anywhere in the string
     */
    ANYWHERE {

        @Override
        public String toMatchString(String pattern) {
            return '%' + pattern + '%';
        }
    },

    /**
     * The pattern may itself contain the %'s
     * @since 2.1
     */
    NONE{

        @Override
        public String toMatchString(String pattern) {
            return pattern;
        }
    };

    /**
     * convert the pattern, by appending/prepending "%"
     * @param pattern input pattern
     * @return formatted match string
     */
    public abstract String toMatchString(String pattern);


}
