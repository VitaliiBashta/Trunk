package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.data.xml.holder.ClassesStatsBalancerHolder;
import l2trunk.gameserver.data.xml.newreader.IXmlReader;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.network.serverpackets.UserInfo;
import l2trunk.gameserver.stats.Stats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public final class ClassesStatsBalancerParser implements IXmlReader {
    private static final Logger LOG = LoggerFactory.getLogger(ClassesStatsBalancerParser.class.getName());
    private final Map<Integer, Map<Stats, ClassesStatsBalancerHolder>> _balance = new HashMap<>();

    private ClassesStatsBalancerParser() {
        load();
    }

    public static ClassesStatsBalancerParser getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        _balance.clear();
        parseDatapackFile("config/balancer/ClassesStatsBalancer.xml");
    }

    public void reload() {
        load();
        synchronizePlayers();
    }

    @Override
    public void parseDocument(Document doc) {
        int classes = 0;
        NamedNodeMap attrs;
        Node attr;
        StatsSet set;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("class".equalsIgnoreCase(d.getNodeName())) {
                        int id = parseInteger(d.getAttributes(), "id");

                        if (!_balance.containsKey(id)) {
                            _balance.put(id, new HashMap<>());
                        }

                        for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                            if ("stat".equalsIgnoreCase(cd.getNodeName())) {
                                attrs = cd.getAttributes();
                                set = new StatsSet();
                                for (int i = 0; i < attrs.getLength(); i++) {
                                    attr = attrs.item(i);
                                    set.set(attr.getNodeName(), attr.getNodeValue());
                                }
                                ClassesStatsBalancerHolder data = new ClassesStatsBalancerHolder(set);
                                _balance.get(id).put(data.getStat(), data);
                                classes++;
                            }
                        }
                    }
                }
            }
        }
        LOG.info(": Loaded: " + classes + " balances for " + _balance.size() + " classes.");
    }

    private void synchronizePlayers() {
        GameObjectsStorage.getAllPlayersStream().forEach(p -> {
            p.updateStats();
            p.broadcastUserInfo(true);
            p.broadcastCharInfo();
            p.broadcastStatusUpdate();
            UserInfo info2 = new UserInfo(p);
            p.sendPacket(info2);
        });
        LOG.info(getClass().getSimpleName() + ": Synchronize Players in game done.");
    }

    public ClassesStatsBalancerHolder getBalanceForClass(int classId, Stats stat) {
        return (_balance.containsKey(classId) ? _balance.get(classId).get(stat) : null);
    }

    private static class SingletonHolder {
        static final ClassesStatsBalancerParser _instance = new ClassesStatsBalancerParser();
    }
}