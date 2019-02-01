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

import java.util.Arrays;
import java.util.stream.Collectors;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRQueryException;
import nl.vpro.jcr.criteria.query.sql2.AndCondition;
import nl.vpro.jcr.criteria.query.sql2.Condition;
import nl.vpro.jcr.criteria.query.sql2.OrCondition;


/**
 * Superclass of binary logical expressions
 *
 * TODO This seems to be exactly equals to {@link Conjunction} / {@link Disjunction}
 * @author Federico Grilli
  */
public class LogicalExpression extends BaseCriterion implements Criterion {

    private static final long serialVersionUID = 4524284746715983618L;

    private final Criterion[] clauses;
    private final BoolOp op;

    protected LogicalExpression(BoolOp op, Criterion... clauses) {
        this.clauses = clauses;
        this.op = op;
    }

    @Override
    public String toXPathString(Criteria criteria) throws JCRQueryException {

        StringBuilder fragment = new StringBuilder();
        if (clauses.length == 0) {

        } else if (clauses.length == 1) {
            fragment.append(clauses[0].toXPathString(criteria));
        } else {
            fragment.append('(');
            boolean needsOp = false;
            for (Criterion clause : clauses) {
                if (needsOp) {
                    fragment.append(' ').append(getOp()).append(' ');
                }
                needsOp = true;
                fragment.append(clause.toXPathString(criteria));
            }
            fragment.append(')');
        }
        log.debug("xpathString is {} ", fragment.toString());
        return fragment.toString();
    }

    @Override
    public Condition toSQLCondition(Criteria criteria) {
        switch(op) {
            case OR:
                return new OrCondition(Arrays.stream(clauses).map(c -> c.toSQLCondition(criteria)).toArray(Condition[]::new));
            case AND:
                return new AndCondition(Arrays.stream(clauses).map(c -> c.toSQLCondition(criteria)).toArray(Condition[]::new));
            default:
                throw new IllegalArgumentException();
        }
    }

    public BoolOp getOp() {
        return op;
    }

    @Override
    public String toString() {
        return Arrays.stream(clauses).map(Object::toString).collect(Collectors.joining(" " + getOp() + " "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogicalExpression that = (LogicalExpression) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(clauses, that.clauses)) return false;
        return op == that.op;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(clauses);
        result = 31 * result + op.hashCode();
        return result;
    }

    enum BoolOp {
        AND,
        OR
    }
}
