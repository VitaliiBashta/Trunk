package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _660_AidingtheFloranVillage extends Quest {
    // MOBS
    private static final int CARSED_SEER = 21106;
    private static final int PLAIN_WATCMAN = 21102;
    private static final int DELU_LIZARDMAN_SHAMAN = 20781;
    private static final int DELU_LIZARDMAN_SAPPLIER = 21104;
    private static final int DELU_LIZARDMAN_COMMANDER = 21107;
    private static final int DELU_LIZARDMAN_SPESIAL_AGENT = 21105;
    //REWARDS
    private static final int SCROLL_ENCANT_ARMOR = 956;
    private static final int SCROLL_ENCHANT_WEAPON = 955;
    // NPC
    private final int MARIA = 30608;
    private final int ALEX = 30291;
    private final int ROUGH_HEWN_ROCK_GOLEM = 21103;
    //ITEMS
    private final int WATCHING_EYES = 8074;
    private final int ROUGHLY_HEWN_ROCK_GOLEM_SHARD = 8075;
    private final int DELU_LIZARDMAN_SCALE = 8076;

    public _660_AidingtheFloranVillage() {
        super(false);

        addStartNpc(MARIA);
        addTalkId(ALEX);

        addKillId(CARSED_SEER, PLAIN_WATCMAN, ROUGH_HEWN_ROCK_GOLEM, DELU_LIZARDMAN_SHAMAN,
                DELU_LIZARDMAN_SAPPLIER, DELU_LIZARDMAN_COMMANDER, DELU_LIZARDMAN_SPESIAL_AGENT);

        addQuestItem(WATCHING_EYES,DELU_LIZARDMAN_SCALE,ROUGHLY_HEWN_ROCK_GOLEM_SHARD);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        long EYES = st.getQuestItemsCount(WATCHING_EYES);
        long SCALE = st.getQuestItemsCount(DELU_LIZARDMAN_SCALE);
        long SHARD = st.getQuestItemsCount(ROUGHLY_HEWN_ROCK_GOLEM_SHARD);
        if ("30608-04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30291-05.htm".equalsIgnoreCase(event)) {
            if (EYES + SCALE + SHARD >= 45) {
                st.giveItems(ADENA_ID, EYES * 100 + SCALE * 100 + SHARD * 100 + 9000);
                st.takeItems(WATCHING_EYES);
                st.takeItems(DELU_LIZARDMAN_SCALE);
                st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD);
            } else {
                st.giveItems(ADENA_ID, EYES * 100 + SCALE * 100 + SHARD * 100);
                st.takeItems(WATCHING_EYES);
                st.takeItems(DELU_LIZARDMAN_SCALE);
                st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD);
            }
            st.playSound(SOUND_ITEMGET);
        } else if ("30291-11.htm".equalsIgnoreCase(event)) {
            if (EYES + SCALE + SHARD >= 99) {
                long n = 100 - EYES;
                long t = 100 - SCALE - EYES;
                if (EYES >= 100)
                    st.takeItems(WATCHING_EYES, 100);
                else {
                    st.takeItems(WATCHING_EYES);
                    if (SCALE >= n)
                        st.takeItems(DELU_LIZARDMAN_SCALE, n);
                    else {
                        st.takeItems(DELU_LIZARDMAN_SCALE);
                        st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD, t);
                    }
                }
                if (Rnd.chance(80)) {
                    st.giveItems(ADENA_ID, 13000);
                    st.giveItems(SCROLL_ENCANT_ARMOR);
                } else
                    st.giveItems(ADENA_ID, 1000);
                st.playSound(SOUND_ITEMGET);
            } else
                htmltext = "30291-14.htm";
        } else if ("30291-12.htm".equalsIgnoreCase(event)) {
            if (EYES + SCALE + SHARD >= 199) {
                long n = 200 - EYES;
                long t = 200 - SCALE - EYES;
                int luck = Rnd.get(15);
                if (EYES >= 200)
                    st.takeItems(WATCHING_EYES, 200);
                else
                    st.takeItems(WATCHING_EYES);
                if (SCALE >= n)
                    st.takeItems(DELU_LIZARDMAN_SCALE, n);
                else
                    st.takeItems(DELU_LIZARDMAN_SCALE);
                st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD, t);
                if (luck < 9) {
                    st.giveItems(ADENA_ID, 20000);
                    st.giveItems(SCROLL_ENCANT_ARMOR);
                } else if (luck < 12)
                    st.giveItems(SCROLL_ENCHANT_WEAPON);
                else
                    st.giveItems(ADENA_ID, 2000);
                st.playSound(SOUND_ITEMGET);
            } else
                htmltext = "30291-14.htm";
        } else if ("30291-13.htm".equalsIgnoreCase(event)) {
            if (EYES + SCALE + SHARD >= 499) {
                long n = 500 - EYES;
                long t = 500 - SCALE - EYES;
                if (EYES >= 500)
                    st.takeItems(WATCHING_EYES, 500);
                else
                    st.takeItems(WATCHING_EYES);
                if (SCALE >= n)
                    st.takeItems(DELU_LIZARDMAN_SCALE, n);
                else {
                    st.takeItems(DELU_LIZARDMAN_SCALE);
                    st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD, t);
                }
                if (Rnd.chance(80)) {
                    st.giveItems(ADENA_ID, 45000);
                    st.giveItems(SCROLL_ENCHANT_WEAPON);
                } else
                    st.giveItems(ADENA_ID, 5000);
                st.playSound(SOUND_ITEMGET);
            } else
                htmltext = "30291-14.htm";
        } else if ("30291-06.htm".equalsIgnoreCase(event)) {
            st.exitCurrentQuest();
            st.playSound(SOUND_FINISH);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == MARIA && cond < 1) {
            if (st.player.getLevel() < 30) {
                htmltext = "30608-01.htm";
                st.exitCurrentQuest();
            } else
                htmltext = "30608-02.htm";
        } else if (npcId == MARIA && cond == 1)
            htmltext = "30608-06.htm";
        else if (npcId == ALEX && cond == 1) {
            htmltext = "30291-01.htm";
            st.playSound(SOUND_MIDDLE);
            st.setCond(2);
        } else if (npcId == ALEX && cond == 2)
            if (st.haveAnyQuestItems(WATCHING_EYES, DELU_LIZARDMAN_SCALE, ROUGHLY_HEWN_ROCK_GOLEM_SHARD)) {
                htmltext = "30291-03.htm";
            } else {
                htmltext = "30291-02.htm";
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int chance = Rnd.get(100) + 1;
        if (st.getCond() == 2)
            if (npcId == 21106 | npcId == 21102 && chance < 79) {
                st.giveItems(WATCHING_EYES);
                st.playSound(SOUND_ITEMGET);
            } else if (npcId == ROUGH_HEWN_ROCK_GOLEM && chance < 75) {
                st.giveItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD);
                st.playSound(SOUND_ITEMGET);
            } else if (npcId == 20781 | npcId == 21104 | npcId == 21107 | npcId == 21105 && chance < 67) {
                st.giveItems(DELU_LIZARDMAN_SCALE);
                st.playSound(SOUND_ITEMGET);
            }
    }
}