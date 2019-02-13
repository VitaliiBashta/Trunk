package l2trunk.gameserver.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class SoulCrystal {

    @XmlAttribute(name = "item_id")
    private int itemId;
    @XmlAttribute(name = "occupation")
    private int level;
    @XmlAttribute(name = "next_item_id")
    private int nextItemId;
    @XmlAttribute(name = "cursed_next_item_id")
    private int cursedNextItemId;

    public SoulCrystal() {
    }

    public SoulCrystal(int itemId, int level, int nextItemId, int cursedNextItemId) {
        this.itemId = itemId;
        this.level = level;
        this.nextItemId = nextItemId;
        this.cursedNextItemId = cursedNextItemId;
    }


    public int getItemId() {
        return itemId;
    }

    public int getLevel() {
        return level;
    }

    public int getNextItemId() {
        return nextItemId;
    }

    public int getCursedNextItemId() {
        return cursedNextItemId;
    }

    @Override
    public String toString() {
        return "SoulCrystal{" +
                "itemId=" + itemId +
                ", occupation=" + level +
                ", nextItemId=" + nextItemId +
                ", cursedNextItemId=" + cursedNextItemId +
                '}';
    }
}
