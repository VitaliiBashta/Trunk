package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _005_MinersFavor extends Quest {
    //Item
    private static final int NECKLACE = 906;
    //NPC
    private final int BOLTER = 30554;
    private final int SHARI = 30517;
    private final int GARITA = 30518;
    private final int REED = 30520;
    private final int BRUNON = 30526;
    //QuestItem
    private final int BOLTERS_LIST = 1547;
    private final int MINING_BOOTS = 1548;
    private final int MINERS_PICK = 1549;
    private final int BOOMBOOM_POWDER = 1550;
    private final int REDSTONE_BEER = 1551;
    private final int BOLTERS_SMELLY_SOCKS = 1552;

    public _005_MinersFavor() {
        super(false);
        addStartNpc(BOLTER);
        addTalkId(SHARI);
        addTalkId(GARITA);
        addTalkId(REED);
        addTalkId(BRUNON);

        addQuestItem(BOLTERS_LIST, BOLTERS_SMELLY_SOCKS, MINING_BOOTS, MINERS_PICK, BOOMBOOM_POWDER, REDSTONE_BEER);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("miner_bolter_q0005_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            st.giveItems(BOLTERS_LIST, 1, false);
            st.giveItems(BOLTERS_SMELLY_SOCKS, 1, false);
        } else if (event.equalsIgnoreCase("blacksmith_bronp_q0005_02.htm")) {
            st.takeItems(BOLTERS_SMELLY_SOCKS, -1);
            st.giveItems(MINERS_PICK, 1, false);
            if (st.getQuestItemsCount(BOLTERS_LIST) > 0 && st.getQuestItemsCount(MINING_BOOTS) + st.getQuestItemsCount(MINERS_PICK) + st.getQuestItemsCount(BOOMBOOM_POWDER) + st.getQuestItemsCount(REDSTONE_BEER) == 4) {
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            } else
                st.playSound(SOUND_ITEMGET);

        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == BOLTER) {
            if (cond == 0) {
                if (st.getPlayer().getLevel() >= 2)
                    htmltext = "miner_bolter_q0005_02.htm";
                else {
                    htmltext = "miner_bolter_q0005_01.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1)
                htmltext = "miner_bolter_q0005_04.htm";
            else if (cond == 2 && st.getQuestItemsCount(MINING_BOOTS) + st.getQuestItemsCount(MINERS_PICK) + st.getQuestItemsCount(BOOMBOOM_POWDER) + st.getQuestItemsCount(REDSTONE_BEER) == 4) {
                htmltext = "miner_bolter_q0005_06.htm";
                st.takeItems(MINING_BOOTS, -1);
                st.takeItems(MINERS_PICK, -1);
                st.takeItems(BOOMBOOM_POWDER, -1);
                st.takeItems(REDSTONE_BEER, -1);
                st.takeItems(BOLTERS_LIST, -1);
                st.giveItems(NECKLACE, 1, false);
                st.getPlayer().addExpAndSp(5672, 446);
                if (st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("ng1"))
                    st.getPlayer().sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide."));
                st.giveItems(ADENA_ID, 2466);
                st.unset("cond");
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(false);
            }
        } else if (cond == 1 && st.getQuestItemsCount(BOLTERS_LIST) > 0) {
            if (npcId == SHARI) {
                if (st.getQuestItemsCount(BOOMBOOM_POWDER) == 0) {
                    htmltext = "trader_chali_q0005_01.htm";
                    st.giveItems(BOOMBOOM_POWDER, 1, false);
                    st.playSound(SOUND_ITEMGET);
                } else
                    htmltext = "trader_chali_q0005_02.htm";
            } else if (npcId == GARITA) {
                if (st.getQuestItemsCount(MINING_BOOTS) == 0) {
                    htmltext = "trader_garita_q0005_01.htm";
                    st.giveItems(MINING_BOOTS, 1, false);
                    st.playSound(SOUND_ITEMGET);
                } else
                    htmltext = "trader_garita_q0005_02.htm";
            } else if (npcId == REED) {
                if (st.getQuestItemsCount(REDSTONE_BEER) == 0) {
                    htmltext = "warehouse_chief_reed_q0005_01.htm";
                    st.giveItems(REDSTONE_BEER, 1, false);
                    st.playSound(SOUND_ITEMGET);
                } else
                    htmltext = "warehouse_chief_reed_q0005_02.htm";
            } else if (npcId == BRUNON && st.getQuestItemsCount(BOLTERS_SMELLY_SOCKS) > 0)
                if (st.getQuestItemsCount(MINERS_PICK) == 0)
                    htmltext = "blacksmith_bronp_q0005_01.htm";
                else
                    htmltext = "blacksmith_bronp_q0005_03.htm";
            if (st.getQuestItemsCount(BOLTERS_LIST) > 0 && st.getQuestItemsCount(MINING_BOOTS) + st.getQuestItemsCount(MINERS_PICK) + st.getQuestItemsCount(BOOMBOOM_POWDER) + st.getQuestItemsCount(REDSTONE_BEER) == 4) {
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            }
        }
        return htmltext;
    }
}
