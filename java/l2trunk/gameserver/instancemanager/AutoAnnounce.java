package l2trunk.gameserver.instancemanager;

import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.AutoAnnounces;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public final class AutoAnnounce implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AutoAnnounce.class);
    private static HashMap<Integer, AutoAnnounces> _lists;
    private static AutoAnnounce _instance;

    public AutoAnnounce() {
        _lists = new HashMap<>();
        LOG.info("AutoAnnounce: Initializing");
        load();
        LOG.info("AutoAnnounce: Loaded " + _lists.size() + " announce.");
    }

    public static AutoAnnounce getInstance() {
        if (_instance == null)
            _instance = new AutoAnnounce();
        return _instance;
    }

    private void load() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            Path file = Config.CONFIG.resolve("autoannounce.xml");
            if (!Files.exists(file)) {
                LOG.warn("AutoAnnounce: NO FILE " + file.toAbsolutePath());
                return;
            }

            Document doc = factory.newDocumentBuilder().parse(file.toFile());
            int counterAnnounce = 0;
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
                if ("list".equalsIgnoreCase(n.getNodeName())) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if ("announce".equalsIgnoreCase(d.getNodeName())) {
                            ArrayList<String> msg = new ArrayList<>();
                            NamedNodeMap attrs = d.getAttributes();
                            int delay = Integer.parseInt(attrs.getNamedItem("delay").getNodeValue());
                            int repeat = Integer.parseInt(attrs.getNamedItem("repeat").getNodeValue());
                            AutoAnnounces aa = new AutoAnnounces(counterAnnounce);
                            for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                                if ("message".equalsIgnoreCase(cd.getNodeName()))
                                    msg.add(String.valueOf(cd.getAttributes().getNamedItem("text").getNodeValue()));
                            }
                            aa.setAnnounce(delay, repeat, msg);
                            _lists.put(counterAnnounce, aa);
                            counterAnnounce++;
                        }
                    }
                }
            LOG.info("AutoAnnounce: Load OK");
        } catch (DOMException | NumberFormatException | ParserConfigurationException | SAXException e) {
            LOG.warn("AutoAnnounce: Error parsing autoannounce.xml file. ", e);
        } catch (IOException e) {
            LOG.warn("AutoAnnounce: IOException parsing autoannounce.xml file. ", e);
        }
    }

    @Override
    public void run() {
        _lists.values().stream()
                .filter(AutoAnnounces::canAnnounce)
                .forEach(list -> {
                    list.getMessage()
                            .forEach(msg -> {
                                Announcements.INSTANCE.announceToAll(msg);
                                System.out.println(msg);
                            });
                    list.updateRepeat();
                });
    }
}