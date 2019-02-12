package l2trunk.gameserver.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public final class ProductItem {
    // Базовые параметры, если продукт не имеет лимита времени продаж
    private static final long NOT_LIMITED_START_TIME = 315547200000L;
    private static final long NOT_LIMITED_END_TIME = 2127445200000L;
    private static final int NOT_LIMITED_START_HOUR = 0;
    private static final int NOT_LIMITED_END_HOUR = 23;
    private static final int NOT_LIMITED_START_MIN = 0;
    private static final int NOT_LIMITED_END_MIN = 59;

    private final int productId;
    private final int category;
    private final int points;
    private final int tabId;

    private final long startTimeSale;
    private final long endTimeSale;
    private final int startHour;
    private final int endHour;
    private final int startMin;
    private final int endMin;

    private List<ProductItemComponent> _components;

    public ProductItem(int productId, int category, int points, int tabId, long startTimeSale, long endTimeSale) {
        this.productId = productId;
        this.category = category;
        this.points = points;
        this.tabId = tabId;

        Calendar calendar;
        if (startTimeSale > 0) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTimeSale);

            this.startTimeSale = startTimeSale;
            startHour = calendar.get(Calendar.HOUR_OF_DAY);
            startMin = calendar.get(Calendar.MINUTE);
        } else {
            this.startTimeSale = NOT_LIMITED_START_TIME;
            startHour = NOT_LIMITED_START_HOUR;
            startMin = NOT_LIMITED_START_MIN;
        }

        if (endTimeSale > 0) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(endTimeSale);

            this.endTimeSale = endTimeSale;
            endHour = calendar.get(Calendar.HOUR_OF_DAY);
            endMin = calendar.get(Calendar.MINUTE);
        } else {
            this.endTimeSale = NOT_LIMITED_END_TIME;
            endHour = NOT_LIMITED_END_HOUR;
            endMin = NOT_LIMITED_END_MIN;
        }
    }

    public List<ProductItemComponent> getComponents() {
        if (_components == null) {
            _components = new ArrayList<>();
        }

        return _components;
    }

    public void setComponents(List<ProductItemComponent> a) {
        _components = a;
    }

    public int getProductId() {
        return productId;
    }

    public int getCategory() {
        return category;
    }

    public int getPoints() {
        return points;
    }

    public int getTabId() {
        return tabId;
    }

    public long getStartTimeSale() {
        return startTimeSale;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public long getEndTimeSale() {
        return endTimeSale;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMin() {
        return endMin;
    }
}
