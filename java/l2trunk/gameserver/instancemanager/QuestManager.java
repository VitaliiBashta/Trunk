package l2trunk.gameserver.instancemanager;

import l2trunk.gameserver.model.quest.Quest;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuestManager {
    public static final int TUTORIAL_QUEST_ID = 255;

    private static final Map<String, Quest> _questsByName = new ConcurrentHashMap<>();
    private static final Map<Integer, Quest> _questsById = new ConcurrentHashMap<>();

    public static Quest getQuest(String name) {
        return _questsByName.get(name);
    }

    public static Quest getQuest(Class<?> quest) {
        return getQuest(quest.getSimpleName());
    }

    public static Quest getQuest(int questId) {
        return _questsById.get(questId);
    }

    public static Quest getQuest2(String nameOrId) {
        if (_questsByName.containsKey(nameOrId))
            return _questsByName.get(nameOrId);
        try {
            int questId = Integer.valueOf(nameOrId);
            return _questsById.get(questId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void addQuest(Quest newQuest) {
        _questsByName.put(newQuest.getName(), newQuest);
        _questsById.put(newQuest.getQuestIntId(), newQuest);
    }

    public static Collection<Quest> getQuests() {
        return _questsByName.values();
    }
}