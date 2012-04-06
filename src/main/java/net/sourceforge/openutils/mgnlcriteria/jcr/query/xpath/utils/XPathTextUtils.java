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

package net.sourceforge.openutils.mgnlcriteria.jcr.query.xpath.utils;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A utility class to escape xpath strings
 * @author fgiust
 * @author fgrilli
 * @version $Id$
 */
public final class XPathTextUtils
{

    private static Logger log = LoggerFactory.getLogger(XPathTextUtils.class);

    /**
     * Date format used for date formatting.
     */
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance(
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        TimeZone.getDefault(),
        Locale.ENGLISH);

    private XPathTextUtils()
    {
    }

    /**
     * Convert a string to an XPath 2.0 string literal, suitable for inclusion in a query. See JSR-170 spec v1.0, Sec.
     * 6.6.4.9.
     * @param str Any string.
     * @return A valid XPath 2.0 string literal, including enclosing quotes.
     */
    public static String stringToXPathLiteral(String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return str;
        }
        // Single quotes needed for jcr:contains()
        return str.replaceAll("'", "\"");
    }

    /**
     * Convert a string to a JCR search expression literal, suitable for use in jcr:contains() (inside XPath queries).
     * The characters - and " have special meaning, and may be escaped with a backslash to obtain their literal value.
     * See JSR-170 spec v1.0, Sec. 6.6.5.2.
     * @param str Any string.
     * @return A valid XPath 2.0 string literal suitable for use in jcr:contains(), including enclosing quotes.
     */
    public static String stringToJCRSearchExp(String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return str;
        }

        String parseString = StringUtils.trimToEmpty(str);

        // workaround for https://issues.apache.org/jira/browse/JCR-2732
        parseString = StringUtils.replaceEach(parseString, new String[]{":)", ":(" }, new String[]{": )", ": (" });

        /*
         * http://lucene.apache.org/java/2_4_0/queryparsersyntax.html#Escaping%20Special%20Characters
         * http://www.javalobby.org/java/forums/t86124.html
         */
        String escapeChars = "[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\~\\*\\?\"\\[\\]|]";
        parseString = parseString.replaceAll(escapeChars, "\\\\$0");
        parseString = parseString.replaceAll("\'", "\'\'");

        // workaround for https://issues.apache.org/jira/browse/JCR-2733
        if (StringUtils.startsWith(parseString, "OR "))
        {
            parseString = parseString.replaceFirst("\\bOR\\b", "\"OR\"");
        }

        return parseString;

    }

    /**
     * @param path to encode eg //my//path/2009//*
     * @return String encoded path eg //my//path/_x0032_009//*
     */
    public static String encodeDigitsInPath(String path)
    {
        log.debug("path to encode is {}", path);
        if (StringUtils.isBlank(path))
        {
            String msg = "path cannot be a null or empty string";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        StringBuilder encodedPath = new StringBuilder(path.length());

        int inXpathCondition = 0;
        boolean xpathWithFunction = false;

        // TODO maybe a more robust check is needed
        for (int i = 0; i < path.length(); ++i)
        {
            char ch = path.charAt(i);

            if (i > 0 && path.charAt(i - 1) == '/' && Character.isDigit(ch))
            {
                encodedPath.append("_x" + StringUtils.leftPad(Integer.toHexString(ch), 4, '0') + "_");
            }
            else if (i > 0 && path.charAt(i - 1) == '/' && ch == '-')
            {
                encodedPath.append("_x002d_");
            }
            else if (inXpathCondition <= 0 && ch == ' ')
            {
                encodedPath.append("_x0020_");
            }
            else if (inXpathCondition <= 0 && ch == ',')
            {
                encodedPath.append("_x002c_");
            }
            else
            {

                if (ch == '[')
                {
                    inXpathCondition++;
                }
                else if (ch == '(')
                {
                    // "(" is the beginning of an expression only when used with the element() function
                    if (StringUtils.endsWith(StringUtils.substring(path, 0, i), "element"))
                    {
                        inXpathCondition++;
                        xpathWithFunction = true;
                    }
                    else if (inXpathCondition == 0)
                    {
                        encodedPath.append("_x0028_");
                        continue;
                    }
                }
                else if (inXpathCondition > 0 && ch == ']')
                {
                    inXpathCondition--;
                }
                else if (ch == ')')
                {
                    if (inXpathCondition > 0 && xpathWithFunction)
                    {
                        inXpathCondition--;
                        xpathWithFunction = false;
                    }
                    else if (inXpathCondition == 0)
                    {
                        encodedPath.append("_x0029_");
                        continue;
                    }
                }
                encodedPath.append(ch);
            }
        }
        log.debug("returning encoded path {}", encodedPath);
        return encodedPath.toString();
    }

    /**
     * Get a date in the XSD format "yyyy-MM-ddThh:mm:ss.SSS:+01:00"
     * @param date input calendar
     * @return XSD formatted date
     */
    public static String toXsdDate(Calendar date)
    {
        if (date == null)
        {
            return null;
        }

        String xsdDate = DATE_FORMAT.format(date);
        int length = xsdDate.length();

        return StringUtils.substring(xsdDate, 0, length - 2) + ":" + StringUtils.substring(xsdDate, length - 2, length);
    }

}