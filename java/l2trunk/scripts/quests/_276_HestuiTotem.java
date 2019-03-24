package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _276_HestuiTotem extends Quest {
    //NPCs
    private static final int Tanapi = 30571;
    //Mobs
    private static final int Kasha_Bear = 20479;
    private static final int Kasha_Bear_Totem_Spirit = 27044;
    //items
    private static final int Leather_Pants = 29;
    private static final int Totem_of_Hestui = 1500;
    //Quest items
    private static final int Kasha_Parasite = 1480;
    private static final int Kasha_Crystal = 1481;

    public _276_HestuiTotem() {
        addStartNpc(Tanapi);
        addKillId(Kasha_Bear,Kasha_Bear_Totem_Spirit);
        addQuestItem(Kasha_Parasite,Kasha_Crystal);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("seer_tanapi_q0276_03.htm".equalsIgnoreCase(event) && st.getState() == CREATED && st.player.getRace() == Race.orc && st.player.getLevel() >= 15) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != Tanapi)
            return htmltext;
        int state = st.getState();

        if (state == CREATED) {
            if (st.player.getRace() != Race.orc) {
                htmltext = "seer_tanapi_q0276_00.htm";
                st.exitCurrentQuest();
            } else if (st.player.getLevel() < 15) {
                htmltext = "seer_tanapi_q0276_01.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "seer_tanapi_q0276_02.htm";
                st.setCond(0);
            }
        } else if (state == STARTED)
            if (st.haveQuestItem(Kasha_Crystal)) {
                htmltext = "seer_tanapi_q0276_05.htm";
                st.takeAllItems(Kasha_Parasite,Kasha_Crystal);

                st.giveItems(Leather_Pants);
                st.giveItems(Totem_of_Hestui);
                if (st.getRateQuestsReward() > 1)
                    st.giveAdena( Math.round(ItemHolder.getTemplate(Totem_of_Hestui).referencePrice * (st.getRateQuestsReward() - 1) / 2));

                if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q4")) {
                    st.player.setVar("p1q4");
                    st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
                }

                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            } else
                htmltext = "seer_tanapi_q0276_04.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();

        if (npcId == Kasha_Bear && qs.getQuestItemsCount(Kasha_Crystal) == 0) {
            if (qs.getQuestItemsCount(Kasha_Parasite) < 50) {
                qs.giveItems(Kasha_Parasite);
                qs.playSound(SOUND_ITEMGET);
            } else {
                qs.takeItems(Kasha_Parasite);
                qs.addSpawn(Kasha_Bear_Totem_Spirit);
            }
        } else if (npcId == Kasha_Bear_Totem_Spirit && qs.getQuestItemsCount(Kasha_Crystal) == 0) {
            qs.giveItems(Kasha_Crystal);
            qs.playSound(SOUND_MIDDLE);
        }
    }
}