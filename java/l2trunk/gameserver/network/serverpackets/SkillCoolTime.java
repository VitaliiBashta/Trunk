package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.skills.TimeStamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SkillCoolTime extends L2GameServerPacket {
    private List<Skill> _list = Collections.emptyList();

    public SkillCoolTime(Player player) {
        Collection<TimeStamp> list = player.getSkillReuses();
        _list = new ArrayList<>(list.size());
        for (TimeStamp stamp : list) {
            if (!stamp.hasNotPassed())
                continue;
            l2trunk.gameserver.model.Skill skill = player.getKnownSkill(stamp.getId());
            if (skill == null)
                continue;
            Skill sk = new Skill();
            sk.skillId = skill.getId();
            sk.level = skill.getLevel();
            sk.reuseBase = (int) Math.round(stamp.getReuseBasic() / 1000.);
            sk.reuseCurrent = (int) Math.round(stamp.getReuseCurrent() / 1000.);
            _list.add(sk);
        }
    }

    @Override
    protected final void writeImpl() {
        writeC(0xc7); //packet type
        writeD(_list.size()); //Size of list
        for (Skill sk : _list) {
            writeD(sk.skillId); //Skill Id
            writeD(sk.level); //Skill Level
            writeD(sk.reuseBase); //Total reuse delay, seconds
            writeD(sk.reuseCurrent); //Time remaining, seconds
        }
    }

    private static class Skill {
        int skillId;
        int level;
        int reuseBase;
        int reuseCurrent;
    }
}