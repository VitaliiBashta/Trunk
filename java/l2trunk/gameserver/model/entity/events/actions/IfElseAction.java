package l2trunk.gameserver.model.entity.events.actions;

import l2trunk.gameserver.model.entity.events.EventAction;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

import java.util.Collections;
import java.util.List;

public final class IfElseAction implements EventAction {
    private final String name;
    private final boolean reverse;
    private List<EventAction> ifList = Collections.emptyList();
    private List<EventAction> elseList = Collections.emptyList();

    public IfElseAction(String name, boolean reverse) {
        this.name = name;
        this.reverse = reverse;
    }

    @Override
    public void call(GlobalEvent event) {
        List<EventAction> list = (reverse != event.ifVar(name)) ? ifList : elseList;
        list.forEach(action -> action.call(event));
    }

    public void setIfList(List<EventAction> ifList) {
        this.ifList = ifList;
    }

    public void setElseList(List<EventAction> elseList) {
        this.elseList = elseList;
    }
}
