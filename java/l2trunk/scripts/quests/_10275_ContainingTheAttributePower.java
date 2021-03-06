package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.tables.SkillTable;

public final class _10275_ContainingTheAttributePower extends Quest {
    private final static int Holly = 30839;
    private final static int Weber = 31307;
    private final static int Yin = 32325;
    private final static int Yang = 32326;
    private final static int Water = 27380;
    private final static int Air = 27381;

    private final static int YinSword = 13845;
    private final static int YangSword = 13881;
    private final static int SoulPieceWater = 13861;
    private final static int SoulPieceAir = 13862;

    public _10275_ContainingTheAttributePower() {
        addStartNpc(Holly,Weber);

        addTalkId(Yin,Yang);

        addKillId(Air,Water);

        addQuestItem(YinSword, YangSword, SoulPieceWater, SoulPieceAir);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        Player player = st.player;

        if ("30839-02.htm".equalsIgnoreCase(event) || "31307-02.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30839-05.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("31307-05.htm".equalsIgnoreCase(event)) {
            st.setCond(7);
            st.playSound(SOUND_MIDDLE);
        } else if ("32325-03.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.giveItems(YinSword, 1, Element.FIRE, 10);
            st.playSound(SOUND_MIDDLE);
        } else if ("32326-03.htm".equalsIgnoreCase(event)) {
            st.setCond(8);
            st.giveItems(YangSword, 1, Element.EARTH, 10);
            st.playSound(SOUND_MIDDLE);
        } else if ("32325-06.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(YinSword) ) {
                st.takeItems(YinSword);
                htmltext = "32325-07.htm";
            }
            st.giveItems(YinSword, 1, Element.FIRE, 10);
        } else if ("32326-06.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(YangSword) ) {
                st.takeItems(YangSword);
                htmltext = "32326-07.htm";
            }
            st.giveItems(YangSword, 1, Element.EARTH, 10);
        } else if ("32325-09.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            SkillTable.INSTANCE.getInfo(2635).getEffects(player);
            st.giveItems(YinSword, 1, Element.FIRE, 10);
            st.playSound(SOUND_MIDDLE);
        } else if ("32326-09.htm".equalsIgnoreCase(event)) {
            st.setCond(10);
            SkillTable.INSTANCE.getInfo(2636).getEffects(player);
            st.giveItems(YangSword, 1, Element.EARTH, 10);
            st.playSound(SOUND_MIDDLE);
        } else {
            int item = 0;

            switch (event) {
                case "1":
                    item = 10521;
                    break;
                case "2":
                    item = 10522;
                    break;
                case "3":
                    item = 10523;
                    break;
                case "4":
                    item = 10524;
                    break;
                case "5":
                    item = 10525;
                    break;
                case "6":
                    item = 10526;
                    break;
            }

            if (item > 0) {
                st.giveItems(item, 2, true);
                st.addExpAndSp(202160, 20375);
                st.finish();
                st.playSound(SOUND_FINISH);
                if (npc != null)
                    htmltext = npc.getNpcId() + "-1" + event + ".htm";
                else
                    htmltext = null;
            }
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        int cond = st.getCond();
        int npcId = npc.getNpcId();

        if (id == COMPLETED) {
            if (npcId == Holly)
                htmltext = "30839-0a.htm";
            else if (npcId == Weber)
                htmltext = "31307-0a.htm";
        } else if (id == CREATED)
            if (st.player.getLevel() >= 76)
                if (npcId == Holly)
                    htmltext = "30839-01.htm";
                else
                    htmltext = "31307-01.htm";
            else if (npcId == Holly)
                htmltext = "30839-00.htm";
            else
                htmltext = "31307-00.htm";
        else if (npcId == Holly) {
            if (cond == 1)
                htmltext = "30839-03.htm";
            else if (cond == 2)
                htmltext = "30839-05.htm";
        } else if (npcId == Weber) {
            if (cond == 1)
                htmltext = "31307-03.htm";
            else if (cond == 7)
                htmltext = "31307-05.htm";
        } else if (npcId == Yin) {
            if (cond == 2)
                htmltext = "32325-01.htm";
            else if (cond == 3 || cond == 5)
                htmltext = "32325-04.htm";
            else if (cond == 4) {
                htmltext = "32325-08.htm";
                st.takeAllItems(YinSword,SoulPieceWater);
            } else if (cond == 6)
                htmltext = "32325-10.htm";
        } else if (npcId == Yang)
            if (cond == 7)
                htmltext = "32326-01.htm";
            else if (cond == 8 || cond == 10)
                htmltext = "32326-04.htm";
            else if (cond == 9) {
                htmltext = "32326-08.htm";
                st.takeAllItems(YangSword,SoulPieceAir);
            } else if (cond == 11)
                htmltext = "32326-10.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return;

        int cond = st.getCond();
        int npcId = npc.getNpcId();

        if (npcId == Air) {
            if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == YangSword && (cond == 8 || cond == 10) && st.getQuestItemsCount(SoulPieceAir) < 6 && Rnd.chance(30)) {
                st.giveItems(SoulPieceAir);
                if (st.getQuestItemsCount(SoulPieceAir) >= 6) {
                    st.setCond(cond + 1);
                    st.playSound(SOUND_MIDDLE);
                }
            }
        } else if (npcId == Water)
            if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == YinSword && (cond == 3 || cond == 5) && st.getQuestItemsCount(SoulPieceWater) < 6 && Rnd.chance(30)) {
                st.giveItems(SoulPieceWater);
                if (st.getQuestItemsCount(SoulPieceWater) >= 6) {
                    st.setCond(cond + 1);
                    st.playSound(SOUND_MIDDLE);
                }
            }
    }
}