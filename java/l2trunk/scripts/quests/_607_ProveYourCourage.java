package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _607_ProveYourCourage extends Quest {
    private final static int KADUN_ZU_KETRA = 31370;
    private final static int VARKAS_HERO_SHADITH = 25309;

    // Quest items
    private final static int HEAD_OF_SHADITH = 7235;
    private final static int TOTEM_OF_VALOR = 7219;

    public _607_ProveYourCourage() {
        super(true);

        addStartNpc(KADUN_ZU_KETRA);
        addKillId(VARKAS_HERO_SHADITH);

        addQuestItem(HEAD_OF_SHADITH);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equals(event)) {
            htmltext = "elder_kadun_zu_ketra_q0607_0104.htm";
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if ("607_3".equals(event))
            if (st.haveQuestItem(HEAD_OF_SHADITH)) {
                htmltext = "elder_kadun_zu_ketra_q0607_0201.htm";
                st.takeItems(HEAD_OF_SHADITH);
                st.giveItems(TOTEM_OF_VALOR);
                st.addExpAndSp(0, 10000);
                st.unset("cond");
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
            } else
                htmltext = "elder_kadun_zu_ketra_q0607_0106.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() >= 75) {
                if (st.player.getKetra()> 2)
                    htmltext = "elder_kadun_zu_ketra_q0607_0101.htm";
                else {
                    htmltext = "elder_kadun_zu_ketra_q0607_0102.htm";
                    st.exitCurrentQuest(true);
                }
            } else {
                htmltext = "elder_kadun_zu_ketra_q0607_0103.htm";
                st.exitCurrentQuest(true);
            }
        } else if (cond == 1 && st.getQuestItemsCount(HEAD_OF_SHADITH) == 0)
            htmltext = "elder_kadun_zu_ketra_q0607_0106.htm";
        else if (cond == 2 && st.haveQuestItem(HEAD_OF_SHADITH) )
            htmltext = "elder_kadun_zu_ketra_q0607_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == VARKAS_HERO_SHADITH && st.getCond() == 1) {
            st.giveItems(HEAD_OF_SHADITH);
            st.setCond(2);
            st.playSound(SOUND_ITEMGET);
        }
    }
}