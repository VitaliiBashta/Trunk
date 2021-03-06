package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _293_HiddenVein extends Quest {
    // NPCs
    private static final int Filaur = 30535;
    private static final int Chichirin = 30539;
    // Mobs
    private static final int Utuku_Orc = 20446;
    private static final int Utuku_Orc_Archer = 20447;
    private static final int Utuku_Orc_Grunt = 20448;
    // Quest items
    private static final int Chrysolite_Ore = 1488;
    private static final int Torn_Map_Fragment = 1489;
    private static final int Hidden_Ore_Map = 1490;
    // Chances
    private static final int Torn_Map_Fragment_Chance = 5;
    private static final int Chrysolite_Ore_Chance = 45;

    public _293_HiddenVein() {
        addStartNpc(Filaur);
        addTalkId(Chichirin);
        addKillId(Utuku_Orc,Utuku_Orc_Archer,Utuku_Orc_Grunt);
        addQuestItem(Chrysolite_Ore,Torn_Map_Fragment,Hidden_Ore_Map);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        if ("elder_filaur_q0293_03.htm".equalsIgnoreCase(event) && state == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("elder_filaur_q0293_06.htm".equalsIgnoreCase(event) && state == STARTED) {
            st.playSound(SOUND_FINISH);
            st.finish();
        } else if ("chichirin_q0293_03.htm".equalsIgnoreCase(event) && state == STARTED) {
            if (st.getQuestItemsCount(Torn_Map_Fragment) < 4)
                return "chichirin_q0293_02.htm";
            st.takeItems(Torn_Map_Fragment);
            st.giveItems(Hidden_Ore_Map);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int _state = st.getState();
        int npcId = npc.getNpcId();

        if (_state == CREATED) {
            if (npcId != Filaur)
                return "noquest";
            if (st.player.getRace() != Race.dwarf) {
                st.exitCurrentQuest();
                return "elder_filaur_q0293_00.htm";
            }
            if (st.player.getLevel() < 6) {
                st.exitCurrentQuest();
                return "elder_filaur_q0293_01.htm";
            }
            st.setCond(0);
            return "elder_filaur_q0293_02.htm";
        }

        if (_state != STARTED)
            return "noquest";

        if (npcId == Filaur) {
            long Chrysolite_Ore_count = st.getQuestItemsCount(Chrysolite_Ore);
            long Hidden_Ore_Map_count = st.getQuestItemsCount(Hidden_Ore_Map);
            long reward = st.getQuestItemsCount(Chrysolite_Ore) * 10 + st.getQuestItemsCount(Hidden_Ore_Map) * 1000L;
            if (reward == 0)
                return "elder_filaur_q0293_04.htm";

            if (Chrysolite_Ore_count > 0)
                st.takeItems(Chrysolite_Ore);
            if (Hidden_Ore_Map_count > 0)
                st.takeItems(Hidden_Ore_Map);
            st.giveItems(ADENA_ID, reward);

            if (st.player.getClassId().occupation() == 0) {
                st.player.setVar("p1q2");
                st.player.sendPacket(new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n                  Go find the Newbie Guide."));
                QuestState qs = st.player.getQuestState(_255_Tutorial.class);
                if (qs != null && qs.getInt("Ex") != 10) {
                    st.showQuestionMark(26);
                    qs.set("Ex", 10);
                    if (st.player.getClassId().isMage()) {
                        st.playTutorialVoice("tutorial_voice_027");
                        st.giveItems(5790, 3000);
                    } else {
                        st.playTutorialVoice("tutorial_voice_026");
                        st.giveItems(5789, 6000);
                    }
                }
            }

            return Chrysolite_Ore_count > 0 && Hidden_Ore_Map_count > 0 ? "elder_filaur_q0293_09.htm" : Hidden_Ore_Map_count > 0 ? "elder_filaur_q0293_08.htm" : "elder_filaur_q0293_05.htm";
        }

        if (npcId == Chichirin)
            return "chichirin_q0293_01.htm";

        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;

        if (Rnd.chance(Torn_Map_Fragment_Chance)) {
            qs.giveItems(Torn_Map_Fragment);
            qs.playSound(SOUND_ITEMGET);
        } else if (Rnd.chance(Chrysolite_Ore_Chance)) {
            qs.giveItems(Chrysolite_Ore);
            qs.playSound(SOUND_ITEMGET);
        }
    }
}