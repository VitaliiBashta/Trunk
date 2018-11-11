package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

public class _062_PathOfTheDragoon extends Quest implements ScriptFile {
    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    private final int Shubain = 32194;
    private final int Gwain = 32197;

    private final int FelimLizardmanWarrior = 20014;
    private final int VenomousSpider = 20038;
    private final int TumranBugbear = 20062;

    private final int FelimHead = 9749;
    private final int VenomousSpiderLeg = 9750;
    private final int TumranBugbearHeart = 9751;
    private final int ShubainsRecommendation = 9752;
    private final int GwainsRecommendation = 9753;

    public _062_PathOfTheDragoon() {
        super(false);

        addStartNpc(Gwain);
        addTalkId(Gwain);
        addTalkId(Shubain);
        addKillId(FelimLizardmanWarrior);
        addKillId(VenomousSpider);
        addKillId(TumranBugbear);
        addQuestItem(FelimHead);
        addQuestItem(VenomousSpiderLeg);
        addQuestItem(ShubainsRecommendation);
        addQuestItem(TumranBugbearHeart);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equals("master_tbwain_q0062_06.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("master_shubain_q0062_02.htm"))
            st.setCond(2);
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (npcId == Gwain) {
            if (id == CREATED) {
                if (st.getPlayer().getClassId() != ClassId.maleSoldier) {
                    htmltext = "master_tbwain_q0062_02.htm";
                    st.exitCurrentQuest(true);
                } else if (st.getPlayer().getLevel() < 18) {
                    htmltext = "master_tbwain_q0062_03.htm";
                    st.exitCurrentQuest(true);
                } else
                    htmltext = "master_tbwain_q0062_01.htm";
            } else if (cond == 4) {
                st.takeItems(ShubainsRecommendation, -1);
                st.setCond(5);
                htmltext = "master_tbwain_q0062_08.htm";
            } else if (cond == 5 && st.getQuestItemsCount(TumranBugbearHeart) > 0) {
                st.takeItems(TumranBugbearHeart, -1);
                if (st.getPlayer().getClassId().getLevel() == 1) {
                    st.giveItems(GwainsRecommendation, 1);
                    if (!st.getPlayer().getVarB("prof1")) {
                        st.getPlayer().setVar("prof1", "1", -1);
                        st.addExpAndSp(160267, 11023);
                        //FIXME [G1ta0] дать адены, только если первый чар на акке
                        st.giveItems(ADENA_ID, 81900);
                    }
                }
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
                htmltext = "master_tbwain_q0062_10.htm";
            }
        } else if (npcId == Shubain)
            if (cond == 1)
                htmltext = "master_shubain_q0062_01.htm";
            else if (cond == 2 && st.getQuestItemsCount(FelimHead) >= 5) {
                st.takeItems(FelimHead, -1);
                st.setCond(3);
                htmltext = "master_shubain_q0062_04.htm";
            } else if (cond == 3 && st.getQuestItemsCount(VenomousSpiderLeg) >= 10) {
                st.takeItems(VenomousSpiderLeg, -1);
                st.giveItems(ShubainsRecommendation, 1);
                st.setCond(4);
                htmltext = "master_shubain_q0062_06.htm";
            }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int id = npc.getNpcId();
        int cond = st.getCond();
        if (id == FelimLizardmanWarrior && cond == 2) {
            long count = st.getQuestItemsCount(FelimHead);
            if (count < 5) {
                st.giveItems(FelimHead, 1);
                if (count == 4)
                    st.playSound(SOUND_MIDDLE);
                else
                    st.playSound(SOUND_ITEMGET);
            }
        }
        if (id == VenomousSpider && cond == 3) {
            long count = st.getQuestItemsCount(VenomousSpiderLeg);
            if (count < 10) {
                st.giveItems(VenomousSpiderLeg, 1);
                if (count == 9)
                    st.playSound(SOUND_MIDDLE);
                else
                    st.playSound(SOUND_ITEMGET);
            }
        }
        if (id == TumranBugbear && cond == 5)
            if (st.getQuestItemsCount(TumranBugbearHeart) == 0) {
                st.giveItems(TumranBugbearHeart, 1);
                st.playSound(SOUND_MIDDLE);
            }
        return null;
    }
}