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

package net.sourceforge.openutils.mgnlcriteria.jcr.query.lucene;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import javax.jcr.RepositoryException;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sourceforge.openutils.mgnlcriteria.advanced.impl.QueryExecutorHelper;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.query.lucene.LuceneQueryBuilder;
import org.apache.jackrabbit.core.query.lucene.SearchIndex;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.query.DefaultQueryNodeFactory;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;
import org.apache.lucene.search.Query;


/**
 * Supports magnolia security at the lucene level by encoding acl rules as constraints in the lucene query.
 * @author dschivo
 * @version $Id$
 */
public class AclSearchIndex extends SearchIndex
{

    private DefaultQueryNodeFactory proxiedQueryNodeFactory;

    public AclSearchIndex()
    {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(DefaultQueryNodeFactory.class);
        enhancer.setCallback(new AclMethodInterceptor());
        proxiedQueryNodeFactory = (DefaultQueryNodeFactory) enhancer.create(
            DefaultQueryNodeFactory.class.getConstructors()[0].getParameterTypes(),
            new Object[]{Collections.unmodifiableList(Arrays.asList(new Name[]{
                NameConstants.NT_CHILDNODEDEFINITION,
                NameConstants.NT_FROZENNODE,
                NameConstants.NT_NODETYPE,
                NameConstants.NT_PROPERTYDEFINITION,
                NameConstants.NT_VERSION,
                NameConstants.NT_VERSIONEDCHILD,
                NameConstants.NT_VERSIONHISTORY,
                NameConstants.NT_VERSIONLABELS,
                NameConstants.REP_NODETYPES,
                NameConstants.REP_SYSTEM,
                NameConstants.REP_VERSIONSTORAGE,
                NameConstants.NT_BASE,
                NameConstants.MIX_REFERENCEABLE })) });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DefaultQueryNodeFactory getQueryNodeFactory()
    {

        return proxiedQueryNodeFactory;
    }

    /**
     * Builds a specialized root node of the query tree, enabling decoration of the lucene query with acl constraints.
     * @author dschivo
     * @version $Id$
     */
    class AclMethodInterceptor implements MethodInterceptor
    {

        /**
         * {@inheritDoc}
         */
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
        {
            String name = method.getName();

            if ("createQueryRootNode".equals(name))
            {
                return new QueryRootNode()
                {

                    @Override
                    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException
                    {
                        // the lucene query without acl constraints
                        Query luceneQuery = (Query) super.accept(visitor, data);
                        if (!QueryExecutorHelper.isExecuting())
                        {
                            // not a criteria query: skip lucene decoration
                            return luceneQuery;
                        }
                        try
                        {
                            // retrieves the session
                            Field sessionField = LuceneQueryBuilder.class.getDeclaredField("session");
                            sessionField.setAccessible(true);
                            SessionImpl session = (SessionImpl) sessionField.get(visitor);
                            // adds acl constraints
                            AclQueryDecorator decorator = new AclQueryDecorator(session, AclSearchIndex.this);
                            return decorator.applyAcl(luceneQuery);
                        }
                        catch (Throwable e)
                        {
                            throw new RepositoryException(e);
                        }
                    }
                };
            }
            return proxy.invokeSuper(obj, args);
        }
    }

}
