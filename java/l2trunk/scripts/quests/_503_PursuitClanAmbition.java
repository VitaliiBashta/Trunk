package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public final class _503_PursuitClanAmbition extends Quest {
    // items

    // first part
    private static final int G_Let_Martien = 3866;
    private static final int Bl_Anvil_Coin = 3871;
    // second Part
    private static final int G_Let_Balthazar = 3867;
    // third part
    private static final int G_Let_Rodemai = 3868;
    private final int Th_Wyrm_Eggs = 3842;
    private final int Drake_Eggs = 3841;
    private final int Bl_Wyrm_Eggs = 3840;
    private final int Mi_Drake_Eggs = 3839;
    private final int Brooch = 3843;
    private final int Recipe_Spiteful_Soul_Energy = 14854;
    private final int Spiteful_Soul_Energy = 14855;
    private final int Spiteful_Soul_Vengeance = 14856;
    private final int Imp_Keys = 3847;
    private final int Scepter_Judgement = 3869;

    // the final item
    private final int Proof_Aspiration = 3870;

    private final List<Integer> EggList = List.of(
            Mi_Drake_Eggs,
            Bl_Wyrm_Eggs,
            Drake_Eggs,
            Th_Wyrm_Eggs);

    // NPCs
    private final int Gustaf = 30760;
    private final int Martien = 30645;
    private final int Athrea = 30758;
    private final int Kalis = 30759;
    private final int Fritz = 30761;
    private final int Lutz = 30762;
    private final int Kurtz = 30763;
    private final int Kusto = 30512;
    private final int Balthazar = 30764;
    private final int Rodemai = 30868;
    private final int Coffer = 30765;
    private final int Cleo = 30766;

    // MOBS
    private final int ThunderWyrm1 = 20282;
    private final int ThunderWyrm2 = 20243;
    private final int Drake1 = 20137;
    private final int Drake2 = 20285;
    private final int BlitzWyrm = 27178;
    private final int SpitefulSoulLeader = 20974;
    private final int GraveGuard = 20668;
    private final int GraveKeymaster = 27179;
    private final int ImperialGravekeeper = 27181;

    public _503_PursuitClanAmbition() {
        super(PARTY_ALL);

        addStartNpc(Gustaf);

        addTalkId(Martien, Athrea, Kalis, Fritz, Lutz, Kurtz, Kusto, Balthazar, Rodemai, Coffer, Cleo);

        addKillId(ThunderWyrm1, ThunderWyrm2, Drake1, Drake2, BlitzWyrm, SpitefulSoulLeader, GraveGuard, GraveKeymaster, ImperialGravekeeper);

        addAttackId(ImperialGravekeeper);

        addQuestItem(IntStream.rangeClosed(3839, 3848).toArray());
        addQuestItem(IntStream.rangeClosed(3866, 3869).toArray());

        addQuestItem(Recipe_Spiteful_Soul_Energy, Spiteful_Soul_Energy, Spiteful_Soul_Vengeance);
    }

    private void suscribe_members(QuestState st) {
        int clan = st.player.getClan().clanId();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement offline = con.prepareStatement("SELECT obj_Id FROM characters WHERE clanid=? AND online=0");
             PreparedStatement insertion = con.prepareStatement("REPLACE INTO character_quests (char_id,name,var,value) VALUES (?,?,?,?)")) {
            offline.setInt(1, clan);
            ResultSet rs = offline.executeQuery();
            while (rs.next()) {
                int char_id = rs.getInt("obj_Id");
                try {
                    insertion.setInt(1, char_id);
                    insertion.setString(2, name);
                    insertion.setString(3, "<state>");
                    insertion.setString(4, "Started");
                    insertion.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void offlineMemberExit(QuestState st) {
        int clan = st.player.getClan().clanId();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement offline = con.prepareStatement("DELETE FROM character_quests WHERE name=? AND char_id IN (SELECT obj_id FROM characters WHERE clanId=? AND online=0)")) {
            offline.setString(1, name);
            offline.setInt(2, clan);
            offline.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Player getLeader(QuestState st) {
        Player player = st.player;
        if (player == null)
            return null;
        Clan clan = player.getClan();
        if (clan == null)
            return null;
        return clan.getLeader().getPlayer();
    }

    // returns leaders quest cond, if he is offline will read out of database :)
    private int getLeaderVar(QuestState st, String var) {
        final boolean cond = "cond".equalsIgnoreCase(var);
        try {
            Player leader = getLeader(st);
            if (leader != null) {
                if (cond)
                    return leader.getQuestState(this).getCond();
                else
                    return leader.getQuestState(this).getInt(var);
            }
        } catch (Exception e) {
            return -1;
        }

        Clan clan = st.player.getClan();

        if (clan == null)
            return -1;

        int leaderId = clan.getLeaderId();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement offline = con.prepareStatement("SELECT value FROM character_quests WHERE char_id=? AND var=? AND name=?")) {
            offline.setInt(1, leaderId);
            offline.setString(2, var);
            offline.setString(3, name);
            int val = -1;
            ResultSet rs = offline.executeQuery();
            if (rs.next()) {
                val = rs.getInt("value");
                if (cond && (val & 0x80000000) != 0) {
                    val &= 0x7fffffff;
                    for (int i = 1; i < 32; i++) {
                        val = (val >> 1);
                        if (val == 0)
                            return i;
                    }
                }
            }
            return val;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // set's leaders quest cond, if he is offline will read out of database :)
    // for now, if the leader is not logged in, this assumes that the variable
    // has already been inserted once (initialized, in some sense).
    private void setLeaderVar(QuestState st, int value) {
        Clan clan = st.player.getClan();
        if (clan == null)
            return;
        Player leader = clan.getLeader().player;
        if (leader != null) {
            leader.getQuestState(this).set("ImpGraveKeeper", value);
        } else // для прямой записи в базу cond не корректируем, при загрузке сконвертится само.
        {
            int leaderId = st.player.getClan().getLeaderId();

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement offline = con.prepareStatement("UPDATE character_quests SET value=? WHERE char_id=? AND var=? AND name=?")) {
                offline.setInt(1, value);
                offline.setInt(2, leaderId);
                offline.setString(3, "ImpGraveKeeper");
                offline.setString(4, name);
                offline.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkEggs(QuestState st) {
        int count = 0;
        for (int item : EggList)
            if (st.getQuestItemsCount(item) > 9)
                count += 1;
        return count > 3;
    }

    private void giveItem(int item, long maxcount, QuestState st) {
        Player player = st.player;
        if (player == null)
            return;
        Player leader = getLeader(st);
        if (leader == null)
            return;
        if (player.getDistance(leader) > Config.ALT_PARTY_DISTRIBUTION_RANGE)
            return;
        QuestState qs = leader.getQuestState(this);
        if (qs == null)
            return;
        long count = qs.getQuestItemsCount(item);
        if (count < maxcount) {
            qs.giveItems(item);
            if (count == maxcount - 1)
                qs.playSound(SOUND_MIDDLE);
            else
                qs.playSound(SOUND_ITEMGET);
        }
    }

    private String exit503(boolean completed, QuestState st) {
        if (completed) {
            st.giveItems(Proof_Aspiration);
            st.addExpAndSp(0, 250000);
            st.unset("cond");
            st.unset("Fritz");
            st.unset("Lutz");
            st.unset("Kurtz");
            st.unset("ImpGraveKeeper");
            st.finish();
        } else
            st.exitCurrentQuest();
        st.takeItems(Scepter_Judgement);
        try {
            List<Player> members = st.player.getClan().getOnlineMembers();
            members.stream()
                    .filter(Objects::nonNull)
                    .map(player -> player.getQuestState(this))
                    .filter(Objects::nonNull)
                    .forEach(QuestState::exitCurrentQuest);
            offlineMemberExit(st);
        } catch (Exception e) {
            return "You dont have any members in your Clan, so you can't finish the Pursuit of Aspiration";
        }
        return "Congratulations, you have finished the Pursuit of Clan Ambition";
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        // Events Gustaf
        if ("30760-08.htm".equalsIgnoreCase(event)) {
            st.giveItems(G_Let_Martien);
            st.setCond(1);
            st.set("Fritz");
            st.set("Lutz");
            st.set("Kurtz");
            st.set("ImpGraveKeeper");
            st.start();
        } else if ("30760-12.htm".equalsIgnoreCase(event)) {
            st.giveItems(G_Let_Balthazar);
            st.setCond(4);
        } else if ("30760-16.htm".equalsIgnoreCase(event)) {
            st.giveItems(G_Let_Rodemai);
            st.setCond(7);
        } else if ("30760-20.htm".equalsIgnoreCase(event))
            exit503(true, st);
        else if ("30760-22.htm".equalsIgnoreCase(event))
            st.setCond(13);
        else if ("30760-23.htm".equalsIgnoreCase(event))
            exit503(true, st);
            // Events Martien
        else if ("30645-03.htm".equalsIgnoreCase(event)) {
            st.takeItems(G_Let_Martien);
            st.setCond(2);
            suscribe_members(st);
            List<Player> members = st.player.getClan().getOnlineMembers(st.player.objectId());
            members.forEach(player -> newQuestState(player, STARTED));
        }
        // Events Kurtz
        else if ("30763-03.htm".equalsIgnoreCase(event)) {
            if (st.isSet("Kurtz")) {
                htmltext = "30763-02.htm";
                st.giveItems(Mi_Drake_Eggs, 6);
                st.giveItems(Brooch);
                st.set("Kurtz", 2);
            }
        }
        // Events Lutz
        else if ("30762-03.htm".equalsIgnoreCase(event)) {
            if (st.isSet("Lutz")) {
                htmltext = "30762-02.htm";
                st.giveItems(Mi_Drake_Eggs, 4);
                st.giveItems(Bl_Wyrm_Eggs, 3);
                st.set("Lutz", 2);
            }
            st.addSpawn(BlitzWyrm, npc.getLoc(), 300, 120000);
            st.addSpawn(BlitzWyrm, npc.getLoc(), 300, 120000);
        }
        // Events Fritz
        else if ("30761-03.htm".equalsIgnoreCase(event)) {
            if (st.isSet("Fritz")) {
                htmltext = "30761-02.htm";
                st.giveItems(Bl_Wyrm_Eggs, 3);
                st.set("Fritz", 2);
            }
            st.addSpawn(BlitzWyrm, npc.getLoc(), 300, 120000);
            st.addSpawn(BlitzWyrm, npc.getLoc(), 300, 120000);
        }
        // Events Kusto
        else if ("30512-03.htm".equalsIgnoreCase(event)) {
            st.takeItems(Brooch);
            st.giveItems(Bl_Anvil_Coin);
            st.set("Kurtz", 3);
        }
        // Events Balthazar
        else if ("30764-03.htm".equalsIgnoreCase(event)) {
            st.takeItems(G_Let_Balthazar);
            st.setCond(5);
            st.set("Kurtz", 3);
        } else if ("30764-05.htm".equalsIgnoreCase(event)) {
            st.takeItems(G_Let_Balthazar);
            st.setCond(5);
        } else if ("30764-06.htm".equalsIgnoreCase(event)) {
            st.takeItems(Bl_Anvil_Coin);
            st.set("Kurtz", 4);
            st.giveItems(Recipe_Spiteful_Soul_Energy);
        }
        // Events Rodemai
        else if ("30868-04.htm".equalsIgnoreCase(event)) {
            st.takeItems(G_Let_Rodemai);
            st.setCond(8);
        } else if ("30868-06a.htm".equalsIgnoreCase(event))
            st.setCond(10);
        else if ("30868-10.htm".equalsIgnoreCase(event))
            st.setCond(12);
            // Events Cleo
        else if (!"30766-04.htm".equalsIgnoreCase(event)) {
            if ("30766-08.htm".equalsIgnoreCase(event)) {
                st.takeItems(Scepter_Judgement);
                exit503(false, st);
            }
        } else {
            st.setCond(9);

            NpcInstance n = st.findTemplate(Cleo);
            if (n != null)
                Functions.npcSay(n, "Blood and Honour");

            n = st.findTemplate(Kalis);
            if (n != null)
                Functions.npcSay(n, "Ambition and Power");

            n = st.findTemplate(Athrea);
            if (n != null)
                Functions.npcSay(n, "War and Death");
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int id = st.getState();

        String htmltext = "noquest";
        boolean isLeader = st.player.isClanLeader();
        if (id == CREATED && npcId == Gustaf) {
            if (st.player.getClan() != null) // has Clan
            {
                if (isLeader) // check if getPlayer is clan leader
                {
                    int clanLevel = st.player.getClan().getLevel();
                    if (st.getQuestItemsCount(Proof_Aspiration) > 0) // if he has the proof
                    // already, tell him
                    // what to do now
                    {
                        htmltext = "30760-03.htm";
                        st.exitCurrentQuest();
                    } else if (clanLevel > 3) // if clanLevel > 3 you can take this quest,
                        // because repeatable
                        htmltext = "30760-04.htm";
                    else
                    // if clanLevel < 4 you cant take it
                    {
                        htmltext = "30760-02.htm";
                        st.exitCurrentQuest();
                    }
                } else
                // getPlayer isnt a leader
                {
                    htmltext = "30760-04t.htm";
                    st.exitCurrentQuest();
                }
            } else
            // no Clan
            {
                htmltext = "30760-01.htm";
                st.exitCurrentQuest();
            }
            return htmltext;
        } else if (st.player.getClan() != null && st.player.getClan().getLevel() == 5) // getPlayer has occupation 5 clan already
            return "completed";
        else
            // ######## Leader Area ######
            if (isLeader) {
                if (st.getCond() == 0)
                    st.setCond(1);
                if (!st.isSet("Kurtz"))
                    st.set("Kurtz");
                if (!st.isSet("Lutz"))
                    st.set("Lutz");
                if (!st.isSet("Fritz"))
                    st.set("Fritz");
                int cond = st.getCond();
                int kurtz = st.getInt("Kurtz");
                int lutz = st.getInt("Lutz");
                int fritz = st.getInt("Fritz");

                if (npcId == Gustaf) {
                    if (cond == 1)
                        htmltext = "30760-09.htm";
                    else if (cond == 2)
                        htmltext = "30760-10.htm";
                    else if (cond == 3)
                        htmltext = "30760-11.htm";
                    else if (cond == 4)
                        htmltext = "30760-13.htm";
                    else if (cond == 5)
                        htmltext = "30760-14.htm";
                    else if (cond == 6)
                        htmltext = "30760-15.htm";
                    else if (cond == 7)
                        htmltext = "30760-17.htm";
                    else if (cond == 12)
                        htmltext = "30760-19.htm";
                    else if (cond == 13)
                        htmltext = "30760-24.htm";
                    else
                        htmltext = "30760-18.htm";
                } else if (npcId == Martien) {
                    if (cond == 1)
                        htmltext = "30645-02.htm";
                    else if (cond == 2)
                        if (checkEggs(st) && kurtz > 1 && lutz > 1 && fritz > 1) {
                            htmltext = "30645-05.htm";
                            st.setCond(3);
                            st.takeItems(EggList);
                        } else
                            htmltext = "30645-04.htm";
                    else if (cond == 3)
                        htmltext = "30645-07.htm";
                    else
                        htmltext = "30645-08.htm";
                } else if (npcId == Lutz && cond == 2)
                    htmltext = "30762-01.htm";
                else if (npcId == Kurtz && cond == 2)
                    htmltext = "30763-01.htm";
                else if (npcId == Fritz && cond == 2)
                    htmltext = "30761-01.htm";
                else if (npcId == Kusto) {
                    if (kurtz == 1)
                        htmltext = "30512-01.htm";
                    else if (kurtz == 2)
                        htmltext = "30512-02.htm";
                    else
                        htmltext = "30512-04.htm";
                } else if (npcId == Balthazar) {
                    if (cond == 4)
                        if (kurtz > 2)
                            htmltext = "30764-04.htm";
                        else
                            htmltext = "30764-02.htm";
                    else if (cond == 5)
                        if (st.haveQuestItem(Spiteful_Soul_Energy, 10)) {
                            htmltext = "30764-08.htm";
                            st.takeItems(Spiteful_Soul_Energy);
                            st.takeItems(Brooch);
                            st.setCond(6);
                        } else
                            htmltext = "30764-07.htm";
                    else if (cond == 6)
                        htmltext = "30764-09.htm";
                } else if (npcId == Rodemai) {
                    if (cond == 7)
                        htmltext = "30868-02.htm";
                    else if (cond == 8)
                        htmltext = "30868-05.htm";
                    else if (cond == 9)
                        htmltext = "30868-06.htm";
                    else if (cond == 10)
                        htmltext = "30868-08.htm";
                    else if (cond == 11)
                        htmltext = "30868-09.htm";
                    else if (cond == 12)
                        htmltext = "30868-11.htm";
                } else if (npcId == Cleo) {
                    if (cond == 8)
                        htmltext = "30766-02.htm";
                    else if (cond == 9)
                        htmltext = "30766-05.htm";
                    else if (cond == 10)
                        htmltext = "30766-06.htm";
                    else if (cond == 11 || cond == 12 || cond == 13)
                        htmltext = "30766-07.htm";
                } else if (npcId == Coffer) {
                    if (st.getCond() == 10)
                        if (st.getQuestItemsCount(Imp_Keys) < 6)
                            htmltext = "30765-03a.htm";
                        else if (st.getInt("ImpGraveKeeper") == 3) {
                            htmltext = "30765-02.htm";
                            st.setCond(11);
                            st.takeItems(Imp_Keys, 6);
                            st.giveItems(Scepter_Judgement);
                        } else
                            htmltext = "<html><head><body>(You and your Clan didn't kill the Imperial Gravekeeper by your own, do it try again.)</body></html>";
                    else
                        htmltext = "<html><head><body>(You already have the Scepter of Judgement.)</body></html>";
                } else if (npcId == Kalis)
                    htmltext = "30759-01.htm";
                else if (npcId == Athrea)
                    htmltext = "30758-01.htm";
                return htmltext;
            }
            // ######## Member Area ######
            else {
                int cond = getLeaderVar(st, "cond");
                if (npcId == Martien && (cond == 1 || cond == 2 || cond == 3))
                    htmltext = "30645-01.htm";
                else if (npcId == Rodemai) {
                    if (cond == 9 || cond == 10)
                        htmltext = "30868-07.htm";
                    else if (cond == 7)
                        htmltext = "30868-01.htm";
                } else if (npcId == Balthazar && cond == 4)
                    htmltext = "30764-01.htm";
                else if (npcId == Cleo && cond == 8)
                    htmltext = "30766-01.htm";
                else if (npcId == Kusto && cond > 2 && cond < 6)
                    htmltext = "30512-01a.htm";
                else if (npcId == Coffer && cond == 10)
                    htmltext = "30765-01.htm";
                else if (npcId == Gustaf)
                    if (cond == 3)
                        htmltext = "30760-11t.htm";
                    else if (cond == 4)
                        htmltext = "30760-15t.htm";
                    else if (cond == 12)
                        htmltext = "30760-19t.htm";
                    else if (cond == 13)
                        htmltext = "30766-24t.htm";
                return htmltext;
            }
    }

    @Override
    public void onAttack(NpcInstance npc, QuestState st) {
        if (npc.getMaxHp() / 2 > npc.getCurrentHp())
            if (Rnd.chance(4)) {
                int ImpGraveKepperStat = getLeaderVar(st, "ImpGraveKeeper");
                if (ImpGraveKepperStat == 1) {
                    for (int i = 1; i <= 4; i++)
                        st.addSpawn(27180, 120000);
                    setLeaderVar(st, 2);
                } else {
                    World.getAroundPlayers(npc, 900, 200)
                            .findAny().ifPresent(p -> p.teleToLocation(185462, 20342, -3250));
                }
            }
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = getLeaderVar(st, "cond");
        switch (cond) {
            case 2:
                switch (npcId) {
                    case ThunderWyrm1:
                        if (Rnd.chance(20))
                            giveItem(Th_Wyrm_Eggs, 10, st);
                        break;

                    case ThunderWyrm2:
                        if (Rnd.chance(15))
                            giveItem(Th_Wyrm_Eggs, 10, st);
                        break;
                    case Drake1:
                        if (Rnd.chance(20))
                            giveItem(Drake_Eggs, 10, st);
                        break;
                    case Drake2:
                        if (Rnd.chance(25))
                            giveItem(Drake_Eggs, 10, st);
                        break;
                    case BlitzWyrm:
                        giveItem(Bl_Wyrm_Eggs, 10, st);
                        break;
                }
                break;
            case 5:
                if (npcId == SpitefulSoulLeader && Rnd.chance(25))
                    if (Rnd.chance(50)) {
                        if (getLeaderVar(st, "Kurtz") < 4)
                            return;
                        giveItem(Spiteful_Soul_Vengeance, 40, st);
                    } else
                        giveItem(Spiteful_Soul_Energy, 10, st);
                break;
            case 10:
                switch (npcId) {
                    case GraveGuard:
                        if (Rnd.chance(15))
                            st.addSpawn(GraveKeymaster, 120000);
                        break;
                    case GraveKeymaster:
                        if (Rnd.chance(80))
                            giveItem(Imp_Keys, 6, st);
                        break;
                    case ImperialGravekeeper:
                        NpcInstance spawnedNpc = st.addSpawn(Coffer, 120000);
                        Functions.npcSay(spawnedNpc, "Curse of the gods on the one that defiles the property of the empire!");
                        setLeaderVar(st, 3);
                        break;
                }
                break;
        }
    }
}