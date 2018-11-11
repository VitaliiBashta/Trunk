package l2trunk.gameserver.data;

import java.util.List;
import java.util.Optional;

public class HtmPropList {
    private final List<HtmProp> list;

    HtmPropList(List<HtmProp> list) {
        this.list = list;
    }

    public String getText(String keyWord) {
        Optional<HtmProp> text = list
                .stream()
                .filter(a -> a.getKeyWord().equals(keyWord))
                .findFirst();
        return text.isPresent() ? text.get().getText() : "";
    }
}