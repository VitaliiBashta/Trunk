package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Drop;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.Map;

public final class _340_SubjugationofLizardmen extends Quest {
    // NPCs
    private static final int WEITSZ = 30385;
    private static final int LEVIAN = 30037;
    private static final int ADONIUS = 30375;
    private static final int CHEST_OF_BIFRONS = 30989;
    // Mobs
    private static final int LANGK_LIZARDMAN = 20008;
    private static final int LANGK_LIZARDMAN_SCOUT = 20010;
    private static final int LANGK_LIZARDMAN_WARRIOR = 20014;
    private static final int LANGK_LIZARDMAN_SHAMAN = 21101;
    private static final int LANGK_LIZARDMAN_LEADER = 20356;
    private static final int LANGK_LIZARDMAN_SENTINEL = 21100;
    private static final int LANGK_LIZARDMAN_LIEUTENANT = 20357;
    private static final int SERPENT_DEMON_BIFRONS = 25146;
    // Quest items (Drop)
    private static final int ROSARY = 4257;
    private static final int HOLY_SYMBOL = 4256;
    private static final int TRADE_CARGO = 4255;
    private static final int EVIL_SPIRIT_OF_DARKNESS = 7190;

    private static final Map<Integer, Drop> DROPLIST = new HashMap<>();

    public _340_SubjugationofLizardmen() {
        addStartNpc(WEITSZ);
        addTalkId(LEVIAN,ADONIUS,CHEST_OF_BIFRONS);

        DROPLIST.put(LANGK_LIZARDMAN, new Drop(1, 30, 30).addItem(TRADE_CARGO));
        DROPLIST.put(LANGK_LIZARDMAN_SCOUT, new Drop(1, 30, 33).addItem(TRADE_CARGO));
        DROPLIST.put(LANGK_LIZARDMAN_WARRIOR, new Drop(1, 30, 36).addItem(TRADE_CARGO));
        DROPLIST.put(LANGK_LIZARDMAN_SHAMAN, new Drop(3, 1, 12).addItem(HOLY_SYMBOL).addItem(ROSARY));
        DROPLIST.put(LANGK_LIZARDMAN_LEADER, new Drop(3, 1, 12).addItem(HOLY_SYMBOL).addItem(ROSARY));
        DROPLIST.put(LANGK_LIZARDMAN_SENTINEL, new Drop(3, 1, 12).addItem(HOLY_SYMBOL).addItem(ROSARY));
        DROPLIST.put(LANGK_LIZARDMAN_LIEUTENANT, new Drop(3, 1, 12).addItem(HOLY_SYMBOL).addItem(ROSARY));

        addKillId(SERPENT_DEMON_BIFRONS);
        addKillId(DROPLIST.keySet());

        addQuestItem(TRADE_CARGO,HOLY_SYMBOL,ROSARY);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        int cond = st.getCond();
        if (event.equalsIgnoreCase("30385-4.htm") && state == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("30385-6.htm") && state == STARTED && cond == 1 && st.getQuestItemsCount(TRADE_CARGO) >= 30) {
            st.setCond(2);
            st.takeItems(TRADE_CARGO, -1);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("30375-2.htm") && state == STARTED && cond == 2) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("30989-2.htm") && state == STARTED && cond == 5) {
            st.setCond(6);
            st.giveItems(EVIL_SPIRIT_OF_DARKNESS, 1);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("30037-4.htm") && state == STARTED && cond == 6 && st.getQuestItemsCount(EVIL_SPIRIT_OF_DARKNESS) > 0) {
            st.setCond(7);
            st.takeItems(EVIL_SPIRIT_OF_DARKNESS, -1);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("30385-10.htm") && state == STARTED && cond == 7) {
            st.giveItems(ADENA_ID, 14700);
            st.playSound(SOUND_FINISH);
            st.finish();
        } else if (event.equalsIgnoreCase("30385-7.htm") && state == STARTED && cond == 1 && st.getQuestItemsCount(TRADE_CARGO) >= 30) {
            st.takeItems(TRADE_CARGO, -1);
            st.giveItems(ADENA_ID, 4090);
            st.playSound(SOUND_FINISH);
            st.finish();
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int _state = st.getState();
        int npcId = npc.getNpcId();
        if (_state == CREATED) {
            if (npcId != WEITSZ)
                return "noquest";
            if (st.player.getLevel() < 17) {
                st.exitCurrentQuest();
                return "30385-1.htm";
            }
            st.setCond(0);
            return "30385-2.htm";
        }

        if (_state != STARTED)
            return "noquest";
        int cond = st.getCond();

        if (npcId == WEITSZ && cond == 1)
            return st.getQuestItemsCount(TRADE_CARGO) < 30 ? "30385-8.htm" : "30385-5.htm";
        if (npcId == WEITSZ && cond == 2)
            return "30385-11.htm";
        if (npcId == WEITSZ && cond == 7)
            return "30385-9.htm";
        if (npcId == ADONIUS && cond == 2)
            return "30375-1.htm";
        if (npcId == ADONIUS && cond == 3) {
            if (!st.haveAnyQuestItems(ROSARY,HOLY_SYMBOL))
                return "30375-4.htm";
            st.takeAllItems(ROSARY, HOLY_SYMBOL);
            st.playSound(SOUND_MIDDLE);
            st.setCond(4);
            return "30375-3.htm";
        }
        if (npcId == ADONIUS && cond == 4)
            return "30375-5.htm";
        if (npcId == LEVIAN && cond == 4) {
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
            return "30037-1.htm";
        }
        if (npcId == LEVIAN && cond == 5)
            return "30037-2.htm";
        if (npcId == LEVIAN && cond == 6 && st.haveQuestItem(EVIL_SPIRIT_OF_DARKNESS))
            return "30037-3.htm";
        if (npcId == LEVIAN && cond == 7)
            return "30037-5.htm";
        if (npcId == CHEST_OF_BIFRONS && cond == 5)
            return "30989-1.htm";

        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();
        if (npcId == SERPENT_DEMON_BIFRONS) {
            qs.addSpawn(CHEST_OF_BIFRONS);
            return;
        }

        Drop drop = DROPLIST.get(npcId);
        if (drop == null)
            return;
        int cond = qs.getCond();

        for (int item_id : drop.itemList) {
            long count = qs.getQuestItemsCount(item_id);
            if (cond == drop.condition && count < drop.maxcount && Rnd.chance(drop.chance)) {
                qs.giveItems(item_id);
                if (count + 1 == drop.maxcount)
                    qs.playSound(SOUND_MIDDLE);
                else
                    qs.playSound(SOUND_ITEMGET);
            }
        }
    }
}