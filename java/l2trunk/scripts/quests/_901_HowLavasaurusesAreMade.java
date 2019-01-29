package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _901_HowLavasaurusesAreMade extends Quest {
    // NPC's
    private static final int ROONEY = 32049; // Rooney	Blacksmith of Wind
    // Item's
    private static final int TOTEM_OF_BODY = 21899; // Totem of Body
    private static final int TOTEM_OF_SPIRIT = 21900; // Totem of Spirit
    private static final int TOTEM_OF_COURAGE = 21901; // Totem of Courage
    private static final int TOTEM_OF_FORTITUDE = 21902; // Totem of Fortitude

    // Quest Item's
    private static final int LAVASAURUS_STONE_FRAGMENT = 21909; // Lavasaurus Stone Fragment
    private static final int LAVASAURUS_HEAD_FRAGMENT = 21910; // Lavasaurus Head Fragment
    private static final int LAVASAURUS_BODY_FRAGMENT = 21911; // Lavasaurus Body Fragment
    private static final int LAVASAURUS_HORN_FRAGMENT = 21912; // Lavasaurus Horn Fragment
    private static final List<Integer> fragments = List.of(
            LAVASAURUS_STONE_FRAGMENT, LAVASAURUS_HEAD_FRAGMENT, LAVASAURUS_BODY_FRAGMENT, LAVASAURUS_HORN_FRAGMENT);
    // Monster's
    private static final List<Integer> KILLING_MONSTERS = List.of(18799, 18800, 18801, 18802, 18803);
    // Chance's
    private static final int DROP_CHANCE = 5;

    public _901_HowLavasaurusesAreMade() {
        super(PARTY_ALL);
        addStartNpc(ROONEY);
        addTalkId(ROONEY);
        addQuestItem(fragments);
        addKillId(KILLING_MONSTERS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("blacksmith_rooney_q901_03.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("blacksmith_rooney_q901_12a.htm".equalsIgnoreCase(event)) {
            st.giveItems(TOTEM_OF_BODY);
            st.playSound(SOUND_FINISH);
            st.setState(COMPLETED);
            st.exitCurrentQuest(this);
        } else if ("blacksmith_rooney_q901_12b.htm".equalsIgnoreCase(event)) {
            st.giveItems(TOTEM_OF_SPIRIT);
            st.playSound(SOUND_FINISH);
            st.setState(COMPLETED);
            st.exitCurrentQuest(this);
        } else if ("blacksmith_rooney_q901_12c.htm".equalsIgnoreCase(event)) {
            st.giveItems(TOTEM_OF_FORTITUDE);
            st.playSound(SOUND_FINISH);
            st.setState(COMPLETED);
            st.exitCurrentQuest(this);
        } else if ("blacksmith_rooney_q901_12d.htm".equalsIgnoreCase(event)) {
            st.giveItems(TOTEM_OF_COURAGE);
            st.playSound(SOUND_FINISH);
            st.setState(COMPLETED);
            st.exitCurrentQuest(this);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == ROONEY) {
            if (cond == 0) {
                if (st.getPlayer().getLevel() >= 76) {
                    if (st.isNowAvailable())
                        htmltext = "blacksmith_rooney_q901_01.htm";
                    else
                        htmltext = "blacksmith_rooney_q901_01n.htm";
                } else
                    htmltext = "blacksmith_rooney_q901_00.htm";
            } else if (cond == 1)
                htmltext = "blacksmith_rooney_q901_04.htm";
            else if (cond == 2) {
                if (st.getInt("collect") == 1)
                    htmltext = "blacksmith_rooney_q901_07.htm";
                else {
                    if (st.haveQuestItem(LAVASAURUS_STONE_FRAGMENT, 10)
                            && st.haveQuestItem(LAVASAURUS_HEAD_FRAGMENT, 10)
                            && st.haveQuestItem(LAVASAURUS_BODY_FRAGMENT, 10)
                            && st.haveQuestItem(LAVASAURUS_HORN_FRAGMENT, 10)) {
                        htmltext = "blacksmith_rooney_q901_05.htm";
                        st.takeItems(fragments);
                        st.set("collect", 1);
                    } else
                        htmltext = "blacksmith_rooney_q901_06.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            if (!KILLING_MONSTERS.contains(npc.getNpcId()))
                return null;

            if (!st.haveQuestItem(LAVASAURUS_STONE_FRAGMENT, 10))
                st.rollAndGive(LAVASAURUS_STONE_FRAGMENT, 1, DROP_CHANCE);
            if (!st.haveQuestItem(LAVASAURUS_HEAD_FRAGMENT, 10))
                st.rollAndGive(LAVASAURUS_HEAD_FRAGMENT, 1, DROP_CHANCE);
            if (!st.haveQuestItem(LAVASAURUS_BODY_FRAGMENT, 10))
                st.rollAndGive(LAVASAURUS_BODY_FRAGMENT, 1, DROP_CHANCE);
            if (!st.haveQuestItem(LAVASAURUS_HORN_FRAGMENT, 10))
                st.rollAndGive(LAVASAURUS_HORN_FRAGMENT, 1, DROP_CHANCE);

            if (st.haveQuestItem(LAVASAURUS_STONE_FRAGMENT, 10) && st.haveQuestItem(LAVASAURUS_HEAD_FRAGMENT, 10) && st.haveQuestItem(LAVASAURUS_BODY_FRAGMENT, 10) && st.haveQuestItem(LAVASAURUS_HORN_FRAGMENT, 10)) {
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            }
        }
        return null;
    }

}