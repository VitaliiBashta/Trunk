package l2trunk.gameserver.stats;

import l2trunk.gameserver.stats.funcs.Func;
import l2trunk.gameserver.stats.funcs.FuncTemplate;
import l2trunk.gameserver.stats.triggers.TriggerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StatTemplate {
    protected List<FuncTemplate> funcTemplates = new ArrayList<>();
    private List<TriggerInfo> triggerList = new ArrayList<>();

    public List<TriggerInfo> getTriggerList() {
        return triggerList;
    }

    public void addTrigger(TriggerInfo f) {
        triggerList.add(f);
    }

    public void attachFunc(FuncTemplate f) {
        if (funcTemplates.isEmpty())
            funcTemplates = new ArrayList<>();
        funcTemplates.add(f);
    }

    public void clearAttachedFuncs() {
        funcTemplates.clear();
    }

    public List<FuncTemplate> getAttachedFuncs() {
        return funcTemplates;
    }

    public Stream<Func> getStatFuncs(Object owner) {
        funcTemplates.forEach(a -> a.getFunc(owner));

        return funcTemplates.stream()
                .map(ft -> ft.getFunc(owner));
    }
}
