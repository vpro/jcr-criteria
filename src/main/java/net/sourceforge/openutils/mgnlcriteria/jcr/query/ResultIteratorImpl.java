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

package net.sourceforge.openutils.mgnlcriteria.jcr.query;

import java.util.Iterator;

import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;


/**
 * Wraps a RowIterator, requiring subclasses to adapt each Row to a specific type.
 * @param <T> type of results
 * @author fgiust
 * @version $Id$
 */
public abstract class ResultIteratorImpl<T> implements ResultIterator<T>
{

    /**
     * The jcr RowIterator
     */
    protected RowIterator rowIterator;

    /**
     * @param rowIterator
     */
    public ResultIteratorImpl(RowIterator rowIterator)
    {
        this.rowIterator = rowIterator;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext()
    {
        return rowIterator.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public void remove()
    {
        rowIterator.remove();
    }

    /**
     * {@inheritDoc}
     */
    public void skip(long skipNum)
    {
        rowIterator.skip(skipNum);
    }

    /**
     * {@inheritDoc}
     */
    public long getSize()
    {
        return rowIterator.getSize();
    }

    /**
     * {@inheritDoc}
     */
    public long getPosition()
    {
        return rowIterator.getPosition();
    }

    /**
     * {@inheritDoc}
     */
    public T next()
    {
        return wrap(rowIterator.nextRow());
    }

    /**
     * Adds foreach support.
     */
    public Iterator<T> iterator()
    {
        return this;
    }

    /**
     * Transforms a Row instance, adapting it to a specific type.
     * @param row the jcr Row to wrap
     * @return a transformed version
     */
    protected abstract T wrap(Row row);

}
