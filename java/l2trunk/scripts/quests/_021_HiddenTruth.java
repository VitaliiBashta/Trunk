package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;

public final class _021_HiddenTruth extends Quest {

    // ~~~~~~~~ npcId list: ~~~~~~~~
    private static final int MysteriousWizard = 31522;
    private static final int Tombstone = 31523;
    private static final int GhostofvonHellmannId = 31524;
    private static final int GhostofvonHellmannsPageId = 31525;
    private static final int BrokenBookshelf = 31526;
    private static final int Agripel = 31348;
    private static final int Dominic = 31350;
    private static final int Benedict = 31349;
    private static final int Innocentin = 31328;
    // ~~~~~~~~~~~~ END ~~~~~~~~~~~~

    // ~~~~~~~~ itemId list: ~~~~~~~~
    private static final int CrossofEinhasad = 7140;
    private static final int CrossofEinhasadNextQuest = 7141;
    // ~~~~~~~~~~~~ END ~~~~~~~~~~~~~

    private NpcInstance GhostofvonHellmannsPage;
    private NpcInstance GhostofvonHellmann;

    public _021_HiddenTruth() {
        addStartNpc(MysteriousWizard);

        addTalkId(Tombstone,GhostofvonHellmannId,GhostofvonHellmannsPageId,BrokenBookshelf,
                Agripel,Dominic,Benedict,Innocentin);
    }

    private void spawnGhostofvonHellmannsPage() {
        GhostofvonHellmannsPage = NpcUtils.spawnSingle(GhostofvonHellmannsPageId, Location.of(51462, -54539, -3176));
    }

    private void despawnGhostofvonHellmannsPage() {
        if (GhostofvonHellmannsPage != null)
            GhostofvonHellmannsPage.deleteMe();
        GhostofvonHellmannsPage = null;
    }

    private void spawnGhostofvonHellmann() {
        GhostofvonHellmann = NpcUtils.spawnSingle(GhostofvonHellmannId, Location.findPointToStay(Location.of(51432, -54570, -3136), 50, ReflectionManager.DEFAULT.getGeoIndex()));
    }

    private void despawnGhostofvonHellmann() {
        if (GhostofvonHellmann != null)
            GhostofvonHellmann.deleteMe();
        GhostofvonHellmann = null;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("31522-02.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
        } else if ("html".equalsIgnoreCase(event))
            htmltext = "31328-05.htm";
        else if ("31328-05.htm".equalsIgnoreCase(event)) {
            st.unset("cond");
            st.takeItems(CrossofEinhasad);
            st.giveItemIfNotHave(CrossofEinhasadNextQuest);
            st.addExpAndSp(131228, 11978);
            st.playSound(SOUND_FINISH);
            st.startQuestTimer("html", 1);
            htmltext = "Congratulations! You are completed this quest!<br>The Quest \"Tragedy In Von Hellmann Forest\" become available.<br>Show Cross of Einhasad to High Priest Tifaren.";
            st.finish();
        } else if ("31523-03.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_HORROR2);
            st.setCond(2);
            despawnGhostofvonHellmann();
            spawnGhostofvonHellmann();
        } else if ("31524-06.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            despawnGhostofvonHellmannsPage();
            spawnGhostofvonHellmannsPage();
        } else if ("31526-03.htm".equalsIgnoreCase(event))
            st.playSound(SOUND_ITEM_DROP_EQUIP_ARMOR_CLOTH);
        else if ("31526-08.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ED_CHIMES05);
            st.setCond(5);
        } else if ("31526-14.htm".equalsIgnoreCase(event)) {
            st.giveItems(CrossofEinhasad);
            st.setCond(6);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "This person inaccessible and does not want with you to talk!<br>Are they please returned later...";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == MysteriousWizard) {
            if (cond == 0) {
                if (st.player.getLevel() > 54)
                    htmltext = "31522-01.htm";
                else {
                    htmltext = "31522-03.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "31522-05.htm";
        } else if (npcId == Tombstone) {
            if (cond == 1)
                htmltext = "31523-01.htm";
            else if (cond == 2 || cond == 3) {
                htmltext = "31523-04.htm";
                st.playSound(SOUND_HORROR2);
                despawnGhostofvonHellmann();
                spawnGhostofvonHellmann();
            }
        } else if (npcId == GhostofvonHellmannId) {
            if (cond == 2)
                htmltext = "31524-01.htm";
            else if (cond == 3)
                htmltext = "31524-07b.htm";
            else if (cond == 4)
                htmltext = "31524-07c.htm";
        } else if (npcId == GhostofvonHellmannsPageId) {
            if (cond == 3 || cond == 4) {
                htmltext = "31525-01.htm";
                if (GhostofvonHellmannsPage == null || !GhostofvonHellmannsPage.isMoving) {
                    htmltext = "31525-02.htm";
                    if (cond == 3)
                        st.setCond(4);
                    despawnGhostofvonHellmannsPage();
                }
            }
        } else if (npcId == BrokenBookshelf) {
            if (cond == 4 || cond == 3) {
                despawnGhostofvonHellmannsPage();
                despawnGhostofvonHellmann();
                st.setCond(5);
                htmltext = "31526-01.htm";
            } else if (cond == 5) {
                htmltext = "31526-10.htm";
                st.playSound(SOUND_ED_CHIMES05);
            } else if (cond == 6)
                htmltext = "31526-15.htm";
        } else if (npcId == Agripel && st.haveQuestItem(CrossofEinhasad)) {
            if (cond == 6) {
                if (st.isSet("DOMINIC")  && st.isSet("BENEDICT")) {
                    htmltext = "31348-02.htm";
                    st.setCond(7);
                } else {
                    st.set("AGRIPEL");
                    htmltext = "31348-0" + Rnd.get(3) + ".htm";
                }
            } else if (cond == 7)
                htmltext = "31348-03.htm";
        } else if (npcId == Dominic && st.haveQuestItem(CrossofEinhasad)) {
            if (cond == 6) {
                if (st.isSet("AGRIPEL")  && st.isSet("BENEDICT")) {
                    htmltext = "31350-02.htm";
                    st.setCond(7);
                } else {
                    st.set("DOMINIC");
                    htmltext = "31350-0" + Rnd.get(3) + ".htm";
                }
            } else if (cond == 7)
                htmltext = "31350-03.htm";
        } else if (npcId == Benedict && st.haveQuestItem(CrossofEinhasad)) {
            if (cond == 6) {
                if (st.isSet("AGRIPEL")  && st.isSet("DOMINIC")) {
                    htmltext = "31349-02.htm";
                    st.setCond(7);
                } else {
                    st.set("BENEDICT");
                    htmltext = "31349-0" + Rnd.get(3) + ".htm";
                }
            } else if (cond == 7)
                htmltext = "31349-03.htm";
        } else if (npcId == Innocentin)
            if (cond == 7) {
                if (st.haveQuestItem(CrossofEinhasad))
                    htmltext = "31328-01.htm";
            } else if (cond == 0)
                htmltext = "31328-06.htm";
        return htmltext;
    }
}