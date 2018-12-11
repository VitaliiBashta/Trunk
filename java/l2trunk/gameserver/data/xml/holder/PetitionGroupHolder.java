package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.petition.PetitionMainGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public final class PetitionGroupHolder {
    private static final Map<Integer, PetitionMainGroup> PETITION_GROUPS = new HashMap<>();

    private PetitionGroupHolder() {
    }

    public static void addPetitionGroup(PetitionMainGroup g) {
        PETITION_GROUPS.put(g.getId(), g);
    }

    public static int size() {
        return PETITION_GROUPS.size();
    }

    public static PetitionMainGroup getPetitionGroup(int val) {
        return PETITION_GROUPS.get(val);
    }

    public static Collection<PetitionMainGroup> getPetitionGroups() {
        return PETITION_GROUPS.values();
    }

    public void clear() {
        PETITION_GROUPS.clear();
    }
}
