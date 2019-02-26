package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.RadarControl;
import l2trunk.gameserver.utils.Location;

public final class _1102_Nottingale extends Quest {
    private final static int Nottingale = 32627;
    public _1102_Nottingale() {
        super(false);
        addFirstTalkId(Nottingale);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.player;
        if (!player.isQuestCompleted(_10273_GoodDayToFly.class)) {
            player.sendPacket(new RadarControl(2, 2));
            player.sendPacket(new RadarControl(0, 2, Location.of(-184545, 243120, 1581)));
            htmltext = "32627.htm";
        } else if ("32627-3.htm".equalsIgnoreCase(event)) {
            player.sendPacket(new RadarControl(2, 2));
            player.sendPacket(new RadarControl(0, 2, Location.of(-192361, 254528, 3598)));
        } else if ("32627-4.htm".equalsIgnoreCase(event)) {
            player.sendPacket(new RadarControl(2, 2));
            player.sendPacket(new RadarControl(0, 2, Location.of(-174600, 219711, 4424)));
        } else if ("32627-5.htm".equalsIgnoreCase(event)) {
            player.sendPacket(new RadarControl(2, 2));
            player.sendPacket(new RadarControl(0, 2, Location.of(-181989, 208968, 4424)));
        } else if ("32627-6.htm".equalsIgnoreCase(event)) {
            player.sendPacket(new RadarControl(2, 2));
            player.sendPacket(new RadarControl(0, 2, Location.of(-252898, 235845, 5343)));
        } else if ("32627-8.htm".equalsIgnoreCase(event)) {
            player.sendPacket(new RadarControl(2, 2));
            player.sendPacket(new RadarControl(0, 2, Location.of(-212819, 209813, 4288)));
        } else if ("32627-9.htm".equalsIgnoreCase(event)) {
            player.sendPacket(new RadarControl(2, 2));
            player.sendPacket(new RadarControl(0, 2, Location.of(-246899, 251918, 4352)));
        }
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        QuestState qs = player.getQuestState(this);
        if (qs == null)
            newQuestState(player, STARTED);
        return "";
    }
}