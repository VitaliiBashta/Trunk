package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.model.premium.PremiumAccount;

import java.util.ArrayList;
import java.util.List;

public final class PremiumHolder extends AbstractHolder {
    private static final PremiumHolder _instance = new PremiumHolder();
    private final List<PremiumAccount> _premium = new ArrayList<>();

    public static PremiumHolder getInstance() {
        return _instance;
    }

    public void addPremium(PremiumAccount premium) {
        _premium.add(premium);
    }

    public List<PremiumAccount> getAllPremiums() {
        return _premium;
    }

    public PremiumAccount getPremium(int id) {
        for (PremiumAccount premium : _premium) {
            if (premium.getId() == id) {
                return premium;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return _premium.size();
    }

    @Override
    public void clear() {
        _premium.clear();
    }
}
