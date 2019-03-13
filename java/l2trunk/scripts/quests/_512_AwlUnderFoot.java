package l2trunk.scripts.quests;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.InstantZoneHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.templates.InstantZone;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class _512_AwlUnderFoot extends Quest {
    private final static int INSTANCE_ZONE_ID = 13; // Castles Dungeon

    private final static int FragmentOfTheDungeonLeaderMark = 9798;
    private final static int RewardMarksCount = 1500;
    private final static int KnightsEpaulette = 9912;

    private static final Map<Integer, Prison> _prisons = new ConcurrentHashMap<>();

    private static final int RhiannaTheTraitor = 25546;
    private static final int TeslaTheDeceiver = 25549;
    private static final int SoulHunterChakundel = 25552;

    private static final int DurangoTheCrusher = 25553;
    private static final int BrutusTheObstinate = 25554;
    private static final int RangerKarankawa = 25557;
    private static final int SargonTheMad = 25560;

    private static final int BeautifulAtrielle = 25563;
    private static final int NagenTheTomboy = 25566;
    private static final int JaxTheDestroyer = 25569;

    private static final List<Integer> type1 = List.of(RhiannaTheTraitor, TeslaTheDeceiver, SoulHunterChakundel);
    private static final List<Integer> type2 = List.of(DurangoTheCrusher, BrutusTheObstinate, RangerKarankawa, SargonTheMad);
    private static final List<Integer> type3 = List.of(BeautifulAtrielle, NagenTheTomboy, JaxTheDestroyer);

    public _512_AwlUnderFoot() {
        super(false);

        // Wardens
        addStartNpc(36403, 36404, 36405, 36406, 36407, 36408, 36409, 36410, 36411);
        addQuestItem(FragmentOfTheDungeonLeaderMark);
        addKillId(RhiannaTheTraitor, TeslaTheDeceiver, SoulHunterChakundel, DurangoTheCrusher, BrutusTheObstinate, RangerKarankawa, SargonTheMad, BeautifulAtrielle, NagenTheTomboy, JaxTheDestroyer);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("gludio_prison_keeper_q0512_03.htm") || event.equalsIgnoreCase("gludio_prison_keeper_q0512_05.htm")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("exit".equalsIgnoreCase(event)) {
            st.exitCurrentQuest();
            return null;
        } else if ("enter".equalsIgnoreCase(event))
            if (st.getState() == CREATED || !check(st.player))
                return "gludio_prison_keeper_q0512_01a.htm";
            else
                return enterPrison(st.player);
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (!check(st.player))
            return "gludio_prison_keeper_q0512_01a.htm";
        if (st.getState() == CREATED)
            return "gludio_prison_keeper_q0512_01.htm";
        if (st.haveQuestItem(FragmentOfTheDungeonLeaderMark)) {
            st.giveItems(KnightsEpaulette, st.getQuestItemsCount(FragmentOfTheDungeonLeaderMark));
            st.takeItems(FragmentOfTheDungeonLeaderMark);
            st.playSound(SOUND_FINISH);
            return "gludio_prison_keeper_q0512_08.htm";
        }
        return "gludio_prison_keeper_q0512_09.htm";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        for (Prison prison : _prisons.values())
            if (prison.getReflectionId() == npc.getReflectionId()) {
                switch (npc.getNpcId()) {
                    case RhiannaTheTraitor:
                    case TeslaTheDeceiver:
                    case SoulHunterChakundel:
                        prison.initSpawn(Rnd.get(type2), false);
                        break;
                    case DurangoTheCrusher:
                    case BrutusTheObstinate:
                    case RangerKarankawa:
                    case SargonTheMad:
                        prison.initSpawn(Rnd.get(type3), false);
                        break;
                    case BeautifulAtrielle:
                    case NagenTheTomboy:
                    case JaxTheDestroyer:
                        Party party = st.player.getParty();
                        if (party != null)
                            party.getMembersStream()
                                    .map(member -> member.getQuestState(this))
                                    .filter(Objects::nonNull)
                                    .filter(QuestState::isStarted)
                                    .forEach(qs -> {
                                        qs.giveItems(FragmentOfTheDungeonLeaderMark, RewardMarksCount / party.size());
                                        qs.playSound(SOUND_ITEMGET);
                                        qs.player.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
                                    });

                        else {
                            st.giveItems(FragmentOfTheDungeonLeaderMark, RewardMarksCount);
                            st.playSound(SOUND_ITEMGET);
                            st.player.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
                        }
                        Reflection r = ReflectionManager.INSTANCE.get(prison.getReflectionId());
                        if (r != null)
                            r.startCollapseTimer(300000); // Всех боссов убили, запускаем коллапс через 5 минут
                        break;
                }
                break;
            }
    }

    private boolean check(Player player) {
        Castle castle = ResidenceHolder.getResidenceByObject(Castle.class, player);
        if (castle == null)
            return false;
        Clan clan = player.getClan();
        if (clan == null)
            return false;
        return clan.clanId() == castle.getOwnerId();
    }

    private String enterPrison(Player player) {
        Castle castle = ResidenceHolder.getResidenceByObject(Castle.class, player);
        if (castle == null || castle.getOwner() != player.getClan())
            return "gludio_prison_keeper_q0512_01a.htm";

        if (!areMembersSameClan(player))
            return "gludio_prison_keeper_q0512_01a.htm";

        if (player.canEnterInstance(INSTANCE_ZONE_ID)) {
            InstantZone iz = InstantZoneHolder.getInstantZone(INSTANCE_ZONE_ID);
            Prison prison;
            if (!_prisons.isEmpty()) {
                prison = _prisons.get(castle.getId());
                if (prison != null && prison.isLocked()) {
                    player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
                    return null;
                }

                // Synerge - Add the getPlayer to the instance again
                if (prison != null) {
                    Reflection r = ReflectionManager.INSTANCE.get(prison.getReflectionId());
                    if (r != null) {
                        player.setReflection(r);
                        player.teleToLocation(iz.getTeleportCoord());
                        player.setVar("backCoords", r.getReturnLoc().toXYZString());
                        player.setInstanceReuse(iz.getId(), System.currentTimeMillis());
                        return null;
                    }
                }
            }

            prison = new Prison(castle.getId(), iz);
            _prisons.put(prison.getCastleId(), prison);

            Reflection r = ReflectionManager.INSTANCE.get(prison.getReflectionId());

            r.setReturnLoc(player.getLoc());

            player.getParty().getMembersStream().forEach(member -> {
                if (member != player)
                    newQuestState(member, STARTED);
                member.setReflection(r);
                member.teleToLocation(iz.getTeleportCoord());
                member.setVar("backCoords", r.getReturnLoc().toXYZString());
                member.setInstanceReuse(iz.getId(), System.currentTimeMillis());
            });

            player.getParty().setReflection(r);
            r.setParty(player.getParty());
            r.startCollapseTimer(iz.getTimelimit() * 60 * 1000L);
            player.getParty().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(iz.getTimelimit()));

            prison.initSpawn(Rnd.get(type1), true);
        }
        return null;
    }

    private boolean areMembersSameClan(Player player) {
        if (player.getParty() == null)
            return true;
        return player.getParty().getMembersStream()
        .noneMatch(p ->p.getClan() != player.getClan());
    }

    private class Prison {
        private int castleId;
        private int reflectionId;
        private long lastEnter;

        Prison(int id, InstantZone iz) {
            try {
                Reflection r = new Reflection();
                r.init(iz);
                reflectionId = r.id;
                castleId = id;
                lastEnter = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void initSpawn(int npcId, boolean first) {
            ThreadPoolManager.INSTANCE.schedule(new PrisonSpawnTask(npcId), first ? 60000 : 180000);
        }

        int getReflectionId() {
            return reflectionId;
        }

        int getCastleId() {
            return castleId;
        }

        boolean isLocked() {
            return System.currentTimeMillis() - lastEnter < 4 * 60 * 60 * 1000L;
        }

        private class PrisonSpawnTask extends RunnableImpl {
            final int _npcId;

            PrisonSpawnTask(int npcId) {
                _npcId = npcId;
            }

            @Override
            public void runImpl() {
                addSpawnToInstance(_npcId, Location.of(12152, -49272, -3008, 25958), reflectionId);
            }
        }
    }
}