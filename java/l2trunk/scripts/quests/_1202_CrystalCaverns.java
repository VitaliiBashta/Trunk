package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncMul;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.scripts.bosses.BaylorManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class _1202_CrystalCaverns extends Quest {
    private static final int INCSTANCED_ZONE_ID = 10;
    // items
    //private static final int CONTAMINATED_CRYSTAL = 9690;
    private static final int BLUE_CORAL_KEY = 9698;
    private static final int RED_CORAL_KEY = 9699;
    private static final int WHITE_SEED_OF_EVIL_SHARD = 9597;
    private static final int BLACK_SEED_OF_EVIL_SHARD = 9598;
    private static final int PRISON_KEY = 10015;

    // NPC
    private static final int ORACLE_GUIDE = 32281;
    private static final int ORACLE_GUIDE2 = 32278;
    private static final int ORACLE_GUIDE3 = 32280;

    // Mobs
    private static final int GK1 = 22275;
    private static final int GK2 = 22277;
    private static final int KECHICAPTAIN = 22307;
    private static final int TOURMALINE = 22292;

    private static final int KECHI = 25532;
    private static final int DOLPH = 22299;
    private static final int DARNEL = 25531;
    private static final int TEROD = 22301;
    private static final int WEYLIN = 22298;
    private static final int GUARDIAN = 22303;
    private static final int GUARDIAN2 = 22304;

    private static final int KechisCaptain1 = 22306;
    private static final int KechisCaptain2 = 22307;
    private static final int KechisCaptain3 = 22416;
    private static final int BurningIris = 22418;
    private static final int FlameIris = 22419;
    private static final int BrimstoneIris = 22420;
    private static final int GatekeeperoftheSquare = 22276;
    private static final int GatekeeperofFire = 22278;
    private static final int RodoKnight = 22280;
    private static final int PlazaCaiman = 22281;
    private static final int ChromaticDetainee1 = 22282;
    private static final int ChromaticDetainee2 = 22284;
    private static final int PlazaGaviel = 22286;
    private static final int CrystallineUnicorn = 22287;
    private static final int EmeraldBoar = 22288;
    private static final int PlazaHelm = 22289;
    private static final int Spinel = 22293;
    private static final int ReefGolem = 22297;
    private static final int KechisGuard1 = 22309;
    private static final int KechisGuard2 = 22310;

    private static final int OG1 = 32274;
    private static final int OG2 = 32275;
    private static final int OG3 = 32276;
    private static final int OG4 = 32277;

    private static final List<Integer> HEAL_SKILLLIST = List.of(
            1217, 1218, 1011, 1015, 1401, 5146);
    private static final List<Integer> MOBLIST = List.of(
            KechisCaptain1,
            KechisCaptain2,
            KechisCaptain3,
            BurningIris,
            FlameIris,
            BrimstoneIris,
            Spinel,
            ReefGolem,
            PlazaCaiman,
            ChromaticDetainee1,
            CrystallineUnicorn,
            EmeraldBoar,
            PlazaHelm);

    // Doors
    private static final int DOOR1 = 24220021;
    private static final int DOOR2 = 24220024;

    private static final int DOOR3 = 24220005;
    private static final int DOOR4 = 24220006;

    private static final int DOOR5 = 24220061;
    private static final int DOOR6 = 24220023;

    // -------- Start Coral Garden ------------

    private static final int TEARS = 25534;

    private static final int Garden_Stakato = 22313;
    private static final int Garden_Poison_Moth = 22314;
    private static final int Garden_Guard = 22315;
    private static final int Garden_Guardian_Tree = 22316;
    private static final int Garden_Castalia = 22317;

    private static final int CORAL_GARDEN_GATEWAY = 24220025; // Starting Room

    // --------- End Coral Garden ------------
    private static final Map<Integer, World> worlds = new HashMap<>();

    public _1202_CrystalCaverns() {
        super(true);

        addStartNpc(ORACLE_GUIDE, ORACLE_GUIDE3);

        addFirstTalkId(ORACLE_GUIDE2, OG1, OG2, OG3, OG4);

        addKillId(GK1, GK2, TEROD, WEYLIN, DOLPH, DARNEL, KECHI, GUARDIAN, GUARDIAN2, TOURMALINE, KECHICAPTAIN);

        addKillId(TEARS, Garden_Stakato, Garden_Poison_Moth, Garden_Guard, Garden_Guardian_Tree, Garden_Castalia);

        addSkillUseId(OG1, OG2, OG3, OG4);

        addKillId(MOBLIST);
    }

    @Override
    public void onSkillUse(NpcInstance npc, Skill skill, QuestState qs) {
        World world = worlds.get(qs.player.getReflectionId());
        int skillId = skill.id;
        int npcId = npc.getNpcId();
        if (HEAL_SKILLLIST.contains(skillId) && npc.getCurrentHp() == npc.getMaxHp()) {
            if (npcId == OG2) {
                if (!world.OracleTriggeredRoom1) {
                    world.OracleTriggeredRoom1 = true;
                    despawnNpcF(world);
                }
            } else if (npcId == OG3) {
                if (!world.OracleTriggeredRoom2) {
                    world.OracleTriggeredRoom2 = true;
                    despawnNpcF(world);
                }
            } else if (npcId == OG4) {
                if (!world.OracleTriggeredRoom3) {
                    world.OracleTriggeredRoom3 = true;
                    despawnNpcF(world);
                }
            }
        }
    }

    private void despawnNpcF(World world) {
        world.OracleTriggered.og.forEach(l -> GameObjectsStorage.getAsNpc(OG1).decayMe());
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        World world = worlds.get(player.getReflectionId());
        int npcId = npc.getNpcId();
        boolean maxHp = (npc.getCurrentHp() == npc.getMaxHp());
        Location teleto = null;
        boolean spawn_captain = false;

        // If some steam room is already completed, do not trigger mobs respawn again. Lol, people exploit it and cause double, triple, quad kechi.
        if (world.status > 20) {
            if (world.status >= 23 && (npcId == OG2 || npcId == OG3 || npcId == OG4)) {
                player.teleToLocation(149743, 149986, -12141); // Room 4
                return null;
            } else if (world.status == 22 && (npcId == OG2 || npcId == OG3)) {
                player.teleToLocation(150194, 152610, -12169); // Room 3
                return null;
            } else if (world.status == 21 && npcId == OG2) {
                player.teleToLocation(147529, 152587, -12169); // Room 2
                return null;
            }
        }

        if (npcId == ORACLE_GUIDE2) {
            Reflection r = ReflectionManager.INSTANCE.get(world.instanceId);
            r.openDoor(DOOR5);
            r.openDoor(DOOR6);
        } else if (npcId == OG1)
            spawn_captain = true;
        else if (npcId == OG2) {
            if (world.OracleTriggeredRoom1 && maxHp) {
                runSteamRoom2(world);
                teleto = Location.of(147529, 152587, -12169);
            } else
                spawn_captain = true;
        } else if (npcId == OG3) {
            if (world.OracleTriggeredRoom2 && maxHp) {
                runSteamRoom3(world);
                teleto = Location.of(150194, 152610, -12169);
            } else
                spawn_captain = true;
        } else if (npcId == OG4) {
            if (world.OracleTriggeredRoom3 && maxHp) {
                runSteamRoom4(world);
                teleto = Location.of(149743, 149986, -12141);
            } else
                spawn_captain = true;
        }

        if (spawn_captain && Rnd.chance(50)) {
            NpcInstance captain = addSpawnToInstance(KechisCaptain3, npc.getLoc(), world.instanceId);
            captain.addStatFunc(new FuncMul(Stats.POWER_ATTACK, 0x30, this, 5));
            captain.addStatFunc(new FuncMul(Stats.MAGIC_ATTACK, 0x30, this, 5));
            captain.addStatFunc(new FuncMul(Stats.POWER_DEFENCE, 0x30, this, 5));
            captain.addStatFunc(new FuncMul(Stats.MAGIC_DEFENCE, 0x30, this, 5));
            captain.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, Rnd.get(1, 100));
        }

        if (teleto != null) {
            Party party = player.getParty();
            Location loc = teleto;
            if (party != null)
                party.getMembersStream().forEach(pl -> pl.teleToLocation(loc));
            else
                player.teleToLocation(loc);
        }

        return null;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;

        if ("EnterEmeraldSteam".equalsIgnoreCase(event)) {
            st.start();
            enterInstance(player, 1);
            return null;
        } else if ("EnterCoralGarden".equalsIgnoreCase(event)) {
            st.start();
            enterInstance(player, 2);
            return null;
        } else if ("meet".equalsIgnoreCase(event)) {
            int state = BaylorManager.canIntoBaylorLair(player);
            if (state == 1 || state == 2)
                return "meetingNo.htm";
            else if (state == 4)
                return "meetingNoParty.htm";
            else if (state == 3)
                return "teleportOut.htm";
            st.giveItems(PRISON_KEY);
            BaylorManager.entryToBaylorLair(player);
            return "meeting.htm";
        } else if ("out".equalsIgnoreCase(event)) {
            Location loc = Location.of(149361, 172327, -945);
            if (player.getParty() != null) {
                player.getParty().setReflection(null);
                player.getParty().getMembersStream().forEach(pl ->
                        pl.teleToLocation(loc, 0));
            } else
                player.teleToLocation(loc, 0);
            return null;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == ORACLE_GUIDE3) {
            int state = BaylorManager.canIntoBaylorLair(st.player);
            if (state == 1 || state == 2)
                return "meetingNo.htm";
            else if (state == 4)
                return "meetingNoParty.htm";
            else if (state == 3)
                return "teleportOut.htm";
            else if (state == 0)
                return "meetingOk.htm";
            return "32280.htm";
        }
        return null;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        World world = worlds.get(npc.getReflectionId());
        if (world == null)
            return;

        switch (world.status) {
            case 0:
                if (npcId == GK1) {
                    st.dropItem(npc, BLUE_CORAL_KEY);
                    runEmerald(world);
                } else if (npcId == GK2) {
                    st.dropItem(npc, RED_CORAL_KEY);
                    runSteamRoom1(world);
                }
                break;
            case 1:
                if (checkKillProgress(npc, world.emeraldRoom)) {
                    world.status = 2;
                    addSpawnToInstance(TOURMALINE, Location.of(147937, 145886, -12256, 0), world.instanceId);
                }
                break;
            case 2:
                if (npcId == TOURMALINE) {
                    world.status = 3;
                    addSpawnToInstance(TEROD, Location.of(147191, 146855, -12266, 0), world.instanceId);
                }
                break;
            case 3:
                if (npcId == TEROD) {
                    world.status = 4;
                    addSpawnToInstance(TOURMALINE, Location.of(144840, 143792, -11991, 0), world.instanceId);
                }
                break;
            case 4:
                if (npcId == TOURMALINE) {
                    world.status = 5;
                    addSpawnToInstance(DOLPH, Location.of(142067, 145364, -12036, 0), world.instanceId);
                }
                break;
            case 5:
                if (npcId == DOLPH)
                    world.status = 6;
                break;
            case 20:
                if (npcId == KechisCaptain3)
                    world.killedCaptains += 1;
                if (world.killedCaptains == 3) {
                    for (NpcInstance mob : world.steamRoom1.npclist.keySet())
                        if (mob != null)
                            mob.decayMe();
                    runSteamRoom1Oracle(world);
                } else if (checkKillProgress(npc, world.steamRoom1))
                    runSteamRoom1Oracle(world);
                break;
            case 21:
                if (npcId == KechisCaptain1)
                    world.killedCaptains += 1;
                if (world.killedCaptains == 3) {
                    for (NpcInstance mob : world.steamRoom2.npclist.keySet())
                        if (mob != null)
                            mob.decayMe();
                    runSteamRoom2Oracle(world);
                } else if (checkKillProgress(npc, world.steamRoom2))
                    runSteamRoom2Oracle(world);
                break;
            case 22:
                if (npcId == KechisCaptain2)
                    world.killedCaptains += 1;
                if (world.killedCaptains == 3) {
                    for (NpcInstance mob : world.steamRoom3.npclist.keySet())
                        if (mob != null)
                            mob.decayMe();
                    runSteamRoom3Oracle(world);
                } else if (checkKillProgress(npc, world.steamRoom3))
                    runSteamRoom3Oracle(world);
                break;
            case 23:
                if (npcId == KechisCaptain2)
                    world.killedCaptains += 1;
                if (world.killedCaptains == 3) {
                    for (NpcInstance mob : world.steamRoom4.npclist.keySet())
                        if (mob != null)
                            mob.decayMe();
                    addSpawnToInstance(ORACLE_GUIDE2, Location.of(152243, 150152, -12141, 0), world.instanceId);
                    runKechi(world, npc);
                } else if (checkKillProgress(npc, world.steamRoom4)) {
                    addSpawnToInstance(ORACLE_GUIDE2, Location.of(152243, 150152, -12141, 0), world.instanceId);
                    runKechi(world, npc);
                }
                break;
            case 30:
                if (checkKillProgress(npc, world.CoralGardenHall))
                    runCoralGardenGolems(world);
                break;
        }
        if (world.status >= 1 && world.status <= 6)
            if (npcId == DOLPH || npcId == TEROD || npcId == WEYLIN || npcId == GUARDIAN || npcId == GUARDIAN2) {
                world.bosses = world.bosses - 1;
                if (world.bosses == 0)
                    runDarnel(world);
            }
        long seedsCount = (long) (1 * Config.RATE_DROP_ITEMS);
        if (npcId == DARNEL) {
            addSpawnToInstance(ORACLE_GUIDE3, Location.of(152760, 145944, -12584, 0), world.instanceId);
            st.giveItems(Rnd.chance(50) ? WHITE_SEED_OF_EVIL_SHARD : BLACK_SEED_OF_EVIL_SHARD, seedsCount);
        } else if (npcId == KECHI) {
            addSpawnToInstance(ORACLE_GUIDE3, Location.of(154072, 149528, -12152, 0), world.instanceId);
            st.giveItems(Rnd.chance(50) ? WHITE_SEED_OF_EVIL_SHARD : BLACK_SEED_OF_EVIL_SHARD, seedsCount);
            npc.getReflection().startCollapseTimer(5 * 60 * 1000L);
        } else if (npcId == TEARS) {
            addSpawnToInstance(ORACLE_GUIDE3, Location.of(144307, 154419, -11857, 0), world.instanceId);
            st.giveItems(Rnd.chance(50) ? WHITE_SEED_OF_EVIL_SHARD : BLACK_SEED_OF_EVIL_SHARD, seedsCount);
        }
    }

    private void enterInstance(Player player, int type) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(INCSTANCED_ZONE_ID))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(INCSTANCED_ZONE_ID)) {
            Reflection ref = ReflectionUtils.enterReflection(player, INCSTANCED_ZONE_ID);
            World world = new World();
            world.rewarded = new ArrayList<>();
            world.instanceId = ref.id;
            world.bosses = 5;
            worlds.put(ref.id, world);
            player.getParty().getMembersStream()
                    .filter(member -> member != player)
                    .forEach(member -> newQuestState(member, STARTED));

            if (type == 1) {
                runEmeraldAndSteamFirstRoom(world);
                ref.openDoor(DOOR1);
                ref.openDoor(DOOR2);
            } else if (type == 2) {
                runCoralGardenHall(world);
                ref.openDoor(CORAL_GARDEN_GATEWAY);
            }
        }
    }

    private void runEmeraldAndSteamFirstRoom(World world) {
        world.status = 0;
        addSpawnToInstance(GK1, Location.of(148206, 149486, -12140, 32308), world.instanceId);
        addSpawnToInstance(GK2, Location.of(148203, 151093, -12140, 31100), world.instanceId);
        addSpawnToInstance(GatekeeperofFire, Location.of(147182, 151091, -12140, 32470), world.instanceId);
        addSpawnToInstance(GatekeeperoftheSquare, Location.of(147193, 149487, -12140, 32301), world.instanceId);
        addSpawnToInstance(ChromaticDetainee1, Location.of(144289, 150685, -12140, 49394), world.instanceId);
        addSpawnToInstance(ChromaticDetainee1, Location.of(144335, 149846, -12140, 38440), world.instanceId);
        addSpawnToInstance(ChromaticDetainee1, Location.of(144188, 149230, -12140, 13649), world.instanceId);
        addSpawnToInstance(ChromaticDetainee1, Location.of(144442, 149234, -12140, 19083), world.instanceId);
        addSpawnToInstance(ChromaticDetainee1, Location.of(145949, 149477, -12140, 32941), world.instanceId);
        addSpawnToInstance(ChromaticDetainee1, Location.of(146792, 149545, -12140, 40543), world.instanceId);
        addSpawnToInstance(ChromaticDetainee1, Location.of(145441, 151178, -12140, 36154), world.instanceId);
        addSpawnToInstance(ChromaticDetainee1, Location.of(146735, 150981, -12140, 25702), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(144115, 151086, -12140, 51316), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(145009, 149475, -12140, 31393), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(146952, 151228, -12140, 38140), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(145499, 149614, -12140, 38775), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(144308, 151420, -12140, 48469), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(144214, 149514, -12140, 15265), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(145358, 150956, -12140, 26056), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(145780, 151225, -12140, 39635), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(146644, 151325, -12140, 42053), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(146459, 150968, -12140, 11232), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(145699, 149508, -12140, 34774), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(145397, 149262, -12140, 16218), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(145750, 150944, -12140, 30099), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(144421, 151087, -12140, 21857), world.instanceId);
        addSpawnToInstance(ChromaticDetainee1, Location.of(144154, 150261, -12140, 39283), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(146359, 149355, -12140, 23301), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(147819, 150915, -12140, 25958), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(146507, 149650, -12140, 50727), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(146542, 149262, -12140, 18038), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(147918, 149636, -12140, 36636), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(147643, 149334, -12140, 29038), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(146491, 151144, -12140, 28915), world.instanceId);
        addSpawnToInstance(ChromaticDetainee2, Location.of(147783, 151257, -12140, 37421), world.instanceId);
    }

    private void runEmerald(World world) {
        world.status = 1;
        runSecretRoom1(world);
        runSecretRoom2(world);
        runSecretRoom3(world);
        runSecretRoom4(world);
        world.emeraldRoom = new Room();
        world.emeraldRoom.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(Spinel, Location.of(144158, 143424, -11957, 29058), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(144044, 143448, -11949, 27778), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(142580, 143091, -11872, 7458), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(144013, 142556, -11890, 26562), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(144138, 143833, -12003, 35900), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(143759, 143251, -11916, 24854), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(142588, 144861, -12011, 47303), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(142094, 144289, -11940, 38219), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(142076, 143774, -11883, 48980), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(142653, 143778, -11915, 9493), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(143308, 144206, -11992, 37435), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(143367, 145048, -12034, 16679), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Spinel, Location.of(143597, 145175, -12033, 15198), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Spinel, Location.of(142998, 143444, -11901, 12969), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Spinel, Location.of(144089, 143956, -12014, 38107), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ChromaticDetainee1, Location.of(144394, 147711, -12141, 453), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ChromaticDetainee1, Location.of(145165, 147331, -12128, 29058), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(EmeraldBoar, Location.of(145103, 146978, -12069, 23007), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(EmeraldBoar, Location.of(144732, 147205, -12089, 18082), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(EmeraldBoar, Location.of(143859, 146571, -12036, 9955), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(EmeraldBoar, Location.of(142857, 145851, -12038, 19739), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(CrystallineUnicorn, Location.of(144917, 146979, -12057, 26485), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(CrystallineUnicorn, Location.of(144240, 146965, -12070, 1552), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(CrystallineUnicorn, Location.of(144238, 146428, -12034, 16770), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(CrystallineUnicorn, Location.of(143937, 146699, -12039, 32559), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(CrystallineUnicorn, Location.of(144711, 146645, -12036, 29130), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(CrystallineUnicorn, Location.of(144407, 146617, -12035, 7391), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaCaiman, Location.of(144502, 146926, -12050, 12678), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaHelm, Location.of(143816, 146656, -12039, 10414), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaHelm, Location.of(143753, 146466, -12037, 7091), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaHelm, Location.of(143608, 145754, -12036, 48284), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaHelm, Location.of(143240, 145454, -12037, 39901), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaHelm, Location.of(142606, 144827, -12009, 41533), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaHelm, Location.of(142996, 144395, -11994, 64068), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(142732, 145762, -12038, 54764), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(143312, 145772, -12039, 45440), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(144369, 142957, -11890, 29784), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Spinel, Location.of(144954, 143832, -11976, 37294), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Spinel, Location.of(145367, 143588, -11845, 30279), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(145099, 143959, -11942, 29249), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(145241, 143436, -11883, 26892), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(147631, 145941, -12236, 55236), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(148004, 146336, -12283, 44613), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(149430, 145844, -12336, 43268), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(149467, 145353, -12303, 19506), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(147850, 144090, -12227, 9285), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(147723, 143307, -12227, 49819), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(149033, 143103, -12229, 31151), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaCaiman, Location.of(148920, 143400, -12238, 34526), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ChromaticDetainee1, Location.of(148653, 142813, -12231, 28363), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ChromaticDetainee1, Location.of(147485, 143590, -12227, 62369), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaCaiman, Location.of(148426, 145886, -12296, 42769), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Spinel, Location.of(148658, 144958, -12282, 49451), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Spinel, Location.of(148648, 144098, -12240, 44077), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Spinel, Location.of(149156, 143936, -12238, 42632), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Spinel, Location.of(148388, 143092, -12227, 11429), world.instanceId);
        world.emeraldRoom.npclist.put(newNpc, false);
    }

    // -------- Start Emerald Steam -----------

    private void runSecretRoom1(World world) {
        world.SecretRoom1 = new Room();
        world.SecretRoom1.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(EmeraldBoar, Location.of(143114, 140027, -11888, 15025), world.instanceId);
        world.SecretRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(EmeraldBoar, Location.of(142173, 140973, -11888, 55698), world.instanceId);
        world.SecretRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaHelm, Location.of(143210, 140577, -11888, 17164), world.instanceId);
        world.SecretRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaHelm, Location.of(142638, 140107, -11888, 6571), world.instanceId);
        world.SecretRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(142547, 140938, -11888, 48556), world.instanceId);
        world.SecretRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(WEYLIN, Location.of(142690, 140479, -11887, 7663), world.instanceId);
        world.SecretRoom1.npclist.put(newNpc, false);
        // Blacksmith
        addSpawnToInstance(32359, Location.of(142110, 139896, -11888, 8033), world.instanceId);
    }

    private void runSecretRoom2(World world) {
        world.SecretRoom2 = new Room();
        world.SecretRoom2.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(GUARDIAN, Location.of(146272, 141484, -11888, 15025), world.instanceId);
        world.SecretRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaHelm, Location.of(146870, 140906, -11888, 23832), world.instanceId);
        world.SecretRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaHelm, Location.of(146833, 141741, -11888, 37869), world.instanceId);
        world.SecretRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(EmeraldBoar, Location.of(146591, 142040, -11888, 34969), world.instanceId);
        world.SecretRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(EmeraldBoar, Location.of(145744, 141146, -11888, 12266), world.instanceId);
        world.SecretRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(CrystallineUnicorn, Location.of(146044, 142006, -11888, 38094), world.instanceId);
        world.SecretRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(CrystallineUnicorn, Location.of(146276, 140847, -11888, 22210), world.instanceId);
        world.SecretRoom2.npclist.put(newNpc, false);
    }

    private void runSecretRoom3(World world) {
        world.SecretRoom3 = new Room();
        world.SecretRoom3.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(Spinel, Location.of(144868, 143439, -12816, 5588), world.instanceId);
        world.SecretRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Spinel, Location.of(145369, 144040, -12816, 42939), world.instanceId);
        world.SecretRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(22294, Location.of(145315, 143436, -12813, 27523), world.instanceId);
        world.SecretRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Spinel, Location.of(145043, 143854, -12815, 56775), world.instanceId);
        world.SecretRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(145355, 143729, -12815, 63378), world.instanceId);
        world.SecretRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ReefGolem, Location.of(145126, 143697, -12815, 33214), world.instanceId);
        world.SecretRoom3.npclist.put(newNpc, false);
    }

    private void runSecretRoom4(World world) {
        world.SecretRoom4 = new Room();
        world.SecretRoom4.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(ChromaticDetainee1, Location.of(150930, 141920, -12116, 21592), world.instanceId);
        world.SecretRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(ChromaticDetainee1, Location.of(150212, 141905, -12116, 7201), world.instanceId);
        world.SecretRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaGaviel, Location.of(150661, 141859, -12116, 15452), world.instanceId);
        world.SecretRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(PlazaGaviel, Location.of(150411, 141935, -12116, 13445), world.instanceId);
        world.SecretRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(RodoKnight, Location.of(150280, 142241, -12116, 9672), world.instanceId);
        world.SecretRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(RodoKnight, Location.of(150738, 142110, -12115, 14903), world.instanceId);
        world.SecretRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(GUARDIAN2, Location.of(150564, 142231, -12115, 4836), world.instanceId);
        world.SecretRoom4.npclist.put(newNpc, false);
    }

    private void runDarnel(World world) {
        world.status = 7;
        world.DarnelRoom = new Room();
        world.DarnelRoom.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(DARNEL, Location.of(152759, 145949, -12588, 21592), world.instanceId);
        world.DarnelRoom.npclist.put(newNpc, false);
        Reflection r = ReflectionManager.INSTANCE.get(world.instanceId);
        r.openDoor(DOOR3);
        r.openDoor(DOOR4);
    }

    private void runSteamRoom1(World world) {
        world.status = 20;
        world.killedCaptains = 0;
        world.steamRoom1 = new Room();
        world.steamRoom1.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(KechisCaptain1, Location.of(148755, 152573, -12170, 65497), world.instanceId);
        world.steamRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(KechisCaptain3, Location.of(146862, 152734, -12169, 42584), world.instanceId);
        world.steamRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(KechisCaptain3, Location.of(146014, 152607, -12172, 23694), world.instanceId);
        world.steamRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(KechisCaptain3, Location.of(145346, 152585, -12172, 31490), world.instanceId);
        world.steamRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BurningIris, Location.of(146972, 152421, -12172, 28476), world.instanceId);
        world.steamRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BurningIris, Location.of(145714, 152821, -12172, 58705), world.instanceId);
        world.steamRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BurningIris, Location.of(145336, 152805, -12172, 39590), world.instanceId);
        world.steamRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(FlameIris, Location.of(146530, 152762, -12172, 60307), world.instanceId);
        world.steamRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(FlameIris, Location.of(145941, 152412, -12172, 14182), world.instanceId);
        world.steamRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(FlameIris, Location.of(146243, 152807, -12172, 38832), world.instanceId);
        world.steamRoom1.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(FlameIris, Location.of(145152, 152410, -12172, 21338), world.instanceId);
        world.steamRoom1.npclist.put(newNpc, false);
    }

    private void runSteamRoom1Oracle(World world) {
        world.OracleTriggeredRoom1 = false;
        world.OracleTriggered = new Room();
        world.OracleTriggered.og = new ArrayList<>();
        NpcInstance NewNpc1;
        NpcInstance NewNpc2;
        NpcInstance NewNpc3;
        NpcInstance NewNpc4;
        NewNpc1 = addSpawnToInstance(OG1, Location.of(147090, 152505, -12169, 31613), world.instanceId);
        NewNpc1.setCurrentHp(1, false, true);
        world.OracleTriggered.og.add(NewNpc1.objectId());
        NewNpc2 = addSpawnToInstance(OG2, Location.of(147090, 152575, -12169, 31613), world.instanceId);
        NewNpc2.setCurrentHp(1, false, true);
        NewNpc3 = addSpawnToInstance(OG1, Location.of(147090, 152645, -12169, 31613), world.instanceId);
        NewNpc3.setCurrentHp(1, false, true);
        world.OracleTriggered.og.add(NewNpc3.objectId());
        NewNpc4 = addSpawnToInstance(OG1, Location.of(147090, 152715, -12169, 31613), world.instanceId);
        NewNpc4.setCurrentHp(1, false, true);
        world.OracleTriggered.og.add(NewNpc4.objectId());
    }

    private void runSteamRoom2(World world) {
        world.status = 21;
        world.killedCaptains = 0;
        world.steamRoom2 = new Room();
        world.steamRoom2.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(BrimstoneIris, Location.of(148815, 152804, -12172, 44197), world.instanceId);
        world.steamRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BrimstoneIris, Location.of(149414, 152478, -12172, 25651), world.instanceId);
        world.steamRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BrimstoneIris, Location.of(148482, 152388, -12173, 32189), world.instanceId);
        world.steamRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BrimstoneIris, Location.of(147908, 152861, -12172, 61173), world.instanceId);
        world.steamRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(FlameIris, Location.of(147835, 152484, -12172, 7781), world.instanceId);
        world.steamRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(FlameIris, Location.of(148176, 152627, -12173, 3336), world.instanceId);
        world.steamRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(FlameIris, Location.of(148813, 152453, -12172, 50373), world.instanceId);
        world.steamRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(FlameIris, Location.of(149233, 152773, -12172, 36765), world.instanceId);
        world.steamRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(KechisCaptain1, Location.of(149550, 152718, -12172, 37301), world.instanceId);
        world.steamRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(KechisCaptain1, Location.of(148881, 152601, -12172, 24054), world.instanceId);
        world.steamRoom2.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(KechisCaptain1, Location.of(148183, 152486, -12172, 5289), world.instanceId);
        world.steamRoom2.npclist.put(newNpc, false);
    }

    private void runSteamRoom2Oracle(World world) {
        world.OracleTriggeredRoom2 = false;
        world.OracleTriggered = new Room();
        world.OracleTriggered.og = new ArrayList<>();
        NpcInstance NewNpc1;
        NpcInstance NewNpc2;
        NpcInstance NewNpc3;
        NpcInstance NewNpc4;
        NewNpc1 = addSpawnToInstance(OG1, Location.of(149783, 152505, -12169, 31613), world.instanceId);
        NewNpc1.setCurrentHp(1, false, true);
        world.OracleTriggered.og.add(NewNpc1.objectId());
        NewNpc2 = addSpawnToInstance(OG1, Location.of(149783, 152575, -12169, 31613), world.instanceId);
        NewNpc2.setCurrentHp(1, false, true);
        world.OracleTriggered.og.add(NewNpc2.objectId());
        NewNpc3 = addSpawnToInstance(OG3, Location.of(149783, 152645, -12169, 31613), world.instanceId);
        NewNpc3.setCurrentHp(1, false, true);
        NewNpc4 = addSpawnToInstance(OG1, Location.of(149783, 152715, -12169, 31613), world.instanceId);
        NewNpc4.setCurrentHp(1, false, true);
        world.OracleTriggered.og.add(NewNpc4.objectId());
    }

    private void runSteamRoom3(World world) {
        world.status = 22;
        world.killedCaptains = 0;
        world.steamRoom3 = new Room();
        world.steamRoom3.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(FlameIris, Location.of(150751, 152430, -12172, 29190), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(FlameIris, Location.of(150613, 152778, -12172, 19574), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BurningIris, Location.of(151242, 152832, -12172, 40116), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BurningIris, Location.of(151473, 152656, -12172, 28951), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BrimstoneIris, Location.of(151090, 152401, -12172, 1909), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BurningIris, Location.of(151625, 152372, -12172, 31372), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BrimstoneIris, Location.of(152283, 152577, -12172, 15323), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(FlameIris, Location.of(151906, 152699, -12172, 49605), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BurningIris, Location.of(151134, 152626, -12172, 59956), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BurningIris, Location.of(152105, 152766, -12172, 59956), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BurningIris, Location.of(150416, 152567, -12173, 53744), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(KechisCaptain2, Location.of(150689, 152618, -12172, 34932), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(KechisCaptain2, Location.of(151329, 152558, -12172, 55102), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(KechisCaptain2, Location.of(152054, 152557, -12172, 40959), world.instanceId);
        world.steamRoom3.npclist.put(newNpc, false);
    }

    private void runSteamRoom3Oracle(World world) {
        world.OracleTriggeredRoom3 = false;
        world.OracleTriggered = new Room();
        world.OracleTriggered.og = new ArrayList<>();
        NpcInstance NewNpc1;
        NpcInstance NewNpc2;
        NpcInstance NewNpc3;
        NpcInstance NewNpc4;
        NewNpc1 = addSpawnToInstance(OG1, Location.of(152461, 152505, -12169, 31613), world.instanceId);
        NewNpc1.setCurrentHp(1, false, true);
        world.OracleTriggered.og.add(NewNpc1.objectId());
        NewNpc2 = addSpawnToInstance(OG1, Location.of(152461, 152575, -12169, 31613), world.instanceId);
        NewNpc2.setCurrentHp(1, false, true);
        world.OracleTriggered.og.add(NewNpc2.objectId());
        NewNpc3 = addSpawnToInstance(OG1, Location.of(152461, 152645, -12169, 31613), world.instanceId);
        NewNpc3.setCurrentHp(1, false, true);
        world.OracleTriggered.og.add(NewNpc3.objectId());
        NewNpc4 = addSpawnToInstance(OG4, Location.of(152461, 152715, -12169, 31613), world.instanceId);
        NewNpc4.setCurrentHp(1, false, true);
    }

    private void runSteamRoom4(World world) {
        world.status = 23;
        world.killedCaptains = 0;
        world.steamRoom4 = new Room();
        world.steamRoom4.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(KechisCaptain2, Location.of(150454, 149976, -12173, 28435), world.instanceId);
        world.steamRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(KechisCaptain2, Location.of(151186, 150140, -12173, 37604), world.instanceId);
        world.steamRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(KechisCaptain2, Location.of(151718, 149805, -12172, 26672), world.instanceId);
        world.steamRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BurningIris, Location.of(150755, 149852, -12173, 31074), world.instanceId);
        world.steamRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BurningIris, Location.of(150457, 150173, -12172, 34736), world.instanceId);
        world.steamRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BrimstoneIris, Location.of(151649, 150194, -12172, 35198), world.instanceId);
        world.steamRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BrimstoneIris, Location.of(151254, 149876, -12172, 26433), world.instanceId);
        world.steamRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BrimstoneIris, Location.of(151819, 150010, -12172, 33680), world.instanceId);
        world.steamRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(FlameIris, Location.of(150852, 150030, -12173, 32002), world.instanceId);
        world.steamRoom4.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(FlameIris, Location.of(150031, 149797, -12172, 16560), world.instanceId);
        world.steamRoom4.npclist.put(newNpc, false);
    }

    private void runKechi(World world, NpcInstance captain) {
        world.status = 24;
        world.kechiRoom = new Room();
        world.kechiRoom.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(KechisGuard1, Location.of(154409, 149680, -12151, 8790), world.instanceId);
        world.kechiRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(KechisGuard2, Location.of(154165, 149734, -12159, 4087), world.instanceId);
        world.kechiRoom.npclist.put(newNpc, false);
        if (captain.getReflection().getAllByNpcId(KECHI, true).count() == 0) {//Fix for Second KECHI exploit
            newNpc = addSpawnToInstance(KECHI, Location.of(154069, 149525, -12158, 51165), world.instanceId);
            world.kechiRoom.npclist.put(newNpc, false);
        }
    }

    private void runCoralGardenHall(World world) {
        world.status = 30;
        world.CoralGardenHall = new Room();
        world.CoralGardenHall.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(141740, 150330, -11817, 6633), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(141233, 149960, -11817, 49187), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(141866, 150723, -11817, 13147), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(142276, 151105, -11817, 7823), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(142102, 151640, -11817, 20226), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(142093, 152269, -11817, 3445), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(141569, 152994, -11817, 22617), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(141083, 153210, -11817, 28405), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(140469, 152415, -11817, 41700), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(140180, 151635, -11817, 45729), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(140490, 151126, -11817, 54857), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guard, Location.of(140930, 150269, -11817, 17591), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guard, Location.of(141203, 150210, -11817, 64400), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guard, Location.of(141360, 150357, -11817, 9093), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guard, Location.of(142255, 151694, -11817, 14655), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guard, Location.of(141920, 151124, -11817, 8191), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guard, Location.of(141911, 152734, -11817, 21600), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guard, Location.of(141032, 152929, -11817, 32791), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guard, Location.of(140317, 151837, -11817, 43864), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guard, Location.of(140183, 151939, -11817, 25981), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guardian_Tree, Location.of(140944, 152724, -11817, 12529), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guardian_Tree, Location.of(141301, 154428, -11817, 17207), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guardian_Tree, Location.of(142499, 154437, -11817, 65478), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Castalia, Location.of(142664, 154612, -11817, 8498), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Castalia, Location.of(142711, 154137, -11817, 28756), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Stakato, Location.of(142705, 154378, -11817, 26017), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Castalia, Location.of(141605, 154490, -11817, 31128), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Castalia, Location.of(141115, 154674, -11817, 28781), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Stakato, Location.of(141053, 154431, -11817, 46546), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Stakato, Location.of(141423, 154130, -11817, 60888), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(142249, 154395, -11817, 64346), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Castalia, Location.of(141530, 152803, -11817, 53953), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Castalia, Location.of(142020, 152272, -11817, 55995), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Castalia, Location.of(142134, 151667, -11817, 52687), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Castalia, Location.of(141958, 151021, -11817, 42965), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Castalia, Location.of(140979, 150233, -11817, 38924), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Castalia, Location.of(140509, 150983, -11817, 23466), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Castalia, Location.of(140151, 151410, -11817, 23661), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Castalia, Location.of(140446, 152370, -11817, 13192), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Stakato, Location.of(140249, 152133, -11817, 41391), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Stakato, Location.of(140664, 152655, -11817, 8720), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Stakato, Location.of(141610, 152988, -11817, 57460), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Poison_Moth, Location.of(141189, 154197, -11817, 16792), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guard, Location.of(142315, 154368, -11817, 30260), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guard, Location.of(142577, 154774, -11817, 45981), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Stakato, Location.of(141338, 153089, -11817, 26387), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(Garden_Guardian_Tree, Location.of(140800, 150707, -11817, 55884), world.instanceId);
        world.CoralGardenHall.npclist.put(newNpc, false);
    }

    private void runCoralGardenGolems(World world) {
        world.status = 31;
        addSpawnToInstance(TEARS, Location.of(144298, 154420, -11854, 63371), world.instanceId); // Tears
        addSpawnToInstance(32328, Location.of(140547, 151670, -11813, 32767), world.instanceId);
        addSpawnToInstance(32328, Location.of(141941, 151684, -11813, 63371), world.instanceId);
    }

    // --------- End Emerald Steam ------------

    // -------- Start Coral Garden ------------

    private boolean checkKillProgress(NpcInstance npc, Room room) {
        if (room.npclist.containsKey(npc))
            room.npclist.put(npc, true);
        return room.npclist.values().stream()
                .allMatch(value -> value);
    }

    class World {
        int instanceId;
        int status;
        int killedCaptains;
        int bosses;
        Room OracleTriggered;
        boolean OracleTriggeredRoom1 = true;
        boolean OracleTriggeredRoom2 = true;
        boolean OracleTriggeredRoom3 = true;
        List<Integer> rewarded;
        Room emeraldRoom;
        Room steamRoom1;
        Room steamRoom2;
        Room steamRoom3;
        Room steamRoom4;
        Room SecretRoom1;
        Room SecretRoom2;
        Room SecretRoom3;
        Room SecretRoom4;
        Room DarnelRoom;
        Room kechiRoom;
        Room CoralGardenHall;
    }

    //----------- End Coral Garden ------------

    public class Room {
        Map<NpcInstance, Boolean> npclist;
        List<Integer> og;
    }
}