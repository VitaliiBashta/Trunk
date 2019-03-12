package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.ArrayList;
import java.util.List;


public final class _233_TestOfWarspirit extends Quest {
    // NPCs
    private static final int Somak = 30510;
    private static final int Vivyan = 30030;
    private static final int Sarien = 30436;
    private static final int Racoy = 30507;
    private static final int Manakia = 30515;
    private static final int Orim = 30630;
    private static final int Ancestor_Martankus = 30649;
    private static final int Pekiron = 30682;
    // Mobs
    private static final int Porta = 20213;
    private static final int Excuro = 20214;
    private static final int Mordeo = 20215;
    private static final int Noble_Ant = 20089;
    private static final int Noble_Ant_Leader = 20090;
    private static final int Leto_Lizardman_Shaman = 20581;
    private static final int Leto_Lizardman_Overlord = 20582;
    private static final int Medusa = 20158;
    private static final int Stenoa_Gorgon_Queen = 27108;
    private static final int Tamlin_Orc = 20601;
    private static final int Tamlin_Orc_Archer = 20602;
    // items
    private static final int Dimensional_Diamond = 7562;
    private static final int MARK_OF_WARSPIRIT = 2879;
    // Quest items
    private static final int VENDETTA_TOTEM = 2880;
    private static final int TAMLIN_ORC_HEAD = 2881;
    private static final int WARSPIRIT_TOTEM = 2882;
    private static final int ORIMS_CONTRACT = 2883;
    private static final int PORTAS_EYE = 2884;
    private static final int EXCUROS_SCALE = 2885;
    private static final int MORDEOS_TALON = 2886;
    private static final int BRAKIS_REMAINS1 = 2887;
    private static final int PEKIRONS_TOTEM = 2888;
    private static final int TONARS_SKULL = 2889;
    private static final int TONARS_RIB_BONE = 2890;
    private static final int TONARS_SPINE = 2891;
    private static final int TONARS_ARM_BONE = 2892;
    private static final int TONARS_THIGH_BONE = 2893;
    private static final int TONARS_REMAINS1 = 2894;
    private static final int MANAKIAS_TOTEM = 2895;
    private static final int HERMODTS_SKULL = 2896;
    private static final int HERMODTS_RIB_BONE = 2897;
    private static final int HERMODTS_SPINE = 2898;
    private static final int HERMODTS_ARM_BONE = 2899;
    private static final int HERMODTS_THIGH_BONE = 2900;
    private static final int HERMODTS_REMAINS1 = 2901;
    private static final int RACOYS_TOTEM = 2902;
    private static final int VIVIANTES_LETTER = 2903;
    private static final int INSECT_DIAGRAM_BOOK = 2904;
    private static final int KIRUNAS_SKULL = 2905;
    private static final int KIRUNAS_RIB_BONE = 2906;
    private static final int KIRUNAS_SPINE = 2907;
    private static final int KIRUNAS_ARM_BONE = 2908;
    private static final int KIRUNAS_THIGH_BONE = 2909;
    private static final int KIRUNAS_REMAINS1 = 2910;
    private static final int BRAKIS_REMAINS2 = 2911;
    private static final int TONARS_REMAINS2 = 2912;
    private static final int HERMODTS_REMAINS2 = 2913;
    private static final int KIRUNAS_REMAINS2 = 2914;

    private static final List<Integer> Noble_Ant_Drops = List.of(
            KIRUNAS_THIGH_BONE,
            KIRUNAS_ARM_BONE,
            KIRUNAS_SPINE,
            KIRUNAS_RIB_BONE,
            KIRUNAS_SKULL);
    private static final List<Integer> Leto_Lizardman_Drops = List.of(
            TONARS_SKULL,
            TONARS_RIB_BONE,
            TONARS_SPINE,
            TONARS_ARM_BONE,
            TONARS_THIGH_BONE);
    private static final List<Integer> Medusa_Drops = List.of(
            HERMODTS_RIB_BONE,
            HERMODTS_SPINE,
            HERMODTS_THIGH_BONE,
            HERMODTS_ARM_BONE);

    public _233_TestOfWarspirit() {
        super(false);
        addStartNpc(Somak);

        addTalkId(Vivyan, Sarien, Racoy, Manakia, Orim, Ancestor_Martankus, Pekiron);

        addKillId(Porta, Excuro, Mordeo, Noble_Ant, Noble_Ant_Leader, Leto_Lizardman_Shaman, Leto_Lizardman_Overlord,
                Medusa, Stenoa_Gorgon_Queen, Tamlin_Orc, Tamlin_Orc_Archer);

        addQuestItem(VENDETTA_TOTEM, TAMLIN_ORC_HEAD, WARSPIRIT_TOTEM, ORIMS_CONTRACT, PORTAS_EYE, EXCUROS_SCALE,
                MORDEOS_TALON, BRAKIS_REMAINS1, PEKIRONS_TOTEM, TONARS_SKULL, TONARS_RIB_BONE, TONARS_SPINE,
                TONARS_ARM_BONE, TONARS_THIGH_BONE, TONARS_REMAINS1, MANAKIAS_TOTEM, HERMODTS_SKULL,
                HERMODTS_RIB_BONE, HERMODTS_SPINE, HERMODTS_ARM_BONE, HERMODTS_THIGH_BONE, HERMODTS_REMAINS1,
                RACOYS_TOTEM, VIVIANTES_LETTER, INSECT_DIAGRAM_BOOK, KIRUNAS_SKULL, KIRUNAS_RIB_BONE,
                KIRUNAS_SPINE, KIRUNAS_ARM_BONE, KIRUNAS_THIGH_BONE, KIRUNAS_REMAINS1,
                BRAKIS_REMAINS2, TONARS_REMAINS2, HERMODTS_REMAINS2, KIRUNAS_REMAINS2);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        if ("30510-05.htm".equalsIgnoreCase(event) && state == CREATED) {
            if (!st.player.isVarSet("dd3")) {
                st.giveItems(Dimensional_Diamond, 92);
                st.player.setVar("dd3");
            }
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("30630-04.htm".equalsIgnoreCase(event) && state == STARTED)
            st.giveItems(ORIMS_CONTRACT);
        else if ("30682-02.htm".equalsIgnoreCase(event) && state == STARTED)
            st.giveItems(PEKIRONS_TOTEM);
        else if ("30515-02.htm".equalsIgnoreCase(event) && state == STARTED)
            st.giveItems(MANAKIAS_TOTEM);
        else if ("30507-02.htm".equalsIgnoreCase(event) && state == STARTED)
            st.giveItems(RACOYS_TOTEM);
        else if ("30030-04.htm".equalsIgnoreCase(event) && state == STARTED)
            st.giveItems(VIVIANTES_LETTER);
        else if ("30649-03.htm".equalsIgnoreCase(event) && state == STARTED && st.haveQuestItem(WARSPIRIT_TOTEM)) {
            st.takeAllItems(WARSPIRIT_TOTEM, BRAKIS_REMAINS2, HERMODTS_REMAINS2,
                    KIRUNAS_REMAINS2, TAMLIN_ORC_HEAD, TONARS_REMAINS2);
            st.giveItems(MARK_OF_WARSPIRIT);
            if (!st.player.isVarSet("prof2.3")) {
                st.addExpAndSp(447444, 30704);
                st.giveItems(ADENA_ID, 1000000);
                st.player.setVar("prof2.3");
            }
            st.playSound(SOUND_FINISH);
            st.unset("cond");
            st.exitCurrentQuest();
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (st.haveQuestItem(MARK_OF_WARSPIRIT)) {
            st.exitCurrentQuest();
            return "completed";
        }
        int _state = st.getState();
        int npcId = npc.getNpcId();
        if (_state == CREATED) {
            if (npcId != Somak)
                return "noquest";
            if (st.player.getRace() != Race.orc) {
                st.exitCurrentQuest();
                return "30510-01.htm";
            }
            if (st.player.getClassId().id != 0x32) {
                st.exitCurrentQuest();
                return "30510-02.htm";
            }
            if (st.player.getLevel() < 39) {
                st.exitCurrentQuest();
                return "30510-03.htm";
            }
            st.setCond(0);
            return "30510-04.htm";
        }

        if (_state != STARTED || st.getCond() != 1)
            return "noquest";

        if (npcId == Somak) {
            if (st.haveQuestItem(VENDETTA_TOTEM)) {
                if (st.getQuestItemsCount(TAMLIN_ORC_HEAD) < 13)
                    return "30510-08.htm";
                st.takeItems(VENDETTA_TOTEM);
                st.giveItems(WARSPIRIT_TOTEM);
                st.giveItems(BRAKIS_REMAINS2);
                st.giveItems(HERMODTS_REMAINS2);
                st.giveItems(KIRUNAS_REMAINS2);
                st.giveItems(TONARS_REMAINS2);
                st.playSound(SOUND_MIDDLE);
                return "30510-09.htm";
            }
            if (st.haveQuestItem(WARSPIRIT_TOTEM))
                return "30510-10.htm";
            if (!st.haveAnyQuestItems(BRAKIS_REMAINS1, HERMODTS_REMAINS1, KIRUNAS_REMAINS1, TONARS_REMAINS1))
                return "30510-06.htm";
            st.takeItems(BRAKIS_REMAINS1);
            st.takeItems(HERMODTS_REMAINS1);
            st.takeItems(KIRUNAS_REMAINS1);
            st.takeItems(TONARS_REMAINS1);
            st.giveItems(VENDETTA_TOTEM);
            st.playSound(SOUND_MIDDLE);
            return "30510-07.htm";
        }

        if (npcId == Orim) {
            if (st.haveQuestItem(ORIMS_CONTRACT)) {
                if (st.getQuestItemsCount(PORTAS_EYE) < 10 || st.getQuestItemsCount(EXCUROS_SCALE) < 10 || st.getQuestItemsCount(MORDEOS_TALON) < 10)
                    return "30630-05.htm";
                st.takeAllItems(ORIMS_CONTRACT,PORTAS_EYE,EXCUROS_SCALE,MORDEOS_TALON);
                st.giveItems(BRAKIS_REMAINS1);
                st.playSound(SOUND_MIDDLE);
                return "30630-06.htm";
            }
            if (!st.haveAnyQuestItems(BRAKIS_REMAINS1, BRAKIS_REMAINS2, VENDETTA_TOTEM))
                return "30630-01.htm";
            return "30630-07.htm";
        }

        if (npcId == Pekiron) {
            if (st.haveQuestItem(PEKIRONS_TOTEM)) {
                for (int drop_id : Leto_Lizardman_Drops)
                    if (!st.haveQuestItem(drop_id))
                        return "30682-03.htm";
                st.takeItems(PEKIRONS_TOTEM);
                for (int drop_id : Leto_Lizardman_Drops)
                    if (st.getQuestItemsCount(drop_id) == 0)
                        st.takeItems(drop_id);
                st.giveItems(TONARS_REMAINS1);
                st.playSound(SOUND_MIDDLE);
                return "30682-04.htm";
            }
            if (!st.haveAnyQuestItems(TONARS_REMAINS1, TONARS_REMAINS2, VENDETTA_TOTEM))
                return "30682-01.htm";
            return "30682-05.htm";
        }

        if (npcId == Manakia) {
            if (st.haveQuestItem(MANAKIAS_TOTEM)) {
                if (!st.haveQuestItem(HERMODTS_SKULL))
                    return "30515-03.htm";
                for (int drop_id : Medusa_Drops)
                    if (!st.haveQuestItem(drop_id))
                        return "30515-03.htm";
                st.takeAllItems(MANAKIAS_TOTEM,HERMODTS_SKULL);
                for (int drop_id : Medusa_Drops)
                    if (st.getQuestItemsCount(drop_id) == 0)
                        st.takeItems(drop_id);
                st.giveItems(HERMODTS_REMAINS1);
                st.playSound(SOUND_MIDDLE);
                return "30515-04.htm";
            }
            if (!st.haveAnyQuestItems(HERMODTS_REMAINS1, HERMODTS_REMAINS2, VENDETTA_TOTEM))
                return "30515-01.htm";
            if (!st.haveQuestItem(RACOYS_TOTEM)
                    && (st.haveAnyQuestItems(KIRUNAS_REMAINS2, WARSPIRIT_TOTEM, BRAKIS_REMAINS2, HERMODTS_REMAINS2, TAMLIN_ORC_HEAD, TONARS_REMAINS2)))
                return "30515-05.htm";
        }

        if (npcId == Racoy)
            if (st.haveQuestItem(RACOYS_TOTEM)) {
                if (st.getQuestItemsCount(INSECT_DIAGRAM_BOOK) == 0)
                    return st.haveQuestItem(VIVIANTES_LETTER) ? "30507-04.htm" : "30507-03.htm";
                if (st.getQuestItemsCount(VIVIANTES_LETTER) == 0) {
                    for (int drop_id : Noble_Ant_Drops)
                        if (st.getQuestItemsCount(drop_id) == 0)
                            return "30507-05.htm";
                    st.takeItems(RACOYS_TOTEM);
                    st.takeItems(INSECT_DIAGRAM_BOOK);
                    for (int drop_id : Noble_Ant_Drops)
                        if (st.getQuestItemsCount(drop_id) == 0)
                            st.takeItems(drop_id);
                    st.giveItems(KIRUNAS_REMAINS1);
                    st.playSound(SOUND_MIDDLE);
                    return "30507-06.htm";
                }
            } else {
                if (!st.haveAnyQuestItems(KIRUNAS_REMAINS1, KIRUNAS_REMAINS2, VENDETTA_TOTEM))
                    return "30507-01.htm";
                return "30507-07.htm";
            }

        if (npcId == Vivyan)
            if (st.haveQuestItem(RACOYS_TOTEM)) {
                if (st.getQuestItemsCount(INSECT_DIAGRAM_BOOK) == 0)
                    return st.getQuestItemsCount(VIVIANTES_LETTER) == 0 ? "30030-01.htm" : "30030-05.htm";
                if (st.getQuestItemsCount(VIVIANTES_LETTER) == 0)
                    return "30030-06.htm";
            } else if (!st.haveAnyQuestItems(KIRUNAS_REMAINS1, KIRUNAS_REMAINS2, VENDETTA_TOTEM))
                return "30030-07.htm";

        if (npcId == Sarien)
            if (st.haveQuestItem(RACOYS_TOTEM)) {
                if (st.getQuestItemsCount(INSECT_DIAGRAM_BOOK) == 0 && st.haveQuestItem(VIVIANTES_LETTER)) {
                    st.takeItems(VIVIANTES_LETTER);
                    st.giveItems(INSECT_DIAGRAM_BOOK);
                    st.playSound(SOUND_MIDDLE);
                    return "30436-01.htm";
                }
                if (st.getQuestItemsCount(VIVIANTES_LETTER) == 0 && st.haveQuestItem(INSECT_DIAGRAM_BOOK))
                    return "30436-02.htm";
            } else if (!st.haveAnyQuestItems(KIRUNAS_REMAINS1, KIRUNAS_REMAINS2, VENDETTA_TOTEM))
                return "30436-03.htm";

        if (npcId == Ancestor_Martankus && st.haveQuestItem(WARSPIRIT_TOTEM))
            return "30649-01.htm";

        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED || qs.getCond() < 1)
            return;

        int npcId = npc.getNpcId();

        if (npcId == Porta && qs.getQuestItemsCount(ORIMS_CONTRACT) > 0 && qs.getQuestItemsCount(PORTAS_EYE) < 10) {
            qs.giveItems(PORTAS_EYE, 1);
            qs.playSound(qs.getQuestItemsCount(PORTAS_EYE) == 10 ? SOUND_MIDDLE : SOUND_ITEMGET);
        } else if (npcId == Excuro && qs.haveQuestItem(ORIMS_CONTRACT) && qs.getQuestItemsCount(EXCUROS_SCALE) < 10) {
            qs.giveItems(EXCUROS_SCALE, 1);
            qs.playSound(qs.getQuestItemsCount(EXCUROS_SCALE) == 10 ? SOUND_MIDDLE : SOUND_ITEMGET);
        } else if (npcId == Mordeo && qs.haveQuestItem(ORIMS_CONTRACT) && qs.getQuestItemsCount(MORDEOS_TALON) < 10) {
            qs.giveItems(MORDEOS_TALON, 1);
            qs.playSound(qs.getQuestItemsCount(MORDEOS_TALON) == 10 ? SOUND_MIDDLE : SOUND_ITEMGET);
        } else if ((npcId == Noble_Ant || npcId == Noble_Ant_Leader) && qs.getQuestItemsCount(RACOYS_TOTEM) > 0) {
            List<Integer> drops = new ArrayList<>();
            for (int drop_id : Noble_Ant_Drops)
                if (qs.getQuestItemsCount(drop_id) == 0)
                    drops.add(drop_id);
            if (drops.size() > 0 && Rnd.chance(30)) {
                int drop_id = Rnd.get(drops);
                qs.giveItems(drop_id);
                qs.playSound(drops.size() == 1 ? SOUND_MIDDLE : SOUND_ITEMGET);
            }
            drops.clear();
        } else if ((npcId == Leto_Lizardman_Shaman || npcId == Leto_Lizardman_Overlord) && qs.haveQuestItem(PEKIRONS_TOTEM)) {
            List<Integer> drops = new ArrayList<>();
            for (int drop_id : Leto_Lizardman_Drops)
                if (qs.getQuestItemsCount(drop_id) == 0)
                    drops.add(drop_id);
            if (drops.size() > 0 && Rnd.chance(25)) {
                qs.giveItems(Rnd.get(drops));
                qs.playSound(drops.size() == 1 ? SOUND_MIDDLE : SOUND_ITEMGET);
            }
            drops.clear();
        } else if (npcId == Medusa && qs.getQuestItemsCount(MANAKIAS_TOTEM) > 0) {
            List<Integer> drops = new ArrayList<>();
            for (int drop_id : Medusa_Drops)
                if (qs.getQuestItemsCount(drop_id) == 0)
                    drops.add(drop_id);
            if (drops.size() > 0 && Rnd.chance(30)) {
                qs.giveItems(Rnd.get(drops));
                qs.playSound(drops.size() == 1 && qs.getQuestItemsCount(HERMODTS_SKULL) > 0 ? SOUND_MIDDLE : SOUND_ITEMGET);
            }
            drops.clear();
        } else if (npcId == Stenoa_Gorgon_Queen && qs.haveQuestItem(MANAKIAS_TOTEM) && !qs.haveQuestItem(HERMODTS_SKULL) && Rnd.chance(30)) {
            qs.giveItems(HERMODTS_SKULL);
            boolean _allset = true;
            for (int drop_id : Medusa_Drops)
                if (qs.getQuestItemsCount(drop_id) == 0) {
                    _allset = false;
                    break;
                }
            qs.playSound(_allset ? SOUND_MIDDLE : SOUND_ITEMGET);
        } else if ((npcId == Tamlin_Orc || npcId == Tamlin_Orc_Archer) && qs.haveQuestItem(VENDETTA_TOTEM) && !qs.haveQuestItem(TAMLIN_ORC_HEAD, 13))
            if (Rnd.chance(npcId == Tamlin_Orc ? 30 : 50)) {
                qs.giveItems(TAMLIN_ORC_HEAD);
                qs.playSound(qs.getQuestItemsCount(TAMLIN_ORC_HEAD) == 13 ? SOUND_MIDDLE : SOUND_ITEMGET);
            }
    }
}