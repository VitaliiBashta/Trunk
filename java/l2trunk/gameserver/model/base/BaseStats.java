package l2trunk.gameserver.model.base;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum BaseStats {
    CON {
        @Override
        public final double calcBonus(Creature actor) {
            return actor == null ? 1. : CONbonus.get(actor.getCON());
        }
    },
    DEX {
        @Override
        public final double calcBonus(Creature actor) {
            return actor == null ? 1. : DEXbonus.get(actor.getDEX());
        }
    },
    INT {
        @Override
        public final double calcBonus(Creature actor) {
            return actor == null ? 1. : INTbonus.get(actor.getINT());
        }
    },
    MEN {
        @Override
        public final double calcBonus(Creature actor) {
            return actor == null ? 1. : MENbonus.get(actor.getMEN());
        }
    },
    NONE {
        @Override
        public double calcBonus(Creature actor) {
            return 1;
        }
    },
    STR {
        @Override
        public final double calcBonus(Creature actor) {
            return actor == null ? 1. : STRbonus.get(actor.getSTR());
        }

        @Override
        public final double calcChanceMod(Creature actor) {
            return Math.min(2. - Math.sqrt(calcBonus(actor)), 1.);
        }
    }// не более 1
    ,
    WIT {
        @Override
        public final double calcBonus(Creature actor) {
            return actor == null ? 1. : WITbonus.get(actor.getWIT());
        }
    };

    private static final Logger _log = LoggerFactory.getLogger(BaseStats.class);

    private static final int MAX_STAT_VALUE = 100;

    private static final List<Double> STRbonus = new ArrayList<>();
    private static final List<Double> INTbonus = new ArrayList<>();
    private static final List<Double> DEXbonus = new ArrayList<>();
    private static final List<Double> WITbonus = new ArrayList<>();
    private static final List<Double> CONbonus = new ArrayList<>();
    private static final List<Double> MENbonus = new ArrayList<>();

    static {
        for (int i = 0; i < MAX_STAT_VALUE; i++) {
            STRbonus.add(0.);
            INTbonus.add(0.);
            DEXbonus.add(0.);
            WITbonus.add(0.);
            CONbonus.add(0.);
            MENbonus.add(0.);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        Path file = Config.DATAPACK_ROOT.resolve("data/attribute_bonus.xml");
        Document doc = null;

        try {
            doc = factory.newDocumentBuilder().parse(file.toFile());
        } catch (SAXException | IOException | ParserConfigurationException e) {
            _log.error("Error while loading attribute_bonus!", e);
        }

        if (doc != null)
            for (Node z = doc.getFirstChild(); z != null; z = z.getNextSibling())
                for (Node n = z.getFirstChild(); n != null; n = n.getNextSibling()) {
                    String nodeName = n.getNodeName().toLowerCase();
                    switch (nodeName) {
                        case "str_bonus":
                            getAttributes(n, STRbonus);
                            break;
                        case "int_bonus":
                            getAttributes(n, INTbonus);
                            break;
                        case "con_bonus":
                            getAttributes(n, CONbonus);
                            break;
                        case "men_bonus":
                            getAttributes(n, MENbonus);
                            break;
                        case "dex_bonus": {
                            getAttributes(n, DEXbonus);
                            break;
                        }
                        case "wit_bonus":
                            getAttributes(n, WITbonus);
                            break;
                    }
                }
    }

    private static void getAttributes(Node n, List<Double> statBonus) {
        int i;
        double val;
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            if (d.getNodeName().equals("set")) {
                i = toInt(d.getAttributes().getNamedItem("attribute").getNodeValue());
                val = toInt(d.getAttributes().getNamedItem("val").getNodeValue());
                statBonus.set(i, (100 + val) / 100);
            }
        }
    }

    public abstract double calcBonus(Creature actor);
//    {
//        return 1.;
//    }

    public double calcChanceMod(Creature actor) {
        return 2. - Math.sqrt(calcBonus(actor));
    }
}