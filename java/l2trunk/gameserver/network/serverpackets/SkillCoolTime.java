package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.skills.TimeStamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class SkillCoolTime extends L2GameServerPacket {
    private final List<SkillDescription> list = new ArrayList<>();

    public SkillCoolTime(Player player) {
        Collection<TimeStamp> reuses = player.getSkillReuses();
        for (TimeStamp stamp : reuses) {
            if (stamp.hasNotPassed()) {
                Skill skill = player.getKnownSkill(stamp.id);
                if (skill != null) {
                    SkillDescription sk = new SkillDescription(
                            skill.id, skill.level, (int) Math.round(stamp.getReuseBasic() / 1000.),
                            (int) Math.round(stamp.getReuseCurrent() / 1000.));
                    list.add(sk);
                }
            }
        }
    }

    @Override
    protected final void writeImpl() {
        writeC(0xc7); //packet type
        writeD(list.size()); //Size of list
        list.forEach(sk -> {
            writeD(sk.skillId); //SkillDescription Id
            writeD(sk.level); //SkillDescription Level
            writeD(sk.reuseBase); //Total reuse delay, seconds
            writeD(sk.reuseCurrent); //Time remaining, seconds
        });
    }

    private static class SkillDescription {
        int skillId;
        int level;
        int reuseBase;
        int reuseCurrent;

        SkillDescription(int skillId, int level, int reuseBase, int reuseCurrent) {
            this.skillId = skillId;
            this.level = level;
            this.reuseBase = reuseBase;
            this.reuseCurrent = reuseCurrent;
        }
    }
}