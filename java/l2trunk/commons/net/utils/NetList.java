package l2trunk.commons.net.utils;

import java.util.ArrayList;
import java.util.Iterator;

public final class NetList extends ArrayList<Net> {
    public boolean isInRange(String address) {
        for (Net net : this)
            if (net.isInRange(address))
                return true;
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Net> itr = this.iterator(); itr.hasNext(); ) {
            sb.append(itr.next());
            if (itr.hasNext())
                sb.append(',');
        }
        return sb.toString();
    }
}