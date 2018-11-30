package l2trunk.gameserver.hibenate.dao;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "castle", schema = "l2mythras")
public class CastleEntity {
    private int id;
    private String name;
    private int taxPercent;
    private long treasury;
    private long lastSiegeDate;
    private long ownDate;
    private long siegeDate;
    private int rewardCount;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "tax_percent")
    public int getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(int taxPercent) {
        this.taxPercent = taxPercent;
    }

    @Basic
    @Column(name = "treasury")
    public long getTreasury() {
        return treasury;
    }

    public void setTreasury(long treasury) {
        this.treasury = treasury;
    }

    @Basic
    @Column(name = "last_siege_date")
    public long getLastSiegeDate() {
        return lastSiegeDate;
    }

    public void setLastSiegeDate(long lastSiegeDate) {
        this.lastSiegeDate = lastSiegeDate;
    }

    @Basic
    @Column(name = "own_date")
    public long getOwnDate() {
        return ownDate;
    }

    public void setOwnDate(long ownDate) {
        this.ownDate = ownDate;
    }

    @Basic
    @Column(name = "siege_date")
    public long getSiegeDate() {
        return siegeDate;
    }

    public void setSiegeDate(long siegeDate) {
        this.siegeDate = siegeDate;
    }

    @Basic
    @Column(name = "reward_count")
    public int getRewardCount() {
        return rewardCount;
    }

    public void setRewardCount(int rewardCount) {
        this.rewardCount = rewardCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CastleEntity that = (CastleEntity) o;
        return id == that.id &&
                taxPercent == that.taxPercent &&
                treasury == that.treasury &&
                lastSiegeDate == that.lastSiegeDate &&
                ownDate == that.ownDate &&
                siegeDate == that.siegeDate &&
                rewardCount == that.rewardCount &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, taxPercent, treasury, lastSiegeDate, ownDate, siegeDate, rewardCount);
    }
}
