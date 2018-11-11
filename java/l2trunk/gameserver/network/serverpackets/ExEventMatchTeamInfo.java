package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Summon;

import java.util.ArrayList;
import java.util.List;


public class ExEventMatchTeamInfo extends L2GameServerPacket {
    @SuppressWarnings("unused")
    private final int leader_id;
    @SuppressWarnings("unused")
    private final int loot;
    private final List<EventMatchTeamInfo> members = new ArrayList<>();

    public ExEventMatchTeamInfo(List<Player> party, Player exclude) {
        leader_id = party.get(0).getObjectId();
        loot = party.get(0).getParty().getLootDistribution();

        for (Player member : party)
            if (!member.equals(exclude))
                members.add(new EventMatchTeamInfo(member));
    }

    @Override
    protected void writeImpl() {
        writeEx(0x1C);
        // TODO dcd[dSdddddddddd]
    }

    static class EventMatchTeamInfo {
        final String _name;
        String pet_Name;
        final int _id;
        final int curCp;
        final int maxCp;
        final int curHp;
        final int maxHp;
        final int curMp;
        final int maxMp;
        final int level;
        final int class_id;
        final int race_id;
        final int pet_id;
        int pet_NpcId;
        int pet_curHp;
        int pet_maxHp;
        int pet_curMp;
        int pet_maxMp;
        int pet_level;

        EventMatchTeamInfo(Player member) {
            _name = member.getName();
            _id = member.getObjectId();
            curCp = (int) member.getCurrentCp();
            maxCp = member.getMaxCp();
            curHp = (int) member.getCurrentHp();
            maxHp = member.getMaxHp();
            curMp = (int) member.getCurrentMp();
            maxMp = member.getMaxMp();
            level = member.getLevel();
            class_id = member.getClassId().getId();
            race_id = member.getRace().ordinal();

            Summon pet = member.getPet();
            if (pet != null) {
                pet_id = pet.getObjectId();
                pet_NpcId = pet.getNpcId() + 1000000;
                pet_Name = pet.getName();
                pet_curHp = (int) pet.getCurrentHp();
                pet_maxHp = pet.getMaxHp();
                pet_curMp = (int) pet.getCurrentMp();
                pet_maxMp = pet.getMaxMp();
                pet_level = pet.getLevel();
            } else
                pet_id = 0;
        }
    }
}