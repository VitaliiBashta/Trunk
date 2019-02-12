package l2trunk.scripts.services;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

import static l2trunk.gameserver.utils.ItemFunctions.*;

public final class GrandIsleofPrayerRace extends Functions {
    private static final int RACE_STAMP = 10013;
    private static final int SECRET_KEY = 9694;

    public void startRace() {
        if ( player == null || npc == null)
            return;

        npc.altUseSkill(Skill.SKILL_EVENT_TIMER, player);
        removeItem(player, RACE_STAMP, player.inventory.getCountOf(RACE_STAMP), "GrandIsleofPrayerRace$startRace");
        show("default/32349-2.htm", player, npc);
    }

    public String DialogAppend_32349(Integer val) {
        if (player == null)
            return "";

        // Нет бафа с таймером
        if (player.getEffectList().getEffectsBySkillId(Skill.SKILL_EVENT_TIMER) == null)
            return "<br>[scripts_services.GrandIsleofPrayerRace:startRace|Start the Race.]";

        // Есть бафф с таймером
        long raceStampsCount = player.inventory.getCountOf( RACE_STAMP);
        if (raceStampsCount < 4)
            return "<br>*Race in progress, hurry!*";
        removeItem(player, RACE_STAMP, raceStampsCount, "GrandIsleofPrayerRace Dialog");
        addItem(player, SECRET_KEY, 3);
        player.getEffectList().stopEffect(Skill.SKILL_EVENT_TIMER);
        return "<br>Good job, here is your keys.";
    }
}