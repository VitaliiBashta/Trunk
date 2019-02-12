package l2trunk.scripts.ai.residences.clanhall;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2trunk.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2trunk.gameserver.model.entity.events.objects.SpawnExObject;
import l2trunk.gameserver.model.entity.events.objects.ZoneObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.NpcUtils;
import l2trunk.scripts.npc.model.residences.clanhall.RainbowGourdInstance;
import l2trunk.scripts.npc.model.residences.clanhall.RainbowYetiInstance;

import java.util.List;

public final class RainbowYeti extends CharacterAI {
    public RainbowYeti(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature character) {
        RainbowYetiInstance actor = (RainbowYetiInstance) getActor();
        ClanHallMiniGameEvent miniGameEvent = actor.getEvent(ClanHallMiniGameEvent.class);
        if (miniGameEvent == null)
            return;
        if (character instanceof Player) {
            Player player = (Player) character;

            CMGSiegeClanObject siegeClan = null;
            List<CMGSiegeClanObject> attackers = miniGameEvent.getObjects(ClanHallMiniGameEvent.ATTACKERS);
            for (CMGSiegeClanObject $ : attackers)
                if ($.isParticle(player))
                    siegeClan = $;

            if (siegeClan == null)
                return;

            int index = attackers.indexOf(siegeClan);
            int warIndex;

            RainbowGourdInstance gourdInstance;
            RainbowGourdInstance gourdInstance2;
            switch (skill.id) {
                case 2240: //nectar
                    // убить хп у своего Фрукта :D
                    if (Rnd.chance(90)) {
                        gourdInstance = getGourd(index);
                        if (gourdInstance == null)
                            return;

                        gourdInstance.doDecrease(player);
                    } else
                        actor.addMob(NpcUtils.spawnSingle(35592, actor.getLoc().randomOffset(10)));
                    break;
                case 2241: //mineral water
                    // увеличить ХП у чужого фрукта
                    warIndex = rndEx(attackers.size(), index);
                    if (warIndex == Integer.MIN_VALUE)
                        return;

                    gourdInstance2 = getGourd(warIndex);
                    if (gourdInstance2 == null)
                        return;
                    gourdInstance2.doHeal();
                    break;
                case 2242: //water
                    // обменять ХП с чужим фруктом
                    warIndex = rndEx(attackers.size(), index);
                    if (warIndex == Integer.MIN_VALUE)
                        return;

                    gourdInstance = getGourd(index);
                    gourdInstance2 = getGourd(warIndex);
                    if (gourdInstance2 == null || gourdInstance == null)
                        return;

                    gourdInstance.doSwitch(gourdInstance2);
                    break;
                case 2243: //sulfur
                    // наложить дебафф в чужогой арене
                    warIndex = rndEx(attackers.size(), index);
                    if (warIndex == Integer.MIN_VALUE)
                        return;

                    ZoneObject zone = miniGameEvent.getFirstObject("zone_" + warIndex);
                    if (zone == null)
                        return;
                    zone.setActive(true);
                    ThreadPoolManager.INSTANCE.schedule(() -> zone.setActive(false), 60000L);
                    break;
            }
        }

    }

    private RainbowGourdInstance getGourd(int index) {
        ClanHallMiniGameEvent miniGameEvent = getActor().getEvent(ClanHallMiniGameEvent.class);

        SpawnExObject spawnEx = miniGameEvent.getFirstObject("arena_" + index);

        return (RainbowGourdInstance) spawnEx.getSpawns().get(1).getFirstSpawned();
    }

    private int rndEx(int size, int ex) {
        int rnd = Integer.MIN_VALUE;
        for (int i = 0; i < Byte.MAX_VALUE; i++) {
            rnd = Rnd.get(size);
            if (rnd != ex)
                break;
        }

        return rnd;
    }

}
