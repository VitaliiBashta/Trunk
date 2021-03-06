package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _345_MethodToRaiseTheDead extends Quest {

    private static final int BILL_OF_IASON_HEINE = 4310;
    private static final int CHANCE = 15;
    private static final int CHANCE2 = 50;
    private final int VICTIMS_ARM_BONE = 4274;
    private final int VICTIMS_THIGH_BONE = 4275;
    private final int VICTIMS_SKULL = 4276;
    private final int VICTIMS_RIB_BONE = 4277;
    private final int VICTIMS_SPINE = 4278;
    private final int USELESS_BONE_PIECES = 4280;
    private final int POWDER_TO_SUMMON_DEAD_SOULS = 4281;

    public _345_MethodToRaiseTheDead() {
        super(false);

        addStartNpc(30970);

        addTalkId(30970, 30970, 30912, 30973);

        addQuestItem(VICTIMS_ARM_BONE,
                VICTIMS_THIGH_BONE,
                VICTIMS_SKULL,
                VICTIMS_RIB_BONE,
                VICTIMS_SPINE,
                POWDER_TO_SUMMON_DEAD_SOULS);

        addKillId(20789, 20791);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "1":
                st.setCond(1);
                st.start();
                htmltext = "dorothy_the_locksmith_q0345_03.htm";
                st.playSound(SOUND_ACCEPT);
                break;
            case "2":
                st.setCond(2);
                htmltext = "dorothy_the_locksmith_q0345_07.htm";
                break;
            case "3":
                if (st.haveQuestItem(ADENA_ID, 1000)) {
                    st.takeItems(ADENA_ID, 1000);
                    st.giveItems(POWDER_TO_SUMMON_DEAD_SOULS);
                    st.setCond(3);
                    htmltext = "magister_xenovia_q0345_03.htm";
                    st.playSound(SOUND_ITEMGET);
                } else
                    htmltext = "<html><head><body>You dont have enough adena!</body></html>";
                break;
            case "4":
                htmltext = "medium_jar_q0345_07.htm";
                st.takeAllItems(POWDER_TO_SUMMON_DEAD_SOULS, VICTIMS_ARM_BONE, VICTIMS_THIGH_BONE,
                        VICTIMS_SKULL, VICTIMS_RIB_BONE, VICTIMS_SPINE);
                st.setCond(6);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int level = st.player.getLevel();
        int cond = st.getCond();
        long amount = st.getQuestItemsCount(USELESS_BONE_PIECES);
        boolean haveAllItems = st.haveAllQuestItems(VICTIMS_ARM_BONE, VICTIMS_THIGH_BONE, VICTIMS_SKULL, VICTIMS_RIB_BONE, VICTIMS_SPINE);
        if (npcId == 30970)
            if (id == CREATED) {
                if (level >= 35)
                    htmltext = "dorothy_the_locksmith_q0345_02.htm";
                else {
                    htmltext = "dorothy_the_locksmith_q0345_01.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1 && haveAllItems)
                htmltext = "dorothy_the_locksmith_q0345_06.htm";
            else if (cond == 1)
                htmltext = "dorothy_the_locksmith_q0345_05.htm";
            else if (cond == 7) {
                htmltext = "dorothy_the_locksmith_q0345_14.htm";
                st.setCond(1);
                st.giveAdena(amount * 238);
                st.giveItems(BILL_OF_IASON_HEINE, Rnd.get(7) + 1);
                st.takeItems(USELESS_BONE_PIECES);
            }
        if (npcId == 30912)
            if (cond == 2) {
                htmltext = "magister_xenovia_q0345_01.htm";
                st.playSound(SOUND_MIDDLE);
            } else if (cond == 3)
                htmltext = "<html><head><body>What did the urn say?</body></html>";
            else if (cond == 6) {
                htmltext = "magister_xenovia_q0345_07.htm";
                st.setCond(7);
            }
        if (npcId == 30973)
            if (cond == 3)
                htmltext = "medium_jar_q0345_01.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int random = Rnd.get(100);
        if (random <= CHANCE)
            if (!st.haveQuestItem(VICTIMS_ARM_BONE))
                st.giveItems(VICTIMS_ARM_BONE);
            else if (!st.haveQuestItem(VICTIMS_THIGH_BONE))
                st.giveItems(VICTIMS_THIGH_BONE);
            else if (!st.haveQuestItem(VICTIMS_SKULL))
                st.giveItems(VICTIMS_SKULL);
            else if (!st.haveQuestItem(VICTIMS_RIB_BONE))
                st.giveItems(VICTIMS_RIB_BONE);
            else if (!st.haveQuestItem(VICTIMS_SPINE))
                st.giveItems(VICTIMS_SPINE);
        if (random <= CHANCE2)
            st.giveItems(USELESS_BONE_PIECES, Rnd.get(8) + 1);
    }
}