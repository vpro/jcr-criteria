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

package net.sourceforge.openutils.mgnlcriteria.jcr.query.lucene;

import info.magnolia.cms.security.AccessManager;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.util.SimpleUrlPattern;
import info.magnolia.context.MgnlContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.query.lucene.MatchAllDocsQuery;
import org.apache.jackrabbit.core.query.lucene.QueryDecoratorSupport;
import org.apache.jackrabbit.core.query.lucene.SearchIndex;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;


/**
 * Decorates a lucene query by adding constraints for acl rules.
 * @author dschivo
 * @version $Id$
 */
public class AclQueryDecorator extends QueryDecoratorSupport
{

    private final SessionImpl session;

    /**
     *
     */
    public AclQueryDecorator(SessionImpl session, SearchIndex index)
    {
        super(index);
        this.session = session;
    }

    public Query applyAcl(Query query) throws RepositoryException
    {
        // creates a lucene query for each acl rule
        List<MyPermission> permissionPatterns = new ArrayList<MyPermission>();

        AccessManager accessManager = MgnlContext.getAccessManager(session.getWorkspace().getName());
        List<Permission> permissions = accessManager.getPermissionList();
        if (!permissions.isEmpty())
        {
            try
            {
                Field patternStringField = SimpleUrlPattern.class.getDeclaredField("patternString");
                patternStringField.setAccessible(true);
                for (Permission permission : permissions)
                {
                    if (permission.getPattern() instanceof SimpleUrlPattern)
                    {
                        String pattern = (String) patternStringField.get(permission.getPattern());
                        String[] tokens = StringUtils.splitPreserveAllTokens(pattern, '/');
                        if (tokens.length > 2 && "".equals(tokens[0]) && "*".equals(tokens[tokens.length - 1]))
                        {
                            String basePath = StringUtils.removeEnd(pattern, "/*");
                            boolean deny = (permission.getPermissions() & Permission.READ) == 0;
                            permissionPatterns.add(new MyPermission(basePath, deny));
                        }
                    }
                }
            }
            catch (NoSuchFieldException e)
            {
                throw new RepositoryException(e);
            }
            catch (IllegalAccessException e)
            {
                throw new RepositoryException(e);
            }
        }

        Collections.sort(permissionPatterns, new Comparator<MyPermission>()
        {

            /**
             * {@inheritDoc}
             */
            public int compare(MyPermission o1, MyPermission o2)
            {
                return o1.getBasePath().length() - o2.getBasePath().length();
            }
        });

        Map<String, List<MyPermission>> map = new HashMap<String, List<MyPermission>>();
        for (MyPermission item : permissionPatterns)
        {
            for (String prefix = item.getBasePath(); prefix.length() > 0; prefix = StringUtils.substringBeforeLast(
                prefix,
                "/"))
            {
                if (map.containsKey(prefix))
                {
                    map.get(prefix).add(item);
                    continue;
                }
            }
            List<MyPermission> list = new ArrayList<MyPermission>();
            list.add(item);
            map.put(item.getBasePath(), list);
        }

        Query[] qs = new Query[1 + map.size()];
        int i = 0;
        qs[i++] = query;
        for (List<MyPermission> list : map.values())
        {
            MyPermission[] items = list.toArray(new MyPermission[0]);
            qs[i++] = !list.get(0).isDeny() ? allowQuery(items) : denyQuery(items);
        }
        return booleanQuery(qs);
    }

    private Query allowQuery(MyPermission[] items)
    {
        Query[] qs = new Query[items.length];
        Occur[] os = new Occur[items.length];
        for (int i = 0; i < items.length; i++)
        {
            qs[i] = query(items[i]);
            os[i] = !items[i].isDeny() ? Occur.MUST : Occur.MUST_NOT;
        }
        return booleanQuery(qs, os);
    }

    private Query denyQuery(MyPermission[] items)
    {
        Query[] qs = new Query[items.length];
        Occur[] os = new Occur[items.length];
        for (int i = 0; i < items.length; i++)
        {
            qs[i] = !items[i].isDeny() ? query(items[i]) : notQuery(query(items[i]));
            os[i] = Occur.SHOULD;
        }
        return booleanQuery(qs, os);
    }

    private Query query(MyPermission item)
    {
        String[] tokens = StringUtils.splitPreserveAllTokens(item.getBasePath(), '/');
        Query q = null;
        for (int j = 1; j < tokens.length; j++)
        {
            if (q == null)
            {
                q = descendantSelfAxisQuery(jackrabbitTermQuery("_:PARENT"), nameQuery(tokens[j]));
            }
            else
            {
                q = childAxisQuery(q, tokens[j]);
            }
        }
        return descendantSelfAxisQuery(booleanQuery(q), new MatchAllDocsQuery());
    }

    /**
     * @author dschivo
     * @version $Id$
     */
    private static class MyPermission
    {

        private final String basePath;

        private final boolean deny;

        /**
         * 
         */
        public MyPermission(String basePath, boolean deny)
        {
            this.basePath = basePath;
            this.deny = deny;
        }

        /**
         * Returns the basePath.
         * @return the basePath
         */
        public String getBasePath()
        {
            return basePath;
        }

        /**
         * Returns the deny.
         * @return the deny
         */
        public boolean isDeny()
        {
            return deny;
        }

    }
}
