package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _105_SkirmishWithOrcs extends Quest {
    //NPC
    private static final int Kendell = 30218;
    //QuestItem
    private static final int Kendells1stOrder = 1836;
    private static final int Kendells2stOrder = 1837;
    private static final int Kendells3stOrder = 1838;
    private static final int Kendells4stOrder = 1839;
    private static final int Kendells5stOrder = 1840;
    private static final int Kendells6stOrder = 1841;
    private static final int Kendells7stOrder = 1842;
    private static final int Kendells8stOrder = 1843;
    private static final int KabooChiefs1stTorque = 1844;
    private static final int KabooChiefs2stTorque = 1845;
    private static final int RED_SUNSET_SWORD = 981;
    private static final int RED_SUNSET_STAFF = 754;
    //Item
    //NPC
    private static final int KabooChiefUoph = 27059;
    private static final int KabooChiefKracha = 27060;
    private static final int KabooChiefBatoh = 27061;
    private static final int KabooChiefTanukia = 27062;
    private static final int KabooChiefTurel = 27064;
    private static final int KabooChiefRoko = 27065;
    private static final int KabooChiefKamut = 27067;
    private static final int KabooChiefMurtika = 27068;

    public _105_SkirmishWithOrcs() {
        addStartNpc(Kendell);

        addKillId(KabooChiefUoph,KabooChiefKracha,KabooChiefBatoh,KabooChiefTanukia,KabooChiefTurel,
                KabooChiefRoko,KabooChiefKamut,KabooChiefMurtika);

        addQuestItem(Kendells1stOrder,
                Kendells2stOrder,
                Kendells3stOrder,
                Kendells4stOrder,
                Kendells5stOrder,
                Kendells6stOrder,
                Kendells7stOrder,
                Kendells8stOrder,
                KabooChiefs1stTorque,
                KabooChiefs2stTorque);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("sentinel_kendnell_q0105_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            if (!st.haveAnyQuestItems(Kendells1stOrder,Kendells2stOrder,Kendells3stOrder,Kendells4stOrder)) {
                int n = Rnd.get(4);
                switch (n) {
                    case 0:
                        st.giveItems(Kendells1stOrder);
                        break;
                    case 1:
                        st.giveItems(Kendells2stOrder);
                        break;
                    case 2:
                        st.giveItems(Kendells3stOrder);
                        break;
                    default:
                        st.giveItems(Kendells4stOrder);
                        break;
                }
            }
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getRace() != Race.elf) {
                htmltext = "sentinel_kendnell_q0105_00.htm";
                st.exitCurrentQuest();
            } else if (st.player.getLevel() < 10) {
                htmltext = "sentinel_kendnell_q0105_10.htm";
                st.exitCurrentQuest();
            } else
                htmltext = "sentinel_kendnell_q0105_02.htm";
        } else if (cond == 1 && st.haveAnyQuestItems(Kendells1stOrder,Kendells2stOrder,Kendells3stOrder,Kendells4stOrder))
            htmltext = "sentinel_kendnell_q0105_05.htm";
        else if (cond == 2 && st.haveQuestItem(KabooChiefs1stTorque) ) {
            htmltext = "sentinel_kendnell_q0105_06.htm";
            st.takeAllItems(Kendells1stOrder,Kendells2stOrder,Kendells3stOrder,Kendells4stOrder);
            st.takeItems(KabooChiefs1stTorque);
            int n = Rnd.get(4);
            if (n == 0)
                st.giveItems(Kendells5stOrder);
            else if (n == 1)
                st.giveItems(Kendells6stOrder);
            else if (n == 2)
                st.giveItems(Kendells7stOrder);
            else
                st.giveItems(Kendells8stOrder);
            st.setCond(3);
            st.start();
        } else if (cond == 3 && st.haveAnyQuestItems(Kendells5stOrder,Kendells6stOrder,Kendells7stOrder,Kendells8stOrder))
            htmltext = "sentinel_kendnell_q0105_07.htm";
        else if (cond == 4 && st.haveQuestItem(KabooChiefs2stTorque) ) {
            htmltext = "sentinel_kendnell_q0105_08.htm";
            st.takeAllItems(Kendells5stOrder,Kendells6stOrder,Kendells7stOrder,Kendells8stOrder,KabooChiefs2stTorque);

            if (st.player.getClassId().isMage())
                st.giveItems(RED_SUNSET_STAFF);
            else
                st.giveItems(RED_SUNSET_SWORD);

            st.giveAdena( 17599);
            st.player.addExpAndSp(41478, 3555);

            if (st.player.getClassId().occupation() == 0 ) {
                st.player.setVar("p1q3"); // flag for helper
                st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
                st.giveItems(1060, 100); // healing potion
                for (int item = 4412; item <= 4417; item++)
                    st.giveItems(item, 10); // echo cry
                if (st.player.getClassId().isMage()) {
                    st.playTutorialVoice("tutorial_voice_027");
                    st.giveItems(5790, 3000); // newbie sps
                } else {
                    st.playTutorialVoice("tutorial_voice_026");
                    st.giveItems(5789, 6000); // newbie ss
                }
            }

            st.finish();
            st.playSound(SOUND_FINISH);
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && st.getQuestItemsCount(KabooChiefs1stTorque) == 0) {
            if (npcId == KabooChiefUoph && st.haveQuestItem(Kendells1stOrder) )
                st.giveItems(KabooChiefs1stTorque);
            else if (npcId == KabooChiefKracha && st.haveQuestItem(Kendells2stOrder) )
                st.giveItems(KabooChiefs1stTorque);
            else if (npcId == KabooChiefBatoh && st.haveQuestItem(Kendells3stOrder))
                st.giveItems(KabooChiefs1stTorque);
            else if (npcId == KabooChiefTanukia && st.haveQuestItem(Kendells4stOrder) )
                st.giveItems(KabooChiefs1stTorque);
            if (st.haveQuestItem(KabooChiefs1stTorque)) {
                st.setCond(2);
                st.start();
                st.playSound(SOUND_MIDDLE);
            }
        } else if (cond == 3 && !st.haveQuestItem(KabooChiefs2stTorque) ) {
            if (npcId == KabooChiefTurel && st.haveQuestItem(Kendells5stOrder) )
                st.giveItems(KabooChiefs2stTorque);
            else if (npcId == KabooChiefRoko && st.haveQuestItem(Kendells6stOrder) )
                st.giveItems(KabooChiefs2stTorque);
            else if (npcId == KabooChiefKamut && st.haveQuestItem(Kendells7stOrder) )
                st.giveItems(KabooChiefs2stTorque);
            else if (npcId == KabooChiefMurtika && st.haveQuestItem(Kendells8stOrder) )
                st.giveItems(KabooChiefs2stTorque);
            if (st.haveQuestItem(KabooChiefs2stTorque)) {
                st.setCond(4);
                st.start();
                st.playSound(SOUND_MIDDLE);
            }
        }
    }
}
