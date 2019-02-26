package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.stream.IntStream;

public final class _344_1000YearsEndofLamentation extends Quest {

    // Quest items
    private static final int ARTICLES_DEAD_HEROES = 4269;
    private static final int OLD_KEY = 4270;
    private static final int OLD_HILT = 4271;
    private static final int OLD_TOTEM = 4272;
    private static final int CRUCIFIX = 4273;

    // Chances
    private static final int CHANCE = 36;
    private static final int SPECIAL = 1000;

    // NPCs
    private static final int GILMORE = 30754;
    private static final int RODEMAI = 30756;
    private static final int ORVEN = 30857;
    private static final int KAIEN = 30623;
    private static final int GARVARENTZ = 30704;

    public _344_1000YearsEndofLamentation() {
        super(true);
        addStartNpc(GILMORE);

        addTalkId(RODEMAI, ORVEN, GARVARENTZ, KAIEN);

        addKillId(IntStream.rangeClosed(20236, 20241).toArray());

        addQuestItem(ARTICLES_DEAD_HEROES, OLD_KEY, OLD_HILT, OLD_TOTEM, CRUCIFIX);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        long amount = st.getQuestItemsCount(ARTICLES_DEAD_HEROES);
        int cond = st.getCond();
        int level = st.player.getLevel();
        if ("30754-04.htm".equalsIgnoreCase(event)) {
            if (level >= 48 && cond == 0) {
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
            } else {
                htmltext = "noquest";
                st.exitCurrentQuest();
            }
        } else if ("30754-08.htm".equalsIgnoreCase(event)) {
            st.exitCurrentQuest();
            st.playSound(SOUND_FINISH);
        } else if ("30754-06.htm".equalsIgnoreCase(event) && cond == 1) {
            if (amount == 0)
                htmltext = "30754-06a.htm";
            else {
                if (Rnd.get((int) (SPECIAL / st.getRateQuestsReward())) >= amount)
                    st.giveItems(ADENA_ID, amount * 60);
                else {
                    htmltext = "30754-10.htm";
                    st.set("ok");
                    st.set("amount", (int) amount);
                }
                st.takeItems(ARTICLES_DEAD_HEROES);
            }
        } else if ("30754-11.htm".equalsIgnoreCase(event) && cond == 1)
            if (st.isSet("ok")) {
                int random = Rnd.get(100);
                st.setCond(2);
                st.unset("ok");
                if (random < 25) {
                    htmltext = "30754-12.htm";
                    st.giveItems(OLD_KEY);
                } else if (random < 50) {
                    htmltext = "30754-13.htm";
                    st.giveItems(OLD_HILT);
                } else if (random < 75) {
                    htmltext = "30754-14.htm";
                    st.giveItems(OLD_TOTEM);
                } else
                    st.giveItems(CRUCIFIX);
            } else {
                htmltext = "noquest";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        long amount = st.getQuestItemsCount(ARTICLES_DEAD_HEROES);
        if (id == CREATED) {
            if (st.player.getLevel() >= 48)
                htmltext = "30754-02.htm";
            else {
                htmltext = "30754-01.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == GILMORE && cond == 1) {
            if (amount > 0)
                htmltext = "30754-05.htm";
            else
                htmltext = "30754-09.htm";
        } else if (cond == 2) {
            if (npcId == GILMORE)
                htmltext = "30754-15.htm";
            else if (rewards(st, npcId)) {
                htmltext = npcId + "-01.htm";
                st.setCond(3);
                st.playSound(SOUND_MIDDLE);
            }
        } else if (cond == 3)
            if (npcId == GILMORE) {
                int amt = st.getInt("amount");
                int mission = st.getInt("mission");
                int bonus = 0;
                if (mission == 1)
                    bonus = 1500;
                else if (mission == 2)
                    st.giveItems(4044);
                else if (mission == 3)
                    st.giveItems(4043);
                else if (mission == 4)
                    st.giveItems(4042);
                if (amt > 0) {
                    st.unset("amount");
                    st.giveItems(ADENA_ID, amt * 50 + bonus, true);
                }
                htmltext = "30754-16.htm";
                st.setCond(1);
                st.unset("mission");
            } else
                htmltext = npcId + "-02.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1)
            st.rollAndGive(ARTICLES_DEAD_HEROES, 1, CHANCE + (npc.getNpcId() - 20234) * 2);
    }

    private boolean rewards(QuestState st, int npcId) {
        boolean state = false;
        int chance = Rnd.get(100);
        if (npcId == ORVEN && st.haveQuestItem(CRUCIFIX)) {
            st.set("mission");
            st.takeItems(CRUCIFIX);
            state = true;
            if (chance < 50)
                st.giveItems(1875, 19);
            else if (chance < 70)
                st.giveItems(952, 5);
            else
                st.giveItems(2437);
        } else if (npcId == GARVARENTZ && st.haveQuestItem(OLD_TOTEM)) {
            st.set("mission", 2);
            st.takeItems(OLD_TOTEM);
            state = true;
            if (chance < 45)
                st.giveItems(1882, 70);
            else if (chance < 95)
                st.giveItems(1881, 50);
            else
                st.giveItems(191);
        } else if (npcId == KAIEN && st.haveQuestItem(OLD_HILT)) {
            st.set("mission", 3);
            st.takeItems(OLD_HILT);
            state = true;
            if (chance < 50)
                st.giveItems(1874, 25);
            else if (chance < 75)
                st.giveItems(1887, 10);
            else if (chance < 99)
                st.giveItems(951);
            else
                st.giveItems(133);
        } else if (npcId == RODEMAI && st.haveQuestItem(OLD_KEY)) {
            st.set("mission", 4);
            st.takeItems(OLD_KEY);
            state = true;
            if (chance < 40)
                st.giveItems(1879, 55);
            else if (chance < 90)
                st.giveItems(951);
            else
                st.giveItems(885);
        }
        return state;
    }
}