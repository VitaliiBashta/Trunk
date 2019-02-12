package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Recipe;
import l2trunk.gameserver.model.RecipeComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static l2trunk.commons.lang.NumberUtils.toBoolean;
import static l2trunk.commons.lang.NumberUtils.toInt;

public final class RecipeHolder {
    private static final Logger LOG = LoggerFactory.getLogger(RecipeHolder.class);
    private static RecipeHolder instance;

    private final ConcurrentHashMap<Integer, Recipe> listByRecipeId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Recipe> listByRecipeItem = new ConcurrentHashMap<>();

    private RecipeHolder() {
        listByRecipeId.clear();
        listByRecipeItem.clear();
        try {
            loadFromXML();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            LOG.error("Error while loading Recipes From XML ", e);
        }
    }

    public static RecipeHolder getInstance() {
        if (instance == null)
            instance = new RecipeHolder();
        return instance;
    }

    private void loadFromXML() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        Path file = Config.DATAPACK_ROOT.resolve("data/recipes.xml");
        if (Files.exists(file)) {
            Document doc = factory.newDocumentBuilder().parse(file.toFile());
            List<RecipeComponent> recipePartList = new ArrayList<>();
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if ("list".equalsIgnoreCase(n.getNodeName())) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if ("recipes".equalsIgnoreCase(d.getNodeName())) {
                            recipePartList.clear();
                            NamedNodeMap attrs = d.getAttributes();
                            Node att;
                            StatsSet set = new StatsSet();

                            att = attrs.getNamedItem("id");
                            if (att == null) {
                                LOG.error("Missing id for recipe item, skipping");
                                continue;
                            }
                            int id = toInt(att.getNodeValue(), -1);
                            set.set("id", id);

                            att = attrs.getNamedItem("level");
                            if (att == null) {
                                LOG.error("Missing  level for recipe item id: " + id + ", skipping");
                                continue;
                            }
                            set.set("level", toInt(att.getNodeValue()));

                            att = attrs.getNamedItem("recid");
                            if (att == null) {
                                LOG.error("Missing recid for recipe item id: " + id + ", skipping");
                                continue;
                            }
                            set.set("recid", toInt(att.getNodeValue()));

                            att = attrs.getNamedItem("recipeName");
                            if (att == null) {
                                LOG.error("Missing recipeName for recipe item id: " + id + ", skipping");
                                continue;
                            }
                            set.set("recipeName", att.getNodeValue());

                            att = attrs.getNamedItem("successRate");
                            if (att == null) {
                                LOG.error("Missing successRate for recipe item id: " + id + ", skipping");
                                continue;
                            }
                            set.set("successRate", toInt(att.getNodeValue()));

                            att = attrs.getNamedItem("mp");
                            if (att == null) {
                                LOG.error("Missing mp for recipe item id: " + id + ", skipping");
                                continue;
                            }
                            set.set("mp", toInt(att.getNodeValue()));

                            att = attrs.getNamedItem("itemId");
                            if (att == null) {
                                LOG.error("Missing itemId for recipe item id: " + id + ", skipping");
                                continue;
                            }
                            set.set("itemId", Short.parseShort(att.getNodeValue()));

                            att = attrs.getNamedItem("foundation");
                            if (att == null) {
                                LOG.error("Missing foundation for recipe item id: " + id + ", skipping");
                                continue;
                            }
                            set.set("foundation", Short.parseShort(att.getNodeValue()));

                            att = attrs.getNamedItem("count");
                            if (att == null) {
                                LOG.error("Missing count for recipe item id: " + id + ", skipping");
                                continue;
                            }
                            set.set("count", Short.parseShort(att.getNodeValue()));

                            att = attrs.getNamedItem("exp");
                            if (att == null) {
                                LOG.error("Missing exp for recipe item id: " + id + ", skipping");
                                continue;
                            }
                            set.set("exp", Long.parseLong(att.getNodeValue()));

                            att = attrs.getNamedItem("sp");
                            if (att == null) {
                                LOG.error("Missing sp for recipe item id: " + id + ", skipping");
                                continue;
                            }
                            set.set("sp", toInt(att.getNodeValue()));

                            att = attrs.getNamedItem("dwarven");
                            if (att == null) {
                                LOG.error("Missing type for recipe item id: " + id + ", skipping");
                                continue;
                            }
                            set.set("isDvarvenCraft", toBoolean(att.getNodeValue()));

                            for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                                if ("recitem".equalsIgnoreCase(c.getNodeName())) {
                                    int rpItemId = toInt(c.getAttributes().getNamedItem("item").getNodeValue());
                                    int quantity = toInt(c.getAttributes().getNamedItem("icount").getNodeValue());
                                    recipePartList.add(new RecipeComponent(rpItemId, quantity));
                                }
                            }

                            int level = set.getInteger("level");
                            int recipeId = set.getInteger("recid");
                            String recipeName = set.getString("recipeName");
                            int successRate = set.getInteger("successRate");
                            int mpCost = set.getInteger("mp");
                            int itemId = set.getInteger("itemId");
                            int count = set.getInteger("count");
                            int foundation = set.getInteger("foundation");
                            long exp = set.getLong("exp");
                            long sp = set.getLong("sp");
                            boolean isDvarvenCraft = set.getBool("isDvarvenCraft");

                            Recipe recipeList = new Recipe(id, level, recipeId, recipeName, successRate, mpCost, itemId, foundation, count, exp, sp, isDvarvenCraft);
                            for (RecipeComponent recipePart : recipePartList) {
                                recipeList.addRecipe(recipePart);
                            }
                            listByRecipeId.put(id, recipeList);
                            listByRecipeItem.put(recipeId, recipeList);
                        }
                    }
                }
            }
            LOG.info("RecipeController: Loaded " + listByRecipeId.size() + " Recipes.");
        } else {
            LOG.error("Recipes file (" + file.toAbsolutePath() + ") doesnt exists.");
        }
    }

    public Stream<Recipe> getRecipes() {
        return listByRecipeId.values().stream();
    }

    public Recipe getRecipeByRecipeId(int listId) {
        return listByRecipeId.get(listId);
    }

    public Recipe getRecipeByRecipeItem(int itemId) {
        return listByRecipeItem.get(itemId);
    }
}