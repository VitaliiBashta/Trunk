package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _242_PossessorOfaPreciousSoul2 extends Quest {
    private static final int VIRGILS_LETTER_1_PART = 7677;
    private static final int BLONDE_STRAND = 7590;
    private static final int SORCERY_INGREDIENT = 7596;
    private static final int CARADINE_LETTER = 7678;
    private static final int ORB_OF_BINDING = 7595;

    private static final int PureWhiteUnicorn = 31747;
    private NpcInstance PureWhiteUnicornSpawn = null;

    public _242_PossessorOfaPreciousSoul2() {
        super(false);

        addStartNpc(31742);

        addTalkId(31743, 31751, 31752, 30759, 30738, 31744, 31748, 31746, PureWhiteUnicorn);

        addKillId(27317);

        addQuestItem(ORB_OF_BINDING, SORCERY_INGREDIENT, BLONDE_STRAND);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("31742-2.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.unset("CoRObjId");
            st.takeItems(VIRGILS_LETTER_1_PART);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("31743-5.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("31744-2.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("31751-2.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
        } else if ("30759-2.htm".equalsIgnoreCase(event)) {
            st.takeItems(BLONDE_STRAND);
            st.setCond(7);
            st.playSound(SOUND_MIDDLE);
        } else if ("30759-4.htm".equalsIgnoreCase(event)) {
            st.setCond(9);
            st.playSound(SOUND_MIDDLE);
        } else if ("30738-2.htm".equalsIgnoreCase(event)) {
            st.setCond(8);
            st.giveItems(SORCERY_INGREDIENT);
            st.playSound(SOUND_MIDDLE);
        } else if ("31748-2.htm".equalsIgnoreCase(event)) {
            st.takeItems(ORB_OF_BINDING);
            st.killNpcByObjectId(st.getInt("CoRObjId"));
            st.unset("talk");
            if (st.getInt("prog") < 4) {
                st.inc("prog");
                st.playSound(SOUND_MIDDLE);
            }
            if (st.getInt("prog") >= 4) {
                st.setCond(10);
                st.playSound(SOUND_MIDDLE);
            }
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (!st.player.isSubClassActive())
            return "Subclass only!";

        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 31742) {
            if (cond == 0) {
                if (st.player.isQuestCompleted(_241_PossessorOfaPreciousSoul1.class) && st.player.getLevel() >= 60)
                    htmltext = "31742-1.htm";
                else {
                    htmltext = "31742-0.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "31742-2r.htm";
        } else if (npcId == 31743) {
            if (cond == 1)
                htmltext = "31743-1.htm";
            else if (cond == 2)
                htmltext = "31743-2r.htm";
            else if (cond == 11) {
                htmltext = "31743-6.htm";
                st.giveItems(CARADINE_LETTER);
                st.addExpAndSp(455764, 0);
                st.unset("cond");
                st.unset("CoRObjId");
                st.unset("prog");
                st.unset("talk");
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        } else if (npcId == 31744) {
            if (cond == 2)
                htmltext = "31744-1.htm";
            else if (cond == 3)
                htmltext = "31744-2r.htm";
        } else if (npcId == 31751) {
            if (cond == 3)
                htmltext = "31751-1.htm";
            else if (cond == 4)
                htmltext = "31751-2r.htm";
            else if (cond == 5 && st.haveQuestItem(BLONDE_STRAND)) {
                st.setCond(6);
                htmltext = "31751-3.htm";
            } else if (cond == 6 && st.haveQuestItem(BLONDE_STRAND))
                htmltext = "31751-3r.htm";
        } else if (npcId == 31752) {
            if (cond == 4) {
                st.giveItems(BLONDE_STRAND);
                st.playSound(SOUND_ITEMGET);
                st.setCond(5);
                htmltext = "31752-2.htm";
            } else
                htmltext = "31752-n.htm";
        } else if (npcId == 30759) {
            if (cond == 6 && st.haveQuestItem(BLONDE_STRAND))
                htmltext = "30759-1.htm";
            else if (cond == 7)
                htmltext = "30759-2r.htm";
            else if (cond == 8 && st.getQuestItemsCount(SORCERY_INGREDIENT) == 1)
                htmltext = "30759-3.htm";
        } else if (npcId == 30738) {
            if (cond == 7)
                htmltext = "30738-1.htm";
            else if (cond == 8)
                htmltext = "30738-2r.htm";
        } else if (npcId == 31748) {
            if (cond == 9)
                if (st.haveQuestItem(ORB_OF_BINDING)) {
                    if (npc.objectId() != st.getInt("CoRObjId")) {
                        st.set("CoRObjId", npc.objectId());
                        st.set("talk");
                        htmltext = "31748-1.htm";
                    } else if (st.isSet("talk"))
                        htmltext = "31748-1.htm";
                    else
                        htmltext = "noquest";
                } else
                    htmltext = "31748-0.htm";
        } else if (npcId == 31746) {
            if (st.getCond() == 9)
                htmltext = "31746-1.htm";
            else if (st.getCond() == 10) {
                htmltext = "31746-1.htm";
                npc.doDie(npc);
                if (PureWhiteUnicornSpawn == null || !st.player.knowsObject(PureWhiteUnicornSpawn) || !PureWhiteUnicornSpawn.isVisible())
                    PureWhiteUnicornSpawn = st.addSpawn(PureWhiteUnicorn, npc.getLoc(), 0, 120000);
            } else
                htmltext = "noquest";
        } else if (npcId == PureWhiteUnicorn)
            if (st.getCond() == 10) {
                htmltext = "31747-1.htm";
                st.setCond(11);
            } else if (st.getCond() == 11)
                htmltext = "31747-2.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (!st.player.isSubClassActive())
            return;

        if (st.getCond() == 9 && !st.haveQuestItem(ORB_OF_BINDING, 4))
            st.giveItems(ORB_OF_BINDING);
        if (st.haveQuestItem(ORB_OF_BINDING, 4)) {
            st.playSound(SOUND_MIDDLE);
        } else {
            st.playSound(SOUND_ITEMGET);
        }
    }
}