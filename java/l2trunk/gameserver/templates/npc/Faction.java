package l2trunk.gameserver.templates.npc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Faction {
    private final static String none = "none";
    public final static Faction NONE = new Faction(none);

    private final String factionId;
    private int factionRange;
    private List<Integer> ignoreId = Collections.emptyList();

    public Faction(String factionId) {
        this.factionId = factionId;
    }

    public String getName() {
        return factionId;
    }

    public int getRange() {
        return factionRange;
    }

    public void setRange(int factionRange) {
        this.factionRange = factionRange;
    }

    public void addIgnoreNpcId(int npcId) {
        if (ignoreId.isEmpty())
            ignoreId = new ArrayList<>();
        ignoreId.add(npcId);
    }

    public boolean isIgnoreNpcId(int npcId) {
        return ignoreId.contains(npcId);
    }

    public boolean isNone() {
        return factionId.isEmpty() || factionId.equals(none);
    }

    public boolean equals(Faction faction) {
        return !isNone() && faction.getName().equalsIgnoreCase(factionId);
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return false;
        return equals((Faction) o);
    }

    public String toString() {
        return isNone() ? none : factionId;
    }
}
