package l2trunk.gameserver.data;

import java.util.List;

public final class HtmPropList {
    private final List<HtmProp> list;

    HtmPropList(List<HtmProp> list) {
        this.list = list;
    }

    public String getText(String keyWord) {
        return list.stream()
                .filter(a -> a.getKeyWord().equals(keyWord))
                .map(HtmProp::getText)
                .findFirst().orElse("");
    }
}