package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;
import java.util.Map;

public final class _336_CoinOfMagic extends Quest {
    private static final int COIN_DIAGRAM = 3811;
    private static final int KALDIS_COIN = 3812;
    private static final int MEMBERSHIP_1 = 3813;
    private static final int MEMBERSHIP_2 = 3814;
    private static final int MEMBERSHIP_3 = 3815;

    private static final int BLOOD_MEDUSA = 3472;
    private static final int BLOOD_WEREWOLF = 3473;
    private static final int BLOOD_BASILISK = 3474;
    private static final int BLOOD_DREVANUL = 3475;
    private static final int BLOOD_SUCCUBUS = 3476;
    private static final int BLOOD_DRAGON = 3477;
    private static final int BELETHS_BLOOD = 3478;
    private static final int MANAKS_BLOOD_WEREWOLF = 3479;
    private static final int NIAS_BLOOD_MEDUSA = 3480;
    private static final int GOLD_DRAGON = 3481;
    private static final int GOLD_WYVERN = 3482;
    private static final int GOLD_KNIGHT = 3483;
    private static final int GOLD_GIANT = 3484;
    private static final int GOLD_DRAKE = 3485;
    private static final int GOLD_WYRM = 3486;
    private static final int BELETHS_GOLD = 3487;
    private static final int MANAKS_GOLD_GIANT = 3488;
    private static final int NIAS_GOLD_WYVERN = 3489;
    private static final int SILVER_UNICORN = 3490;
    private static final int SILVER_FAIRY = 3491;
    private static final int SILVER_DRYAD = 3492;
    private static final int SILVER_DRAGON = 3493;
    private static final int SILVER_GOLEM = 3494;
    private static final int SILVER_UNDINE = 3495;
    private static final int BELETHS_SILVER = 3496;
    private static final int MANAKS_SILVER_DRYAD = 3497;
    private static final int NIAS_SILVER_FAIRY = 3498;

    private static final List<Integer> BASIC_COINS = List.of(
            BLOOD_MEDUSA, GOLD_WYVERN, SILVER_UNICORN);

    private static final int SORINT = 30232;
    private static final int BERNARD = 30702;
    private static final int PAGE = 30696;
    private static final int HAGGER = 30183;
    private static final int STAN = 30200;
    private static final int RALFORD = 30165;
    private static final int FERRIS = 30847;
    private static final int COLLOB = 30092;
    private static final int PANO = 30078;
    private static final int DUNING = 30688;
    private static final int LORAIN = 30673;

    private static final int TimakOrcArcher = 20584;
    private static final int TimakOrcSoldier = 20585;
    private static final int TimakOrcShaman = 20587;
    private static final int Lakin = 20604;
    private static final int TorturedUndead = 20678;
    private static final int HatarHanishee = 20663;
    private static final int Shackle = 20235;
    private static final int TimakOrc = 20583;
    private static final int HeadlessKnight = 20146;
    private static final int RoyalCaveServant = 20240;
    private static final int MalrukSuccubusTuren = 20245;
    private static final int Formor = 20568;
    private static final int FormorElder = 20569;
    private static final int VanorSilenosShaman = 20685;
    private static final int TarlkBugbearHighWarrior = 20572;
    private static final int OelMahum = 20161;
    private static final int OelMahumWarrior = 20575;
    private static final int HaritLizardmanMatriarch = 20645;
    private static final int HaritLizardmanShaman = 20644;

    // not spawned
    private static final int Shackle2 = 20279;
    private static final int HeadlessKnight2 = 20280;
    private static final int MalrukSuccubusTuren2 = 20284;
    private static final int RoyalCaveServant2 = 20276;

    // New
    private static final int GraveLich = 21003;
    private static final int DoomServant = 21006;
    private static final int DoomArcher = 21008;
    private static final int DoomKnight = 20674;

    private static final int Kookaburra2 = 21276;
    private static final int Kookaburra3 = 21275;
    private static final int Kookaburra4 = 21274;
    private static final int Antelope2 = 21278;
    private static final int Antelope3 = 21279;
    private static final int Antelope4 = 21280;
    private static final int Bandersnatch2 = 21282;
    private static final int Bandersnatch3 = 21284;
    private static final int Bandersnatch4 = 21283;
    private static final int Buffalo2 = 21287;
    private static final int Buffalo3 = 21288;
    private static final int Buffalo4 = 21286;

    private static final int ClawsofSplendor = 21521;
    private static final int WisdomofSplendor = 21526;
    private static final int PunishmentofSplendor = 21531;
    private static final int WailingofSplendor = 21539;

    private static final int HungeredCorpse = 20954;
    private static final int BloodyGhost = 20960;
    private static final int NihilInvader = 20957;
    private static final int DarkGuard = 20959;

    private static final int[][] PROMOTE = {
            {},
            {},
            {
                    SILVER_DRYAD,
                    BLOOD_BASILISK,
                    BLOOD_SUCCUBUS,
                    SILVER_UNDINE,
                    GOLD_GIANT,
                    GOLD_WYRM
            },
            {
                    BLOOD_WEREWOLF,
                    GOLD_DRAKE,
                    SILVER_FAIRY,
                    BLOOD_DREVANUL,
                    GOLD_KNIGHT,
                    SILVER_GOLEM
            }
    };

    private static final Map<Integer, Integer> EXCHANGE_LEVEL = Map.of(
            PAGE, 3,
            LORAIN, 3,
            HAGGER, 3,
            RALFORD, 2,
            STAN, 2,
            DUNING, 2,
            FERRIS, 1,
            COLLOB, 1,
            PANO, 1);
    private static final List<Integer> BLOOD_MEDUSA_DROP = List.of(
            TimakOrcArcher, TimakOrcSoldier, TimakOrcShaman, Lakin, TorturedUndead, HatarHanishee);
    private static final List<Integer> GOLD_WYVERN_DROP = List.of(
            TimakOrc, Shackle, HeadlessKnight, RoyalCaveServant, MalrukSuccubusTuren);
    private static final List<Integer> SILVER_UNICORN_DROP = List.of(
            Formor, FormorElder, VanorSilenosShaman, TarlkBugbearHighWarrior, OelMahum, OelMahumWarrior);

    private static final List<Integer> UNKNOWN = List.of(
            GraveLich, DoomServant, DoomArcher, DoomKnight,
            Kookaburra2, Kookaburra3, Kookaburra4,
            Antelope2, Antelope3, Antelope4,
            Bandersnatch2, Bandersnatch3, Bandersnatch4,
             Buffalo2, Buffalo3, Buffalo4,
            ClawsofSplendor, WisdomofSplendor, PunishmentofSplendor, WailingofSplendor,
            HungeredCorpse, BloodyGhost, NihilInvader, DarkGuard);

    public _336_CoinOfMagic() {
        super(true);
        addStartNpc(SORINT);

        addTalkId(
                SORINT,
                BERNARD,
                PAGE,
                HAGGER,
                STAN,
                RALFORD,
                FERRIS,
                COLLOB,
                PANO,
                DUNING,
                LORAIN
        );

        addKillId(BLOOD_MEDUSA_DROP);
        addKillId(GOLD_WYVERN_DROP);
        addKillId(SILVER_UNICORN_DROP);

        addKillId(UNKNOWN);

        addKillId(HaritLizardmanMatriarch);
        addKillId(HaritLizardmanShaman);

        addQuestItem(COIN_DIAGRAM,
                KALDIS_COIN,
                MEMBERSHIP_1,
                MEMBERSHIP_2,
                MEMBERSHIP_3);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int cond = st.getCond();
        if ("30702-06.htm".equalsIgnoreCase(event)) {
            if (cond < 7) {
                st.setCond(7);
                st.playSound(SOUND_ACCEPT);
            }
        } else if ("30232-22.htm".equalsIgnoreCase(event)) {
            if (cond < 6)
                st.setCond(6);
        } else if ("30232-23.htm".equalsIgnoreCase(event)) {
            if (cond < 5)
                st.setCond(5);
        } else if ("30702-02.htm".equalsIgnoreCase(event))
            st.setCond(2);
        else if ("30232-05.htm".equalsIgnoreCase(event)) {
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            st.giveItems(COIN_DIAGRAM, 1);
            st.setCond(1);
        } else if ("30232-04.htm".equalsIgnoreCase(event) || "30232-18a.htm".equalsIgnoreCase(event)) {
            st.exitCurrentQuest(true);
            st.playSound(SOUND_GIVEUP);
        } else if ("raise".equalsIgnoreCase(event))
            htmltext = promote(st);
        return htmltext;
    }

    private String promote(QuestState st) {
        int grade = st.getInt("grade");
        String html;
        if (grade == 1)
            html = "30232-15.htm";
        else {
            int h = 0;
            for (int i : PROMOTE[grade])
                if (st.getQuestItemsCount(i) > 0)
                    h += 1;
            if (h == 6) {
                for (int i : PROMOTE[grade])
                    st.takeItems(i, 1);
                html = "30232-" + (19 - grade) + ".htm";
                st.takeItems(3812 + grade);
                st.giveItems(3811 + grade);
                st.set("grade", grade - 1);
                if (grade == 3)
                    st.setCond(9);
                else if (grade == 2)
                    st.setCond(11);
                st.playSound(SOUND_FANFARE_MIDDLE);
            } else {
                html = "30232-" + (16 - grade) + ".htm";
                if (grade == 3)
                    st.setCond(8);
                else if (grade == 2)
                    st.setCond(9);
            }
        }
        return html;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int grade = st.getInt("grade");
        if (npcId == SORINT) {
            if (id == CREATED) {
                if (st.player.getLevel() < 40) {
                    htmltext = "30232-01.htm";
                    st.exitCurrentQuest(true);
                } else
                    htmltext = "30232-02.htm";
            } else if (st.haveQuestItem(COIN_DIAGRAM) ) {
                if (st.haveQuestItem(KALDIS_COIN) ) {
                    st.takeItems(KALDIS_COIN);
                    st.takeItems(COIN_DIAGRAM);
                    st.giveItems(MEMBERSHIP_3);
                    st.set("grade", 3);
                    st.setCond(4);
                    st.playSound(SOUND_FANFARE_MIDDLE);
                    htmltext = "30232-07.htm";
                } else
                    htmltext = "30232-06.htm";
            } else if (grade == 3)
                htmltext = "30232-12.htm";
            else if (grade == 2)
                htmltext = "30232-11.htm";
            else if (grade == 1)
                htmltext = "30232-10.htm";
        } else if (npcId == BERNARD) {
            if (st.getQuestItemsCount(COIN_DIAGRAM) > 0 && grade == 0)
                htmltext = "30702-01.htm";
            else if (grade == 3)
                htmltext = "30702-05.htm";
        } else if (grade <= EXCHANGE_LEVEL.get(npcId))
            htmltext = npcId + "-01.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        int grade = st.getInt("grade");
        int chance = npc.getLevel() + grade * 3 - 20;
        int npcId = npc.getNpcId();
        if (npcId == HaritLizardmanMatriarch || npcId == HaritLizardmanShaman) {
            if (cond == 2)
                if (st.rollAndGive(KALDIS_COIN, 1, 1, 1, 10 * npc.getTemplate().rateHp))
                    st.setCond(3);
            return;
        }
        if (BLOOD_MEDUSA_DROP.contains(npcId)) {
            st.rollAndGive(BLOOD_MEDUSA, 1, chance);
        }
        if (GOLD_WYVERN_DROP.contains(npcId)) {
            st.rollAndGive(GOLD_WYVERN, 1, chance);
        }
        if (SILVER_UNICORN_DROP.contains(npcId)) {
            st.rollAndGive(SILVER_UNICORN, 1, chance);
        }
        if (UNKNOWN.contains(npcId))
            st.rollAndGive(Rnd.get(BASIC_COINS), 1, chance * npc.getTemplate().rateHp);
    }
}