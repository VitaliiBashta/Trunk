package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.SpecialMonsterInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class MeleonInstance extends SpecialMonsterInstance {
    public final static int Young_Watermelon = 13271;
    public final static int Young_Honey_Watermelon = 13275;
    private final static int Rain_Watermelon = 13273;
    private final static int Defective_Watermelon = 13272;
    private final static int Rain_Honey_Watermelon = 13277;
    private final static int Defective_Honey_Watermelon = 13276;
    private final static int Large_Rain_Watermelon = 13274;
    private final static int Large_Rain_Honey_Watermelon = 13278;

    private Player player;

    public MeleonInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    public Player getSpawner() {
        return player;
    }

    public void setSpawner(Player spawner) {
        player = spawner;
    }

    @Override
    public void reduceCurrentHp(double i, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
        if (attacker.getActiveWeaponInstance() == null)
            return;

        int weaponId = attacker.getActiveWeaponInstance().getItemId();

        if (getNpcId() == Defective_Honey_Watermelon || getNpcId() == Rain_Honey_Watermelon || getNpcId() == Large_Rain_Honey_Watermelon) {
            // Разрешенное оружие для больших тыкв:
            // 4202 Chrono Cithara
            // 5133 Chrono Unitus
            // 5817 Chrono Campana
            // 7058 Chrono Darbuka
            // 8350 Chrono Maracas
            if (weaponId != 4202 && weaponId != 5133 && weaponId != 5817 && weaponId != 7058 && weaponId != 8350)
                return;
            i = 1;
        } else if (getNpcId() == Rain_Watermelon || getNpcId() == Defective_Watermelon || getNpcId() == Large_Rain_Watermelon) {
            i = 5;
        } else
            return;

        super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
    }

}