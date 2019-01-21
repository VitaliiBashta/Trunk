package l2trunk.gameserver.model.base;

import java.util.ArrayList;
import java.util.List;

public class MultiSellEntry {
    private final List<MultiSellIngredient> ingredients = new ArrayList<>();
    private final List<MultiSellIngredient> _production = new ArrayList<>();
    private int entryId;
    private long tax;

    public MultiSellEntry() {
    }

    public MultiSellEntry(int id) {
        entryId = id;
    }

    public MultiSellEntry(int id, int product, int prod_count, int enchant) {
        entryId = id;
        addProduct(new MultiSellIngredient(product, prod_count, enchant));
    }

    public int getEntryId() {
        return entryId;
    }

    /**
     * @param entryId The entryId to set.
     */
    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public void addIngredient(MultiSellIngredient ingredient) {
        if (ingredient.getItemCount() > 0)
            ingredients.add(ingredient);
    }

    public List<MultiSellIngredient> getIngredients() {
        return ingredients;
    }


    public void addProduct(MultiSellIngredient ingredient) {
        _production.add(ingredient);
    }

    /**
     * @return Returns the ingredients.
     */
    public List<MultiSellIngredient> getProduction() {
        return _production;
    }

    public long getTax() {
        return tax;
    }

    public void setTax(long tax) {
        this.tax = tax;
    }

    @Override
    public int hashCode() {
        return entryId;
    }

    @Override
    public MultiSellEntry clone() {
        MultiSellEntry ret = new MultiSellEntry(entryId);
        for (MultiSellIngredient i : ingredients)
            ret.addIngredient(i.clone());
        for (MultiSellIngredient i : _production)
            ret.addProduct(i.clone());
        return ret;
    }
}