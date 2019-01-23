package l2trunk.gameserver.model.entity.residence;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.dao.DominionDAO;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class Dominion extends Residence {
    private final Set<Integer> flags = new TreeSet<>();
    private Castle castle;
    private int _lordObjectId;

    public Dominion(StatsSet set) {
        super(set);
    }

    @Override
    public void init() {
        initEvent();

        castle = ResidenceHolder.getResidence(Castle.class, getId() - 80);
        castle.setDominion(this);

        loadData();

        _siegeDate.setTimeInMillis(0);
        if (getOwner() != null) {
            DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
            runnerEvent.registerDominion(this);
        }
    }

    @Override
    public void rewardSkills() {
        Clan owner = getOwner();
        if (owner != null) {
            if (!flags.contains(getId()))
                return;

            for (int dominionId : flags) {
                Dominion dominion = ResidenceHolder.getResidence(Dominion.class, dominionId);
                dominion.getSkills().forEach(skill -> {
                    owner.addSkill(skill, false);
                    owner.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));
                });
            }
        }
    }

    @Override
    public void removeSkills() {
        Clan owner = getOwner();
        if (owner != null) {
            for (int dominionId : flags) {
                Dominion dominion = ResidenceHolder.getResidence(Dominion.class, dominionId);
                for (Skill skill : dominion.getSkills())
                    owner.removeSkill(skill.id);
            }
        }
    }

    public void addFlag(int dominionId) {
        flags.add(dominionId);
    }

    public void removeFlag(int dominionId) {
        flags.remove(dominionId);
    }

    public List<Integer> getFlags() {
        return new ArrayList<>(flags);
    }

    @Override
    public ResidenceType getType() {
        return ResidenceType.Dominion;
    }

    @Override
    protected void loadData() {
        DominionDAO.INSTANCE.select(this);
    }

    @Override
    public void changeOwner(Clan clan) {
        int newLordObjectId;
        if (clan == null) {
            if (_lordObjectId > 0)
                newLordObjectId = 0;
            else
                return;
        } else {
            newLordObjectId = clan.getLeaderId();

            SystemMessage2 message = new SystemMessage2(SystemMsg.CLAN_LORD_C2_WHO_LEADS_CLAN_S1_HAS_BEEN_DECLARED_THE_LORD_OF_THE_S3_TERRITORY).addName(clan.getLeader().getPlayer()).addString(clan.getName()).addResidenceName(getCastle());
            GameObjectsStorage.getAllPlayersStream().forEach(player -> player.sendPacket(message));
        }

        _lordObjectId = newLordObjectId;

        setJdbcState(JdbcEntityState.UPDATED);
        update();

        GameObjectsStorage.getAllNpcs()
                .filter(npc -> npc.getDominion() == this)
                .forEach(NpcInstance::broadcastCharInfoImpl);
    }

    public int getLordObjectId() {
        return _lordObjectId;
    }

    public void setLordObjectId(int lordObjectId) {
        _lordObjectId = lordObjectId;
    }

    @Override
    public Clan getOwner() {
        return castle.getOwner();
    }

    @Override
    public int getOwnerId() {
        return castle.getOwnerId();
    }

    public Castle getCastle() {
        return castle;
    }

    @Override
    public void update() {
        DominionDAO.INSTANCE.update(this);
    }
}
