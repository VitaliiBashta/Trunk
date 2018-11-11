package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.AbsServerMail;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Donate implements IVoicedCommandHandler {
    private static final int DONATION_ITEM_ID = 37000;
    private static final int DONATION_MULT_PER_EURO = 3; // this is 1e for 10x 37000
    private static final String[] COMMANDS = {
            "donate"
    };
    private final static String[] sqlCommands =
            {"SELECT * FROM donations WHERE email=? AND retrieved=?",
                    "UPDATE donations SET retrieved=?, retriever_ip=?, retriever_acc=?, retriever_char=?, retrieval_date=? WHERE email=?"
            };
    private final HashMap<Integer, Attempt> commandAttempts = new HashMap<>();

    private static String formatDate(final Date date, final String format) {
        final DateFormat dateFormat = new SimpleDateFormat(format);
        if (date != null)
            return dateFormat.format(date);
        return null;
    }

    private Attempt getAttempt(Player player) {
        final Attempt att = commandAttempts.get(player.getObjectId());
        if (att == null) {
            final Attempt att2 = new Attempt();
            commandAttempts.put(player.getObjectId(), att2);
            return att2;
        }
        return att;
    }
    // (1e-9e NO Bonus)
    // (10€-24€ 10%+ Bonus)
    // (25€-99€ 15%+ Bonus)
    // (100€-199€ 20%+ Bonus)
    // (200€-299€ 25%+ Bonus)
    // (300€+ 35%+ Bonus)

    @Override
    public boolean useVoicedCommand(String command, Player player, String args) {
        if (args == null || args.length() == 0) {
            player.sendMessage("Usage: .donate email@");
            return false;
        }
        if (!args.contains("@") || args.contains(" ")) {
            Functions.sendDebugMessage(player, args + " is not a valid email address!");
            return false;
        }
        return retrieveDonation(args, player);
    }

    private synchronized boolean retrieveDonation(final String txn_id, final Player player) {
        final Attempt attempt = getAttempt(player);
        if (!attempt.allowAttempt()) {
            player.sendMessage("You are temporarly bocked for 3 minutes from using this command! example: .donate email@gmail.com");
            return false;
        }
        if (txn_id == null || player == null)
            return false;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement(sqlCommands[0])) {
            st.setString(1, txn_id);
            st.setString(2, "false");
            try (ResultSet rs = st.executeQuery()) {
                int amount = 0;
                while (rs.next())
                    amount += rs.getInt("amount");
                if (amount > 0) {
                    final int mult = amount > 299 ? 35 : amount > 199 ? 25 : amount > 99 ? 20 : amount > 24 ? 15 : amount > 9 ? 10 : 1;
                    amount *= DONATION_MULT_PER_EURO;
                    final int toGive = amount * (100 + mult) / 100;
                    if (mult > 1)
                        player.sendMessage("You have obtained (" + amount + ") Donator Coins. \\n You gain a +" + mult + "% Bonus(" + (toGive - amount) + "), for a total of (" + toGive + ").");
                    else
                        player.sendMessage("You have obtained " + toGive + " Donator Coins. There is No Bonus for your donation! Check Donation page on our website.");
                    attempt.onAllow();
                    new DonationSuccessMail(player, toGive);
                    try (PreparedStatement st2 = con.prepareStatement(sqlCommands[1])) {
                        st2.setString(1, "true");
                        st2.setString(2, player.getIP());
                        st2.setString(3, player.getAccountName());
                        st2.setString(4, player.getName());
                        st2.setString(5, formatDate(new Date(), "dd/MM/yyyy H:mm:ss"));
                        st2.setString(6, txn_id);
                        st2.executeUpdate();
                    }
                    return true;
                } else {
                    attempt.onDeny();
                    if (attempt.getTries() == 3)
                        new DonationBlockedMail(player);
                    else
                        new DonationFailedMail(player);

                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        player.sendMessage("Please try again later or contact an Admin!");
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        return COMMANDS;
    }

    private static final class DonationSuccessMail extends AbsServerMail {
        DonationSuccessMail(Player player, int toGive) {
            super(player, DONATION_ITEM_ID, toGive);
        }

        @Override
        protected void prepare() {
            _mail.setTopic("Automatic Donation Success!");
            _mail.setBody(" Thank you for your Donation! \\n Here are your Donator Coins, in case you need any admin support you can contact us on \\n Skype: L2Mythras.Eu or e-mail: L2MYTHRAS@GMAIL.COM");
        }
    }

    private static final class DonationFailedMail extends AbsServerMail {
        DonationFailedMail(Player player) {
            super(player, 0, 0);
        }

        @Override
        protected void prepare() {
            _mail.setTopic("Automatic Donation Failed!");
            _mail.setBody(" Hello, \\n It looks like your donation is still not listed on our Account! \\n If is a mistake please try again carefully! \\n After 3 wrong tries you will have reuse for 3 minutes on this command! \\n Contact us: \\n Skype: L2Mythras.Eu \\n E-Mail: L2MYTHRAS@GMAIL.COM ");
        }
    }

    private static final class DonationBlockedMail extends AbsServerMail {
        DonationBlockedMail(Player player) {
            super(player, 0, 0);
        }

        @Override
        protected void prepare() {
            _mail.setTopic("You are temporarly blocked from using .donate");
            _mail.setBody(" Hello, \\n You tries  \\n Remember: \\n After 3 wrong tries you will have reuse for 3 minutes on this command! \\n Contact us: \\n Skype: L2Mythras.Eu \\n E-Mail: L2MYTHRAS@GMAIL.COM ");
        }
    }

    static class Attempt {
        private int tries;
        private long banEx;

        void onDeny() {
            if (++tries > 3) {
                banEx = System.currentTimeMillis() + 180000L;
                tries = 0;
            }
        }

        void onAllow() {
            tries = 0;
            banEx = 0;
        }

        boolean allowAttempt() {
            if (banEx > System.currentTimeMillis())
                return false;
            return true;
        }

        int getTries() {
            return tries;
        }

    }
}