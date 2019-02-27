package l2trunk.scripts.quests;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static l2trunk.gameserver.model.base.ClassId.*;

public abstract class SagasSuperclass extends Quest {
    private static final Map<ClassId, Class<? extends SagasSuperclass>> QUESTS = new HashMap<>();

    static {
        QUESTS.put(berserker, _067_SagaOfTheDoombringer.class);
        QUESTS.put(femaleSoulbreaker, _068_SagaOfTheSoulHound.class);
        QUESTS.put(maleSoulbreaker, _068_SagaOfTheSoulHound.class);
        QUESTS.put(arbalester, _069_SagaOfTheTrickster.class);
        QUESTS.put(paladin, _070_SagaOfThePhoenixKnight.class);
        QUESTS.put(templeKnight, _071_SagaOfEvasTemplar.class);
        QUESTS.put(swordSinger, _072_SagaOfTheSwordMuse.class);
        QUESTS.put(gladiator, _073_SagaOfTheDuelist.class);
        QUESTS.put(warlord, _074_SagaOfTheDreadnoughts.class);
        QUESTS.put(destroyer, _075_SagaOfTheTitan.class);
        QUESTS.put(tyrant, _076_SagaOfTheGrandKhavatari.class);
        QUESTS.put(overlord, _077_SagaOfTheDominator.class);
        QUESTS.put(warcryer, _078_SagaOfTheDoomcryer.class);
        QUESTS.put(treasureHunter, _079_SagaOfTheAdventurer.class);
        QUESTS.put(plainsWalker, _080_SagaOfTheWindRider.class);
        QUESTS.put(abyssWalker, _081_SagaOfTheGhostHunter.class);
        QUESTS.put(hawkeye, _082_SagaOfTheSagittarius.class);
        QUESTS.put(silverRanger, _083_SagaOfTheMoonlightSentinel.class);
        QUESTS.put(phantomRanger, _084_SagaOfTheGhostSentinel.class);
        QUESTS.put(bishop, _085_SagaOfTheCardinal.class);
        QUESTS.put(prophet, _086_SagaOfTheHierophant.class);
        QUESTS.put(elder, _087_SagaOfEvasSaint.class);
        QUESTS.put(sorceror, _088_SagaOfTheArchmage.class);
        QUESTS.put(spellsinger, _089_SagaOfTheMysticMuse.class);
        QUESTS.put(spellhowler, _090_SagaOfTheStormScreamer.class);
        QUESTS.put(warlock, _091_SagaOfTheArcanaLord.class);
        QUESTS.put(elementalSummoner, _092_SagaOfTheElementalMaster.class);
        QUESTS.put(phantomSummoner, _093_SagaOfTheSpectralMaster.class);
        QUESTS.put(necromancer, _094_SagaOfTheSoultaker.class);
        QUESTS.put(darkAvenger, _095_SagaOfTheHellKnight.class);
        QUESTS.put(bladedancer, _096_SagaOfTheSpectralDancer.class);
        QUESTS.put(shillienKnight, _097_SagaOfTheShillienTemplar.class);
        QUESTS.put(shillienElder, _098_SagaOfTheShillienSaint.class);
        QUESTS.put(bountyHunter, _099_SagaOfTheFortuneSeeker.class);
        QUESTS.put(warsmith, _100_SagaOfTheMaestro.class);
    }

    private final List<Spawn> Spawn_List = new ArrayList<>();
    private final List<Integer> Archon_Minions = List.of(
            21646, 21647, 21648, 21649, 21650, 21651);
    private final List<Integer> Guardian_Angels = List.of(
            27214, 27215, 27216);
    private final List<Integer> Archon_Hellisha_Norm = List.of(
            18212, 18213, 18214, 18215, 18216, 18217, 18218, 18219);
    protected int id = 0;
    ClassId classid ;
    List<Integer> NPC;
    List<Integer> items;
    List<Integer> mob;

    List<Location> locs;

    List<String> text;

    SagasSuperclass(boolean party) {
        super(party);
        cleanTempVars();
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new SpawnCleaner(), 60000, 10000);
    }

    private static QuestState findQuest(Player player) {
        return player.getQuestState(QUESTS.get(player.getClassId()));
    }

    private static void process_step_15to16(QuestState st) {
        if (st == null || st.getCond() != 15)
            return;
        int halishasMark = ((SagasSuperclass) st.quest).items.get(3);
        int resonanceAmulet = ((SagasSuperclass) st.quest).items.get(8);

        st.takeItems(halishasMark);
        st.giveItemIfNotHave(resonanceAmulet);
        st.setCond(16);
        st.playSound(SOUND_MIDDLE);
    }

    private void cleanTempVars() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement("DELETE FROM character_quests WHERE name=? AND (var='spawned' OR var='kills' OR var='Archon' OR var LIKE 'Mob_%')")) {
            st.setString(1, name);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void FinishQuest(QuestState st, Player player) {
        st.addExpAndSp(2586527, 0);
        st.giveItems(ADENA_ID, 5000000);
        st.giveItems(6622, 1, true);
        st.exitCurrentQuest();
        player.setClassId(getClassId(player), false, true);
        if (!player.isSubClassActive() && player.getBaseClassId() == getPrevClass(player))
            player.setBaseClass(getClassId(player));
        player.broadcastCharInfo();
        Cast(st.findTemplate(NPC.get(0)), player, 4339);
    }

    void registerNPCs() {
        addStartNpc(NPC.get(0));
        addAttackId(mob.get(2));
        addFirstTalkId(NPC.get(4));

        addTalkId(NPC);

        addKillId(mob);

        addKillId(Archon_Minions);

        addKillId(Guardian_Angels);

        addKillId(Archon_Hellisha_Norm);

        for (int itemId : items)
            if (itemId != 0 && itemId != 7080 && itemId != 7081 && itemId != 6480 && itemId != 6482)
                addQuestItem(itemId);
    }

    ClassId getClassId(Player player) {
        return classid;
    }

    ClassId getPrevClass(Player player) {
        return classid.parent;
    }

    private void Cast(NpcInstance npc, Creature target, int skillId) {
        target.broadcastPacket(new MagicSkillUse(target, skillId, 1, 6000));
        target.broadcastPacket(new MagicSkillUse(npc, skillId, 1, 6000));
    }

    private void AddSpawn(Player player, NpcInstance mob, int TimeToLive) {
        synchronized (Spawn_List) {
            Spawn_List.add(new Spawn(mob, player.getStoredId(), TimeToLive));
        }
    }

    private NpcInstance FindMySpawn(Player player, int npcId) {
        if (npcId == 0 || player == null)
            return null;
        long charStoredId = player.getStoredId();
        synchronized (Spawn_List) {
            for (Spawn spawn : Spawn_List)
                if (spawn.charStoreId == charStoredId && spawn.npcId == npcId)
                    return spawn.getNPC();
        }
        return null;
    }

    private void DeleteSpawn(long charStoredId, int npcId) {
        if (npcId == 0 || charStoredId == 0)
            return;
        synchronized (Spawn_List) {
            Iterator<Spawn> it = Spawn_List.iterator();
            while (it.hasNext()) {
                Spawn spawn = it.next();
                if (spawn.charStoreId == charStoredId && spawn.npcId == npcId) {
                    NpcInstance npc = spawn.getNPC();
                    if (npc != null)
                        npc.deleteMe();
                    it.remove();
                }
            }
        }
    }

    private void DeleteMySpawn(Player player, int npcId) {
        if (npcId > 0 && player != null)
            DeleteSpawn(player.getStoredId(), npcId);
    }

    private NpcInstance spawn(int id, Location loc) {
        return new SimpleSpawner(id)
                .setLoc(loc)
                .stopRespawn()
                .doSpawn(true);
    }

    private void giveHallishaMark(QuestState st) {
        if (GameObjectsStorage.getNpc(st.getInt("Archon")) != null)
            return; // Не убили, или убили чужого

        st.cancelQuestTimer("Archon Hellisha has despawned");

        if (st.getQuestItemsCount(items.get(3)) < 700)
            st.giveItems(items.get(3), Rnd.get(1, 4)); // freya change
        else {
            st.takeItems(items.get(3), 20);
            NpcInstance Archon = spawn(mob.get(1), st.player.getLoc());
            AddSpawn(st.player, Archon, 600000);
            int ArchonId = Archon.objectId();
            st.set("Archon", ArchonId);
            st.startQuestTimer("Archon Hellisha has despawned", 600000, Archon);
            Archon.setRunning();
            Archon.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.player, 100000);
            AutoChat(Archon, text.get(13).replace("PLAYERNAME", st.player.getName()));
        }
    }

    private QuestState findRightState(Player player, NpcInstance npc) {
        if (player == null || npc == null)
            return null;
        long npcStoredId = npc.getStoredId(), charStoredId = player.getStoredId();

        synchronized (Spawn_List) {
            for (Spawn spawn : Spawn_List)
                if (spawn.charStoreId == charStoredId && spawn.npcStoreId == npcStoredId)
                    return player.getQuestState(this);

            for (Spawn spawn : Spawn_List)
                if (spawn.npcStoreId == npcStoredId) {
                    player = GameObjectsStorage.getAsPlayer(spawn.charStoreId);
                    return player == null ? null : player.getQuestState(this);
                }
        }

        return null;
    }

    private void AutoChat(NpcInstance npc, String text) {
        if (npc != null)
            Functions.npcSay(npc, text);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = ""; // simple initialization...if none of the events match, return nothing.
        Player player = st.player;

        if ("0-011.htm".equalsIgnoreCase(event) || "0-012.htm".equalsIgnoreCase(event) || "0-013.htm".equalsIgnoreCase(event) || event.equalsIgnoreCase("0-014.htm") || event.equalsIgnoreCase("0-015.htm"))
            htmltext = event;
        else if ("accept".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.giveItems(items.get(10));
            htmltext = "0-03.htm";
        } else if ("0-1".equals(event)) {
            if (player.getLevel() < 76) {
                htmltext = "0-02.htm";
                st.exitCurrentQuest();
            } else
                htmltext = "0-05.htm";
        } else if ("0-2".equals(event)) {
            if (player.getLevel() >= 76) {
                htmltext = "0-07.htm";
                st.takeItems(items.get(10));
                FinishQuest(st, player);
            } else {
                st.takeItems(items.get(10));
                st.playSound(SOUND_MIDDLE);
                st.setCond(20);
                htmltext = "0-08.htm";
            }
        } else if ("1-3".equals(event)) {
            st.setCond(3);
            htmltext = "1-05.htm";
        } else if ("1-4".equals(event)) {
            st.setCond(4);
            st.takeItems(items.get(0), 1);
            if (items.get(11) != 0)
                st.takeItems(items.get(11), 1);
            st.giveItems(items.get(1));
            htmltext = "1-06.htm";
        } else if ("2-1".equals(event)) {
            st.setCond(2);
            htmltext = "2-05.htm";
        } else if ("2-2".equals(event)) {
            st.setCond(5);
            st.takeItems(items.get(1), 1);
            st.giveItems(items.get(4));
            htmltext = "2-06.htm";
        } else if ("3-5".equals(event))
            htmltext = "3-07.htm";
        else if ("3-6".equals(event)) {
            st.setCond(11);
            htmltext = "3-02.htm";
        } else if ("3-7".equals(event)) {
            st.setCond(12);
            htmltext = "3-03.htm";
        } else if ("3-8".equals(event)) {
            st.setCond(13);
            st.takeItems(items.get(2), 1);
            st.giveItems(items.get(7));
            htmltext = "3-08.htm";
        } else if ("4-1".equals(event))
            htmltext = "4-010.htm";
        else if ("4-2".equals(event)) {
            st.giveItems(items.get(9));
            st.setCond(18);
            st.playSound(SOUND_MIDDLE);
            htmltext = "4-011.htm";
        } else if ("4-3".equals(event)) {
            st.giveItems(items.get(9));
            st.setCond(18);
            st.unset("Quest0");
            st.playSound(SOUND_MIDDLE);
            NpcInstance Mob_2 = FindMySpawn(player, NPC.get(4));
            if (Mob_2 != null) {
                AutoChat(Mob_2, text.get(13).replace("PLAYERNAME", player.getName()));
                DeleteMySpawn(player, NPC.get(4));
                st.cancelQuestTimer("Mob_2 has despawned");
                st.cancelQuestTimer("NPC_4 Timer");
            }
            return null;
        } else if ("5-1".equals(event)) {
            st.setCond(6);
            st.takeItems(items.get(4), 1);
            Cast(st.findTemplate(NPC.get(5)), player, 4546);
            st.playSound(SOUND_MIDDLE);
            htmltext = "5-02.htm";
        } else if ("6-1".equals(event)) {
            st.setCond(8);
            st.takeItems(items.get(5), 1);
            Cast(st.findTemplate(NPC.get(6)), player, 4546);
            st.playSound(SOUND_MIDDLE);
            htmltext = "6-03.htm";
        } else if ("7-1".equals(event)) {
            if (FindMySpawn(player, mob.get(0)) == null) {
                NpcInstance Mob_1 = spawn(mob.get(0), locs.get(0));
                AddSpawn(player, Mob_1, 180000);
                st.startQuestTimer("Mob_0 Timer", 500L, Mob_1);
                st.startQuestTimer("Mob_1 has despawned", 120000L, Mob_1);
                htmltext = "7-02.htm";
            } else
                htmltext = "7-03.htm";
        } else if ("7-2".equals(event)) {
            st.setCond(10);
            st.takeItems(items.get(6), 1);
            Cast(st.findTemplate(NPC.get(7)), player, 4546);
            st.playSound(SOUND_MIDDLE);
            htmltext = "7-06.htm";
        } else if ("8-1".equals(event)) {
            st.setCond(14);
            st.takeItems(items.get(7), 1);
            Cast(st.findTemplate(NPC.get(8)), player, 4546);
            st.playSound(SOUND_MIDDLE);
            htmltext = "8-02.htm";
        } else if ("9-1".equals(event)) {
            st.setCond(17);
            st.takeItems(items.get(8), 1);
            Cast(st.findTemplate(NPC.get(9)), player, 4546);
            st.playSound(SOUND_MIDDLE);
            htmltext = "9-03.htm";
        } else if ("10-1".equals(event)) {
            if (!st.isSet("Quest0") || FindMySpawn(player, NPC.get(4)) == null) {
                DeleteMySpawn(player, NPC.get(4));
                DeleteMySpawn(player, mob.get(2));
                st.set("Quest0");
                st.set("Quest1", 45);

                NpcInstance NPC_4 = spawn(NPC.get(4), locs.get(2));
                NpcInstance Mob_2 = spawn(mob.get(2), locs.get(1));
                AddSpawn(player, Mob_2, 300000);
                AddSpawn(player, NPC_4, 300000);
                st.startQuestTimer("Mob_2 Timer", 1000, Mob_2);
                st.startQuestTimer("Mob_2 despawn", 59000, Mob_2);
                st.startQuestTimer("NPC_4 Timer", 500, NPC_4);
                st.startQuestTimer("NPC_4 despawn", 60000, NPC_4);
                htmltext = "10-02.htm";
            } else if (st.getInt("Quest1") == 45)
                htmltext = "10-03.htm";
            else if (st.isSet("Tab")) {
                NpcInstance Mob_2 = FindMySpawn(player, NPC.get(4));
                if (Mob_2 == null || !st.player.knowsObject(Mob_2)) {
                    DeleteMySpawn(player, NPC.get(4));
                    Mob_2 = spawn(NPC.get(4), locs.get(2));
                    AddSpawn(player, Mob_2, 300000);
                    st.set("Quest0");
                    st.unset("Quest1"); // На всякий случай
                    st.startQuestTimer("NPC_4 despawn", 180000, Mob_2);
                }
                htmltext = "10-04.htm";
            }
        } else if ("10-2".equals(event)) {
            st.setCond(19);
            st.takeItems(items.get(9), 1);
            Cast(st.findTemplate(NPC.get(10)), player, 4546);
            st.playSound(SOUND_MIDDLE);
            htmltext = "10-06.htm";
        } else if ("11-9".equals(event)) {
            st.setCond(15);
            htmltext = "11-03.htm";
        } else if ("Mob_0 Timer".equalsIgnoreCase(event)) {
            AutoChat(FindMySpawn(player, mob.get(0)), text.get(0).replace("PLAYERNAME", player.getName()));
            return null;
        } else if ("Mob_1 has despawned".equalsIgnoreCase(event)) {
            AutoChat(FindMySpawn(player, mob.get(0)), text.get(1).replace("PLAYERNAME", player.getName()));
            DeleteMySpawn(player, mob.get(0));
            return null;
        } else if ("Archon Hellisha has despawned".equalsIgnoreCase(event)) {
            AutoChat(npc, text.get(6).replace("PLAYERNAME", player.getName()));
            DeleteMySpawn(player, mob.get(1));
            return null;
        } else if ("Mob_2 Timer".equalsIgnoreCase(event)) {
            NpcInstance NPC_4 = FindMySpawn(player, NPC.get(4));
            NpcInstance Mob_2 = FindMySpawn(player, mob.get(2));
            if (NPC_4.knowsObject(Mob_2)) {
                NPC_4.setRunning();
                NPC_4.getAI().setIntentionAttack(Mob_2);
                Mob_2.setRunning();
                Mob_2.getAI().setIntentionAttack(NPC_4);
                AutoChat(Mob_2, text.get(14).replace("PLAYERNAME", player.getName()));
            } else
                st.startQuestTimer("Mob_2 Timer", 1000, npc);
            return null;
        } else if ("Mob_2 despawn".equalsIgnoreCase(event)) {
            NpcInstance Mob_2 = FindMySpawn(player, mob.get(2));
            AutoChat(Mob_2, text.get(15).replace("PLAYERNAME", player.getName()));
            st.set("Quest0", 2);
            if (Mob_2 != null)
                Mob_2.reduceCurrentHp(9999999, Mob_2, null, true, true, false, false, false, false, false);
            DeleteMySpawn(player, mob.get(2));
            return null;
        } else if ("NPC_4 Timer".equalsIgnoreCase(event)) {
            AutoChat(FindMySpawn(player, NPC.get(4)), text.get(7).replace("PLAYERNAME", player.getName()));
            st.startQuestTimer("NPC_4 Timer 2", 1500, npc);
            if (st.getInt("Quest1") == 45)
                st.unset("Quest1");
            return null;
        } else if ("NPC_4 Timer 2".equalsIgnoreCase(event)) {
            AutoChat(FindMySpawn(player, NPC.get(4)), text.get(8).replace("PLAYERNAME", player.getName()));
            st.startQuestTimer("NPC_4 Timer 3", 10000, npc);
            return null;
        } else if ("NPC_4 Timer 3".equalsIgnoreCase(event)) {
            if (!st.isSet("Quest0")) {
                st.startQuestTimer("NPC_4 Timer 3", 13000, npc);
                AutoChat(FindMySpawn(player, NPC.get(4)), text.get(Rnd.get(9, 10)).replace("PLAYERNAME", player.getName()));
            }
            return null;
        } else if ("NPC_4 despawn".equalsIgnoreCase(event)) {
            st.inc("Quest1");
            NpcInstance NPC_4 = FindMySpawn(player, NPC.get(4));
            if (st.getInt("Quest0") == 1 || st.getInt("Quest0") == 2 || st.getInt("Quest1") > 3) {
                st.unset("Quest0");
                AutoChat(NPC_4, text.get(Rnd.get(11, 12)).replace("PLAYERNAME", player.getName()));
                if (NPC_4 != null)
                    NPC_4.reduceCurrentHp(9999999, NPC_4, null, true, true, false, false, false, false, false);
                DeleteMySpawn(player, NPC.get(4));
            } else
                st.startQuestTimer("NPC_4 despawn", 1000, npc);
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.player;
        if (player.getClassId() != getPrevClass(player)) {
            st.exitCurrentQuest();
            return htmltext;
        }

        if (cond == 0) {
            if (npcId == NPC.get(0))
                htmltext = "0-01.htm";
        } else if (cond == 1) {
            if (npcId == NPC.get(0))
                htmltext = "0-04.htm";
            else if (npcId == NPC.get(2))
                htmltext = "2-01.htm";
        } else if (cond == 2) {
            if (npcId == NPC.get(2))
                htmltext = "2-02.htm";
            else if (npcId == NPC.get(1))
                htmltext = "1-01.htm";
        } else if (cond == 3) {
            if (npcId == NPC.get(1)) {
                if (st.haveQuestItem(items.get(0))) {
                    if (items.get(11) == 0)
                        htmltext = "1-03.htm";
                    else if (st.haveQuestItem(items.get(11)))
                        htmltext = "1-03.htm";
                    else
                        htmltext = "1-02.htm";
                } else
                    htmltext = "1-02.htm";
            } else if (npcId == 31537) {
                if (st.haveQuestItem(7546)) {
                    htmltext = "tunatun_q72_02.htm";
                } else {
                    st.giveItems(7546, 1);
                    return null;
                }
            }
        } else if (cond == 4) {
            if (npcId == NPC.get(1))
                htmltext = "1-04.htm";
            else if (npcId == NPC.get(2))
                htmltext = "2-03.htm";
        } else if (cond == 5) {
            if (npcId == NPC.get(2))
                htmltext = "2-04.htm";
            else if (npcId == NPC.get(5))
                htmltext = "5-01.htm";
        } else if (cond == 6) {
            if (npcId == NPC.get(5))
                htmltext = "5-03.htm";
            else if (npcId == NPC.get(6))
                htmltext = "6-01.htm";
        } else if (cond == 7) {
            if (npcId == NPC.get(6))
                htmltext = "6-02.htm";
        } else if (cond == 8) {
            if (npcId == NPC.get(6))
                htmltext = "6-04.htm";
            else if (npcId == NPC.get(7))
                htmltext = "7-01.htm";
        } else if (cond == 9) {
            if (npcId == NPC.get(7))
                htmltext = "7-05.htm";
        } else if (cond == 10) {
            if (npcId == NPC.get(7))
                htmltext = "7-07.htm";
            else if (npcId == NPC.get(3))
                htmltext = "3-01.htm";
        } else if (cond == 11 || cond == 12) {
            if (npcId == NPC.get(3))
                if (st.getQuestItemsCount(items.get(2)) > 0)
                    htmltext = "3-05.htm";
                else
                    htmltext = "3-04.htm";
        } else if (cond == 13) {
            if (npcId == NPC.get(3))
                htmltext = "3-06.htm";
            else if (npcId == NPC.get(8))
                htmltext = "8-01.htm";
        } else if (cond == 14) {
            if (npcId == NPC.get(8))
                htmltext = "8-03.htm";
            else if (npcId == NPC.get(11))
                htmltext = "11-01.htm";
        } else if (cond == 15) {
            if (npcId == NPC.get(11))
                htmltext = "11-02.htm";
            else if (npcId == NPC.get(9))
                htmltext = "9-01.htm";
        } else if (cond == 16) {
            if (npcId == NPC.get(9))
                htmltext = "9-02.htm";
        } else if (cond == 17) {
            if (npcId == NPC.get(9))
                htmltext = "9-04.htm";
            else if (npcId == NPC.get(10))
                htmltext = "10-01.htm";
        } else if (cond == 18) {
            if (npcId == NPC.get(10))
                htmltext = "10-05.htm";
        } else if (cond == 19) {
            if (npcId == NPC.get(10))
                htmltext = "10-07.htm";
            if (npcId == NPC.get(0))
                htmltext = "0-06.htm";
        } else if (cond == 20)
            if (npcId == NPC.get(0))
                if (player.getLevel() >= 76) {
                    htmltext = "0-09.htm";
                    if (getClassId(player).occupation() ==2)
                        FinishQuest(st, player);
                } else
                    htmltext = "0-010.htm";
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        String htmltext = "";
        QuestState st = player.getQuestState(name);
        if (st == null)
            return htmltext;
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == NPC.get(4))
            if (cond == 17) {
                QuestState st2 = findRightState(player, npc);
                if (st2 != null)
                    if (st == st2) {
                        if (st.isSet("Tab")) {
                            if (!st.isSet("Quest0"))
                                htmltext = "4-04.htm";
                            else if (st.isSet("Quest0"))
                                htmltext = "4-06.htm";
                        } else if (st.isSet("Quest0")) {
                            htmltext = "4-03.htm";
                        } else htmltext = "4-01.htm";
                    } else if (st.isSet("Tab")) {
                        if (!st.isSet("Quest0"))
                            htmltext = "4-05.htm";
                        else if (st.isSet("Quest0"))
                            htmltext = "4-07.htm";
                    } else if (!st.isSet("Quest0"))
                        htmltext = "4-02.htm";
            } else if (cond == 18)
                htmltext = "4-08.htm";
        return htmltext;
    }

    @Override
    public void onAttack(NpcInstance npc, QuestState st) {
        Player player = st.player;
        if (st.getCond() == 17)
            if (npc.getNpcId() == mob.get(2)) {
                QuestState st2 = findRightState(player, npc);
                if (st == st2) {
                    st.inc("Quest0");
                    if (st.isSet("Quest0"))
                        AutoChat(npc, text.get(16).replace("PLAYERNAME", player.getName()));
                    if (st.getInt("Quest0") > 15) {
                        st.set("Quest0");
                        AutoChat(npc, text.get(17).replace("PLAYERNAME", player.getName()));
                        npc.reduceCurrentHp(9999999, npc, null, true, true, false, false, false, false, false);
                        DeleteMySpawn(player, mob.get(2));
                        st.cancelQuestTimer("Mob_2 despawn");
                        st.set("Tab");
                    }
                }
            }
    }

    private boolean isArchonMinions(int npcId) {
        for (int id : Archon_Minions)
            if (id == npcId)
                return true;
        return false;
    }

    private boolean isArchonHellishaNorm(int npcId) {
        for (int id : Archon_Hellisha_Norm)
            if (id == npcId)
                return true;
        return false;
    }

    private boolean isGuardianAngels(int npcId) {
        for (int id : Guardian_Angels)
            if (id == npcId)
                return true;
        return false;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        Player player = st.player;
        if (player.getActiveClassId() != getPrevClass(player))
            return;

        if (isArchonMinions(npcId)) {
            Party party = player.getParty();
            if (party != null) {
                party.getMembers().stream()
                        .filter(p -> p.getDistance(player) <= Config.ALT_PARTY_DISTRIBUTION_RANGE)
                        .map(SagasSuperclass::findQuest)
                        .filter(Objects::nonNull)
                        .filter(st1 -> st1.getCond() == 15)
                        .forEach(st1 ->
                                ((SagasSuperclass) st1.quest).giveHallishaMark(st1));
            } else {
                QuestState st1 = findQuest(player);
                if (st1 != null && st1.getCond() == 15)
                    ((SagasSuperclass) st1.quest).giveHallishaMark(st1);
            }
        } else if (isArchonHellishaNorm(npcId)) {
            QuestState st1 = findQuest(player);
            if (st1 != null)
                if (st1.getCond() == 15) {
                    // This is just a guess....not really sure what it actually says, if anything
                    AutoChat(npc, ((SagasSuperclass) st1.quest).text.get(4).replace("PLAYERNAME", st1.player.getName()));
                    process_step_15to16(st1);
                }
        } else if (isGuardianAngels(npcId)) {
            QuestState st1 = findQuest(player);
            if (st1 != null)
                if (st1.getCond() == 6)
                    if (st1.getInt("kills") < 9)
                        st1.inc("kills");
                    else {
                        st1.playSound(SOUND_MIDDLE);
                        st1.giveItems(((SagasSuperclass) st1.quest).items.get(5));
                        st1.setCond(7);
                    }
        } else {
            int cond = st.getCond();
            if (npcId == mob.get(0) && cond == 8) {
                QuestState st2 = findRightState(player, npc);
                if (st2 != null) {
                    if (!player.isInParty())
                        if (st == st2) {
                            AutoChat(npc, text.get(12).replace("PLAYERNAME", player.getName()));
                            st.giveItems(items.get(6));
                            st.setCond(9);
                            st.playSound(SOUND_MIDDLE);
                        }
                    st.cancelQuestTimer("Mob_1 has despawned");
                    DeleteMySpawn(st2.player, mob.get(0));
                }
            } else if (npcId == mob.get(1) && cond == 15) {
                QuestState st2 = findRightState(player, npc);
                if (st2 != null) {
                    if (!player.isInParty())
                        if (st == st2) {
                            AutoChat(npc, text.get(4).replace("PLAYERNAME", player.getName()));
                            process_step_15to16(st);
                        } else
                            AutoChat(npc, text.get(5).replace("PLAYERNAME", player.getName()));
                    st.cancelQuestTimer("Archon Hellisha has despawned");
                    DeleteMySpawn(st2.player, mob.get(1));
                }
            } else if (npcId == mob.get(2) && cond == 17) {
                QuestState st2 = findRightState(player, npc);
                if (st == st2) {
                    st.set("Quest0");
                    AutoChat(npc, text.get(17).replace("PLAYERNAME", player.getName()));
                    npc.reduceCurrentHp(9999999, npc, null, true, true, false, false, false, false, false);
                    DeleteMySpawn(player, mob.get(2));
                    st.cancelQuestTimer("Mob_2 despawn");
                    st.set("Tab");
                }
            }
        }
    }

    private class Spawn {
        final int npcId;
        final int TimeToLive;
        final long spawned_at;
        final int charStoreId;
        final int npcStoreId;

        Spawn(NpcInstance npc, int charStoreId, int TimeToLive) {
            npcId = npc.getNpcId();
            npcStoreId = npc.getStoredId();
            this.charStoreId = charStoreId;
            this.TimeToLive = TimeToLive;
            spawned_at = System.currentTimeMillis();
        }

        NpcInstance getNPC() {
            return GameObjectsStorage.getAsNpc(npcStoreId);
        }
    }

    public class SpawnCleaner extends RunnableImpl {
        @Override
        public void runImpl() {
            synchronized (Spawn_List) {
                long curr_time = System.currentTimeMillis();
                Iterator<Spawn> itr = Spawn_List.iterator();
                while (itr.hasNext()) {
                    Spawn spawn = itr.next();
                    NpcInstance npc = spawn.getNPC();
                    if (curr_time - spawn.spawned_at > spawn.TimeToLive || npc == null) {
                        if (npc != null)
                            npc.deleteMe();
                        itr.remove();
                    }
                }
            }
        }
    }
}
