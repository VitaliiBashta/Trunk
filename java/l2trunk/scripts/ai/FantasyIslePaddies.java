package l2trunk.scripts.ai;

import l2trunk.gameserver.GameTimeController;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.listener.game.OnDayNightChangeListener;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.FantasiIsleParadEvent;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class FantasyIslePaddies extends CharacterAI {
    public FantasyIslePaddies(NpcInstance actor) {
        super(actor);
        GameTimeController.INSTANCE.addListener(new StartEvent());
    }

    private class StartEvent implements OnDayNightChangeListener {
        private StartEvent() {
            if (GameTimeController.INSTANCE.isNowNight())
                onNight();
            else
                onDay();
        }

        /**
         * Вызывается, когда на сервере наступает ночь
         */
        @Override
        public void onNight() {
            NpcInstance actor = (NpcInstance) getActor();
            if (actor != null) {
                FantasiIsleParadEvent n_event = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 10031);
                FantasiIsleParadEvent d_event = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 10032);
                n_event.registerActions();
                d_event.stopEvent();
            }
        }

        /**
         * Вызывается, когда на сервере наступает день
         */
        @Override
        public void onDay() {
            NpcInstance actor = (NpcInstance) getActor();
            if (actor != null) {
                FantasiIsleParadEvent n_event = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 10031);
                FantasiIsleParadEvent d_event = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 10032);
                n_event.stopEvent();
                d_event.registerActions();
            }
        }
    }
}