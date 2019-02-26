package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _328_SenseForBusiness extends Quest {
    //NPC
    private static final int SARIEN = 30436;
    //items
    private final int MONSTER_EYE_CARCASS = 1347;
    private final int MONSTER_EYE_LENS = 1366;
    private final int BASILISK_GIZZARD = 1348;

    public _328_SenseForBusiness() {
        super(false);

        addStartNpc(SARIEN);
        addKillId(20055,20059,20067,20068,20070,20072);
        addQuestItem(MONSTER_EYE_CARCASS,MONSTER_EYE_LENS,BASILISK_GIZZARD);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("trader_salient_q0328_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("trader_salient_q0328_06.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int id = st.getState();
        if (id == CREATED)
            st.setCond(0);
        if (st.getCond() == 0) {
            if (st.player.getLevel() >= 21) {
                htmltext = "trader_salient_q0328_02.htm";
                return htmltext;
            }
            htmltext = "trader_salient_q0328_01.htm";
            st.exitCurrentQuest();
        } else {
            long carcass = st.getQuestItemsCount(MONSTER_EYE_CARCASS);
            long lenses = st.getQuestItemsCount(MONSTER_EYE_LENS);
            long gizzard = st.getQuestItemsCount(BASILISK_GIZZARD);
            if (carcass + lenses + gizzard > 0) {
                st.giveItems(ADENA_ID, 30 * carcass + 2000 * lenses + 75 * gizzard);
                st.takeItems(MONSTER_EYE_CARCASS, -1);
                st.takeItems(MONSTER_EYE_LENS, -1);
                st.takeItems(BASILISK_GIZZARD, -1);
                htmltext = "trader_salient_q0328_05.htm";
            } else
                htmltext = "trader_salient_q0328_04.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int n = Rnd.get(1, 100);
        if (npcId == 20055) {
            if (n < 47) {
                st.giveItems(MONSTER_EYE_CARCASS);
                st.playSound(SOUND_ITEMGET);
            } else if (n < 49) {
                st.giveItems(MONSTER_EYE_LENS);
                st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20059) {
            if (n < 51) {
                st.giveItems(MONSTER_EYE_CARCASS);
                st.playSound(SOUND_ITEMGET);
            } else if (n < 53) {
                st.giveItems(MONSTER_EYE_LENS);
                st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20067) {
            if (n < 67) {
                st.giveItems(MONSTER_EYE_CARCASS);
                st.playSound(SOUND_ITEMGET);
            } else if (n < 69) {
                st.giveItems(MONSTER_EYE_LENS);
                st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20068) {
            if (n < 75) {
                st.giveItems(MONSTER_EYE_CARCASS);
                st.playSound(SOUND_ITEMGET);
            } else if (n < 77) {
                st.giveItems(MONSTER_EYE_LENS);
                st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20070) {
            if (n < 50) {
                st.giveItems(BASILISK_GIZZARD);
                st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20072)
            if (n < 51) {
                st.giveItems(BASILISK_GIZZARD);
                st.playSound(SOUND_ITEMGET);
            }
    }
}