package l2trunk.gameserver.listener.actor;

import l2trunk.gameserver.listener.CharListener;
import l2trunk.gameserver.model.Creature;

public interface OnKillListener extends CharListener
{
	void onKill(Creature actor, Creature victim);

	/**
	 * FIXME [VISTALL]
	 * Когда на игрока добавить OnKillListener, он не добавляется суммону, и нужно вручну добавлять
	 * но при ресумоне, проследить трудно
	 * Если возратить тру, то с убийцы будет братся игрок, и на нем вызывать onKill
	 * @return
	 */
    boolean ignorePetOrSummon();
}
