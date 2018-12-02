package l2trunk.gameserver.model.entity;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum  MonsterRace {
    INSTANCE;
    private static final Logger _log = LoggerFactory.getLogger(MonsterRace.class);
    private static MonsterRace _instance;
    private final NpcInstance[] monsters;
    private final int[] first= new int[2];
    private final int[] second= new int[2];
    private Constructor<?> _constructor;
    private int[][] speeds =new int[8][20];

    MonsterRace() {
        monsters = new NpcInstance[8];
    }

    public void newRace() {
        int random;

        for (int i = 0; i < 8; i++) {
            int id = 31003;
            random = Rnd.get(24);
            for (int j = i - 1; j >= 0; j--)
                if (monsters[j].getTemplate().npcId == id + random)
                    random = Rnd.get(24);
            try {
                NpcTemplate template = NpcHolder.getTemplate(id + random);
                _constructor = template.getInstanceConstructor();
                int objectId = IdFactory.getInstance().getNextId();
                monsters[i] = (NpcInstance) _constructor.newInstance(objectId, template);
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
                _log.error("Error while creating new Monster Race", e);
            }
        }
        newSpeeds();
    }

    public void newSpeeds() {
        speeds = new int[8][20];
        int total = 0;
        first[1] = 0;
        second[1] = 0;
        for (int i = 0; i < 8; i++) {
            total = 0;
            for (int j = 0; j < 20; j++) {
                if (j == 19)
                    speeds[i][j] = 100;
                else
                    speeds[i][j] = Rnd.get(65, 124);
                total += speeds[i][j];
            }
            if (total >= first[1]) {
                second[0] = first[0];
                second[1] = first[1];
                first[0] = 8 - i;
                first[1] = total;
            } else if (total >= second[1]) {
                second[0] = 8 - i;
                second[1] = total;
            }
        }
    }

    public NpcInstance[] getMonsters() {
        return monsters;
    }

    public int[][] getSpeeds() {
        return speeds;
    }

    public int getFirstPlace() {
        return first[0];
    }

    public int getSecondPlace() {
        return second[0];
    }
}