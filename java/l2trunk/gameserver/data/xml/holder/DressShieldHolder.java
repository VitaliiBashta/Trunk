package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.DressShieldData;

import java.util.ArrayList;
import java.util.List;

public final class DressShieldHolder  {
    private DressShieldHolder() {
    }

    private static final List<DressShieldData> shields = new ArrayList<>();


    public static void addShield(DressShieldData shield) {
        shields.add(shield);
    }

    public List<DressShieldData> getAllShields() {
        return shields;
    }

    public DressShieldData getShield(int id) {
        for (DressShieldData shield : shields) {
            if (shield.getId() == id)
                return shield;
        }

        return null;
    }

    public static int size() {
        return shields.size();
    }

    public void clear() {
        shields.clear();
    }
}
