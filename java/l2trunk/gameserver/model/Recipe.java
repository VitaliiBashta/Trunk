package l2trunk.gameserver.model;

import l2trunk.commons.lang.Pair;

import java.util.ArrayList;
import java.util.List;

public final class Recipe {
    public final int id;
    public final int level;
    public final int recipeId;
    public final String recipeName;
    public final int successRate;
    public final int mpCost;
    public final int itemId;
    public final int foundation;

    public final int count;
    public final boolean isDwarvenCraft;
    /**
     * The table containing all l2fecipeInstance (1 line of the recipe : Item-Quantity needed) of the l2fecipeList
     */
    private List<Pair<Integer,Integer>> recipes;

    public Recipe(int id, int level, int recipeId, String recipeName, int successRate, int mpCost, int itemId, int foundation, int count, long exp, long sp, boolean isDwarvenCraft) {
        this.id = id;
        recipes = new ArrayList<>();
        this.level = level;
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.successRate = successRate;
        this.mpCost = mpCost;
        this.itemId = itemId;
        this.foundation = foundation;
        this.count = count;
        this.isDwarvenCraft = isDwarvenCraft;
    }
    public void addRecipe(Pair<Integer, Integer> recipe) {
        recipes.add(recipe);
    }
    /**
     * Return the table containing all l2fecipeInstance (1 line of the recipe : Item-Quantity needed) of the l2fecipeList.<BR><BR>
     */
    public List<Pair<Integer,Integer>> getRecipes() {
        return recipes;
    }

}