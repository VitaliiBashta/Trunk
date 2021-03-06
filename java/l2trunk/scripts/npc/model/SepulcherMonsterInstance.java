package l2trunk.scripts.npc.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.network.serverpackets.NpcSay;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.bosses.FourSepulchersSpawn;

import java.util.concurrent.Future;

public final class SepulcherMonsterInstance extends MonsterInstance {
    private final static int HALLS_KEY = 7260;
    public int mysteriousBoxId = 0;
    private Future<?> victimShout = null;
    private Future<?> _victimSpawnKeyBoxTask = null;
    private Future<?> _changeImmortalTask = null;
    private Future<?> _onDeadEventTask = null;

    public SepulcherMonsterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onSpawn() {
        switch (getNpcId()) {
            case 18150:
            case 18151:
            case 18152:
            case 18153:
            case 18154:
            case 18155:
            case 18156:
            case 18157:
                if (_victimSpawnKeyBoxTask != null)
                    _victimSpawnKeyBoxTask.cancel(false);
                _victimSpawnKeyBoxTask = ThreadPoolManager.INSTANCE.schedule(new VictimSpawnKeyBox(this), 300000);
                if (victimShout != null)
                    victimShout.cancel(false);
                victimShout = ThreadPoolManager.INSTANCE.schedule(new VictimShout(this), 5000);
                break;
            case 18196:
            case 18197:
            case 18198:
            case 18199:
            case 18200:
            case 18201:
            case 18202:
            case 18203:
            case 18204:
            case 18205:
            case 18206:
            case 18207:
            case 18208:
            case 18209:
            case 18210:
            case 18211:
                break;
            case 18231:
            case 18232:
            case 18233:
            case 18234:
            case 18235:
            case 18236:
            case 18237:
            case 18238:
            case 18239:
            case 18240:
            case 18241:
            case 18242:
            case 18243:
                if (_changeImmortalTask != null)
                    _changeImmortalTask.cancel(false);
                _changeImmortalTask = ThreadPoolManager.INSTANCE.schedule(new ChangeImmortal(this), 1600);
                break;
            case 18256:
                break;
        }
        super.onSpawn();
    }

    @Override
    protected void onDeath(Creature killer) {
        super.onDeath(killer);

        switch (getNpcId()) {
            case 18120:
            case 18121:
            case 18122:
            case 18123:
            case 18124:
            case 18125:
            case 18126:
            case 18127:
            case 18128:
            case 18129:
            case 18130:
            case 18131:
            case 18149:
            case 18158:
            case 18159:
            case 18160:
            case 18161:
            case 18162:
            case 18163:
            case 18164:
            case 18165:
            case 18183:
            case 18184:
            case 18212:
            case 18213:
            case 18214:
            case 18215:
            case 18216:
            case 18217:
            case 18218:
            case 18219:
                if (_onDeadEventTask != null)
                    _onDeadEventTask.cancel(false);
                _onDeadEventTask = ThreadPoolManager.INSTANCE.schedule(new OnDeadEvent(this), 3500);
                break;

            case 18150:
            case 18151:
            case 18152:
            case 18153:
            case 18154:
            case 18155:
            case 18156:
            case 18157:
                if (_victimSpawnKeyBoxTask != null) {
                    _victimSpawnKeyBoxTask.cancel(false);
                    _victimSpawnKeyBoxTask = null;
                }
                if (victimShout != null) {
                    victimShout.cancel(false);
                    victimShout = null;
                }
                if (_onDeadEventTask != null)
                    _onDeadEventTask.cancel(false);
                _onDeadEventTask = ThreadPoolManager.INSTANCE.schedule(new OnDeadEvent(this), 3500);
                break;

            case 18141:
            case 18142:
            case 18143:
            case 18144:
            case 18145:
            case 18146:
            case 18147:
            case 18148:
                if (FourSepulchersSpawn.isViscountMobsAnnihilated(mysteriousBoxId) && !hasPartyAKey(killer.getPlayer())) {
                    if (_onDeadEventTask != null)
                        _onDeadEventTask.cancel(false);
                    _onDeadEventTask = ThreadPoolManager.INSTANCE.schedule(new OnDeadEvent(this), 3500);
                }
                break;

            case 18220:
            case 18221:
            case 18222:
            case 18223:
            case 18224:
            case 18225:
            case 18226:
            case 18227:
            case 18228:
            case 18229:
            case 18230:
            case 18231:
            case 18232:
            case 18233:
            case 18234:
            case 18235:
            case 18236:
            case 18237:
            case 18238:
            case 18239:
            case 18240:
                if (FourSepulchersSpawn.isDukeMobsAnnihilated(mysteriousBoxId)) {
                    if (_onDeadEventTask != null)
                        _onDeadEventTask.cancel(false);
                    _onDeadEventTask = ThreadPoolManager.INSTANCE.schedule(new OnDeadEvent(this), 3500);
                }
                break;
        }
    }

    @Override
    protected void onDelete() {
        if (_victimSpawnKeyBoxTask != null) {
            _victimSpawnKeyBoxTask.cancel(false);
            _victimSpawnKeyBoxTask = null;
        }
        if (_onDeadEventTask != null) {
            _onDeadEventTask.cancel(false);
            _onDeadEventTask = null;
        }

        super.onDelete();
    }

    private boolean hasPartyAKey(Player player) {
        return player.getParty().getMembersStream()
                .anyMatch(m -> m.haveItem(HALLS_KEY));
    }

    @Override
    public boolean canChampion() {
        return false;
    }

    private class VictimShout extends RunnableImpl {
        private final SepulcherMonsterInstance _activeChar;

        VictimShout(SepulcherMonsterInstance activeChar) {
            _activeChar = activeChar;
        }

        @Override
        public void runImpl() {
            if (_activeChar.isDead())
                return;

            if (!_activeChar.isVisible())
                return;

            broadcastPacket(new NpcSay(SepulcherMonsterInstance.this, ChatType.ALL, "forgive me!!"));
        }
    }

    private class VictimSpawnKeyBox extends RunnableImpl {
        private final SepulcherMonsterInstance activeChar;

        VictimSpawnKeyBox(SepulcherMonsterInstance activeChar) {
            this.activeChar = activeChar;
        }

        @Override
        public void runImpl() {
            if (activeChar.isDead() || !activeChar.isVisible())
                return;

            FourSepulchersSpawn.spawnKeyBox(activeChar);
            broadcastPacket(new NpcSay(SepulcherMonsterInstance.this, ChatType.ALL, "Many thanks for rescue me."));
            if (victimShout != null) {
                victimShout.cancel(false);
                victimShout = null;
            }
        }
    }

    private class OnDeadEvent extends RunnableImpl {
        final SepulcherMonsterInstance _activeChar;

        OnDeadEvent(SepulcherMonsterInstance activeChar) {
            _activeChar = activeChar;
        }

        @Override
        public void runImpl() {
            switch (_activeChar.getNpcId()) {
                case 18120:
                case 18121:
                case 18122:
                case 18123:
                case 18124:
                case 18125:
                case 18126:
                case 18127:
                case 18128:
                case 18129:
                case 18130:
                case 18131:
                case 18149:
                case 18158:
                case 18159:
                case 18160:
                case 18161:
                case 18162:
                case 18163:
                case 18164:
                case 18165:
                case 18183:
                case 18184:
                case 18212:
                case 18213:
                case 18214:
                case 18215:
                case 18216:
                case 18217:
                case 18218:
                case 18219:
                    FourSepulchersSpawn.spawnKeyBox(_activeChar);
                    break;

                case 18150:
                case 18151:
                case 18152:
                case 18153:
                case 18154:
                case 18155:
                case 18156:
                case 18157:
                    FourSepulchersSpawn.spawnExecutionerOfHalisha(_activeChar);
                    break;

                case 18141:
                case 18142:
                case 18143:
                case 18144:
                case 18145:
                case 18146:
                case 18147:
                case 18148:
                    FourSepulchersSpawn.spawnMonster(_activeChar.mysteriousBoxId);
                    break;

                case 18220:
                case 18221:
                case 18222:
                case 18223:
                case 18224:
                case 18225:
                case 18226:
                case 18227:
                case 18228:
                case 18229:
                case 18230:
                case 18231:
                case 18232:
                case 18233:
                case 18234:
                case 18235:
                case 18236:
                case 18237:
                case 18238:
                case 18239:
                case 18240:
                    FourSepulchersSpawn.spawnArchonOfHalisha(_activeChar.mysteriousBoxId);
                    break;
            }
        }
    }

    private class ChangeImmortal extends RunnableImpl {
        private final SepulcherMonsterInstance activeChar;

        ChangeImmortal(SepulcherMonsterInstance mob) {
            activeChar = mob;
        }

        @Override
        public void runImpl() {
            SkillTable.INSTANCE.getInfo(4616).getEffects(activeChar);// Invulnerable by petrification
        }
    }
}