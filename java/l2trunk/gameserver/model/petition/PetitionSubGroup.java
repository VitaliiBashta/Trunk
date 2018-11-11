package l2trunk.gameserver.model.petition;

import l2trunk.gameserver.handler.petition.IPetitionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetitionSubGroup extends PetitionGroup {
    private static final Logger LOG = LoggerFactory.getLogger(PetitionSubGroup.class);
    private IPetitionHandler _handler;

    public PetitionSubGroup(int id, String handler) {
        super(id);

        Class<?> clazz = null;

        try {
            clazz = Class.forName("l2trunk.scripts.handler.petition." + handler);
            _handler = (IPetitionHandler) clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            LOG.error("Error while creating PetitionSubGroup: ", e);
            throw new Error(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public IPetitionHandler getHandler() {
        return _handler;
    }
}
