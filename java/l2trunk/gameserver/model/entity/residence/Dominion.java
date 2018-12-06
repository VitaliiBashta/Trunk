package l2trunk.gameserver.model.entity.residence;

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
import l2trunk.gameserver.templates.StatsSet;

import java.util.Set;
import java.util.TreeSet;
//import org.napile.primitive.sets.IntSet;
//import org.napile.primitive.sets.impl.TreeIntSet;

public final class Dominion extends Residence {
    private static final long serialVersionUID = 1L;
    private final Set<Integer> _flags = new TreeSet<>();
    private Castle castle;
    private int _lordObjectId;

    public Dominion(StatsSet set) {
        super(set);
    }

    @Override
    public void init() {
        initEvent();

        castle = ResidenceHolder.getInstance().getResidence(Castle.class, getId() - 80);
        castle.setDominion(this);

        loadData();

        _siegeDate.setTimeInMillis(0);
        if (getOwner() != null) {
            DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
            runnerEvent.registerDominion(this);
        }
    }

    @Override
    public void rewardSkills() {
        Clan owner = getOwner();
        if (owner != null) {
            if (!_flags.contains(getId()))
                return;

            for (int dominionId : _flags) {
                Dominion dominion = ResidenceHolder.getInstance().getResidence(Dominion.class, dominionId);
                for (Skill skill : dominion.getSkills()) {
                    owner.addSkill(skill, false);
                    owner.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));
                }
            }
        }
    }

    @Override
    public void removeSkills() {
        Clan owner = getOwner();
        if (owner != null) {
            for (int dominionId : _flags) {
                Dominion dominion = ResidenceHolder.getInstance().getResidence(Dominion.class, dominionId);
                for (Skill skill : dominion.getSkills())
                    owner.removeSkill(skill.getId());
            }
        }
    }

    public void addFlag(int dominionId) {
        _flags.add(dominionId);
    }

    public void removeFlag(int dominionId) {
        _flags.remove(dominionId);
    }

    public int[] getFlags() {
        return _flags.stream().mapToInt(Number::intValue).toArray();
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
            GameObjectsStorage.getAllPlayers().forEach(player -> player.sendPacket(message));
        }

        _lordObjectId = newLordObjectId;

        setJdbcState(JdbcEntityState.UPDATED);
        update();

        for (NpcInstance npc : GameObjectsStorage.getAllNpcsForIterate())
            if (npc.getDominion() == this)
                npc.broadcastCharInfoImpl();
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
