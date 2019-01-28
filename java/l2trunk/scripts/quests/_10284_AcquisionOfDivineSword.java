package l2trunk.scripts.quests;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import static l2trunk.scripts.quests._10283_RequestOfIceMerchant.JINIA;
import static l2trunk.scripts.quests._10283_RequestOfIceMerchant.RAFFORTY;

public final class _10284_AcquisionOfDivineSword extends Quest {
    private static final int KRUN = 32653;
    private static final int COLD_RESISTANCE_POTION = 15514;
    private static final int INJ_KEGOR = 18846;
    private static final int MITHRIL_MILLIPEDE = 22766;
    private int _count = 0;

    public _10284_AcquisionOfDivineSword() {
        super(false);
        addStartNpc(RAFFORTY);
        addTalkId(JINIA, KRUN, INJ_KEGOR);
        addKillId(MITHRIL_MILLIPEDE);
        addQuestItem(COLD_RESISTANCE_POTION);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("rafforty_q10284_02.htm".equalsIgnoreCase(event)) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("enterinstance".equalsIgnoreCase(event)) {
            st.setCond(2);
            enterInstance(st.getPlayer(), 140);
            return null;
        } else if ("jinia_q10284_03.htm".equalsIgnoreCase(event)) {
            if (!st.getPlayer().getReflection().isDefault()) {
                st.getPlayer().getReflection().startCollapseTimer(60 * 1000L);
                st.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(1));
            }
            st.setCond(3);
        } else if ("leaveinstance".equalsIgnoreCase(event)) {
            st.getPlayer().getReflection().collapse();
            return null;
        } else if ("entermines".equalsIgnoreCase(event)) {
            st.setCond(4);
            if (st.getQuestItemsCount(COLD_RESISTANCE_POTION) < 1)
                st.giveItems(COLD_RESISTANCE_POTION, 1);
            enterInstance(st.getPlayer(), 138);
            return null;
        } else if ("leavemines".equalsIgnoreCase(event)) {
            st.giveItems(ADENA_ID, 296425);
            st.addExpAndSp(921805, 82230);
            st.playSound(SOUND_FINISH);
            st.setState(COMPLETED);
            st.exitCurrentQuest(false);
            st.getPlayer().getReflection().collapse();
            return null;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == RAFFORTY) {
            if (cond == 0) {
                QuestState qs = st.getPlayer().getQuestState(_10283_RequestOfIceMerchant.class);
                if (st.getPlayer().getLevel() >= 82 && qs != null && qs.isCompleted())
                    htmltext = "rafforty_q10284_01.htm";
                else {
                    htmltext = "rafforty_q10284_00.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1 || cond == 2)
                htmltext = "rafforty_q10284_02.htm";
        } else if (npcId == JINIA) {
            if (cond == 2)
                htmltext = "jinia_q10284_01.htm";
            else if (cond == 3)
                htmltext = "jinia_q10284_02.htm";
        } else if (npcId == KRUN) {
            if (cond == 3 || cond == 4 || cond == 5)
                htmltext = "krun_q10284_01.htm";
        } else if (npcId == INJ_KEGOR) {
            if (cond == 4) {
                st.takeAllItems(COLD_RESISTANCE_POTION);
                st.setCond(5);
                htmltext = "kegor_q10284_01.htm";
                for (int i = 0; i < 4; i++) {
                    NpcInstance mob = st.getPlayer().getReflection().addSpawnWithoutRespawn(MITHRIL_MILLIPEDE, Location.findPointToStay(st.getPlayer(), 50, 100), st.getPlayer().getGeoIndex());
                    mob.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, st.getPlayer(), 300);
                }
            } else if (cond == 5)
                htmltext = "kegor_q10284_02.htm";
            else if (cond == 6)
                htmltext = "kegor_q10284_03.htm";
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 5 && npcId == MITHRIL_MILLIPEDE) {
            if (_count < 3)
                _count++;
            else {
                st.setCond(6);
                st.getPlayer().getReflection().startCollapseTimer(3 * 60 * 1000L);
                st.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(3));
            }
        }
        return null;
    }

    private void enterInstance(Player player, int izId) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(izId))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(izId)) {
            ReflectionUtils.enterReflection(player, izId);
        }
    }
}