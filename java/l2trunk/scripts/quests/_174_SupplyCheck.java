package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _174_SupplyCheck extends Quest {
    private final int Marcela = 32173;
    private final int Benis = 32170; // warehouse keeper
    private final int Nika = 32167; // grocerer

    private final int WarehouseManifest = 9792;
    private final int GroceryStoreManifest = 9793;

    private static final int WoodenBreastplate = 23;
    private static final int WoodenGaiters = 2386;
    private static final int LeatherTunic = 429;
    private static final int LeatherStockings = 464;
    private static final int WoodenHelmet = 43;
    private static final int LeatherShoes = 37;
    private static final int Gloves = 49;

    public _174_SupplyCheck() {
        addStartNpc(Marcela);
        addTalkId(Benis, Nika); //Erinu, Casca
        addQuestItem(WarehouseManifest, GroceryStoreManifest); // WeaponShopManifest, SupplyReport
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("zerstorer_morsell_q0174_04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == Marcela) {
            if (cond == 0) {
                if (st.player.getLevel() == 1) {
                    st.exitCurrentQuest();
                    htmltext = "zerstorer_morsell_q0174_02.htm";
                } else
                    htmltext = "zerstorer_morsell_q0174_01.htm";
            } else if (cond == 1)
                htmltext = "zerstorer_morsell_q0174_05.htm";
            else if (cond == 2) {
                st.setCond(3);
                st.takeItems(WarehouseManifest);
                htmltext = "zerstorer_morsell_q0174_06.htm";
            } else if (cond == 3)
                htmltext = "zerstorer_morsell_q0174_07.htm";
            else if (cond == 4) {
                if (st.player.getClassId().isMage() && !st.player.getClassId().equalsOrChildOf(ClassId.orcMage)) {
                    st.giveItems(LeatherTunic);
                    st.giveItems(LeatherStockings);
                } else {
                    st.giveItems(WoodenBreastplate);
                    st.giveItems(WoodenGaiters);
                }
                st.giveItems(WoodenHelmet);
                st.giveItems(LeatherShoes);
                st.giveItems(Gloves);
                st.giveAdena( 2466);
                st.player.addExpAndSp(5672, 446);
                if (st.player.getClassId().occupation() == 0)
                    st.player.sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide."));
                st.finish();
                htmltext = "zerstorer_morsell_q0174_12.htm";
            }
        } else if (npcId == Benis)
            if (cond == 1) {
                st.setCond(2);
                st.giveItems(WarehouseManifest);
                htmltext = "warehouse_keeper_benis_q0174_01.htm";
            } else
                htmltext = "warehouse_keeper_benis_q0174_02.htm";

        else if (npcId == Nika)
            if (cond < 3)
                htmltext = "subelder_casca_q0174_01.htm";
            else if (cond == 3) {
                st.setCond(4);
                st.giveItems(GroceryStoreManifest, 1);
                htmltext = "trader_neagel_q0174_02.htm";
            } else
                htmltext = "trader_neagel_q0174_03.htm";
        return htmltext;
    }
}