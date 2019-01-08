package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

public final class _659_IdRatherBeCollectingFairyBreath extends Quest implements ScriptFile {
    //NPC
    private final int GALATEA = 30634;
    //Mobs
    private final List<Integer> MOBS = List.of(
            20078, 21026, 21025, 21024, 21023);
    //Quest Item
    private final int FAIRY_BREATH = 8286;

    public _659_IdRatherBeCollectingFairyBreath() {
        super(false);

        addStartNpc(GALATEA);
        addTalkId(GALATEA);
        addTalkId(GALATEA);
        addTalkId(GALATEA);
        addKillId(MOBS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("high_summoner_galatea_q0659_0103.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("high_summoner_galatea_q0659_0203.htm")) {
            long count = st.getQuestItemsCount(FAIRY_BREATH);
            if (count > 0) {
                long reward = 0;
                if (count < 10)
                    reward = count * 50;
                else
                    reward = count * 50 + 5365;
                st.takeItems(FAIRY_BREATH, -1);
                st.giveItems(ADENA_ID, reward);
            }
        } else if (event.equalsIgnoreCase("high_summoner_galatea_q0659_0204.htm"))
            st.exitCurrentQuest(true);
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = 0;
        if (id != CREATED)
            cond = st.getCond();
        if (npcId == GALATEA)
            if (st.getPlayer().getLevel() < 26) {
                htmltext = "high_summoner_galatea_q0659_0102.htm";
                st.exitCurrentQuest(true);
            } else if (cond == 0)
                htmltext = "high_summoner_galatea_q0659_0101.htm";
            else if (cond == 1)
                if (st.getQuestItemsCount(FAIRY_BREATH) == 0)
                    htmltext = "high_summoner_galatea_q0659_0105.htm";
                else
                    htmltext = "high_summoner_galatea_q0659_0105.htm";
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1)
            MOBS.stream()
                    .filter(i -> (npcId == i && Rnd.chance(30)))
                    .forEach(i -> {
                        st.giveItems(FAIRY_BREATH, 1);
                        st.playSound(SOUND_ITEMGET);
                    });
        return null;
    }
}