package l2trunk.gameserver.stats;

import l2trunk.gameserver.stats.funcs.Func;
import l2trunk.gameserver.stats.funcs.FuncTemplate;
import l2trunk.gameserver.stats.triggers.TriggerInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatTemplate {
    protected List<FuncTemplate> _funcTemplates = new ArrayList<>();
    private List<TriggerInfo> _triggerList = new ArrayList<>();

    public List<TriggerInfo> getTriggerList() {
        return _triggerList;
    }

    public void addTrigger(TriggerInfo f) {
        _triggerList.add(f);
    }

    public void attachFunc(FuncTemplate f) {
        if (_funcTemplates.isEmpty())
            _funcTemplates = new ArrayList<>();
        _funcTemplates.add(f);
    }

    public void clearAttachedFuncs() {
        _funcTemplates.clear();
    }

    public List<FuncTemplate> getAttachedFuncs() {
        return _funcTemplates;
    }

    public List<Func> getStatFuncs(Object owner) {

        _funcTemplates.forEach(a -> a.getFunc(owner));


        if (_funcTemplates.size() == 0)
            return Collections.emptyList();

        List<Func> funcs = new ArrayList<>();
        for (FuncTemplate _funcTemplate : _funcTemplates) {
            funcs.add(_funcTemplate.getFunc(owner));
        }
        return funcs;

//        if (_funcTemplates.length == 0)
//            return Func.EMPTY_FUNC_ARRAY;
//
//        Func[] funcs = new Func[_funcTemplates.length];
//        for (int i = 0; i < funcs.length; i++) {
//            funcs[i] = _funcTemplates[i].getFunc(owner);
//        }
//        return funcs;
    }
}
