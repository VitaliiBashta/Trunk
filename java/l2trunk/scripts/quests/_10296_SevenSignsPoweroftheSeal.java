package l2trunk.scripts.quests;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class _10296_SevenSignsPoweroftheSeal extends Quest {
    private static final int Eris = 32792;
    private static final int ElcardiaInzone1 = 32787;
    private static final int EtisEtina = 18949;
    private static final int ElcardiaHome = 32784;
    private static final int Hardin = 30832;
    private static final int Wood = 32593;
    private static final int Franz = 32597;

    private static final Location hiddenLoc = Location.of(120744, -87432, -3392);

    public _10296_SevenSignsPoweroftheSeal() {
        super(false);
        addStartNpc(Eris);
        addTalkId(ElcardiaInzone1, ElcardiaHome, Hardin, Wood, Franz);
        addKillId(EtisEtina);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        String htmltext = event;
        if ("eris_q10296_3.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("start_scene".equalsIgnoreCase(event)) {
            st.setCond(2);
            teleportElcardia(player, hiddenLoc);
            ThreadPoolManager.INSTANCE.schedule(() -> {
                player.teleToLocation(Location.of(76736, -241021, -10832));
                teleportElcardia(player);
            }, 60500L);
            player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_BOSS_OPENING);
            return null;
        } else if ("teleport_back".equalsIgnoreCase(event)) {
            player.teleToLocation(Location.of(76736, -241021, -10832));
            teleportElcardia(player);
            return null;
        } else if ("elcardiahome_q10296_3.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
        } else if ("hardin_q10296_3.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
        } else if ("enter_instance".equalsIgnoreCase(event)) {
            enterInstance(player);
            return null;
        } else if ("franz_q10296_3.htm".equalsIgnoreCase(event)) {
            if (player.getLevel() >= 81) {
                st.addExpAndSp(225000000, 22500000);
                st.giveItems(17265, 1);
                st.complete();
                st.playSound(SOUND_FINISH);
                st.finish();
            } else
                htmltext = "franz_q10296_0.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.player;
        if (player.getBaseClassId() != player.getActiveClassId())
            return "no_subclass_allowed.htm";

        if (npcId == Eris) {
            if (cond == 0) {
                if (player.getLevel() >= 81 &&player.isQuestCompleted(_10295_SevenSignsSolinasTomb.class))
                    htmltext = "eris_q10296_1.htm";
                else {
                    htmltext = "eris_q10296_0.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "eris_q10296_4.htm";
            else if (cond == 2)
                htmltext = "eris_q10296_5.htm";
            else if (cond >= 3)
                htmltext = "eris_q10296_6.htm";
        } else if (npcId == ElcardiaInzone1) {
            if (cond == 1)
                htmltext = "elcardia_q10296_1.htm";
            else if (cond == 2) {
                if (!st.isSet("EtisKilled"))
                    htmltext = "elcardia_q10296_1.htm";
                else {
                    st.setCond(3);
                    htmltext = "elcardia_q10296_2.htm";
                }
            } else if (cond >= 3)
                htmltext = "elcardia_q10296_4.htm";
        } else if (npcId == ElcardiaHome) {
            if (cond == 3)
                htmltext = "elcardiahome_q10296_1.htm";
            else if (cond >= 4)
                htmltext = "elcardiahome_q10296_3.htm";
        } else if (npcId == Hardin) {
            if (cond == 4)
                htmltext = "hardin_q10296_1.htm";
            else if (cond == 5)
                htmltext = "hardin_q10296_4.htm";
        } else if (npcId == Wood) {
            if (cond == 5)
                htmltext = "wood_q10296_1.htm";
        } else if (npcId == Franz) {
            if (cond == 5)
                htmltext = "franz_q10296_1.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == EtisEtina) {
            st.set("EtisKilled");
            st.player.getReflection().getNpcs()
                    .filter(n -> n.getNpcId() == ElcardiaInzone1)
                    .forEach(n -> n.teleToLocation(Location.of(120664, -86968, -3392)));
            ThreadPoolManager.INSTANCE.schedule(() -> teleportElcardia(st.player), 60500L);
            st.player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_BOSS_CLOSING);

        }
    }

    private void teleportElcardia(Player player) {
        player.getReflection().getNpcs()
                .filter(n -> n.getNpcId() == ElcardiaInzone1)
                .forEach(n -> {
                    n.teleToLocation(Location.findPointToStay(player, 60));
                    if (n.isBlocked())
                        n.setBlock();
                });
    }

    private void teleportElcardia(Player player, Location loc) {
        player.getReflection().getNpcs()
                .filter(n -> n.getNpcId() == ElcardiaInzone1)
                .forEach(n -> {
                    n.teleToLocation(loc);
                    n.setBlock(true);
                });
    }

    private void enterInstance(Player player) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(146))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(146)) {
            ReflectionUtils.enterReflection(player, 146);
        }
    }
}