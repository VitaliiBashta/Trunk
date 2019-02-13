package l2trunk.scripts.services;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class VitaminManager extends Functions {
    private static final int PetCoupon = 13273;
    private static final int SpecialPetCoupon = 14065;

    private static final int WeaselNeck = 13017;
    private static final int PrincNeck = 13018;
    private static final int BeastNeck = 13019;
    private static final int FoxNeck = 13020;

    private static final int KnightNeck = 13548;
    private static final int SpiritNeck = 13549;
    private static final int OwlNeck = 13550;
    private static final int TurtleNeck = 13551;

    public void giveWeasel() {
        String htmltext;
        if (player.haveItem(PetCoupon)) {
            removeItem(player, PetCoupon, 1, "VitaminManager");
            addItem(player, WeaselNeck, 1);
            htmltext = npc.getNpcId() + "-ok.htm";
        } else
            htmltext = npc.getNpcId() + "-no.htm";

        npc.showChatWindow(player, "default/" + htmltext);
    }

    public void givePrinc() {
        String htmltext;
        if (player.haveItem( PetCoupon)) {
            removeItem(player, PetCoupon, 1, "givePrinc");
            addItem(player, PrincNeck, 1);
            htmltext = npc.getNpcId() + "-ok.htm";
        } else
            htmltext = npc.getNpcId() + "-no.htm";

        npc.showChatWindow(player, "default/" + htmltext);
    }

    public void giveBeast() {
        String htmltext;
        if (player.haveItem( PetCoupon) ) {
            removeItem(player, PetCoupon, 1, "giveBeast");
            addItem(player, BeastNeck, 1);
            htmltext = npc.getNpcId() + "-ok.htm";
        } else
            htmltext = npc.getNpcId() + "-no.htm";

        npc.showChatWindow(player, "default/" + htmltext);
    }

    public void giveFox() {
        String htmltext;
        if (player.haveItem( PetCoupon) ) {
            removeItem(player, PetCoupon, 1, "giveFox");
            addItem(player, FoxNeck, 1);
            htmltext = npc.getNpcId() + "-ok.htm";
        } else
            htmltext = npc.getNpcId() + "-no.htm";

        npc.showChatWindow(player, "default/" + htmltext);
    }

    public void giveKnight() {
        String htmltext;
        if (player.haveItem(SpecialPetCoupon) ) {
            removeItem(player, SpecialPetCoupon, 1, "giveKnight");
            addItem(player, KnightNeck, 1);
            htmltext = npc.getNpcId() + "-ok.htm";
        } else
            htmltext = npc.getNpcId() + "-no.htm";

        npc.showChatWindow(player, "default/" + htmltext);
    }

    public void giveSpirit() {
        String htmltext;
        if (player.haveItem( SpecialPetCoupon) ) {
            removeItem(player, SpecialPetCoupon, 1, "giveSpirit");
            addItem(player, SpiritNeck, 1);
            htmltext = npc.getNpcId() + "-ok.htm";
        } else
            htmltext = npc.getNpcId() + "-no.htm";

        npc.showChatWindow(player, "default/" + htmltext);
    }

    public void giveOwl() {
        String htmltext;
        if (player.haveItem( SpecialPetCoupon)) {
            removeItem(player, SpecialPetCoupon, 1, "giveOwl");
            addItem(player, OwlNeck, 1);
            htmltext = npc.getNpcId() + "-ok.htm";
        } else
            htmltext = npc.getNpcId() + "-no.htm";

        npc.showChatWindow(player, "default/" + htmltext);
    }

    public void giveTurtle() {
        String htmltext;
        if (player.haveItem(SpecialPetCoupon) ) {
            removeItem(player, SpecialPetCoupon, 1, "giveTurtle");
            addItem(player, TurtleNeck, 1);
            htmltext = npc.getNpcId() + "-ok.htm";
        } else
            htmltext = npc.getNpcId() + "-no.htm";

        npc.showChatWindow(player, "default/" + htmltext);
    }
}