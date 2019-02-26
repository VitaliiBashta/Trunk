package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2trunk.gameserver.utils.Strings;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class WhoAmI implements IVoicedCommandHandler {
    private static final List<String> _commandList = List.of("stats");

    @Override
    public List<String> getVoicedCommandList() {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(String command, Player player, String args) {
        Player playerToShow = player.isGM() && player.getTarget() != null && player.getTarget() instanceof Player ? (Player)player.getTarget(): player;

        // TODO [G1ta0] add reflective
        // TODO [G1ta0] may want to show the stats according to the purpose
        double hpRegen = Formulas.calcHpRegen(playerToShow);
        double cpRegen = Formulas.calcCpRegen(playerToShow);
        double mpRegen = Formulas.calcMpRegen(playerToShow);
        double hpDrain = playerToShow.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0., player, null);
        double mpDrain = playerToShow.calcStat(Stats.ABSORB_DAMAGEMP_PERCENT, 0., player, null);
        double hpGain = playerToShow.calcStat(Stats.HEAL_EFFECTIVNESS, 100., player, null);
        double mpGain = playerToShow.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., player, null);
        double critPerc = 2 * playerToShow.calcStat(Stats.CRITICAL_DAMAGE, player, null);
        double critStatic = playerToShow.calcStat(Stats.CRITICAL_DAMAGE_STATIC, player, null);
        double mCritRate = playerToShow.calcStat(Stats.MCRITICAL_RATE, player, null);
        double blowRate = playerToShow.calcStat(Stats.FATALBLOW_RATE, player, null);

        ItemInstance shld = playerToShow.getSecondaryWeaponInstance();
        boolean shield = shld != null && shld.getItemType() == WeaponType.NONE;

        double shieldDef = shield ? playerToShow.calcStat(Stats.SHIELD_DEFENCE, player.getTemplate().baseShldDef, player, null) : 0.;
        double shieldRate = shield ? playerToShow.calcStat(Stats.SHIELD_RATE, player, null) : 0.;

        double xpRate = Config.RATE_XP ;
        double spRate = Config.RATE_SP ;
        double dropRate = Config.RATE_DROP_ITEMS ;
        double adenaRate = Config.RATE_DROP_ADENA ;
        double spoilRate = Config.RATE_DROP_SPOIL ;
        double fireResist = playerToShow.calcStat(Element.FIRE.getDefence(), 0., player, null);
        double windResist = playerToShow.calcStat(Element.WIND.getDefence(), 0., player, null);
        double waterResist = playerToShow.calcStat(Element.WATER.getDefence(), 0., player, null);
        double earthResist = playerToShow.calcStat(Element.EARTH.getDefence(), 0., player, null);
        double holyResist = playerToShow.calcStat(Element.HOLY.getDefence(), 0., player, null);
        double unholyResist = playerToShow.calcStat(Element.UNHOLY.getDefence(), 0., player, null);

        double bleedPower = playerToShow.calcStat(Stats.BLEED_POWER, player, null);
        double bleedResist = playerToShow.calcStat(Stats.BLEED_RESIST, player, null);
        double poisonPower = playerToShow.calcStat(Stats.POISON_POWER, player, null);
        double poisonResist = playerToShow.calcStat(Stats.POISON_RESIST, player, null);
        double stunPower = playerToShow.calcStat(Stats.STUN_POWER, player, null);
        double stunResist = playerToShow.calcStat(Stats.STUN_RESIST, player, null);
        double rootPower = playerToShow.calcStat(Stats.ROOT_POWER, player, null);
        double rootResist = playerToShow.calcStat(Stats.ROOT_RESIST, player, null);
        double sleepPower = playerToShow.calcStat(Stats.SLEEP_POWER, player, null);
        double sleepResist = playerToShow.calcStat(Stats.SLEEP_RESIST, player, null);
        double paralyzePower = playerToShow.calcStat(Stats.PARALYZE_POWER, player, null);
        double paralyzeResist = playerToShow.calcStat(Stats.PARALYZE_RESIST, player, null);
        double mentalPower = playerToShow.calcStat(Stats.MENTAL_POWER, player, null);
        double mentalResist = playerToShow.calcStat(Stats.MENTAL_RESIST, player, null);
        double debuffPower = playerToShow.calcStat(Stats.DEBUFF_POWER, player, null);
        double debuffResist = playerToShow.calcStat(Stats.DEBUFF_RESIST, player, null);
        double cancelPower = playerToShow.calcStat(Stats.CANCEL_POWER, player, null);
        double cancelResist = playerToShow.calcStat(Stats.CANCEL_RESIST, player, null);

        double swordResist = 100. - playerToShow.calcStat(Stats.SWORD_WPN_VULNERABILITY, player, null);
        double dualResist = 100. - playerToShow.calcStat(Stats.DUAL_WPN_VULNERABILITY, player, null);
        double bluntResist = 100. - playerToShow.calcStat(Stats.BLUNT_WPN_VULNERABILITY, player, null);
        double daggerResist = 100. - playerToShow.calcStat(Stats.DAGGER_WPN_VULNERABILITY, player, null);
        double bowResist = 100. - playerToShow.calcStat(Stats.BOW_WPN_VULNERABILITY, player, null);
        double crossbowResist = 100. - playerToShow.calcStat(Stats.CROSSBOW_WPN_VULNERABILITY, player, null);
        double poleResist = 100. - playerToShow.calcStat(Stats.POLE_WPN_VULNERABILITY, player, null);
        double fistResist = 100. - playerToShow.calcStat(Stats.FIST_WPN_VULNERABILITY, player, null);

        double critChanceResist = 100. - playerToShow.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, player, null);
        double critDamResistStatic = playerToShow.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, player, null);
        double critDamResist = 100. - 100 * (playerToShow.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, 1., player, null) - critDamResistStatic);

        String dialog = HtmCache.INSTANCE.getNotNull(player.isGM() ? "command/whoamiGM.htm" : "command/whoami.htm", player);

        NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);

        String sb = dialog;
        sb = sb.replaceFirst("%hpRegen%", df.format(hpRegen));
        sb = sb.replaceFirst("%cpRegen%", df.format(cpRegen));
        sb = sb.replaceFirst("%mpRegen%", df.format(mpRegen));
        sb = sb.replaceFirst("%hpDrain%", df.format(hpDrain));
        sb = sb.replaceFirst("%mpDrain%", df.format(mpDrain));
        sb = sb.replaceFirst("%hpGain%", df.format(hpGain));
        sb = sb.replaceFirst("%mpGain%", df.format(mpGain));
        sb = sb.replaceFirst("%critPerc%", df.format(critPerc));
        sb = sb.replaceFirst("%critStatic%", df.format(critStatic));
        sb = sb.replaceFirst("%mCritRate%", df.format(mCritRate));
        sb = sb.replaceFirst("%blowRate%", df.format(blowRate));
        sb = sb.replaceFirst("%shieldDef%", df.format(shieldDef));
        sb = sb.replaceFirst("%shieldRate%", df.format(shieldRate));
        if (Config.show_rates) {
            sb = sb.replaceFirst("%xpRate%", df.format(xpRate));
            sb = sb.replaceFirst("%spRate%", df.format(spRate));
            sb = sb.replaceFirst("%dropRate%", df.format(dropRate));
            sb = sb.replaceFirst("%adenaRate%", df.format(adenaRate));
            sb = sb.replaceFirst("%spoilRate%", df.format(spoilRate));
        }
        sb = sb.replaceFirst("%fireResist%", df.format(fireResist));
        sb = sb.replaceFirst("%windResist%", df.format(windResist));
        sb = sb.replaceFirst("%waterResist%", df.format(waterResist));
        sb = sb.replaceFirst("%earthResist%", df.format(earthResist));
        sb = sb.replaceFirst("%holyResist%", df.format(holyResist));
        sb = sb.replaceFirst("%darkResist%", df.format(unholyResist));
        sb = sb.replaceFirst("%bleedPower%", df.format(bleedPower));
        sb = sb.replaceFirst("%bleedResist%", df.format(bleedResist));
        sb = sb.replaceFirst("%poisonPower%", df.format(poisonPower));
        sb = sb.replaceFirst("%poisonResist%", df.format(poisonResist));
        sb = sb.replaceFirst("%stunPower%", df.format(stunPower));
        sb = sb.replaceFirst("%stunResist%", df.format(stunResist));
        sb = sb.replaceFirst("%rootPower%", df.format(rootPower));
        sb = sb.replaceFirst("%rootResist%", df.format(rootResist));
        sb = sb.replaceFirst("%sleepPower%", df.format(sleepPower));
        sb = sb.replaceFirst("%sleepResist%", df.format(sleepResist));
        sb = sb.replaceFirst("%paralyzePower%", df.format(paralyzePower));
        sb = sb.replaceFirst("%paralyzeResist%", df.format(paralyzeResist));
        sb = sb.replaceFirst("%mentalPower%", df.format(mentalPower));
        sb = sb.replaceFirst("%mentalResist%", df.format(mentalResist));
        sb = sb.replaceFirst("%debuffPower%", df.format(debuffPower));
        sb = sb.replaceFirst("%debuffResist%", df.format(debuffResist));
        sb = sb.replaceFirst("%cancelPower%", df.format(cancelPower));
        sb = sb.replaceFirst("%cancelResist%", df.format(cancelResist));
        sb = sb.replaceFirst("%swordResist%", df.format(swordResist));
        sb = sb.replaceFirst("%dualResist%", df.format(dualResist));
        sb = sb.replaceFirst("%bluntResist%", df.format(bluntResist));
        sb = sb.replaceFirst("%daggerResist%", df.format(daggerResist));
        sb = sb.replaceFirst("%bowResist%", df.format(bowResist));
        sb = sb.replaceFirst("%crossbowResist%", df.format(crossbowResist));
        sb = sb.replaceFirst("%fistResist%", df.format(fistResist));
        sb = sb.replaceFirst("%poleResist%", df.format(poleResist));
        sb = sb.replaceFirst("%critChanceResist%", df.format(critChanceResist));
        sb = sb.replaceFirst("%critDamResist%", df.format(critDamResist));

        NpcHtmlMessage msg = new NpcHtmlMessage(0);
        msg.setHtml(Strings.bbParse(sb));
        player.sendPacket(msg);

        return true;
    }
}
