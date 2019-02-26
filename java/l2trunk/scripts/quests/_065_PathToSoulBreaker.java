package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;

public final class _065_PathToSoulBreaker extends Quest {
    private static final int Vitus = 32213;
    private static final int Kekropus = 32138;
    private static final int Casca = 32139;
    private static final int Holst = 32199;
    private static final int Harlan = 30074;
    private static final int Jacob = 30073;
    private static final int Lucas = 30071;
    private static final int Xaber = 30075;
    private static final int Liam = 30076; //(listto)
    private static final int Vesa = 30123;
    private static final int Zerom = 30124;
    private static final int Felton = 30879;
    private static final int Meldina = 32214;
    private static final int Katenar = 32332;
    private static final int Box = 32243;
    private static final int Guardian_Angel = 27332;
    private static final int Wyrm = 20176;

    private static final int DD = 7562;
    private static final int Sealed_Doc = 9803;
    private static final int Wyrm_Heart = 9804;
    private static final int Kekropus_Rec = 9805;
    private static final int SB_Certificate = 9806;
    private NpcInstance Katenar_Spawn;
    private NpcInstance Guardian_Angel_Spawn;

    public _065_PathToSoulBreaker() {
        super(false);

        addStartNpc(Vitus);

        addTalkId(Vitus,Kekropus,Casca,Holst,Harlan,Lucas,Jacob,Xaber,Liam,Vesa,Zerom,Felton,Meldina,Katenar,Box);
        addKillId(Guardian_Angel,Wyrm);
    }

    private void Despawn_Katenar() {
        if (Katenar_Spawn != null)
            Katenar_Spawn.deleteMe();
        Katenar_Spawn = null;
    }

    private void spawnKatenar(QuestState st) {
        Katenar_Spawn = NpcUtils.spawnSingle(Katenar,Location.findPointToStay(st.player, 50, 100) );
    }

    private void despawnGuardianAngel() {
        if (Guardian_Angel_Spawn != null)
            Guardian_Angel_Spawn.deleteMe();
        Guardian_Angel_Spawn = null;
    }

    private void Spawn_Guardian_Angel(QuestState st) {
        Guardian_Angel_Spawn = NpcUtils.spawnSingle(Guardian_Angel,Location.findPointToStay(st.player, 50, 100));
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("32213-02.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            if (!st.player.isVarSet("dd1")) {
                st.giveItems(DD, 47);
                st.player.setVar("dd1");
            }
            st.playSound(SOUND_ACCEPT);
        }
        if ("32138-03.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.start();
        }
        if ("32139-01.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.start();
        }
        if ("32139-03.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.start();
        }
        if ("32199-01.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.start();
        }
        if ("30071-01.htm".equalsIgnoreCase(event)) {
            st.setCond(8);
            st.start();
        }
        if ("32214-01.htm".equalsIgnoreCase(event)) {
            st.setCond(11);
            st.start();
        }
        if ("30879-02.htm".equalsIgnoreCase(event)) {
            st.setCond(12);
            st.start();
        }
        if ("32332-01.htm".equalsIgnoreCase(event)) {
            st.cancelQuestTimer("Katenar_Fail");
            st.giveItems(Sealed_Doc);
            st.setCond(13);
            st.unset("id");
            st.start();
            Despawn_Katenar();
        }
        if ("32139-06.htm".equalsIgnoreCase(event)) {
            st.takeItems(Sealed_Doc);
            st.setCond(14);
            st.start();
        }
        if (event.equalsIgnoreCase("32138-05.htm")) {
            st.setCond(15);
            st.start();
        }
        if ("32138-09.htm".equalsIgnoreCase(event)) {
            st.takeItems(Wyrm_Heart, 10);
            st.giveItems(Kekropus_Rec);
            st.setCond(17);
            st.start();
        }
        if ("Guardian_Angel_Fail".equalsIgnoreCase(event)) {
            despawnGuardianAngel();
            htmltext = null;
        }
        if ("Katenar_Fail".equalsIgnoreCase(event)) {
            Despawn_Katenar();
            htmltext = null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Vitus) {
            if (st.haveQuestItem(SB_Certificate) ) {
                htmltext = "completed";
                st.exitCurrentQuest();
            } else if (cond == 0)
                if (st.player.getClassId().id == 0x7e || st.player.getClassId().id == 0x7d) {
                    if (st.player.getLevel() >= 39)
                        htmltext = "32213.htm";
                    else {
                        htmltext = "32213-00a.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "32213-000.htm";
                    st.exitCurrentQuest();
                }
            else if (cond == 17) {
                htmltext = "32213-03.htm";
                st.takeItems(Kekropus_Rec, 1);
                if (!st.player.isVarSet("prof2.1")) {
                    st.addExpAndSp(196875, 13510);
                    st.giveItems(ADENA_ID, 35597);
                    st.player.setVar("prof2.1");
                }
                st.giveItems(SB_Certificate);
                st.exitCurrentQuest();
            }
        } else if (npcId == Kekropus) {
            if (cond == 1)
                htmltext = "32138.htm";
            if (cond == 14)
                htmltext = "32138-04.htm";
            if (cond == 16)
                htmltext = "32138-06.htm";
        } else if (npcId == Casca) {
            if (cond == 2)
                htmltext = "32139.htm";
            if (cond == 3)
                htmltext = "32139-02.htm";
            if (cond == 13)
                htmltext = "32139-04.htm";
        } else if (npcId == Holst) {
            if (cond == 4)
                htmltext = "32199.htm";
            if (cond == 5) {
                st.setCond(6);
                htmltext = "32199-02.htm";
            }
        } else if (npcId == Harlan) {
            if (cond == 6)
                htmltext = "30074.htm";
        } else if (npcId == Jacob) {
            if (cond == 6) {
                htmltext = "30073.htm";
                st.setCond(7);
                st.start();
            }
        } else if (npcId == Lucas) {
            if (cond == 7)
                htmltext = "30071.htm";
        } else if (npcId == Xaber) {
            if (cond == 8)
                htmltext = "30075.htm";
        } else if (npcId == Liam) {
            if (cond == 8) {
                htmltext = "30076.htm";
                st.setCond(9);
                st.start();
            }
        } else if (npcId == Zerom) {
            if (cond == 9)
                htmltext = "30124.htm";
        } else if (npcId == Vesa) {
            if (cond == 9) {
                htmltext = "30123.htm";
                st.setCond(10);
                st.start();
            }
        } else if (npcId == Meldina) {
            if (cond == 10)
                htmltext = "32214.htm";
        } else if (npcId == Box) {
            if (cond == 12) {
                htmltext = "32243-01.htm";
                if (World.getAroundPlayers(st.player).anyMatch(cha -> cha.getRace() == Race.kamael))
                    htmltext = "32243-02.htm";

                if (!htmltext.equals("32243-02.htm")) {
                    despawnGuardianAngel();

                    st.set("id", 0);
                    Spawn_Guardian_Angel(st);
                    st.startQuestTimer("Guardian_Angel_Fail", 120000);
                    // Натравим ангела
                    if (Guardian_Angel_Spawn != null) {
                        Guardian_Angel_Spawn.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.player, 1);
                    }
                }
            } else
                htmltext = "32243.htm";
        } else if (npcId == Felton) {
            if (cond == 11)
                htmltext = "30879.htm";
            if (cond == 12)
                htmltext = "30879.htm";
        } else if (npcId == Katenar && st.isSet("id"))
            if (cond == 12)
                htmltext = "32332.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Guardian_Angel) {
            st.cancelQuestTimer("Guardian_Angel_Fail");

            despawnGuardianAngel();

            if (cond == 12) {
                if (World.getAroundPlayers(st.player)
                        .anyMatch(cha -> cha.getRace() == Race.kamael))
                    return;
                Despawn_Katenar();

                st.set("id");
                spawnKatenar(st);
                st.startQuestTimer("Katenar_Fail", 120000);
                if (Katenar_Spawn != null)
                    Functions.npcSay(Katenar_Spawn, "I am late!");
            }
        }
        if (cond == 15 && npcId == Wyrm && Rnd.chance(40)) {
            st.giveItems(Wyrm_Heart);
            if (st.getQuestItemsCount(Wyrm_Heart) < 10)
                st.playSound(SOUND_ITEMGET);
            else {
                st.playSound(SOUND_MIDDLE);
                st.start();
                st.setCond(16);
            }
        }
    }
}