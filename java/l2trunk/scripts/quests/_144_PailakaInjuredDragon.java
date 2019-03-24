package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class _144_PailakaInjuredDragon extends Quest {
    // NPC
    private static final int KETRAOSHAMAN = 32499;
    private static final int KOSUPPORTER = 32502;
    private static final int KOIO = 32509;
    private static final int KOSUPPORTER2 = 32512;

    private static final int VSWARRIOR1 = 18636;
    private static final int VSWARRIOR2 = 18642;
    private static final int VSCOMMAO1 = 18646;
    private static final int VSCOMMAO2 = 18654;
    private static final int VSGMAG1 = 18649;
    private static final int VSGMAG2 = 18650;
    private static final int VSHGAPG1 = 18655;
    private static final int VSHGAPG2 = 18657;

    private static final List<Integer> Pailaka3rd = List.of(
            18635, VSWARRIOR1, 18638, 18639, 18640, 18641,
            VSWARRIOR2, 18644, 18645, VSCOMMAO1, 18648,
            VSGMAG1, VSGMAG2, 18652, 18653, VSCOMMAO2,
            VSHGAPG1, 18656, VSHGAPG2, 18658, 18659);

    private static final List<Integer> Antelopes = List.of(18637, 18643, 18647, 18651);

    // BOSS
    private static final int LATANA = 18660;

    // ITEMS
    private static final int ScrollOfEscape = 736;
    private static final int SPEAR = 13052;
    private static final int ENCHSPEAR = 13053;
    private static final int LASTSPEAR = 13054;
    private static final int STAGE1 = 13056;
    private static final int STAGE2 = 13057;

    private static final List<Integer> PAILAKA3DROP = List.of(8600, 8601, 8603, 8604);
    private static final List<Integer> ANTELOPDROP = List.of(13032, 13033);

    // REWARDS
    private static final int PSHIRT = 13296;

    private static final int[][] BUFFS = {{4357, 2}, // Haste Lv2
            {4342, 2}, // Wind Walk Lv2
            {4356, 3}, // Empower Lv3
            {4355, 3}, // Acumen Lv3
            {4351, 6}, // Concentration Lv6
            {4345, 3}, // Might Lv3
            {4358, 3}, // Guidance Lv3
            {4359, 3}, // Focus Lv3
            {4360, 3}, // Death Wisper Lv3
            {4352, 2}, // Berserker Spirit Lv2
            {4354, 4}, // Vampiric Rage Lv4
            {4347, 6} // Blessed Body Lv6
    };

    private static final int izId = 45;

    public _144_PailakaInjuredDragon() {
        addStartNpc(KETRAOSHAMAN);
        addTalkId(KOSUPPORTER, KOIO, KOSUPPORTER2);
        addAttackId(LATANA, VSWARRIOR1, VSWARRIOR2, VSCOMMAO1, VSCOMMAO2, VSGMAG1, VSGMAG2, VSHGAPG1, VSHGAPG2);
        addKillId(LATANA);
        addKillId(Pailaka3rd);
        addKillId(Antelopes);
        addQuestItem(STAGE1, STAGE2, SPEAR, ENCHSPEAR, LASTSPEAR, 13033, 13032);
    }

    private void makeBuff(NpcInstance npc, Player player, int skillId, int level) {
        List<Creature> target = new ArrayList<>();
        target.add(player);
        npc.broadcastPacket(new MagicSkillUse(npc, player, skillId, level));
        npc.callSkill(skillId, level, target, true);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        String htmltext = event;
        if ("Enter".equalsIgnoreCase(event)) {
            enterInstance(player);
            return null;
        } else if (event.startsWith("buff")) {
            int[] skill = BUFFS[toInt(event.split("buff")[1])];
            if (st.getInt("spells") < 4) {
                makeBuff(npc, player, skill[0], skill[1]);
                st.inc("spells");
                htmltext = "32509-06.htm";
                return htmltext;
            }
            if (st.getInt("spells") == 4) {
                makeBuff(npc, player, skill[0], skill[1]);
                st.inc("spells");
                htmltext = "32509-05.htm";
                return htmltext;
            }
        } else if ("Support".equalsIgnoreCase(event)) {
            if (st.getInt("spells") < 5)
                htmltext = "32509-06.htm";
            else
                htmltext = "32509-04.htm";
            return htmltext;
        } else if ("32499-02.htm".equalsIgnoreCase(event)) {
            st.unset("spells");
            st.set("stage");
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("32499-05.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_ACCEPT);
        } else if ("32502-05.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
            st.giveItems(SPEAR);
        } else if ("32512-02.htm".equalsIgnoreCase(event)) {
            st.takeAllItems(SPEAR,ENCHSPEAR, LASTSPEAR);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        int id = st.getState();
        Player player = st.player;
        if (npcId == KETRAOSHAMAN) {
            if (cond == 0) {
                if (player.getLevel() < 73 || player.getLevel() > 77) {
                    htmltext = "32499-no.htm";
                    st.exitCurrentQuest();
                } else
                    return "32499-01.htm";
            } else if (id == COMPLETED)
                htmltext = "32499-no.htm";
            else if (cond == 1 || cond == 2 || cond == 3)
                htmltext = "32499-06.htm";
            else
                htmltext = "32499-07.htm";
        } else if (npcId == KOSUPPORTER) {
            if (cond == 1 || cond == 2)
                htmltext = "32502-01.htm";
            else
                htmltext = "32502-05.htm";
        } else if (npcId == KOIO) {
            if (st.getQuestItemsCount(SPEAR) > 0 && st.getQuestItemsCount(STAGE1) == 0)
                htmltext = "32509-01.htm";
            if (st.haveQuestItem(ENCHSPEAR)  && st.getQuestItemsCount(STAGE2) == 0)
                htmltext = "32509-01.htm";
            if (st.getQuestItemsCount(SPEAR) == 0 && st.getQuestItemsCount(STAGE1) > 0)
                htmltext = "32509-07.htm";
            if (st.getQuestItemsCount(ENCHSPEAR) == 0 && st.haveQuestItem(STAGE2) )
                htmltext = "32509-07.htm";
            if (st.getQuestItemsCount(SPEAR) == 0 && st.getQuestItemsCount(ENCHSPEAR) == 0)
                htmltext = "32509-07.htm";
            if (st.getQuestItemsCount(STAGE1) == 0 && st.getQuestItemsCount(STAGE2) == 0)
                htmltext = "32509-01.htm";
            if (st.haveAllQuestItems(SPEAR,STAGE1) ) {
                st.takeAllItems(SPEAR,STAGE1);
                st.giveItems(ENCHSPEAR);
                htmltext = "32509-02.htm";
            }
            if (st.haveAllQuestItems(ENCHSPEAR,STAGE2) ) {
                st.takeAllItems(ENCHSPEAR, STAGE2);
                st.giveItems(LASTSPEAR);
                htmltext = "32509-03.htm";
            }
            if (st.haveQuestItem(LASTSPEAR) )
                htmltext = "32509-03.htm";
        } else if (npcId == KOSUPPORTER2)
            if (cond == 4) {
                st.giveItems(ScrollOfEscape);
                st.giveItems(PSHIRT);
                st.addExpAndSp(28000000, 2850000);
                st.setCond(5);
                st.complete();
                st.playSound(SOUND_FINISH);
                st.finish();
                player.setVitality(Config.VITALITY_LEVELS.get(4));
                player.getReflection().startCollapseTimer(60000);
                htmltext = "32512-01.htm";
            } else if (id == COMPLETED)
                htmltext = "32512-03.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        Player player = st.player;
        int npcId = npc.getNpcId();
        int refId = player.getReflectionId();

        switch (npcId) {
            case VSWARRIOR1:
            case VSWARRIOR2:
                if (st.isSet("stage"))
                    st.set("stage", 2);
                break;
            case VSCOMMAO1:
            case VSCOMMAO2:
                if (st.getInt("stage") == 2)
                    st.inc("stage");
                if (st.haveQuestItem(SPEAR) && st.getQuestItemsCount(STAGE1) == 0)
                    st.giveItems(STAGE1);
                break;
            case VSGMAG1:
            case VSGMAG2:
                if (st.getInt("stage") == 3)
                    st.inc("stage");
                if (st.haveQuestItem(ENCHSPEAR)  && st.getQuestItemsCount(STAGE2) == 0)
                    st.giveItems(STAGE2);
                break;
            case VSHGAPG1:
            case VSHGAPG2:
                if (st.getInt("stage") == 4)
                    st.inc("stage");
                break;
            case LATANA:
                st.setCond(4);
                st.playSound(SOUND_MIDDLE);
                addSpawnToInstance(KOSUPPORTER2, npc.getLoc(), refId);
                break;
        }

        if (Pailaka3rd.contains(npcId))
            if (Rnd.get(100) < 30)
                st.dropItem(npc, Rnd.get(PAILAKA3DROP));

        if (Antelopes.contains(npcId))
            st.dropItem(npc, Rnd.get(ANTELOPDROP), Rnd.get(1, 10));
    }

    @Override
    public void onAttack(NpcInstance npc, QuestState st) {
        Player player = st.player;
        int npcId = npc.getNpcId();
        switch (npcId) {
            case VSCOMMAO1:
            case VSCOMMAO2:
                if (st.getInt("stage") < 2) {
                    player.teleToLocation(122789, -45692, -3036);
                    return ;
                }
                break;
            case VSGMAG1:
            case VSGMAG2:
                if (st.isSet("stage")) {
                    player.teleToLocation(122789, -45692, -3036);
                    return ;
                } else if (st.getInt("stage") == 2) {
                    player.teleToLocation(116948, -46445, -2673);
                    return ;
                }
                break;
            case VSHGAPG1:
            case VSHGAPG2:
                if (st.isSet("stage") ) {
                    player.teleToLocation(122789, -45692, -3036);
                    return ;
                } else if (st.getInt("stage") == 2) {
                    player.teleToLocation(116948, -46445, -2673);
                    return ;
                } else if (st.getInt("stage") == 3) {
                    player.teleToLocation(112445, -44118, -2700);
                    return ;
                }
                break;
            case LATANA:
                if (st.isSet("stage") ) {
                    player.teleToLocation(122789, -45692, -3036);
                    return ;
                } else if (st.getInt("stage") == 2) {
                    player.teleToLocation(116948, -46445, -2673);
                    return ;
                } else if (st.getInt("stage") == 3) {
                    player.teleToLocation(112445, -44118, -2700);
                    return ;
                } else if (st.getInt("stage") == 4) {
                    player.teleToLocation(109947, -41433, -2311);
                    return ;
                }
                break;
        }
    }

    private void enterInstance(Player player) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(izId))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(izId)) {
            ReflectionUtils.enterReflection(player, izId);
        }
    }
}