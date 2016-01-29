package nl.vpro.jcr.criteria.query.xpath.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class XPathTextUtilsTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNullToXPath() {
        XPathTextUtils.toXPath(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyStringToXPath() {
        XPathTextUtils.toXPath("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidPathToXPath() {
        XPathTextUtils.toXPath("//site//*");
    }

    @Test
    public void testSimplePathToXPath() {
        assertEquals("/jcr:root/site//*", XPathTextUtils.toXPath("/site"));
    }

    @Test
    public void testPathToXPathEncoding() {
        assertEquals("/jcr:root/_x0033_voor12/nieuws//*", XPathTextUtils.toXPath("/3voor12/nieuws"));
    }
}