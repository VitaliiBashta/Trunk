package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Summon;

import java.util.List;
import java.util.stream.Collectors;

public final class PartySmallWindowAll extends L2GameServerPacket {
    private final int leaderId;
    private final int loot;
    private final List<PartySmallWindowMemberInfo> members;

    public PartySmallWindowAll(Party party, Player exclude) {
        leaderId = party.getLeader().objectId();
        loot = party.getLootDistribution();

        members = party.getMembersStream()
                .filter(member -> member != exclude)
                .map(PartySmallWindowMemberInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    protected final void writeImpl() {
        writeC(0x4E);
        writeD(leaderId); // c3 party leader id
        writeD(loot); //c3 party loot type (0,1,2,....)
        writeD(members.size());
        members.forEach(member -> {
            writeD(member._id);
            writeS(member._name);
            writeD(member.curCp);
            writeD(member.maxCp);
            writeD(member.curHp);
            writeD(member.maxHp);
            writeD(member.curMp);
            writeD(member.maxMp);
            writeD(member.level);
            writeD(member.class_id);
            writeD(0);//writeD(0x01); ??
            writeD(member.race_id);
            writeD(0);
            writeD(0);

            if (member.pet_id != 0) {
                writeD(member.pet_id);
                writeD(member.pet_NpcId);
                writeS(member.pet_Name);
                writeD(member.pet_curHp);
                writeD(member.pet_maxHp);
                writeD(member.pet_curMp);
                writeD(member.pet_maxMp);
                writeD(member.pet_level);
            } else
                writeD(0);
        });
    }

    public static class PartySmallWindowMemberInfo {
        public final String _name;
        public final int _id;
        public final int curCp;
        public final int maxCp;
        public final int curHp;
        public final int maxHp;
        public final int curMp;
        public final int maxMp;
        public final int level;
        public final int class_id;
        public final int race_id;
        final int pet_id;
        String pet_Name;
        int pet_NpcId;
        int pet_curHp;
        int pet_maxHp;
        int pet_curMp;
        int pet_maxMp;
        int pet_level;

        public PartySmallWindowMemberInfo(Player member) {
            _name = member.getName();
            _id = member.objectId();
            curCp = (int) member.getCurrentCp();
            maxCp = member.getMaxCp();
            curHp = (int) member.getCurrentHp();
            maxHp = member.getMaxHp();
            curMp = (int) member.getCurrentMp();
            maxMp = member.getMaxMp();
            level = member.getLevel();
            class_id = member.getClassId().id;
            race_id = member.getRace().ordinal();

            Summon pet = member.getPet();
            if (pet != null) {
                pet_id = pet.objectId();
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