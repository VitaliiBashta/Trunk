package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;

public final class _064_CertifiedBerserker extends Quest {
    // NPC
    private static final int ORKURUS = 32207;
    private static final int TENAIN = 32215;
    private static final int GORT = 32252;
    private static final int HARKILGAMED = 32236;
    private static final int ENTIEN = 32200;

    // Mobs
    private static final int BREKA_ORC = 20267;
    private static final int BREKA_ORC_ARCHER = 20268;
    private static final int BREKA_ORC_SHAMAN = 20269;
    private static final int BREKA_ORC_OVERLORD = 20270;
    private static final int BREKA_ORC_WARRIOR = 20271;
    private static final int ROAD_SCAVENGER = 20551;
    private static final int DEAD_SEEKER = 20202;
    private static final int STAKATO = 20234;
    private static final int DIVINE = 27323;

    // Quest Item
    private static final int Dimenional_Diamonds = 7562;
    private static final int BREKA_ORC_HEAD = 9754;
    private static final int MESSAGE_PLATE = 9755;
    private static final int REPORT1 = 9756;
    private static final int REPORT2 = 9757;
    private static final int H_LETTER = 9758;
    private static final int T_REC = 9759;
    private static final int OrkurusRecommendation = 9760;

    private NpcInstance HARKILGAMED_SPAWN;

    private void DESPAWN_HARKILGAMED() {
        if (HARKILGAMED_SPAWN != null)
            HARKILGAMED_SPAWN.deleteMe();
        HARKILGAMED_SPAWN = null;
    }

    private void SPAWN_HARKILGAMED(QuestState st) {
        HARKILGAMED_SPAWN = NpcUtils.spawnSingle(HARKILGAMED,Location.findPointToStay(st.player, 50, 100));
    }

    public _064_CertifiedBerserker() {
        super(false);

        addStartNpc(ORKURUS);

        addTalkId(ORKURUS,TENAIN,GORT,ENTIEN,HARKILGAMED);

        addKillId(BREKA_ORC,BREKA_ORC_ARCHER,BREKA_ORC_SHAMAN,BREKA_ORC_OVERLORD,BREKA_ORC_WARRIOR,
                ROAD_SCAVENGER,DEAD_SEEKER,STAKATO,DIVINE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("32207-01a.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            if (!st.player.isVarSet("dd1")) {
                st.giveItems(Dimenional_Diamonds, 48);
                st.player.setVar("dd1");
            }
            st.playSound(SOUND_ACCEPT);
        } else if ("32215-01a.htm".equalsIgnoreCase(event))
            st.setCond(2);
        else if ("32252-01a.htm".equalsIgnoreCase(event))
            st.setCond(5);
        else if ("32215-03d.htm".equalsIgnoreCase(event)) {
            st.takeItems(MESSAGE_PLATE);
            st.setCond(8);
        } else if ("32236-01a.htm".equalsIgnoreCase(event)) {
            st.setCond(13);
            st.giveItems(H_LETTER);
            st.cancelQuestTimer("HARKILGAMED_Fail");
            DESPAWN_HARKILGAMED();
        } else if ("32215-05a.htm".equalsIgnoreCase(event)) {
            st.setCond(14);
            st.takeItems(H_LETTER);
            st.giveItems(T_REC);
        } else if ("32207-03a.htm".equalsIgnoreCase(event)) {
            if (!st.player.isVarSet("prof2.1")) {
                st.addExpAndSp(174503, 11973);
                st.giveItems(ADENA_ID, 31552);
                st.player.setVar("prof2.1");
            }
            st.giveItems(OrkurusRecommendation);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        if ("HARKILGAMED_Fail".equalsIgnoreCase(event)) {
            DESPAWN_HARKILGAMED();
            return null;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == ORKURUS) {
            if (st.getQuestItemsCount(OrkurusRecommendation) != 0) {
                htmltext = "32207-00.htm";
                st.exitCurrentQuest();
            } else if (cond == 0) {
                if (st.player.getClassId().id == 0x7D) {
                    if (st.player.getLevel() >= 39)
                        htmltext = "32207-01.htm";
                    else {
                        htmltext = "32207-02.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "32207-02a.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 14) {
                st.takeItems(T_REC, -1);
                htmltext = "32207-03.htm";
            }

        } else if (npcId == TENAIN) {
            if (cond == 1)
                htmltext = "32215-01.htm";
            else if (cond == 3) {
                htmltext = "32215-02.htm";
                st.takeItems(BREKA_ORC_HEAD);
                st.setCond(4);
            } else if (cond > 1 && st.getQuestItemsCount(BREKA_ORC_HEAD) == 20) {
                htmltext = "32215-02.htm";
                st.takeItems(BREKA_ORC_HEAD);
                st.setCond(4);
            } else if (cond == 7)
                htmltext = "32215-03.htm";
            else if (cond == 11) {
                st.setCond(12);
                htmltext = "32215-04.htm";
            } else if (cond == 13) {
                st.setCond(14);
                htmltext = "32215-05.htm";
            }

        } else if (npcId == GORT) {
            if (cond == 4)
                htmltext = "32252-01.htm";
            else if (cond == 6) {
                htmltext = "32252-02.htm";
                st.setCond(7);
            } else if (cond > 4 && st.haveQuestItem(MESSAGE_PLATE)) {
                htmltext = "32252-02.htm";
                st.setCond(7);
            }
        } else if (npcId == ENTIEN) {
            if (cond == 8) {
                st.setCond(9);
                htmltext = "32200-01.htm";
            } else if (cond == 10) {
                st.setCond(11);
                st.takeItems(REPORT1);
                st.takeItems(REPORT2);
                htmltext = "32200-02.htm";
            }
        } else if (npcId == HARKILGAMED)
            if (cond == 12)
                htmltext = "32236-01.htm";
        return htmltext;

    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 2)
            if (npcId == BREKA_ORC || npcId == BREKA_ORC_ARCHER || npcId == BREKA_ORC_SHAMAN || npcId == BREKA_ORC_OVERLORD || npcId == BREKA_ORC_WARRIOR)
                if (st.getQuestItemsCount(BREKA_ORC_HEAD) <= 19) {
                    st.giveItems(BREKA_ORC_HEAD);
                    if (st.getQuestItemsCount(BREKA_ORC_HEAD) == 20) {
                        st.playSound(SOUND_MIDDLE);
                        st.setCond(3);
                    } else
                        st.playSound(SOUND_ITEMGET);
                }
        if (cond == 5 && npcId == ROAD_SCAVENGER && Rnd.chance(20) && st.getQuestItemsCount(MESSAGE_PLATE) == 0) {
            st.giveItems(MESSAGE_PLATE);
            st.setCond(6);
            st.playSound(SOUND_MIDDLE);
        }
        if (cond == 9 && Rnd.chance(30)) {
            if (npcId == DEAD_SEEKER && st.getQuestItemsCount(REPORT1) == 0)
                st.giveItems(REPORT1);
            else if (npcId == STAKATO && st.getQuestItemsCount(REPORT2) == 0)
                st.giveItems(REPORT2);
            if (st.getQuestItemsCount(REPORT1) == 1 && st.getQuestItemsCount(REPORT2) == 1) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(10);
            } else
                st.playSound(SOUND_ITEMGET);
        }
        if (cond == 12 && npcId == DIVINE && Rnd.chance(35)) {
            DESPAWN_HARKILGAMED();
            SPAWN_HARKILGAMED(st);
            st.playSound(SOUND_MIDDLE);
            if (!st.isRunningQuestTimer("HARKILGAMED_Fail"))
                st.startQuestTimer("HARKILGAMED_Fail", 120000);
        }
    }
}