package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _343_UndertheShadowoftheIvoryTower extends Quest {
    //mob
    private static final List<Integer> MOBS = List.of(
            20563, 20564, 20565, 20566);
    private static final int CHANCE = 50;
    private static final int ECTOPLASM = 4365;
    //NPC
    private final int CEMA = 30834;
    private final int ICARUS = 30835;
    private final int MARSHA = 30934;
    private final int TRUMPIN = 30935;
    //items
    private final int ORB = 4364;
    //Var
    private final int[] AllowClass = {
            0xb,
            0xc,
            0xd,
            0xe,
            0x1a,
            0x1b,
            0x1c,
            0x27,
            0x28,
            0x29
    };

    public _343_UndertheShadowoftheIvoryTower() {
        super(false);

        addStartNpc(CEMA);
        addTalkId(CEMA,ICARUS,MARSHA,TRUMPIN);

        addKillId(MOBS);

        addQuestItem(ORB);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int random1 = Rnd.get(3);
        int random2 = Rnd.get(2);
        long orbs = st.getQuestItemsCount(ORB);
        if (event.equalsIgnoreCase("30834-03.htm")) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("30834-08.htm")) {
            if (orbs > 0) {
                st.giveItems(ADENA_ID, orbs * 120);
                st.takeItems(ORB, -1);
            } else
                htmltext = "30834-08.htm";
        } else if (event.equalsIgnoreCase("30834-09.htm")) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        } else if ("30934-02.htm".equalsIgnoreCase(event) || "30934-03.htm".equalsIgnoreCase(event)) {
            if (orbs < 10)
                htmltext = "noorbs.htm";
            else if (event.equalsIgnoreCase("30934-03.htm"))
                if (orbs >= 10) {
                    st.takeItems(ORB, 10);
                    st.set("playing");
                } else
                    htmltext = "noorbs.htm";
        } else if (event.equalsIgnoreCase("30934-04.htm")) {
            if (st.isSet("playing")) {
                if (random1 == 0) {
                    htmltext = "30934-05.htm";
                    st.giveItems(ORB, 10);
                } else if (random1 == 1)
                    htmltext = "30934-06.htm";
                else {
                    htmltext = "30934-04.htm";
                    st.giveItems(ORB, 20);
                }
                st.unset("playing");
            } else {
                htmltext = "Player is cheating";
                st.takeItems(ORB);
                st.exitCurrentQuest();
            }
        } else if ("30934-05.htm".equalsIgnoreCase(event)) {
            if (st.isSet("playing")) {
                if (random1 == 0) {
                    htmltext = "30934-04.htm";
                    st.giveItems(ORB, 20);
                } else if (random1 == 1) {
                    htmltext = "30934-05.htm";
                    st.giveItems(ORB, 10);
                } else
                    htmltext = "30934-06.htm";
                st.unset("playing");
            } else {
                htmltext = "Player is cheating";
                st.takeItems(ORB);
                st.exitCurrentQuest();
            }
        } else if ("30934-06.htm".equalsIgnoreCase(event)) {
            if (st.isSet("playing")) {
                if (random1 == 0) {
                    htmltext = "30934-04.htm";
                    st.giveItems(ORB, 20);
                } else if (random1 == 1)
                    htmltext = "30934-06.htm";
                else {
                    htmltext = "30934-05.htm";
                    st.giveItems(ORB, 10);
                }
                st.unset("playing");
            } else {
                htmltext = "Player is cheating";
                st.takeItems(ORB);
                st.exitCurrentQuest();
            }
        } else if ("30935-02.htm".equalsIgnoreCase(event) || "30935-03.htm".equalsIgnoreCase(event)) {
            st.unset("toss");
            if (orbs < 10)
                htmltext = "noorbs.htm";
        } else if ("30935-05.htm".equalsIgnoreCase(event)) {
            if (orbs >= 10) {
                if (random2 == 0) {
                    if (st.getInt("toss") == 4) {
                        st.unset("toss");
                        st.giveItems(ORB, 150);
                        htmltext = "30935-07.htm";
                    } else {
                        st.inc("toss");
                        htmltext = "30935-04.htm";
                    }
                } else {
                    st.unset("toss");
                    st.takeItems(ORB, 10);
                }
            } else
                htmltext = "noorbs.htm";
        } else if ("30935-06.htm".equalsIgnoreCase(event)) {
            if (orbs >= 10) {
                int toss = st.getInt("toss");
                st.unset("toss");
                if (toss == 1)
                    st.giveItems(ORB, 10);
                else if (toss == 2)
                    st.giveItems(ORB, 30);
                else if (toss == 3)
                    st.giveItems(ORB, 70);
                else if (toss == 4)
                    st.giveItems(ORB, 150);
            } else
                htmltext = "noorbs.htm";
        } else if ("30835-02.htm".equalsIgnoreCase(event))
            if (st.haveQuestItem(ECTOPLASM)) {
                st.takeItems(ECTOPLASM, 1);
                int random = Rnd.get(1000);
                if (random <= 119)
                    st.giveItems(955);
                else if (random <= 169)
                    st.giveItems(951);
                else if (random <= 329)
                    st.giveItems(2511, Rnd.get(200) + 401);
                else if (random <= 559)
                    st.giveItems(2510, Rnd.get(200) + 401);
                else if (random <= 561)
                    st.giveItems(316);
                else if (random <= 578)
                    st.giveItems(630);
                else if (random <= 579)
                    st.giveItems(188);
                else if (random <= 581)
                    st.giveItems(885);
                else if (random <= 582)
                    st.giveItems(103);
                else if (random <= 584)
                    st.giveItems(917);
                else
                    st.giveItems(736);
            } else
                htmltext = "30835-03.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        if (npcId == CEMA) {
            if (id != STARTED) {
                for (int i : AllowClass)
                    if (st.player.getClassId().id == i && st.player.getLevel() >= 40)
                        htmltext = "30834-01.htm";
                if (!"30834-01.htm".equals(htmltext)) {
                    htmltext = "30834-07.htm";
                    st.exitCurrentQuest();
                }
            } else if (st.haveQuestItem(ORB))
                htmltext = "30834-06.htm";
            else
                htmltext = "30834-05.htm";
        } else if (npcId == ICARUS)
            htmltext = "30835-01.htm";
        else if (npcId == MARSHA)
            htmltext = "30934-01.htm";
        else if (npcId == TRUMPIN)
            htmltext = "30935-01.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (Rnd.chance(CHANCE)) {
            st.giveItems(ORB);
            st.playSound(SOUND_ITEMGET);
        }
    }
}