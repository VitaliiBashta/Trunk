package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.PremiumHolder;
import l2trunk.gameserver.model.premium.PremiumAccount;
import l2trunk.gameserver.model.premium.PremiumGift;
import l2trunk.gameserver.model.premium.PremiumKeys;
import l2trunk.gameserver.model.premium.PremiumRemoveItems;
import l2trunk.gameserver.utils.TimeUtils;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class PremiumParser extends AbstractFileParser<PremiumHolder> {
    private static final PremiumParser _instance = new PremiumParser();

    private PremiumParser() {
        super(PremiumHolder.getInstance());
    }

    public static PremiumParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/premium/premium.xml");
    }

    @Override
    public String getDTDFileName() {
        return "premium.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<org.dom4j.Element> iterator = rootElement.elementIterator("premium"); iterator.hasNext(); ) {
            List<PremiumGift> list = new ArrayList<>();

            Element premium = iterator.next();
            int id = Integer.parseInt(premium.attributeValue("id"));
            String name = premium.attributeValue("name");
            String icon = premium.attributeValue("icon");

            int time = parseTime(premium.element("time"));

            Element gifts = premium.element("gifts");
            parseGifts(gifts, list);

            Element price = premium.element("price");
            int price_id = Integer.parseInt(price.attributeValue("id"));
            long price_count = Long.parseLong(price.attributeValue("count"));

            PremiumAccount pa = new PremiumAccount(id, time, name, icon, list, price_id, price_count);
            parseRates(premium, pa);

            getHolder().addPremium(pa);
        }
    }

    private int parseTime(Element times) {
        int days = Integer.parseInt(times.attributeValue("days"));
        int hours = Integer.parseInt(times.attributeValue("hours"));
        int minutes = Integer.parseInt(times.attributeValue("minutes"));

        return (int) ((TimeUtils.addDay(days) + TimeUtils.addHours(hours) + TimeUtils.addMinutes(minutes)) / 1000L);
    }

    private void parseGifts(Element gifts, List<PremiumGift> list) {
        for (Iterator<org.dom4j.Element> it = gifts.elementIterator(); it.hasNext(); ) {
            Element gift = it.next();

            if ("gift".equalsIgnoreCase(gift.getName())) {
                int id = Integer.parseInt(gift.attributeValue("id"));
                long count = Long.parseLong(gift.attributeValue("count"));
                boolean removable = Boolean.parseBoolean(gift.attributeValue("removable"));
                PremiumGift _gift = new PremiumGift(id, count, removable);
                list.add(_gift);

                if (removable)
                    PremiumRemoveItems.getInstance().add(_gift);
            }
        }
    }

    private void parseRates(Element premium, PremiumAccount pa) {
        for (Iterator<org.dom4j.Element> it = premium.elementIterator(); it.hasNext(); ) {
            Element rate = it.next();

            if ("rate".equalsIgnoreCase(rate.getName())) {
                String type = rate.attributeValue("type");
                PremiumKeys key = PremiumKeys.find(type);
                if (key != null)
                    pa.setRate(key, rate.attributeValue("value"));
                else
                    this._log.error("Try parse rate by type: " + type, this);
            }
        }
    }
}