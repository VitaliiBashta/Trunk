package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.TimeUtils;

public final class _504_CompetitionForTheBanditStronghold extends Quest {
    // NPC
    private static final int MESSENGER = 35437;
    // MOBS
    private static final int TARLK_BUGBEAR = 20570;
    private static final int TARLK_BUGBEAR_WARRIOR = 20571;
    private static final int TARLK_BUGBEAR_HIGH_WARRIOR = 20572;
    private static final int TARLK_BASILISK = 20573;
    private static final int ELDER_TARLK_BASILISK = 20574;

    // ITEMS
    private static final int AMULET = 4332;
    private static final int ALIANCE_TROPHEY = 5009;
    private static final int CONTEST_CERTIFICATE = 4333;

    public _504_CompetitionForTheBanditStronghold() {
        super(PARTY_ALL);

        addStartNpc(MESSENGER);
        addTalkId(MESSENGER);
        addKillId(TARLK_BUGBEAR,TARLK_BUGBEAR_WARRIOR,TARLK_BUGBEAR_HIGH_WARRIOR,TARLK_BASILISK,ELDER_TARLK_BASILISK);
        addQuestItem(CONTEST_CERTIFICATE, AMULET, ALIANCE_TROPHEY);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("azit_messenger_q0504_02.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.giveItems(CONTEST_CERTIFICATE);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        Player player = st.player;
        Clan clan = player.getClan();
        ClanHall clanhall = ResidenceHolder.getClanHall(35);

        if (clanhall.getSiegeEvent().isRegistrationOver()) {
            htmltext = null;
            showHtmlFile(player, "azit_messenger_q0504_03.htm", TimeUtils.toSimpleFormat(clanhall.getSiegeDate()));
        } else if (clan == null || player.objectId() != clan.getLeaderId())
            htmltext = "azit_messenger_q0504_05.htm";
        else if (player.objectId() == clan.getLeaderId() && clan.getLevel() < 4)
            htmltext = "azit_messenger_q0504_04.htm";
        else if (clanhall.getSiegeEvent().getSiegeClan(SiegeEvent.ATTACKERS, player.getClan()) != null)
            htmltext = "azit_messenger_q0504_06.htm";
        else if (clan.getHasHideout() > 0)
            htmltext = "azit_messenger_q0504_10.htm";
        else {
            if (cond == 0)
                htmltext = "azit_messenger_q0504_01.htm";
            else if (st.getQuestItemsCount(CONTEST_CERTIFICATE) == 1 && st.getQuestItemsCount(AMULET) < 30)
                htmltext = "azit_messenger_q0504_07.htm";
            else if (st.getQuestItemsCount(ALIANCE_TROPHEY) >= 1)
                htmltext = "azit_messenger_q0504_07a.htm";
            else if (st.getQuestItemsCount(CONTEST_CERTIFICATE) == 1 && st.getQuestItemsCount(AMULET) == 30) {
                st.takeItems(AMULET);
                st.takeItems(CONTEST_CERTIFICATE);
                st.giveItems(ALIANCE_TROPHEY);
                st.playSound(SOUND_FINISH);
                st.setCond(-1);
                htmltext = "azit_messenger_q0504_08.htm";
            }
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getQuestItemsCount(AMULET) < 30) {
            st.giveItems(AMULET);
            st.playSound(SOUND_ITEMGET);
        }
    }
}
