package l2trunk.gameserver.data.xml.parser;

import l2trunk.gameserver.data.xml.holder.NpcStatsBalancerHolder;
import l2trunk.gameserver.data.xml.newreader.IXmlReader;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public final class NpcStatsBalancerParser implements IXmlReader {
    private static final Logger _log = LoggerFactory.getLogger(NpcStatsBalancerParser.class);
    private final Map<Integer, Map<Stats, NpcStatsBalancerHolder>> _monsterXmlStats = new HashMap<>();

    private NpcStatsBalancerParser() {
        load();
    }

    public static NpcStatsBalancerParser getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        _monsterXmlStats.clear();
        parseDatapackFile("config/balancer/NpcStatsBalancer.xml");
        _log.info(getClass().getSimpleName() + ": Loaded " + _monsterXmlStats.size() + " monsterXmlStats.");
    }

    public void reload() {
        load();
    }

    @Override
    public void parseDocument(Document doc) {
        NamedNodeMap attrs;
        Node attr;
        StatsSet set;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("npc".equalsIgnoreCase(d.getNodeName())) {
                        int id = parseInteger(d.getAttributes(), "id");
                        //System.out.println(id);
                        if (!_monsterXmlStats.containsKey(id)) {
                            _monsterXmlStats.put(id, new HashMap<>());
                        }
                        for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                            if ("stat".equalsIgnoreCase(cd.getNodeName())) {
                                attrs = cd.getAttributes();
                                set = new StatsSet();
                                for (int i = 0; i < attrs.getLength(); i++) {
                                    attr = attrs.item(i);
                                    set.set(attr.getNodeName(), attr.getNodeValue());
                                }
                                NpcStatsBalancerHolder data = new NpcStatsBalancerHolder(set);
                                _monsterXmlStats.get(id).put(data.getStat(), data);
                            }
                        }
                    }
                }
            }
        }
    }

    public NpcStatsBalancerHolder getXmlStatsForNpc(int classId, Stats stat) {
        return (_monsterXmlStats.containsKey(classId) ? _monsterXmlStats.get(classId).get(stat) : null);
    }

    private static class SingletonHolder {
        static final NpcStatsBalancerParser _instance = new NpcStatsBalancerParser();
    }
}
