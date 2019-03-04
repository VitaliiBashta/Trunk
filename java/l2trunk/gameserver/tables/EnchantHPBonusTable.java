package l2trunk.gameserver.tables;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class EnchantHPBonusTable {
    private static final Logger LOG = LoggerFactory.getLogger(EnchantHPBonusTable.class);
    private static final Map<Integer, List<Integer>> armorHPBonus = new HashMap<>();
    private static int onepieceFactor = 100;

    public static void init() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            Path file = Config.DATAPACK_ROOT.resolve("data/enchant_bonus.xml");
            Document doc = factory.newDocumentBuilder().parse(file.toFile());

            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
                if ("list".equalsIgnoreCase(n.getNodeName()))
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        NamedNodeMap attrs = d.getAttributes();
                        Node att;
                        if ("options".equalsIgnoreCase(d.getNodeName())) {
                            att = attrs.getNamedItem("onepiece_factor");
                            if (att == null) {
                                LOG.info("EnchantHPBonusTable: Missing onepiece_factor, skipping");
                                continue;
                            }
                            onepieceFactor = toInt(att.getNodeValue());
                        } else if ("enchant_bonus".equalsIgnoreCase(d.getNodeName())) {
                            Integer grade;

                            att = attrs.getNamedItem("grade");
                            if (att == null) {
                                LOG.info("EnchantHPBonusTable: Missing grade, skipping");
                                continue;
                            }
                            grade = Integer.parseInt(att.getNodeValue());

                            att = attrs.getNamedItem("values");
                            if (att == null) {
                                LOG.info("EnchantHPBonusTable: Missing bonus id: " + grade + ", skipping");
                                continue;
                            }
                            StringTokenizer st = new StringTokenizer(att.getNodeValue(), ",");
                            int tokenCount = st.countTokens();
                            List<Integer> bonus = new ArrayList<>();
                            for (int i = 0; i < tokenCount; i++) {
                                Integer value = Integer.decode(st.nextToken().trim());
                                if (value == null) {
                                    LOG.info("EnchantHPBonusTable: Bad Hp value!! grade: " + grade + " token: " + i);
                                    value = 0;
                                }
                                bonus.add(value);
                            }
                            armorHPBonus.put(grade, bonus);
                        }
                    }
            LOG.info("EnchantHPBonusTable: Loaded bonuses for " + armorHPBonus.size() + " grades.");
        } catch (DOMException | SAXException | ParserConfigurationException | NumberFormatException | IOException e) {
            LOG.warn("EnchantHPBonusTable: Lists could not be initialized.", e);
        }
    }

    public static int getHPBonus(ItemInstance item) {
        final List<Integer> bonuses;

        if (item.getEnchantLevel() == 0)
            return 0;

        bonuses = armorHPBonus.get(item.getTemplate().getCrystalType().externalOrdinal);

        if (bonuses == null || bonuses.size() == 0)
            return 0;

        int bonus = bonuses.get(Math.min(item.getEnchantLevel(), bonuses.size()) - 1);
        if (item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
            bonus = (int) (bonus * onepieceFactor / 100.0D);

        return bonus;
    }
}
