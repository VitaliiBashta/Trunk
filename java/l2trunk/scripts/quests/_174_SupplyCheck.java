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
    //int Erinu = 32164; // weapon seller
    //int Casca = 32139; // vice hierarch

    private final int WarehouseManifest = 9792;
    private final int GroceryStoreManifest = 9793;
    //int WeaponShopManifest = 9794;
    //int SupplyReport = 9795;

    private static final int WoodenBreastplate = 23;
    private static final int WoodenGaiters = 2386;
    private static final int LeatherTunic = 429;
    private static final int LeatherStockings = 464;
    private static final int WoodenHelmet = 43;
    private static final int LeatherShoes = 37;
    private static final int Gloves = 49;

    public _174_SupplyCheck() {
        super(false);

        addStartNpc(Marcela);
        addTalkId(Benis, Nika); //Erinu, Casca
        addQuestItem(WarehouseManifest, GroceryStoreManifest); // WeaponShopManifest, SupplyReport
    }

    @Override
    public String onEvent(String event, QuestState qs, NpcInstance npc) {
        if (event.equalsIgnoreCase("zerstorer_morsell_q0174_04.htm")) {
            qs.setCond(1);
            qs.setState(STARTED);
            qs.playSound(SOUND_ACCEPT);
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
                    st.exitCurrentQuest(true);
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
                if (st.player.getClassId().isMage && !st.player.getClassId().equalsOrChildOf(ClassId.orcMage)) {
                    st.giveItems(LeatherTunic);
                    st.giveItems(LeatherStockings);
                } else {
                    st.giveItems(WoodenBreastplate);
                    st.giveItems(WoodenGaiters);
                }
                st.giveItems(WoodenHelmet);
                st.giveItems(LeatherShoes);
                st.giveItems(Gloves);
                st.giveItems(ADENA_ID, 2466, true);
                st.player.addExpAndSp(5672, 446);
                if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("ng1"))
                    st.player.sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide."));
                st.exitCurrentQuest(false);
                htmltext = "zerstorer_morsell_q0174_12.htm";
            }
			/*
			{
				st.setCond(5);
				st.takeItems(GroceryStoreManifest, -1);
				htmltext = "zerstorer_morsell_q0174_08.htm";
			}
			else if (cond == 5)
				htmltext = "zerstorer_morsell_q0174_09.htm";

			else if (cond == 6)
			{
				st.setCond(7);
				st.takeItems(WeaponShopManifest, -1);
				st.giveItems(SupplyReport, 1);
				htmltext = "zerstorer_morsell_q0174_10.htm";
			}
			else if (cond == 7)
				htmltext = "zerstorer_morsell_q0174_11.htm";

			else if (cond == 8)
			{
				if (st.player().getClassId().isMage() && !st.player().getClassId().equalsOrChildOf(ClassId.orcMage))
				{
					st.giveItems(LeatherTunic, 1);
					st.giveItems(LeatherStockings, 1);
				}
				else
				{
					st.giveItems(WoodenBreastplate, 1);
					st.giveItems(WoodenGaiters, 1);
				}
				st.giveItems(WoodenHelmet, 1);
				st.giveItems(LeatherShoes, 1);
				st.giveItems(Gloves, 1);
				st.giveItems(ADENA_ID, 2466, true);
				st.player().addExpAndSp(5672, 446, false, false);
				if (st.player().getClassId().occupation() == 1 && !st.player().isVarSet("ng1"))
					st.player().sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
				st.exitCurrentQuest(false);
				htmltext = "zerstorer_morsell_q0174_12.htm";
			}
			 */
        } else if (npcId == Benis)
            if (cond == 1) {
                st.setCond(2);
                st.giveItems(WarehouseManifest, 1);
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
		/*
		else if (npcId == Erinu)
			if (cond < 5)
				htmltext = "subelder_casca_q0174_01.htm";
			else if (cond == 5)
			{
				st.setCond(6);
				st.giveItems(WeaponShopManifest, 1);
				htmltext = "trader_erinu_q0174_02.htm";
			}
			else
				htmltext = "subelder_casca_q0174_03.htm";

		else if (npcId == Casca)
			if (cond < 7)
				htmltext = "subelder_casca_q0174_01.htm";
			else if (cond == 7)
			{
				st.setCond(8);
				st.takeItems(SupplyReport, -1);
				htmltext = "subelder_casca_q0174_02.htm";
			}
			else
				htmltext = "subelder_casca_q0174_03.htm";
		 */
        return htmltext;
    }
}