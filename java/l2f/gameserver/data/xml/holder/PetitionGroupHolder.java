package l2f.gameserver.data.xml.holder;

import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.model.petition.PetitionMainGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class PetitionGroupHolder extends AbstractHolder {
    private static PetitionGroupHolder _instance = new PetitionGroupHolder();

    private Map<Integer,PetitionMainGroup> _petitionGroups = new HashMap<>();

    private PetitionGroupHolder() {
    }

    public static PetitionGroupHolder getInstance() {
        return _instance;
    }

    public void addPetitionGroup(PetitionMainGroup g) {
        _petitionGroups.put(g.getId(), g);
    }

    public PetitionMainGroup getPetitionGroup(int val) {
        return _petitionGroups.get(val);
    }

    public Collection<PetitionMainGroup> getPetitionGroups() {
        return _petitionGroups.values();
    }

    @Override
    public int size() {
        return _petitionGroups.size();
    }

    @Override
    public void clear() {
        _petitionGroups.clear();
    }
}
