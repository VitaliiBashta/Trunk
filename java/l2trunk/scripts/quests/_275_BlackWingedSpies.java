package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _275_BlackWingedSpies extends Quest {
    // NPCs
    private static final int Tantus = 30567;
    // Mobs
    private static final int Darkwing_Bat = 20316;
    private static final int Varangkas_Tracker = 27043;
    // Quest items
    private static final int Darkwing_Bat_Fang = 1478;
    private static final int Varangkas_Parasite = 1479;
    // Chances
    private static final int Varangkas_Parasite_Chance = 10;

    public _275_BlackWingedSpies() {
        addStartNpc(Tantus);
        addKillId(Darkwing_Bat, Varangkas_Tracker);
        addQuestItem(Darkwing_Bat_Fang, Varangkas_Parasite);
    }

    private static void spawn_Varangkas_Tracker(QuestState st) {
        st.takeItems(Varangkas_Parasite);
        st.giveItems(Varangkas_Parasite);
        st.addSpawn(Varangkas_Tracker);
    }

    private static void give_Darkwing_Bat_Fang(QuestState st, long _count) {
        long max_inc = 70 - st.getQuestItemsCount(Darkwing_Bat_Fang);
        if (max_inc < 1)
            return;
        if (_count > max_inc)
            _count = max_inc;
        st.giveItems(Darkwing_Bat_Fang, _count);
        st.playSound(_count == max_inc ? SOUND_MIDDLE : SOUND_ITEMGET);
        if (_count == max_inc)
            st.setCond(2);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("neruga_chief_tantus_q0275_03.htm".equalsIgnoreCase(event) && st.getState() == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (npc.getNpcId() != Tantus)
            return "noquest";
        int _state = st.getState();

        if (_state == CREATED) {
            if (st.player.getRace() != Race.orc) {
                st.exitCurrentQuest();
                return "neruga_chief_tantus_q0275_00.htm";
            }
            if (st.player.getLevel() < 11) {
                st.exitCurrentQuest();
                return "neruga_chief_tantus_q0275_01.htm";
            }
            st.setCond(0);
            return "neruga_chief_tantus_q0275_02.htm";
        }

        if (_state != STARTED)
            return "noquest";
        int cond = st.getCond();

        if (!st.haveQuestItem(Darkwing_Bat_Fang, 70)) {
            if (cond != 1)
                st.setCond(1);
            return "neruga_chief_tantus_q0275_04.htm";
        }
        if (cond == 2) {
            st.giveItems(ADENA_ID, 4550);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
            return "neruga_chief_tantus_q0275_05.htm";
        }
        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();
        long Darkwing_Bat_Fang_count = qs.getQuestItemsCount(Darkwing_Bat_Fang);

        if (npcId == Darkwing_Bat && Darkwing_Bat_Fang_count < 70) {
            if (Darkwing_Bat_Fang_count > 10 && Darkwing_Bat_Fang_count < 65 && Rnd.chance(Varangkas_Parasite_Chance)) {
                spawn_Varangkas_Tracker(qs);
                return;
            }
            give_Darkwing_Bat_Fang(qs, 1);
        } else if (npcId == Varangkas_Tracker && Darkwing_Bat_Fang_count < 70 && qs.getQuestItemsCount(Varangkas_Parasite) > 0) {
            qs.takeItems(Varangkas_Parasite, -1);
            give_Darkwing_Bat_Fang(qs, 5);
        }
    }
}