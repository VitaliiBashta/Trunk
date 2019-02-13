package l2trunk.scripts.ai.door;

import l2trunk.gameserver.ai.DoorAI;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.ConfirmDlg;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class ResidenceDoor extends DoorAI {
    public ResidenceDoor(DoorInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtTwiceClick(final Player player) {
        final DoorInstance door = getActor();

        Residence residence = ResidenceHolder.getResidence(door.getTemplate().getAIParams().getInteger("residence_id"));
        if (residence.getOwner() != null && player.getClan() != null && player.getClan() == residence.getOwner() && (player.getClanPrivileges() & Clan.CP_CS_ENTRY_EXIT) == Clan.CP_CS_ENTRY_EXIT) {
            SystemMsg msg = door.isOpen() ? SystemMsg.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE : SystemMsg.WOULD_YOU_LIKE_TO_OPEN_THE_GATE;
            player.ask(new ConfirmDlg(msg, 0), new OnAnswerListener() {
                @Override
                public void sayYes() {
                    if (door.isOpen())
                        door.closeMe();
                    else
                        door.openMe();
                }

                @Override
                public void sayNo() {
                    //
                }
            });
        }
    }
}
