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

package org.apache.jackrabbit.core.query.lucene;

import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;


/**
 * Provides helper methods using classes only accessible from this package.
 * @author dschivo
 */
public class QueryDecoratorSupport
{

    /**
     * The search index.
     */
    protected final SearchIndex index;

    /**
     *
     */
    public QueryDecoratorSupport(SearchIndex index)
    {
        this.index = index;
    }

    protected Query nameQuery(String n)
    {
        return new NameQuery(
            NameFactoryImpl.getInstance().create("", n),
            index.getIndexFormatVersion(),
            index.getNamespaceMappings());
    }

    protected Query jackrabbitTermQuery(String f)
    {
        return new JackrabbitTermQuery(new Term(f));
    }

    protected Query booleanQuery(Query... qs)
    {
        return booleanQuery(qs, null);
    }

    protected Query booleanQuery(Query[] qs, Occur[] os)
    {
        BooleanQuery bq = new BooleanQuery();
        for (int i = 0; i < qs.length; i++)
        {
            Occur o = (os != null && i < os.length) ? os[i] : Occur.MUST;
            bq.add(qs[i], o);
        }
        return bq;
    }

    protected Query childAxisQuery(Query q, String n)
    {
        return new ChildAxisQuery(index.getContext().getItemStateManager(), q, NameFactoryImpl.getInstance().create(
            "",
            n), index.getIndexFormatVersion(), index.getNamespaceMappings());
    }

    protected Query descendantSelfAxisQuery(Query cq, Query sq)
    {
        return new DescendantSelfAxisQuery(cq, sq, 1);
    }
    
    protected Query notQuery(Query cq)
    {
        return new NotQuery(cq);
    }
}
