package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.skills.skillclasses.Transformation;
import l2trunk.gameserver.stats.Env;

public final class EffectTransformation extends Effect {
    private final boolean isFlyingTransform;

    public EffectTransformation(Env env, EffectTemplate template) {
        super(env, template);
        int id = (int) template.value;
        isFlyingTransform = template.getParam().getBool("isFlyingTransform", id == 8 || id == 9 || id == 260); // TODO сделать через параметр
    }

    @Override
    public boolean checkCondition() {
        if (effected instanceof Player) {
            return !isFlyingTransform || effected.getX() <= -166168;
        } else return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        Player player = (Player) effected;
        player.setTransformationTemplate(skill.npcId);
        if (skill instanceof Transformation)
            player.setTransformationName(((Transformation) skill).transformationName);

        int id = (int) calc();
        if (isFlyingTransform) {
            boolean isVisible = player.isVisible();
            if (player.getPet() != null)
                player.getPet().unSummon();
            player.decayMe();
            player.setFlying(true);
            player.setLoc(player.getLoc().addZ(300)); // Немного поднимаем чара над землей

            player.setTransformation(id);
            if (isVisible)
                player.spawnMe();
        } else
            player.setTransformation(id);
    }

    @Override
    public void onExit() {
        super.onExit();

        if (effected instanceof Player) {
            Player player = (Player) effected;

            if (skill instanceof Transformation)
                player.setTransformationName(null);

            if (isFlyingTransform) {
                boolean isVisible = player.isVisible();
                player.decayMe();
                player.setFlying(false);
                player.setLoc(player.getLoc().correctGeoZ());
                player.setTransformation(0);
                if (isVisible)
                    player.spawnMe();
            } else
                player.setTransformation(0);
        }
    }
}