package l2trunk.scripts.ai;

import l2trunk.gameserver.GameTimeController;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.listener.game.OnDayNightChangeListener;
import l2trunk.gameserver.model.instances.NpcInstance;

/**
 * АИ для мобов, меняющих агресивность в ночное время.<BR>
 * Наследуется на прямую от Mystic.
 *
 */
public final class NightAgressionMystic extends Mystic {
    public NightAgressionMystic(NpcInstance actor) {
        super(actor);
        GameTimeController.INSTANCE.addListener(new NightAgressionDayNightListener());
    }

    private class NightAgressionDayNightListener implements OnDayNightChangeListener {
        private NightAgressionDayNightListener() {
            if (GameTimeController.INSTANCE.isNowNight())
                onNight();
            else
                onDay();
        }

        /**
         * Вызывается, когда на сервере наступает день
         */
        @Override
        public void onDay() {
            getActor().setAggroRange(0);
        }

        /**
         * Вызывается, когда на сервере наступает ночь
         */
        @Override
        public void onNight() {
            getActor().setAggroRange(-1);
        }
    }
}