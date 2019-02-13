package l2trunk.gameserver.model.exchange;

import java.util.List;

public class Change {
    private final int id;
    private final String name;
    private final String icon;
    private final int cost_id;
    private final long cost_count;
    private final boolean attribute_change;
    private final boolean is_upgrade;
    private final List<Variant> variants;

    public Change(int id, String name, String icon, int cost_id, long cost_count, boolean attribute_change, boolean is_upgrade, List<Variant> variants) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.cost_id = cost_id;
        this.cost_count = cost_count;
        this.attribute_change = attribute_change;
        this.is_upgrade = is_upgrade;
        this.variants = variants;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public int getCostId() {
        return cost_id;
    }

    public long getCostCount() {
        return cost_count;
    }

    public boolean attChange() {
        return attribute_change;
    }

    public boolean isUpgrade() {
        return is_upgrade;
    }

    public List<Variant> getList() {
        return variants;
    }

    public Variant getVariant(int id) {
        return variants.stream()
                .filter(var -> var.number == id)
                .findFirst().orElse(null);
    }
}
