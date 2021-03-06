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

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.jcr.RangeIterator;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.checkerframework.checker.nullness.qual.NonNull;


/**
 * Wraps a RowIterator, requiring subclasses to adapt each Row to a specific type.
 * @param <T> type of results
 * @author fgiust
  */
public class ResultIteratorImpl<T> implements ResultIterator<T> {

    /**
     * The jcr RowIterator
     */
    @Delegate(types = {ResultIterator.class, RangeIterator.class, Iterator.class},  excludes = Wrapped.class)
    protected final RowIterator rowIterator;
    protected final Function<Row, T> wrapper;

    public  ResultIteratorImpl(@NonNull RowIterator rowIterator, @NonNull Function<Row, T> wrapper) {
        this.rowIterator = rowIterator;
        this.wrapper = wrapper;
    }


    @Override
    public T next() {
        return wrapper.apply(rowIterator.nextRow());
    }


    private interface Wrapped<T> {
        T next();
        void forEachRemaining(Consumer<? super T> action);

    }

}
