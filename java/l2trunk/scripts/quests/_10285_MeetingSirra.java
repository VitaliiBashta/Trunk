package l2trunk.scripts.quests;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import static l2trunk.scripts.quests._10283_RequestOfIceMerchant.JINIA;
import static l2trunk.scripts.quests._10283_RequestOfIceMerchant.RAFFORTY;

public final class _10285_MeetingSirra extends Quest {
     static final int JINIA_2 = 32781;
     static final int KEGOR = 32761;
     static final int SIRRA = 32762;

    public _10285_MeetingSirra() {
        super(false);
        addStartNpc(RAFFORTY);
        addTalkId(JINIA, JINIA_2, KEGOR, SIRRA);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("rafforty_q10285_03.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("enterinstance".equalsIgnoreCase(event)) {
            if (st.getCond() == 1)
                st.setCond(2);
            enterInstance(st.player, 141);
            return null;
        } else if ("jinia_q10285_02.htm".equalsIgnoreCase(event))
            st.setCond(3);
        else if ("kegor_q10285_02.htm".equalsIgnoreCase(event))
            st.setCond(4);
        else if ("sirraspawn".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.player.getReflection().addSpawnWithoutRespawn(SIRRA, new Location(-23848, -8744, -5413, 49152));
            st.player.getAroundNpc(1000, 100)
                    .filter(sirra -> sirra.getNpcId() == SIRRA)
                    .forEach(sirra ->
                            Functions.npcSay(sirra, "You listen to it that you know about everything. But I can no longer listen to your philosophising!"));
            return null;
        } else if ("sirra_q10285_07.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.player.getAroundNpc(1000, 100)
                    .filter(n -> n.getNpcId() == 32762)
                    .forEach(GameObject::deleteMe);
        } else if ("jinia_q10285_10.htm".equalsIgnoreCase(event)) {
            if (!st.player.getReflection().isDefault()) {
                st.player.getReflection().startCollapseTimer(60 * 1000L);
                st.player.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(1));
            }
            st.setCond(7);
        } else if ("exitinstance".equalsIgnoreCase(event)) {
            st.player.getReflection().collapse();
            return null;
        } else if ("enterfreya".equalsIgnoreCase(event)) {
            st.setCond(9);
            enterInstance(st.player, 137);
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
                if (st.player.getLevel() >= 82 && st.player.isQuestCompleted(_10284_AcquisionOfDivineSword.class))
                    htmltext = "rafforty_q10285_01.htm";
                else {
                    htmltext = "rafforty_q10285_00.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond >= 1 && cond < 7)
                htmltext = "rafforty_q10285_03.htm";
            else if (cond == 10) {
                htmltext = "rafforty_q10285_04.htm";
                st.giveItems(ADENA_ID, 283425);
                st.addExpAndSp(939075, 83855);
                st.setState(COMPLETED);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(false);
            }
        } else if (npcId == JINIA) {
            if (cond == 2)
                htmltext = "jinia_q10285_01.htm";
            else if (cond == 4)
                htmltext = "jinia_q10285_03.htm";
            else if (cond == 6)
                htmltext = "jinia_q10285_05.htm";
            else if (cond == 7)
                htmltext = "jinia_q10285_10.htm";
        } else if (npcId == KEGOR) {
            if (cond == 3)
                htmltext = "kegor_q10285_01.htm";
        } else if (npcId == SIRRA) {
            if (cond == 5)
                htmltext = "sirra_q10285_01.htm";
        } else if (npcId == JINIA_2) {
            if (cond == 7 || cond == 8) {
                st.setCond(8);
                htmltext = "jinia2_q10285_01.htm";
            } else if (cond == 9)
                htmltext = "jinia2_q10285_02.htm";
        }
        return htmltext;
    }

    private void enterInstance(Player player, int izId) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(izId))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(izId)) {
            Reflection newInstance = ReflectionUtils.enterReflection(player, izId);
            if (izId == 137)
                ThreadPoolManager.INSTANCE.schedule(new FreyaSpawn(newInstance, player), 2 * 60 * 1000L);
        }
    }

    private class FreyaSpawn extends RunnableImpl {
        private final Player _player;
        private final Reflection _r;

        FreyaSpawn(Reflection r, Player player) {
            _r = r;
            _player = player;
        }

        @Override
        public void runImpl() {
            if (_r != null) {
                NpcInstance freya = _r.addSpawnWithoutRespawn(18847, new Location(114720, -117085, -11088, 15956));
                ThreadPoolManager.INSTANCE.schedule(new FreyaMovie(_player, _r, freya), 2 * 60 * 1000L);
            }
        }
    }

    private class FreyaMovie extends RunnableImpl {
        final Player _player;
        final Reflection _r;
        final NpcInstance _npc;

        FreyaMovie(Player player, Reflection r, NpcInstance npc) {
            _player = player;
            _r = r;
            _npc = npc;
        }

        @Override
        public void runImpl() {
            _r.getSpawns().forEach(Spawner::deleteAll);

            if (_npc != null && !_npc.isDead())
                _npc.deleteMe();
            _player.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_FREYA_FORCED_DEFEAT);
            ThreadPoolManager.INSTANCE.schedule(() -> {
                _player.getQuestState(_10285_MeetingSirra.class).setCond(10);
                _r.collapse();
            }, 23000L);
        }
    }

}