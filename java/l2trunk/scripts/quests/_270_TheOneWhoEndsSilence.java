package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _270_TheOneWhoEndsSilence extends Quest {
    private static final int Greymore = 32757;
    private static final int TatteredMonkClothes = 15526;
    private static final List<Integer> LowMobs = List.of(22791, 22790, 22793);
    private static final List<Integer> HighMobs = List.of(
            22794, 22795, 22797, 22798, 22799, 22800);

    private static final List<Integer> receipes = List.of(
            10373, 10374, 10375, 10376, 10377, 10378, 10379, 10380, 10381);

    private static final List<Integer> scrolls = List.of(
            5593, 5594, 5595, 9898);

    private static final List<Integer> materials = List.of(
            10397, 10398, 10399, 10400, 10401, 10402, 10403, 10405);

    public _270_TheOneWhoEndsSilence() {
        super(false);
        addStartNpc(Greymore);
        addKillId(LowMobs);
        addKillId(HighMobs);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("greymore_q270_03.htm".equalsIgnoreCase(event)) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("showrags".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(TatteredMonkClothes) < 1)
                htmltext = "greymore_q270_05.htm";
            else if (st.getQuestItemsCount(TatteredMonkClothes) < 100)
                htmltext = "greymore_q270_06.htm";
            else if (st.getQuestItemsCount(TatteredMonkClothes) >= 100)
                htmltext = "greymore_q270_07.htm";
        } else if ("rags100".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(TatteredMonkClothes) >= 100) {
                st.takeItems(TatteredMonkClothes, 100);
                int i = Rnd.get(1, 3);
                if (i == 1)
                    st.giveItems(Rnd.get(receipes));
                else if (i == 2)
                    st.giveItems(Rnd.get(materials));
                else
                    st.giveItems(Rnd.get(scrolls));
                htmltext = "greymore_q270_09.htm";
            } else
                htmltext = "greymore_q270_08.htm";
        } else if ("rags200".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(TatteredMonkClothes) >= 200) {
                st.takeItems(TatteredMonkClothes, 200);
                if (Rnd.chance(50))
                    st.giveItems(Rnd.get(receipes));
                else
                    st.giveItems(Rnd.get(materials));
                st.giveItems(Rnd.get(scrolls));
                htmltext = "greymore_q270_09.htm";
            } else
                htmltext = "greymore_q270_08.htm";
        } else if ("rags300".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(TatteredMonkClothes) >= 300) {
                st.takeItems(TatteredMonkClothes, 300);
                //Recipes
                st.giveItems(Rnd.get(scrolls));
                //Material
                st.giveItems(Rnd.get(materials));
                // SP Scrolls
                st.giveItems(Rnd.get(scrolls));

                htmltext = "greymore_q270_09.htm";
            } else
                htmltext = "greymore_q270_08.htm";
        } else if ("rags400".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(TatteredMonkClothes) >= 400) {
                st.takeItems(TatteredMonkClothes, 400);
                st.giveItems(Rnd.get(receipes));
                st.giveItems(Rnd.get(materials));
                st.giveItems(Rnd.get(scrolls), 2);

                htmltext = "greymore_q270_09.htm";
            } else
                htmltext = "greymore_q270_08.htm";
        } else if ("rags500".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(TatteredMonkClothes) >= 500) {
                st.takeItems(TatteredMonkClothes, 500);
                st.giveItems(Rnd.get(receipes), 2);
                st.giveItems(Rnd.get(materials), 2);
                st.giveItems(Rnd.get(scrolls));
                htmltext = "greymore_q270_09.htm";
            } else
                htmltext = "greymore_q270_08.htm";
        } else if ("quit".equalsIgnoreCase(event)) {
            htmltext = "greymore_q270_10.htm";
            st.exitCurrentQuest(true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npc.getNpcId() == Greymore) {
            if (cond == 0) {
                if (st.player.getLevel() >= 82 && st.player.isQuestCompleted(_10288_SecretMission.class))
                    htmltext = "greymore_q270_01.htm";
                else {
                    htmltext = "greymore_q270_00.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1)
                htmltext = "greymore_q270_04.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 1) {
            if (LowMobs.contains(npc.getNpcId()) && Rnd.chance(40))
                st.giveItems(TatteredMonkClothes, 1, true);
            else if (HighMobs.contains(npc.getNpcId()))
                st.giveItems(TatteredMonkClothes, 1, true);
        }
    }
}