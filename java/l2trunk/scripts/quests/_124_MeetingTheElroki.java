package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _124_MeetingTheElroki extends Quest {
    //NPC
    private final int Marquez = 32113;
    private final int Mushika = 32114;
    private final int Asamah = 32115;
    private final int Karakawei = 32117;
    private final int Mantarasa = 32118;
    //item
    private final int Mushika_egg = 8778;

    public _124_MeetingTheElroki() {
        addStartNpc(Marquez);
        addTalkId(Mushika,Asamah,Karakawei,Mantarasa);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();

        if ("marquez_q0124_03.htm".equals(event))
            st.start();
        else if ("marquez_q0124_04.htm".equals(event) && cond == 0) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("marquez_q0124_06.htm".equals(event) && cond == 1) {
            st.setCond(2);
            st.playSound(SOUND_ITEMGET);
        } else if ("mushika_q0124_03.htm".equals(event) && cond == 2) {
            st.setCond(3);
            st.playSound(SOUND_ITEMGET);
        } else if ("asama_q0124_06.htm".equals(event) && cond == 3) {
            st.setCond(4);
            st.playSound(SOUND_ITEMGET);
        } else if ("shaman_caracawe_q0124_03.htm".equals(event) && cond == 4)
            st.set("id");
        if ("shaman_caracawe_q0124_05.htm".equals(event) && cond == 4) {
            st.setCond(5);
            st.playSound(SOUND_ITEMGET);
        }
        if ("egg_of_mantarasa_q0124_02.htm".equals(event) && cond == 5) {
            st.giveItems(Mushika_egg);
            st.setCond(6);
            st.playSound(SOUND_MIDDLE);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Marquez) {
            if (cond == 0) {
                if (st.player.getLevel() < 75) {
                    htmltext = "marquez_q0124_02.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "marquez_q0124_01.htm";
            } else if (cond == 1)
                htmltext = "marquez_q0124_04.htm";
            else if (cond == 2)
                htmltext = "marquez_q0124_07.htm";
        } else if (npcId == Mushika && cond == 2)
            htmltext = "mushika_q0124_01.htm";
        else if (npcId == Asamah) {
            if (cond == 3)
                htmltext = "asama_q0124_03.htm";
            else if (cond == 6) {
                htmltext = "asama_q0124_08.htm";
                st.takeItems(Mushika_egg, 1);
                st.addExpAndSp(301922, 30294);
                st.giveItems(ADENA_ID, 100013);
                st.playSound(SOUND_FINISH);
                st.complete();
                st.finish();
            }
        } else if (npcId == Karakawei) {
            if (cond == 4) {
                htmltext = "shaman_caracawe_q0124_01.htm";

                if (st.isSet("id"))
                    htmltext = "shaman_caracawe_q0124_03.htm";
            }
        } else if (npcId == Mantarasa && cond == 5)
            htmltext = "egg_of_mantarasa_q0124_01.htm";
        return htmltext;
    }
}