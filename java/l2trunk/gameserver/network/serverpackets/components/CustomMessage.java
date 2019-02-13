package l2trunk.gameserver.network.serverpackets.components;

import l2trunk.gameserver.data.StringHolder;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CustomMessage {
    private static final Logger _log = LoggerFactory.getLogger(CustomMessage.class);

    private String text;
    private int mark = 0;

    public CustomMessage(String text) {
        this.text = text;
    }

    public CustomMessage(String address, Object... args) {
        text = StringHolder.INSTANCE.getNotNull(address);
        add(args);
    }

    public CustomMessage addNumber(long number) {
        text = text.replace("{" + mark + "}", String.valueOf(number));
        mark++;
        return this;
    }

    private void add(Object... args) {
        for (Object arg : args)
            if (arg instanceof String)
                addString((String) arg);
            else if (arg instanceof Integer)
                addNumber((Integer) arg);
            else if (arg instanceof Long)
                addNumber((Long) arg);
            else if (arg instanceof ItemTemplate)
                addItemName((ItemTemplate) arg);
            else if (arg instanceof ItemInstance)
                addItemName((ItemInstance) arg);
            else if (arg instanceof Creature)
                addCharName((Creature) arg);
            else if (arg instanceof Skill)
                this.addSkillName((Skill) arg);
            else {
                _log.warn("unknown CustomMessage arg type: " + arg);
                Thread.dumpStack();
            }

    }

    public CustomMessage addString(String str) {
        text = text.replace("{" + mark + "}", str);
        mark++;
        return this;
    }

    private CustomMessage addSkillName(Skill skill) {
        text = text.replace("{" + mark + "}", skill.name);
        mark++;
        return this;
    }

    public CustomMessage addSkillName(int skillId, int skillLevel) {
        return addSkillName(SkillTable.INSTANCE.getInfo(skillId, skillLevel));
    }

    public CustomMessage addItemName(ItemTemplate item) {
        text = text.replace("{" + mark + "}", item.getName());
        mark++;
        return this;
    }

    public CustomMessage addItemName(int itemId) {
        return addItemName(ItemHolder.getTemplate(itemId));
    }

    private void addItemName(ItemInstance item) {
        addItemName(item.getTemplate());
    }

    private CustomMessage addCharName(Creature cha) {
        text = text.replace("{" + mark + "}", cha.getName());
        mark++;
        return this;
    }

    @Override
    public String toString() {
        return text;
    }
}