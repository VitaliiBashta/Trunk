package l2f.gameserver.model.petition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PetitionMainGroup extends PetitionGroup {
    private final Map<Integer,PetitionSubGroup> _subGroups = new HashMap<>();

    public PetitionMainGroup(int id) {
        super(id);
    }

    public void addSubGroup(PetitionSubGroup subGroup) {
        _subGroups.put(subGroup.getId(), subGroup);
    }

    public PetitionSubGroup getSubGroup(int val) {
        return _subGroups.get(val);
    }

    public Collection<PetitionSubGroup> getSubGroups() {
        return _subGroups.values();
    }
}
