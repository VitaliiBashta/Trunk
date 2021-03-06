package l2trunk.scripts.quests;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class _196_SevenSignsSealoftheEmperor extends Quest {
    // NPCs
    private static final int IasonHeine = 30969;
    private static final int MerchantofMammon = 32584;
    private static final int PromiseofMammon = 32585;
    private static final int Shunaiman = 32586;
    private static final int Leon = 32587;
    private static final int DisciplesGatekeeper = 32657;
    private static final int CourtMagician = 32598;
    //private static int EmperorsSealDevice = 27384;
    private static final int Wood = 32593;

    private NpcInstance MerchantofMammonSpawn;

    // ITEMS
    private static final int ElmoredenHolyWater = 13808;
    private static final int CourtMagiciansMagicStaff = 13809;
    private static final int SealOfBinding = 13846;
    private static final int SacredSwordofEinhasad = 15310;

    //Doors
    private static final int door11 = 17240111;

    private static final int izId = 112;

    public _196_SevenSignsSealoftheEmperor() {
        addStartNpc(IasonHeine);
        addTalkId( MerchantofMammon, PromiseofMammon, Shunaiman, Leon, DisciplesGatekeeper, CourtMagician, Wood);
        addQuestItem(ElmoredenHolyWater, CourtMagiciansMagicStaff, SealOfBinding, SacredSwordofEinhasad);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        String htmltext = event;
        Reflection ref = player.getReflection();

        if ("iasonheine_q196_1d.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("iasonheine_q196_2.htm".equalsIgnoreCase(event)) {
            if (GameObjectsStorage.getAllByNpcId(MerchantofMammon, false).count() ==0) {
                MerchantofMammonSpawn = st.addSpawn(MerchantofMammon, Location.of(109763, 219944, -3512, 16384), 0, 120 * 1000);
                Functions.npcSay(MerchantofMammonSpawn, "Who dares summon the Merchant of Mammon?!");
            }
        } else if ("merchantofmammon_q196_2.htm".equalsIgnoreCase(event)) {
            if (MerchantofMammonSpawn != null) {
                MerchantofMammonSpawn.deleteMe();
                MerchantofMammonSpawn = null;
            }
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("teleport_instance".equalsIgnoreCase(event)) {
            if ((st.getCond() == 3 || st.getCond() == 4))
                enterInstance(player);
            else
                player.sendMessage("You can only access the Necropolis of Dawn while carrying Seal of the Emperor quest.");
            return null;
        } else if ("collapse_instance".equalsIgnoreCase(event)) {
            ref.collapse();
            htmltext = "leon_q196_1.htm";
        } else if ("shunaiman_q196_2.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
            player.sendPacket(new SystemMessage(SystemMessage.BY_USING_THE_SKILL_OF_EINHASAD_S_HOLY_SWORD_DEFEAT_THE_EVIL_LILIMS));
            player.sendPacket(new SystemMessage(SystemMessage.BY_USING_THE_HOLY_WATER_OF_EINHASAD_OPEN_THE_DOOR_POSSESSED_BY_THE_CURSE_OF_FLAMES));
            st.giveItems(SacredSwordofEinhasad);
            st.giveItems(ElmoredenHolyWater);
        } else if ("courtmagician_q196_2.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ITEMGET);
            st.giveItems(CourtMagiciansMagicStaff);
            player.sendPacket(new SystemMessage(SystemMessage.BY_USING_THE_COURT_MAGICIAN_S_MAGIC_STAFF_OPEN_THE_DOOR_ON_WHICH_THE_MAGICIAN_S_BARRIER_IS));
        } else if (event.equalsIgnoreCase("free_anakim")) {
            player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ_SEALING_EMPEROR_1ST);
            player.sendPacket(new SystemMessage(SystemMessage.IN_ORDER_TO_HELP_ANAKIM_ACTIVATE_THE_SEALING_DEVICE_OF_THE_EMPEROR_WHO_IS_POSSESED_BY_THE_EVIL));
            ref.openDoor(door11);
            ThreadPoolManager.INSTANCE.schedule(new SpawnLilithRoom(ref), 17000);
            return null;
        } else if ("shunaiman_q196_4.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
            st.takeAllItems(SealOfBinding,ElmoredenHolyWater, CourtMagiciansMagicStaff, SacredSwordofEinhasad);
        } else if ("leon_q196_2.htm".equalsIgnoreCase(event))
            player.getReflection().collapse();
        else if ("iasonheine_q196_6.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.playSound(SOUND_MIDDLE);
        } else if ("wood_q196_2.htm".equalsIgnoreCase(event))
            if (player.getBaseClassId() == player.getActiveClassId()) {
                st.addExpAndSp(25000000, 2500000);
                st.complete();
                st.finish();
                st.playSound(SOUND_FINISH);
            } else
                return "subclass_forbidden.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.player;
        String htmltext = "noquest";
        if (npcId == IasonHeine) {
            if (cond == 0) {
                if (player.getLevel() >= 79 && player.isQuestCompleted(_195_SevenSignsSecretRitualofthePriests.class))
                    htmltext = "iasonheine_q196_1.htm";
                else {
                    htmltext = "iasonheine_q196_0.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "iasonheine_q196_1a.htm";
            else if (cond == 2) {
                st.setCond(3);
                st.playSound(SOUND_MIDDLE);
                htmltext = "iasonheine_q196_3.htm";
            } else if (cond == 3 || cond == 4)
                htmltext = "iasonheine_q196_4.htm";
            else if (cond == 5)
                htmltext = "iasonheine_q196_5.htm";
            else if (cond == 6)
                htmltext = "iasonheine_q196_6a.htm";
        } else if (npcId == MerchantofMammon) {
            if (cond == 1 && MerchantofMammonSpawn != null)
                htmltext = "merchantofmammon_q196_1.htm";
            else
                htmltext = "merchantofmammon_q196_0.htm";
        } else if (npcId == Shunaiman) {
            if (cond == 3)
                htmltext = "shunaiman_q196_1.htm";
            else if (cond == 4 && st.getQuestItemsCount(SealOfBinding) >= 4)
                htmltext = "shunaiman_q196_3.htm";
            else if (cond == 4 && st.getQuestItemsCount(SealOfBinding) < 4)
                htmltext = "shunaiman_q196_3a.htm";
            else if (cond == 5)
                htmltext = "shunaiman_q196_4a.htm";
        } else if (npcId == CourtMagician) {
            if (cond == 4 && st.getQuestItemsCount(CourtMagiciansMagicStaff) < 1)
                htmltext = "courtmagician_q196_1.htm";
            else
                htmltext = "courtmagician_q196_1a.htm";
        } else if (npcId == DisciplesGatekeeper) {
            if (cond == 4)
                htmltext = "disciplesgatekeeper_q196_1.htm";
        } else if (npcId == Leon) {
            if (cond == 5)
                htmltext = "leon_q196_1.htm";
            else
                htmltext = "leon_q196_1a.htm";
        } else if (npcId == Wood)
            if (cond == 6)
                htmltext = "wood_q196_1.htm";
        return htmltext;
    }

    private void enterInstance(Player player) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(izId))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(izId)) {
            ReflectionUtils.enterReflection(player, izId);
        }
    }

    private class SpawnLilithRoom extends RunnableImpl {
        final Reflection reflection;

        SpawnLilithRoom(Reflection r) {
            reflection = r;
        }

        @Override
        public void runImpl() {
            if (reflection != null) {
                reflection.addSpawnWithoutRespawn(32715, Location.of(-83175, 217021, -7504, 49151)); //Lilith
                reflection.addSpawnWithoutRespawn(32718, Location.of(-83179, 216479, -7504, 16384)); //Anakim
                reflection.addSpawnWithoutRespawn(32717, Location.of(-83222, 217055, -7504, 49151)); //liliths_shadow_guard_ssq
                reflection.addSpawnWithoutRespawn(32716, Location.of(-83127, 217056, -7504, 49151)); //liliths_agent_wizard_ssq
                reflection.addSpawnWithoutRespawn(32719, Location.of(-83227, 216443, -7504, 16384)); //anakims_holly_ssq
                reflection.addSpawnWithoutRespawn(32721, Location.of(-83179, 216432, -7504, 16384)); //anakims_sacred_ssq
                reflection.addSpawnWithoutRespawn(32720, Location.of(-83134, 216443, -7504, 16384)); //anakims_divine_ssq
            }
        }
    }
}