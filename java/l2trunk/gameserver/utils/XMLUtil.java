package l2trunk.gameserver.utils;

import org.w3c.dom.Node;

import static l2trunk.commons.lang.NumberUtils.toBoolean;

public final class XMLUtil {

    public static boolean getAttributeBooleanValue(Node n, String item, boolean dflt) {
        final Node d = n.getAttributes().getNamedItem(item);
        if (d == null)
            return dflt;
        final String val = d.getNodeValue();
        if (val == null)
            return dflt;
        return toBoolean(val);
    }

}