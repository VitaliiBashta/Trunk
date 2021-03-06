package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;

public final class _10292_SevenSignsGirlOfDoubt extends Quest {
    // NPC
    private static final int WOOD = 32593;
    private static final int FRANZ = 32597;
    private static final int ELCARDIA = 32784;
    private static final int HARDIN = 30832;

    // MOBD
    private static final List<Integer> MOBS = List.of(22801, 22802, 22803, 22804, 22805, 22806);
    private static final int CREATURE_OF_THE_DUSK_1 = 27422;
    private static final int CREATURE_OF_THE_DUSK_2 = 27424;

    // ITEMS
    private static final int ELCARDIAS_MARK = 17226;

    public _10292_SevenSignsGirlOfDoubt() {
        addStartNpc(WOOD);
        addTalkId( FRANZ, ELCARDIA, HARDIN);
        addKillId(MOBS);
        addKillId(CREATURE_OF_THE_DUSK_1, CREATURE_OF_THE_DUSK_2);
        addQuestItem(ELCARDIAS_MARK);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        if ("priest_wood_q10292_3.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("priest_wood_q10292_4.htm".equalsIgnoreCase(event))
            enterInstance(player);
        else if ("witness_of_dawn_q10292_2.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("elcadia_abyssal_saintess_q10292_2.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("elcadia_abyssal_saintess_q10292_9.htm".equalsIgnoreCase(event)) {
            st.setCond(7);
            st.playSound(SOUND_MIDDLE);
        } else if ("hardin_q10292_1.htm".equalsIgnoreCase(event)) {
            st.setCond(8);
            st.playSound(SOUND_MIDDLE);
        } else if ("spawnTestMobs".equalsIgnoreCase(event)) {
            int reflectId = player.getReflectionId();
            st.set("CreatureOfTheDusk1");
            st.set("CreatureOfTheDusk2");
            addSpawnToInstance(CREATURE_OF_THE_DUSK_1, Location.of(89416, -237992, -9632), reflectId);
            addSpawnToInstance(CREATURE_OF_THE_DUSK_2, Location.of(89416, -238136, -9632), reflectId);
            return null;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.player;
        if (player.getBaseClassId() != player.getActiveClassId())
            return "no_subclass_allowed.htm";
        switch (npcId) {
            case WOOD:
                if (cond == 0) {
                    if (player.getLevel() >= 81 && player.isQuestCompleted(_198_SevenSignsEmbryo.class))
                        htmltext = "priest_wood_q10292_0.htm";
                    else {
                        htmltext = "priest_wood_q10292_0n.htm";
                        st.exitCurrentQuest();
                    }
                } else if (cond == 1)
                    htmltext = "priest_wood_q10292_3.htm";
                else if (cond > 1 && !st.isCompleted())
                    htmltext = "priest_wood_q10292_5.htm";
                else if (st.isCompleted())
                    htmltext = "priest_wood_q10292_6.htm";
                break;
            case FRANZ:
                if (cond == 1)
                    htmltext = "witness_of_dawn_q10292_0.htm";
                else if (cond == 2)
                    htmltext = "witness_of_dawn_q10292_4.htm";
                break;
            case ELCARDIA:
                if (cond == 2)
                    htmltext = "elcadia_abyssal_saintess_q10292_0.htm";
                else if (cond == 3)
                    htmltext = "elcadia_abyssal_saintess_q10292_2.htm";
                else if (cond == 4) {
                    htmltext = "elcadia_abyssal_saintess_q10292_3.htm";
                    st.takeItems(ELCARDIAS_MARK);
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(5);
                } else if (cond == 5)
                    htmltext = "elcadia_abyssal_saintess_q10292_5.htm";
                else if (cond == 6)
                    htmltext = "elcadia_abyssal_saintess_q10292_6.htm";
                else if (cond == 7)
                    htmltext = "elcadia_abyssal_saintess_q10292_9.htm";
                else if (cond == 8) {
                    htmltext = "elcadia_abyssal_saintess_q10292_10.htm";
                    st.addExpAndSp(10000000, 1000000);
                    st.complete();
                    st.finish();
                    st.playSound(SOUND_FINISH);
                }
                break;
            case HARDIN:
                if (cond == 7)
                    htmltext = "hardin_q10292_0.htm";
                else if (cond == 8)
                    htmltext = "hardin_q10292_2.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (cond == 3 && MOBS.contains(npcId) && Rnd.chance(70)) {
            st.giveItems(ELCARDIAS_MARK);
            if (st.getQuestItemsCount(ELCARDIAS_MARK) < 10)
                st.playSound(SOUND_ITEMGET);
            else {
                st.playSound(SOUND_MIDDLE);
                st.setCond(4);
            }
        } else if (npcId == CREATURE_OF_THE_DUSK_1) {
            st.set("CreatureOfTheDusk1", 2);
            if (st.getInt("CreatureOfTheDusk2") == 2) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(6);
            }
        } else if (npcId == CREATURE_OF_THE_DUSK_2) {
            st.set("CreatureOfTheDusk2", 2);
            if (st.getInt("CreatureOfTheDusk1") == 2) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(6);
            }
        }
    }

    private void enterInstance(Player player) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(145))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(145)) {
            ReflectionUtils.enterReflection(player, 145);
        }
    }
}