package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _606_WarwithVarkaSilenos extends Quest {
    // NPC
    private static final int KADUN_ZU_KETRA = 31370;

    // Quest items
    private static final int VARKAS_MANE = 7233;
    private static final int VARKAS_MANE_DROP_CHANCE = 80;
    private static final int HORN_OF_BUFFALO = 7186;

    private static final List<Integer> VARKA_NPC_LIST = List.of(
            21350, 21351, 21353, 21354, 21355, 21357, 21358, 21360, 21361, 21362,
            21364, 21365, 21366, 21368, 21369, 21370, 21371, 21372, 21373, 21374);

    public _606_WarwithVarkaSilenos() {
        super(true);
        addStartNpc(KADUN_ZU_KETRA);
        addKillId(VARKA_NPC_LIST);

        addQuestItem(VARKAS_MANE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "quest_accept":
                htmltext = "elder_kadun_zu_ketra_q0606_0104.htm";
                st.setCond(1);
                st.setState(STARTED);
                st.playSound(SOUND_ACCEPT);
                break;
            case "606_3":
                long ec = st.getQuestItemsCount(VARKAS_MANE) / 5;
                if (ec > 0) {
                    htmltext = "elder_kadun_zu_ketra_q0606_0202.htm";
                    st.takeItems(VARKAS_MANE, ec * 5);
                    st.giveItems(HORN_OF_BUFFALO, ec);
                } else
                    htmltext = "elder_kadun_zu_ketra_q0606_0203.htm";
                break;
            case "606_4":
                htmltext = "elder_kadun_zu_ketra_q0606_0204.htm";
                st.takeItems(VARKAS_MANE, -1);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.getPlayer().getLevel() >= 74)
                htmltext = "elder_kadun_zu_ketra_q0606_0101.htm";
            else {
                htmltext = "elder_kadun_zu_ketra_q0606_0103.htm";
                st.exitCurrentQuest(true);
            }
        } else if (cond == 1 && st.getQuestItemsCount(VARKAS_MANE) == 0)
            htmltext = "elder_kadun_zu_ketra_q0606_0106.htm";
        else if (cond == 1 && st.getQuestItemsCount(VARKAS_MANE) > 0)
            htmltext = "elder_kadun_zu_ketra_q0606_0105.htm";
        return htmltext;
    }

    private boolean isVarkaNpc(int npc) {
        for (int i : VARKA_NPC_LIST)
            if (npc == i)
                return true;
        return false;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (isVarkaNpc(npc.getNpcId()) && st.getCond() == 1)
            st.rollAndGive(VARKAS_MANE, 1, VARKAS_MANE_DROP_CHANCE);
        return null;
    }
}