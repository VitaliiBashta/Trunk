package l2trunk.scripts.quests;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;

import java.util.HashMap;
import java.util.Map;

public final class _999_T1Tutorial extends Quest {
    private static final int RECOMMENDATION_01 = 1067;
    private static final int RECOMMENDATION_02 = 1068;
    private static final int LEAF_OF_MOTHERTREE = 1069;
    private static final int BLOOD_OF_JUNDIN = 1070;
    private static final int LICENSE_OF_MINER = 1498;
    private static final int VOUCHER_OF_FLAME = 1496;
    private static final int SOULSHOT_NOVICE = 5789;
    private static final int SPIRITSHOT_NOVICE = 5790;
    private static final int BLUE_GEM = 6353;
    private static final int DIPLOMA = 9881;
    private static final Map<String, Event> events = new HashMap<>();
    private static final Map<Integer, Talk> talks = new HashMap<>();

    static {
        events.put("32133_02", new Event("32133-03.htm", Location.of(-119692, 44504, 380), DIPLOMA, 0x7b, SOULSHOT_NOVICE, 200, 0x7c, SOULSHOT_NOVICE, 200));
        events.put("30008_02", new Event("30008-03.htm", null, RECOMMENDATION_01, 0x00, SOULSHOT_NOVICE, 200, 0x00, 0, 0));
        events.put("30008_04", new Event("30008-04.htm", Location.of(-84081, 243277, -3723), 0, 0x00, 0, 0, 0, 0, 0));
        events.put("30017_02", new Event("30017-03.htm", null, RECOMMENDATION_02, 0x0a, SPIRITSHOT_NOVICE, 100, 0x00, 0, 0));
        events.put("30017_04", new Event("30017-04.htm", Location.of(-84081, 243277, -3723), 0, 0x0a, 0, 0, 0x00, 0, 0));
        events.put("30370_02", new Event("30370-03.htm", null, LEAF_OF_MOTHERTREE, 0x19, SPIRITSHOT_NOVICE, 100, 0x12, SOULSHOT_NOVICE, 200));
        events.put("30370_04", new Event("30370-04.htm", Location.of(45491, 48359, -3086), 0, 0x19, 0, 0, 0x12, 0, 0));
        events.put("30129_02", new Event("30129-03.htm", null, BLOOD_OF_JUNDIN, 0x26, SPIRITSHOT_NOVICE, 100, 0x1f, SOULSHOT_NOVICE, 200));
        events.put("30129_04", new Event("30129-04.htm", Location.of(12116, 16666, -4610), 0, 0x26, 0, 0, 0x1f, 0, 0));
        events.put("30528_02", new Event("30528-03.htm", null, LICENSE_OF_MINER, 0x35, SOULSHOT_NOVICE, 200, 0x00, 0, 0));
        events.put("30528_04", new Event("30528-04.htm", Location.of(115642, -178046, -941), 0, 0x35, 0, 0, 0x00, 0, 0));
        events.put("30573_02", new Event("30573-03.htm", null, VOUCHER_OF_FLAME, 0x31, SPIRITSHOT_NOVICE, 100, 0x2c, SOULSHOT_NOVICE, 200));
        events.put("30573_04", new Event("30573-04.htm", Location.of(-45067, -113549, -235), 0, 0x31, 0, 0, 0x2c, 0, 0));
    }

    static {
        talks.put(30017, new Talk(0, new String[]{
                "30017-01.htm",
                "30017-02.htm",
                "30017-04.htm"
        }, 0, 0));
        talks.put(30008, new Talk(0, new String[]{
                "30008-01.htm",
                "30008-02.htm",
                "30008-04.htm"
        }, 0, 0));
        talks.put(30370, new Talk(1, new String[]{
                "30370-01.htm",
                "30370-02.htm",
                "30370-04.htm"
        }, 0, 0));
        talks.put(30129, new Talk(2, new String[]{
                "30129-01.htm",
                "30129-02.htm",
                "30129-04.htm"
        }, 0, 0));
        talks.put(30573, new Talk(3, new String[]{
                "30573-01.htm",
                "30573-02.htm",
                "30573-04.htm"
        }, 0, 0));
        talks.put(30528, new Talk(4, new String[]{
                "30528-01.htm",
                "30528-02.htm",
                "30528-04.htm"
        }, 0, 0));
        talks.put(30018, new Talk(0, new String[]{
                "30131-01.htm",
                "",
                "30019-03a.htm",
                "30019-04.htm",
        }, 1, RECOMMENDATION_02));
        talks.put(30019, new Talk(0, new String[]{
                "30131-01.htm",
                "",
                "30019-03a.htm",
                "30019-04.htm",
        }, 1, RECOMMENDATION_02));
        talks.put(30020, new Talk(0, new String[]{
                "30131-01.htm",
                "",
                "30019-03a.htm",
                "30019-04.htm",
        }, 1, RECOMMENDATION_02));
        talks.put(30021, new Talk(0, new String[]{
                "30131-01.htm",
                "",
                "30019-03a.htm",
                "30019-04.htm",
        }, 1, RECOMMENDATION_02));
        talks.put(30009, new Talk(0, new String[]{
                "30530-01.htm",
                "30009-03.htm",
                "",
                "30009-04.htm",
        }, 1, RECOMMENDATION_01));
        talks.put(30011, new Talk(0, new String[]{
                "30530-01.htm",
                "30009-03.htm",
                "",
                "30009-04.htm",
        }, 1, RECOMMENDATION_01));
        talks.put(30012, new Talk(0, new String[]{
                "30530-01.htm",
                "30009-03.htm",
                "",
                "30009-04.htm",
        }, 1, RECOMMENDATION_01));
        talks.put(30056, new Talk(0, new String[]{
                "30530-01.htm",
                "30009-03.htm",
                "",
                "30009-04.htm",
        }, 1, RECOMMENDATION_01));
        talks.put(30400, new Talk(1, new String[]{
                "30131-01.htm",
                "30400-03.htm",
                "30400-03a.htm",
                "30400-04.htm",
        }, 1, LEAF_OF_MOTHERTREE));
        talks.put(30401, new Talk(1, new String[]{
                "30131-01.htm",
                "30400-03.htm",
                "30400-03a.htm",
                "30400-04.htm",
        }, 1, LEAF_OF_MOTHERTREE));
        talks.put(30402, new Talk(1, new String[]{
                "30131-01.htm",
                "30400-03.htm",
                "30400-03a.htm",
                "30400-04.htm",
        }, 1, LEAF_OF_MOTHERTREE));
        talks.put(30403, new Talk(1, new String[]{
                "30131-01.htm",
                "30400-03.htm",
                "30400-03a.htm",
                "30400-04.htm",
        }, 1, LEAF_OF_MOTHERTREE));
        talks.put(30131, new Talk(2, new String[]{
                "30131-01.htm",
                "30131-03.htm",
                "30131-03a.htm",
                "30131-04.htm",
        }, 1, BLOOD_OF_JUNDIN));
        talks.put(30404, new Talk(2, new String[]{
                "30131-01.htm",
                "30131-03.htm",
                "30131-03a.htm",
                "30131-04.htm",
        }, 1, BLOOD_OF_JUNDIN));
        talks.put(30574, new Talk(3, new String[]{
                "30575-01.htm",
                "30575-03.htm",
                "30575-03a.htm",
                "30575-04.htm",
        }, 1, VOUCHER_OF_FLAME));
        talks.put(30575, new Talk(3, new String[]{
                "30575-01.htm",
                "30575-03.htm",
                "30575-03a.htm",
                "30575-04.htm",
        }, 1, VOUCHER_OF_FLAME));
        talks.put(30530, new Talk(4, new String[]{
                "30530-01.htm",
                "30530-03.htm",
                "",
                "30530-04.htm",
        }, 1, LICENSE_OF_MINER));
        talks.put(32133, new Talk(5, new String[]{
                "32133-01.htm",
                "32133-02.htm",
                "32133-04.htm"
        }, 0, 0));
        talks.put(32134, new Talk(5, new String[]{
                "32134-01.htm",
                "32134-03.htm",
                "",
                "32134-04.htm",
        }, 1, DIPLOMA));
    }

    public _999_T1Tutorial() {
        addStartNpc(30008, 30009, 30017, 30019, 30129, 30131, 30573, 30575, 30370, 30528, 30530, 30400, 30401, 30402, 30403, 30404, 32133, 32134);
        addFirstTalkId(30008, 30009, 30017, 30019, 30129, 30131, 30573, 30575, 30370, 30528, 30530, 30400, 30401, 30402, 30403, 30404, 32133, 32134);

        addKillId(18342, 20001);
    }

    @Override
    public String onEvent(String event, final QuestState st, NpcInstance npc) {
        QuestState qs = st.player.getQuestState(_255_Tutorial.class);
        if (qs == null)
            return null;

        final Player player = st.player;

        String htmltext;
        int Ex = qs.getInt("Ex");
        int classId = player.getClassId().id;
        boolean isMage = (player.getClassId().race != Race.orc) && player.getClassId().isMage();
        if ("TimerEx_NewbieHelper".equalsIgnoreCase(event)) {
            if (Ex == 0) {
                if (isMage)
                    st.playTutorialVoice("tutorial_voice_009b");
                else
                    st.playTutorialVoice("tutorial_voice_009a");
                qs.set("Ex");
            } else if (Ex == 3) {
                st.playTutorialVoice("tutorial_voice_010a");
                qs.set("Ex", 4);
            }
            return null;
        } else if ("TimerEx_GrandMaster".equalsIgnoreCase(event)) {
            if (Ex >= 4) {
                st.showQuestionMark(7);
                st.playSound(SOUND_TUTORIAL);
                st.playTutorialVoice("tutorial_voice_025");
            }
            return null;
        } else if ("isle".equalsIgnoreCase(event)) {
            st.addRadar(Location.of(-119692, 44504, 380));
            player.teleToLocation(-120050, 44500, 360);
            String title = npc == null ? "" : npc.getTitle() + " " + npc.getName();
            htmltext = "<html><body>" + title + "<br>Go to the <font color=\"LEVEL\">Isle of Souls</font> and meet the <font color=\"LEVEL\">Newbie Guide</font> there to learn a number of important tips. He will also give you an item to assist your development.<br>Follow the direction arrow above your head and it will lead you to the Newbie Guide. Good luck!</body></html>";
        } else {
            final Event e = events.get(event);
            htmltext = e.htm;
            if (st.haveQuestItem(e.item) && !st.isSet("onlyone")) {
                st.addExpAndSp(0, 50);
                st.startQuestTimer("TimerEx_GrandMaster", 60000);
                st.takeItems(e.item, 1);
                if (Ex <= 3)
                    qs.set("Ex", 4);
                if (classId == e.classId1) {
                    st.giveItems(e.gift1, e.count1);
                    if (e.gift1 == SPIRITSHOT_NOVICE)
                        st.playTutorialVoice("tutorial_voice_027");
                    else
                        st.playTutorialVoice("tutorial_voice_026");
                } else if (classId == e.classId2) {
                    if (e.gift2 != 0) {
                        st.giveItems(e.gift2, e.count2);
                        st.playTutorialVoice("tutorial_voice_026");
                    }
                }
                st.set("step", 3);
                st.set("onlyone");
            }

            if (e.radarLoc != null) {
                ThreadPoolManager.INSTANCE.schedule(() -> st.addRadarWithMap(e.radarLoc), 100L);
            }
        }
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        String htmltext = "";
        QuestState qs = player.getQuestState(_255_Tutorial.class);
        if (qs == null)
            return htmltext;

        QuestState st = player.getQuestState(this);
        if (st == null) {
            newQuestState(player, CREATED);
            st = player.getQuestState(this);
        }

        int Ex = qs.getInt("Ex");
        int npcId = npc.getNpcId();
        int step = st.getInt("step");
        boolean onlyone = st.isSet("onlyone");
        int level = player.getLevel();
        boolean isMage = (player.getClassId().race != Race.orc) && player.getClassId().isMage();

        Talk t = talks.get(npcId);
        if (t == null)
            return "";

        if ((level >= 10 || onlyone) && t.npcTyp == 1)
            htmltext = "30575-05.htm";
        else if (!onlyone && level < 10) {
            if (player.getRace().ordinal() == t.raceId)
                htmltext = t.htmlfiles[0];
            if (t.npcTyp == 1) {
                if (step == 0 && Ex < 0) {
                    qs.unset("Ex");
                    st.startQuestTimer("TimerEx_NewbieHelper", 30000);
                    if (isMage) {
                        st.set("step");
                        st.start();
                    } else {
                        htmltext = "30530-01.htm";
                        st.set("step");
                        st.start();
                    }
                } else if (step == 1 && st.getQuestItemsCount(t.item) == 0 && Ex <= 2) {
                    if (st.haveQuestItem(BLUE_GEM)) {
                        st.takeItems(BLUE_GEM);
                        st.giveItems(t.item);
                        st.set("step", 2);
                        qs.set("Ex", 3);
                        st.startQuestTimer("TimerEx_NewbieHelper", 30000);
                        qs.set("ucMemo", 3);
                        if (isMage) {
                            st.playTutorialVoice("tutorial_voice_027");
                            st.giveItems(SPIRITSHOT_NOVICE, 100);
                            htmltext = t.htmlfiles[2];
                            if (htmltext.isEmpty())
                                htmltext = "<html><body>" + (npc.getTitle().isEmpty() ? "" : npc.getTitle() + " ") + npc.getName() + "<br>I am sorry. I only help warriors. Please go to another Newbie Helper who may assist you.</body></html>";
                        } else {
                            st.playTutorialVoice("tutorial_voice_026");
                            st.giveItems(SOULSHOT_NOVICE, 200);
                            htmltext = t.htmlfiles[1];
                            if (htmltext.isEmpty())
                                htmltext = "<html><body>" + (npc.getTitle().isEmpty() ? "" : npc.getTitle() + " ") + npc.getName() + "<br>I am sorry. I only help mystics. Please go to another Newbie Helper who may assist you.</body></html>";
                        }
                    } else {
                        if (isMage) {
                            htmltext = "30131-02.htm";
                            if (player.getRace().ordinal() == 3)
                                htmltext = "30575-02.htm";
                        } else
                            htmltext = "30530-02.htm";
                    }
                } else if (step == 2)
                    htmltext = t.htmlfiles[3];
            } else if (t.npcTyp == 0) {
                if (step == 1)
                    htmltext = t.htmlfiles[0];
                else if (step == 2)
                    htmltext = t.htmlfiles[1];
                else if (step == 3)
                    htmltext = t.htmlfiles[2];
            }
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        QuestState qs = st.player.getQuestState(_255_Tutorial.class);
        if (qs == null)
            return;
        int Ex = qs.getInt("Ex");
        if (Ex <= 1) {
            st.playTutorialVoice("tutorial_voice_011");
            st.showQuestionMark(3);
            qs.set("Ex", 2);
        }
        if (Ex <= 2 && !st.haveQuestItem(BLUE_GEM))
            ThreadPoolManager.INSTANCE.schedule(new DropGem(npc, st), 3000);
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    private static class Event {
        final String htm;
        final Location radarLoc;
        final int item;
        final int classId1;
        final int gift1;
        final int count1;
        final int classId2;
        final int gift2;
        final int count2;

        Event(String htm, Location radarLoc, int item, int classId1, int gift1, int count1, int classId2, int gift2, int count2) {
            this.htm = htm;
            this.radarLoc = radarLoc;
            this.item = item;
            this.classId1 = classId1;
            this.gift1 = gift1;
            this.count1 = count1;
            this.classId2 = classId2;
            this.gift2 = gift2;
            this.count2 = count2;
        }
    }

    private static class Talk {
        final int raceId;
        final String[] htmlfiles;
        final int npcTyp;
        final int item;

        Talk(int raceId, String[] htmlfiles, int npcTyp, int item) {
            this.raceId = raceId;
            this.htmlfiles = htmlfiles;
            this.npcTyp = npcTyp;
            this.item = item;
        }
    }

    public static class DropGem extends RunnableImpl {
        private final NpcInstance npc;
        private final QuestState st;

        DropGem(NpcInstance npc, QuestState st) {
            this.npc = npc;
            this.st = st;
        }

        public void runImpl() {
            if (st != null && npc != null) {
                npc.dropItem(st.player, BLUE_GEM, 1);
                st.playSound(SOUND_TUTORIAL);
            }
        }
    }
}