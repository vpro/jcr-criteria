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

package net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Represents an strategy for matching strings using "like".
 * @author Federico Grilli
 */
@SuppressWarnings("serial")
public abstract class MatchMode implements Serializable
{

    /**
     * Match the start of the string to the pattern
     */
    public static final MatchMode START = new MatchMode("START")
    {

        @Override
        public String toMatchString(String pattern)
        {
            return pattern + '%';
        }
    };

    /**
     * Match the end of the string to the pattern
     */
    public static final MatchMode END = new MatchMode("END")
    {

        @Override
        public String toMatchString(String pattern)
        {
            return '%' + pattern;
        }
    };

    /**
     * Match the pattern anywhere in the string
     */
    public static final MatchMode ANYWHERE = new MatchMode("ANYWHERE")
    {

        @Override
        public String toMatchString(String pattern)
        {
            return '%' + pattern + '%';
        }
    };

    private static final Map<String, MatchMode> INSTANCES = new HashMap<String, MatchMode>();

    private static final long serialVersionUID = -7446324572335777782L;

    private final String name;

    protected MatchMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    static
    {
        INSTANCES.put(END.name, END);
        INSTANCES.put(START.name, START);
        INSTANCES.put(ANYWHERE.name, ANYWHERE);
    }

    /**
     * convert the pattern, by appending/prepending "%"
     * @param pattern input pattern
     * @return formatted match string
     */
    public abstract String toMatchString(String pattern);

}
