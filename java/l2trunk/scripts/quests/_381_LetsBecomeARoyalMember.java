package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _381_LetsBecomeARoyalMember extends Quest {
    //Quest items
    private static final int KAILS_COIN = 5899;
    private static final int COIN_ALBUM = 5900;
    private static final int MEMBERSHIP_1 = 3813;
    private static final int CLOVER_COIN = 7569;
    private static final int ROYAL_MEMBERSHIP = 5898;
    //NPCs
    private static final int SORINT = 30232;
    private static final int SANDRA = 30090;
    //MOBs
    private static final int ANCIENT_GARGOYLE = 21018;
    private static final int VEGUS = 27316;
    //CHANCES (custom values, feel free to change them)
    private static final int GARGOYLE_CHANCE = 5;
    private static final int VEGUS_CHANCE = 100;

    public _381_LetsBecomeARoyalMember() {
        super(false);

        addStartNpc(SORINT);
        addTalkId(SANDRA);

        addKillId(ANCIENT_GARGOYLE, VEGUS);

        addQuestItem(KAILS_COIN, COIN_ALBUM, CLOVER_COIN);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("warehouse_keeper_sorint_q0381_02.htm".equalsIgnoreCase(event))
            if (st.player.getLevel() >= 55 && st.haveQuestItem(MEMBERSHIP_1)) {
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                htmltext = "warehouse_keeper_sorint_q0381_03.htm";
            } else {
                htmltext = "warehouse_keeper_sorint_q0381_02.htm";
                st.exitCurrentQuest();
            }
        else if ("sandra_q0381_02.htm".equalsIgnoreCase(event))
            if (st.getCond() == 1) {
                st.set("id");
                st.playSound(SOUND_ACCEPT);
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";

        int cond = st.getCond();
        int npcId = npc.getNpcId();
        boolean haveAlbum = st.haveQuestItem(COIN_ALBUM);

        if (npcId == SORINT) {
            if (cond == 0)
                htmltext = "warehouse_keeper_sorint_q0381_01.htm";
            else if (cond == 1) {
                boolean haveCoin = st.haveQuestItem(KAILS_COIN);
                if (haveCoin && haveAlbum) {
                    st.takeAllItems(KAILS_COIN, COIN_ALBUM);
                    st.giveItems(ROYAL_MEMBERSHIP);
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest();
                    htmltext = "warehouse_keeper_sorint_q0381_06.htm";
                } else if (!haveAlbum)
                    htmltext = "warehouse_keeper_sorint_q0381_05.htm";
                else htmltext = "warehouse_keeper_sorint_q0381_04.htm";
            }
        } else {
            long clover = st.getQuestItemsCount(CLOVER_COIN);
            if (haveAlbum)
                htmltext = "sandra_q0381_05.htm";
            else if (clover > 0) {
                st.takeItems(CLOVER_COIN);
                st.giveItems(COIN_ALBUM);
                st.playSound(SOUND_ITEMGET);
                htmltext = "sandra_q0381_04.htm";
            } else if (!st.isSet("id"))
                htmltext = "sandra_q0381_01.htm";
            else
                htmltext = "sandra_q0381_03.htm";
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();

        boolean haveAlbum = st.haveQuestItem(COIN_ALBUM);
        boolean haveCoin = st.haveQuestItem(KAILS_COIN);
        boolean haveClover = st.haveQuestItem(CLOVER_COIN);

        if (npcId == ANCIENT_GARGOYLE && !haveCoin ) {
            if (Rnd.chance(GARGOYLE_CHANCE)) {
                st.giveItems(KAILS_COIN);
                if (haveAlbum  || haveClover)
                    st.playSound(SOUND_MIDDLE);
                else
                    st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == VEGUS && !haveClover && !haveAlbum  && st.isSet("id"))
            if (Rnd.chance(VEGUS_CHANCE)) {
                st.giveItems(CLOVER_COIN);
                if (haveCoin)
                    st.playSound(SOUND_MIDDLE);
                else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}