package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.templates.SoulCrystal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.FIELD)
public final class SoulCrystalHolder {
    private static final Logger _log = LoggerFactory.getLogger(SoulCrystalHolder.class);
    private static SoulCrystalHolder instance;
    private static Path path = Paths.get("./data/soul_crystals.xml");
    @XmlTransient
    private final Map<Integer, SoulCrystal> crystals = new HashMap<>();
    //    private final Path path = Config.DATAPACK_ROOT.resolve("data/soul_crystals.xml");
    @XmlElement(name = "crystal")
    private List<SoulCrystal> list;

    public SoulCrystalHolder() {
    }

    public static SoulCrystalHolder getInstance() {
        if (instance != null)
            return instance;

        instance = Unmarshaller.unMarshall(path, SoulCrystalHolder.class);

        instance.list.forEach(c -> instance.crystals.put(c.getItemId(), c));
        _log.info("Loaded " + instance.crystals.size() + " Soul crystals by JAXB");
        return instance;
    }

    public SoulCrystal getCrystal(int item) {
        return crystals.get(item);
    }

    public Collection<SoulCrystal> getCrystals() {
        return crystals.values();
    }

    public int size() {
        return crystals.size();
    }

    public void clear() {
        crystals.clear();
    }
}
