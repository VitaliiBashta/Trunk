package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _025_HidingBehindTheTruth extends Quest {
    private static final int EARRING_OF_BLESSING = 874;
    private static final int GEMSTONE_KEY = 7157;
    private static final int LIDIAS_DRESS = 7155;
    private static final int MAP_FOREST_OF_DEADMAN = 7063;
    private static final int NECKLACE_OF_BLESSING = 936;
    private static final int RING_OF_BLESSING = 905;
    // Список NPC
    private final int AGRIPEL = 31348;
    private final int BENEDICT = 31349;
    private final int BROKEN_BOOK_SHELF = 31534;
    private final int COFFIN = 31536;
    private final int MAID_OF_LIDIA = 31532;
    private final int MYSTERIOUS_WIZARD = 31522;
    private final int TOMBSTONE = 31531;
    // Список итемов
    private final int CONTRACT = 7066;
    private final int SUSPICIOUS_TOTEM_DOLL_1 = 7151;
    private final int SUSPICIOUS_TOTEM_DOLL_2 = 7156;
    private final int SUSPICIOUS_TOTEM_DOLL_3 = 7158;

    // Список мобов
    // Triol's Pawn
    private final int TRIOLS_PAWN = 27218;

    private NpcInstance COFFIN_SPAWN = null;

    public _025_HidingBehindTheTruth() {
        super(false);

        addStartNpc(BENEDICT);

        addTalkId(AGRIPEL,BROKEN_BOOK_SHELF,COFFIN,MAID_OF_LIDIA,MYSTERIOUS_WIZARD,TOMBSTONE);

        addKillId(TRIOLS_PAWN);

        addQuestItem(SUSPICIOUS_TOTEM_DOLL_3);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        if ("StartQuest".equalsIgnoreCase(event)) {
            if (st.getCond() == 0)
                st.start();
            if (st.player.isQuestCompleted(_024_InhabitantsOfTheForestOfTheDead.class)) {
                st.playSound(SOUND_ACCEPT);
                if (st.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL_1) == 0) {
                    st.setCond(2);
                    return "31349-03a.htm";
                }
                return "31349-03.htm";
            } else {
                st.setCond(1);
                return "31349-02.htm";
            }
        } else if ("31349-10.htm".equalsIgnoreCase(event))
            st.setCond(4);
        else if ("31348-08.htm".equalsIgnoreCase(event)) {
            if (st.getCond() == 4) {
                st.setCond(5);
                st.takeItems(SUSPICIOUS_TOTEM_DOLL_1);
                st.takeItems(SUSPICIOUS_TOTEM_DOLL_2);
                st.giveItemIfNotHave(GEMSTONE_KEY);
            } else if (st.getCond() == 5)
                return "31348-08a.htm";
        } else if ("31522-04.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
                st.giveItemIfNotHave(MAP_FOREST_OF_DEADMAN);
        } else if ("31534-07.htm".equalsIgnoreCase(event)) {

            st.addSpawn(TRIOLS_PAWN, player.getLoc());
            st.setCond(7);
        } else if ("31534-11.htm".equalsIgnoreCase(event)) {
            st.set("id", 8);
            st.giveItems(CONTRACT);
        } else if ("31532-07.htm".equalsIgnoreCase(event))
            st.setCond(11);
        else if ("31531-02.htm".equalsIgnoreCase(event)) {
            st.setCond(12);

            if (COFFIN_SPAWN != null)
                COFFIN_SPAWN.deleteMe();
            COFFIN_SPAWN = st.addSpawn(COFFIN);

            st.startQuestTimer("Coffin_Despawn", 120000);
        } else if (event.equalsIgnoreCase("Coffin_Despawn")) {
            if (COFFIN_SPAWN != null)
                COFFIN_SPAWN.deleteMe();

            if (st.getCond() == 12)
                st.setCond(11);
            return null;
        } else if ("Lidia_wait".equalsIgnoreCase(event)) {
            st.set("id", 14);
            return null;
        } else if ("31532-21.htm".equalsIgnoreCase(event))
            st.setCond(15);
        else if ("31522-13.htm".equalsIgnoreCase(event))
            st.setCond(16);
        else if ("31348-16.htm".equalsIgnoreCase(event))
            st.setCond(17);
        else if ("31348-17.htm".equalsIgnoreCase(event))
            st.setCond(18);
        else if ("31348-14.htm".equalsIgnoreCase(event))
            st.set("id", 16);
        else if ("End1".equalsIgnoreCase(event)) {
            if (st.getCond() != 17)
                return "31532-24.htm";
            st.giveItems(RING_OF_BLESSING, 2);
            st.giveItems(EARRING_OF_BLESSING);
            st.addExpAndSp(572277, 53750);
            st.finish();
            return "31532-25.htm";
        } else if ("End2".equalsIgnoreCase(event)) {
            if (st.getCond() != 18)
                return "31522-15a.htm";
            st.giveItems(NECKLACE_OF_BLESSING);
            st.giveItems(EARRING_OF_BLESSING);
            st.addExpAndSp(572277, 53750);
            st.finish();
            return "31522-16.htm";
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        int IntId = st.getInt("id");
        switch (npcId) {
            case BENEDICT:
                if (cond == 0 || cond == 1)
                    return "31349-01.htm";
                if (cond == 2)
                    return st.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL_1) == 0 ? "31349-03a.htm" : "31349-03.htm";
                if (cond == 3)
                    return "31349-03.htm";
                if (cond == 4)
                    return "31349-11.htm";
                break;

            case MYSTERIOUS_WIZARD:
                if (cond == 2) {
                    st.setCond(3);
                    st.giveItems(SUSPICIOUS_TOTEM_DOLL_2);
                    return "31522-01.htm";
                }
                if (cond == 3)
                    return "31522-02.htm";
                if (cond == 5)
                    return "31522-03.htm";
                if (cond == 6)
                    return "31522-05.htm";
                if (cond == 8) {
                    if (IntId != 8)
                        return "31522-05.htm";
                    st.setCond(9);
                    return "31522-06.htm";
                }
                if (cond == 15)
                    return "31522-06a.htm";
                if (cond == 16)
                    return "31522-12.htm";
                if (cond == 17)
                    return "31522-15a.htm";
                if (cond == 18) {
                    st.set("id", 18);
                    return "31522-15.htm";
                }
                break;

            case AGRIPEL:
                if (cond == 4)
                    return "31348-01.htm";
                if (cond == 5)
                    return "31348-03.htm";
                if (cond == 16)
                    return IntId == 16 ? "31348-15.htm" : "31348-09.htm";
                if (cond == 17 || cond == 18)
                    return "31348-15.htm";
                break;

            case BROKEN_BOOK_SHELF:
                if (cond == 6)
                    return "31534-01.htm";
                if (cond == 7)
                    return "31534-08.htm";
                if (cond == 8)
                    return IntId == 8 ? "31534-06.htm" : "31534-10.htm";
                break;

            case MAID_OF_LIDIA:
                if (cond == 9)
                    return st.getQuestItemsCount(CONTRACT) > 0 ? "31532-01.htm" : "You have no Contract...";
                if (cond == 11 || cond == 12)
                    return "31532-08.htm";
                if (cond == 13) {
                    if (st.getQuestItemsCount(LIDIAS_DRESS) == 0)
                        return "31532-08.htm";
                    st.setCond(14);
                    st.startQuestTimer("Lidia_wait", 60000);
                    st.takeItems(LIDIAS_DRESS, 1);
                    return "31532-09.htm";
                }
                if (cond == 14)
                    return IntId == 14 ? "31532-10.htm" : "31532-09.htm";
                if (cond == 17) {
                    st.set("id", 17);
                    return "31532-23.htm";
                }
                if (cond == 18)
                    return "31532-24.htm";
                break;

            case TOMBSTONE:
                if (cond == 11)
                    return "31531-01.htm";
                if (cond == 12)
                    return "31531-02.htm";
                if (cond == 13)
                    return "31531-03.htm";
                break;

            case COFFIN:
                if (cond == 12) {
                    st.setCond(13);
                    st.giveItems(LIDIAS_DRESS, 1);
                    return "31536-01.htm";
                }
                if (cond == 13)
                    return "31531-03.htm";
                break;
        }
        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs == null || qs.getState() != STARTED)
            return;

        if (npc.getNpcId() == TRIOLS_PAWN && qs.getCond() == 7) {
            qs.giveItems(SUSPICIOUS_TOTEM_DOLL_3);
            qs.playSound(SOUND_MIDDLE);
            qs.setCond(8);
        }
    }
}