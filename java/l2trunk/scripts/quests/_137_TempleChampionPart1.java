package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _137_TempleChampionPart1 extends Quest {
    // NPCs
    private static final int SYLVAIN = 30070;

    // ITEMs
    private static final int FRAGMENT = 10340;
    private static final int BadgeTempleExecutor = 10334;
    private static final int BadgeTempleMissionary = 10339;

    // Monsters
    private final static int GraniteGolem = 20083;
    private final static int HangmanTree = 20144;
    private final static int AmberBasilisk = 20199;
    private final static int Strain = 20200;
    private final static int Ghoul = 20201;
    private final static int DeadSeeker = 20202;

    public _137_TempleChampionPart1() {
        addStartNpc(SYLVAIN);
        addKillId(GraniteGolem, HangmanTree, AmberBasilisk, Strain, Ghoul, DeadSeeker);
        addQuestItem(FRAGMENT);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {

        if ("sylvain_q0137_04.htm".equalsIgnoreCase(event)) {
            st.takeAllItems(BadgeTempleExecutor, BadgeTempleMissionary);
            st.setCond(1);
            st.start();
            st.unset("talk");
            st.playSound(SOUND_ACCEPT);
        } else if ("sylvain_q0137_08.htm".equalsIgnoreCase(event))
            st.set("talk");
        else if ("sylvain_q0137_10.htm".equalsIgnoreCase(event))
            st.set("talk", 2);
        else if ("sylvain_q0137_13.htm".equalsIgnoreCase(event)) {
            st.unset("talk");
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("sylvain_q0137_24.htm".equalsIgnoreCase(event)) {
            st.giveAdena(69146);
            st.playSound(SOUND_FINISH);
            st.addExpAndSp(219975, 13047);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == SYLVAIN)
            if (cond == 0) {
                if (st.player.getLevel() >= 35 && st.haveAllQuestItems(BadgeTempleExecutor, BadgeTempleMissionary))
                    htmltext = "sylvain_q0137_01.htm";
                else {
                    htmltext = "sylvain_q0137_03.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1) {
                if (!st.isSet("talk"))
                    htmltext = "sylvain_q0137_05.htm";
                else if (st.isSet("talk"))
                    htmltext = "sylvain_q0137_08.htm";
                else if (st.getInt("talk") == 2)
                    htmltext = "sylvain_q0137_10.htm";
            } else if (cond == 2)
                htmltext = "sylvain_q0137_13.htm";
            else if (cond == 3 && st.haveQuestItem(FRAGMENT, 30)) {
                htmltext = "sylvain_q0137_15.htm";
                st.set("talk");
                st.takeItems(FRAGMENT);
            } else if (cond == 3 && st.isSet("talk"))
                htmltext = "sylvain_q0137_16.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 2)
            st.giveItemIfNotHave(FRAGMENT, 30);
        if (st.haveQuestItem(FRAGMENT, 30)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        }
    }
}