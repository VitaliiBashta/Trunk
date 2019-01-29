package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.TimeUtils;

public final class _655_AGrandPlanForTamingWildBeasts extends Quest {
    private static final int MESSENGER = 35627;

    private final static int STONE = 8084;
    private final static int TRAINER_LICENSE = 8293;

    public _655_AGrandPlanForTamingWildBeasts() {
        super(false);

        addStartNpc(MESSENGER);
        addTalkId(MESSENGER);

        addQuestItem(STONE, TRAINER_LICENSE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("farm_messenger_q0655_06.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmlText = "noquest";
        int cond = st.getCond();
        Player player = st.getPlayer();
        Clan clan = player.getClan();
        ClanHall clanhall = ResidenceHolder.getResidence(63);

        if (clanhall.getSiegeEvent().isRegistrationOver()) {
            htmlText = null;
            showHtmlFile(player, "farm_messenger_q0655_02.htm", false, "%siege_time%", TimeUtils.toSimpleFormat(clanhall.getSiegeDate()));
        } else if (clan == null || player.getObjectId() != clan.getLeaderId())
            htmlText = "farm_messenger_q0655_03.htm";
        else if (player.getObjectId() == clan.getLeaderId() && clan.getLevel() < 4)
            htmlText = "farm_messenger_q0655_05.htm";
        else if (clanhall.getSiegeEvent().getSiegeClan(SiegeEvent.ATTACKERS, player.getClan()) != null)
            htmlText = "farm_messenger_q0655_07.htm";
        else if (clan.getHasHideout() > 0)
            htmlText = "farm_messenger_q0655_04.htm";
        else if (cond == 0)
            htmlText = "farm_messenger_q0655_01.htm";
        else if (cond == 1 && st.getQuestItemsCount(STONE) < 10)
            htmlText = "farm_messenger_q0655_08.htm";
        else if (cond == 1 && st.getQuestItemsCount(STONE) == 10) {
            st.setCond(-1);
            st.takeItems(STONE, -1);
            st.giveItems(TRAINER_LICENSE, 1);
            htmlText = "farm_messenger_q0655_10.htm";
        } else if (st.getQuestItemsCount(TRAINER_LICENSE) == 1)
            htmlText = "farm_messenger_q0655_09.htm";

        return htmlText;
    }
}