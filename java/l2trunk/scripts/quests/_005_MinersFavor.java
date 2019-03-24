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
        addStartNpc(BOLTER);
        addTalkId(SHARI, GARITA, REED, BRUNON);

        addQuestItem(BOLTERS_LIST, BOLTERS_SMELLY_SOCKS, MINING_BOOTS, MINERS_PICK, BOOMBOOM_POWDER, REDSTONE_BEER);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("miner_bolter_q0005_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.giveItems(BOLTERS_LIST);
            st.giveItems(BOLTERS_SMELLY_SOCKS);
        } else if ("blacksmith_bronp_q0005_02.htm".equalsIgnoreCase(event)) {
            st.takeItems(BOLTERS_SMELLY_SOCKS);
            st.giveItems(MINERS_PICK);
            if (st.haveAllQuestItems(BOLTERS_LIST, MINING_BOOTS, MINERS_PICK, BOOMBOOM_POWDER, REDSTONE_BEER)) {
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
                if (st.player.getLevel() >= 2)
                    htmltext = "miner_bolter_q0005_02.htm";
                else {
                    htmltext = "miner_bolter_q0005_01.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "miner_bolter_q0005_04.htm";
            else if (cond == 2 && st.haveAllQuestItems(MINING_BOOTS, MINERS_PICK, BOOMBOOM_POWDER, REDSTONE_BEER)) {
                htmltext = "miner_bolter_q0005_06.htm";
                st.takeAllItems(MINING_BOOTS, MINERS_PICK, BOOMBOOM_POWDER, REDSTONE_BEER, BOLTERS_LIST);
                st.giveItems(NECKLACE);
                st.player.addExpAndSp(5672, 446);
                if (st.player.getClassId().occupation() == 0)
                    st.player.sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide."));
                st.giveAdena(2466);
                st.unset("cond");
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        } else if (cond == 1 && st.haveQuestItem(BOLTERS_LIST)) {
            if (npcId == SHARI) {
                if (!st.haveQuestItem(BOOMBOOM_POWDER)) {
                    htmltext = "trader_chali_q0005_01.htm";
                    st.giveItems(BOOMBOOM_POWDER);
                    st.playSound(SOUND_ITEMGET);
                } else
                    htmltext = "trader_chali_q0005_02.htm";
            } else if (npcId == GARITA) {
                if (!st.haveQuestItem(MINING_BOOTS)) {
                    htmltext = "trader_garita_q0005_01.htm";
                    st.giveItems(MINING_BOOTS);
                    st.playSound(SOUND_ITEMGET);
                } else
                    htmltext = "trader_garita_q0005_02.htm";
            } else if (npcId == REED) {
                if (st.getQuestItemsCount(REDSTONE_BEER) == 0) {
                    htmltext = "warehouse_chief_reed_q0005_01.htm";
                    st.giveItems(REDSTONE_BEER);
                    st.playSound(SOUND_ITEMGET);
                } else
                    htmltext = "warehouse_chief_reed_q0005_02.htm";
            } else if (npcId == BRUNON && st.haveQuestItem(BOLTERS_SMELLY_SOCKS))
                if (st.getQuestItemsCount(MINERS_PICK) == 0)
                    htmltext = "blacksmith_bronp_q0005_01.htm";
                else
                    htmltext = "blacksmith_bronp_q0005_03.htm";
            if (st.haveAllQuestItems(BOLTERS_LIST, MINING_BOOTS, MINERS_PICK, BOOMBOOM_POWDER, REDSTONE_BEER)) {
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            }
        }
        return htmltext;
    }
}
