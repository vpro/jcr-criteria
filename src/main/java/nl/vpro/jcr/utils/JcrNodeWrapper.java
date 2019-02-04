package nl.vpro.jcr.utils;

import lombok.experimental.Delegate;

import javax.jcr.Node;

/**
 * @author Michiel Meeuwissen
 */
public abstract class JcrNodeWrapper implements Node {

    @Delegate(types = Node.class)
    protected abstract Node getNode();

}
