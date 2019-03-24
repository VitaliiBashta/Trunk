package l2trunk.scripts.quests;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.instances.player.ShortCut;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.entity.CCPHelpers.CCPSecondaryPassword;
import l2trunk.gameserver.model.entity.ChangeLogManager;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.SchemeBufferInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.mail.Mail;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.item.ItemTemplate.Grade;
import l2trunk.gameserver.templates.item.WeaponTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Util;

import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toInt;


public final class _255_Tutorial extends Quest implements OnPlayerEnterListener {

    // table for Question Mark Clicked (24) newbie lvl [raceId, html]
    private final Map<Integer, String> QMCb = new HashMap<>();

    // table for Question Mark Clicked (35) 1st class transfer [raceId, html]
    private final Map<Integer, String> QMCc = new HashMap<>();

    // table for Tutorial Close Link (26) 2nd class transfer [raceId, html]
    private final Map<Integer, String> TCLa = new HashMap<>();

    // table for Tutorial Close Link (23) 2nd class transfer [raceId, html]
    private final Map<Integer, String> TCLb = new HashMap<>();

    // table for Tutorial Close Link (24) 2nd class transfer [raceId, html]
    private final Map<Integer, String> TCLc = new HashMap<>();


    public _255_Tutorial() {
        super(false);

        CharListenerList.addGlobal(this);


        QMCb.put(0, "tutorial_human009.htm");
        QMCb.put(10, "tutorial_human009.htm");
        QMCb.put(18, "tutorial_elf009.htm");
        QMCb.put(25, "tutorial_elf009.htm");
        QMCb.put(31, "tutorial_delf009.htm");
        QMCb.put(38, "tutorial_delf009.htm");
        QMCb.put(44, "tutorial_orc009.htm");
        QMCb.put(49, "tutorial_orc009.htm");
        QMCb.put(53, "tutorial_dwarven009.htm");
        QMCb.put(123, "tutorial_kamael009.htm");
        QMCb.put(124, "tutorial_kamael009.htm");

        QMCc.put(0, "tutorial_21.htm");
        QMCc.put(10, "tutorial_21a.htm");
        QMCc.put(18, "tutorial_21b.htm");
        QMCc.put(25, "tutorial_21c.htm");
        QMCc.put(31, "tutorial_21g.htm");
        QMCc.put(38, "tutorial_21h.htm");
        QMCc.put(44, "tutorial_21d.htm");
        QMCc.put(49, "tutorial_21e.htm");
        QMCc.put(53, "tutorial_21f.htm");

        TCLa.put(1, "tutorial_22w.htm");
        TCLa.put(4, "tutorial_22.htm");
        TCLa.put(7, "tutorial_22b.htm");
        TCLa.put(11, "tutorial_22c.htm");
        TCLa.put(15, "tutorial_22d.htm");
        TCLa.put(19, "tutorial_22e.htm");
        TCLa.put(22, "tutorial_22f.htm");
        TCLa.put(26, "tutorial_22g.htm");
        TCLa.put(29, "tutorial_22h.htm");
        TCLa.put(32, "tutorial_22n.htm");
        TCLa.put(35, "tutorial_22o.htm");
        TCLa.put(39, "tutorial_22p.htm");
        TCLa.put(42, "tutorial_22q.htm");
        TCLa.put(45, "tutorial_22i.htm");
        TCLa.put(47, "tutorial_22j.htm");
        TCLa.put(50, "tutorial_22k.htm");
        TCLa.put(54, "tutorial_22l.htm");
        TCLa.put(56, "tutorial_22m.htm");

        TCLb.put(4, "tutorial_22aa.htm");
        TCLb.put(7, "tutorial_22ba.htm");
        TCLb.put(11, "tutorial_22ca.htm");
        TCLb.put(15, "tutorial_22da.htm");
        TCLb.put(19, "tutorial_22ea.htm");
        TCLb.put(22, "tutorial_22fa.htm");
        TCLb.put(26, "tutorial_22ga.htm");
        TCLb.put(32, "tutorial_22na.htm");
        TCLb.put(35, "tutorial_22oa.htm");
        TCLb.put(39, "tutorial_22pa.htm");
        TCLb.put(50, "tutorial_22ka.htm");

        TCLc.put(4, "tutorial_22ab.htm");
        TCLc.put(7, "tutorial_22bb.htm");
        TCLc.put(11, "tutorial_22cb.htm");
        TCLc.put(15, "tutorial_22db.htm");
        TCLc.put(19, "tutorial_22eb.htm");
        TCLc.put(22, "tutorial_22fb.htm");
        TCLc.put(26, "tutorial_22gb.htm");
        TCLc.put(32, "tutorial_22nb.htm");
        TCLc.put(35, "tutorial_22ob.htm");
        TCLc.put(39, "tutorial_22pb.htm");
        TCLc.put(50, "tutorial_22kb.htm");
    }

    private static boolean cantSeeTutorial(Player player) {
        return player.containsQuickVar("watchingTutorial");
    }

    private static void addToTutorialQueue(Player player, String pageToCheck) {
        if (!player.containsQuickVar("tutorialsToSee")) {
            player.addQuickVar("tutorialsToSee", pageToCheck);
        }
    }

    private static void onTutorialClose(QuestState st) {
        Player player = st.player;
        if (player.containsQuickVar("tutorialsToSee")) {
            String tutorialToSee = player.getQuickVarS("tutorialsToSee");
            if ("checkChangeLog".equals(tutorialToSee)) checkChangeLog(st);
            else if ("checkClassMaster".equals(tutorialToSee)) checkClassMaster(st);
        }
    }

    private static void checkChangeLog(QuestState st) {
        Player player = st.player;
        if (cantSeeTutorial(player)) {
            addToTutorialQueue(player, "checkChangeLog");
        } else {
            int lastNotSeenChange = ChangeLogManager.INSTANCE.getNotSeenChangeLog(player);
            if (lastNotSeenChange >= 0) {
                String change = ChangeLogManager.INSTANCE.getChangeLog(lastNotSeenChange);
                st.showTutorialHTML(change);
            }
        }
    }

    private static void checkClassMaster(QuestState st) {
        Player player = st.player;

        if (cantSeeTutorial(player)) {
            addToTutorialQueue(player, "OpenClassMaster");
            return;
        }

        ClassId classId = player.getClassId();
        int jobLevel = classId.occupation()+1;

        if (Config.ALLOW_CLASS_MASTERS_LIST.isEmpty() || !Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel))
            jobLevel = 4;

        if (canChangeClass(player, jobLevel)) {
            StringBuilder html = new StringBuilder();
            html.append("<html noscrollbar><head><title>L2Mythras Newbie Tutorial</title></head>");
            html.append("<body><table border=0 cellpadding=0 cellspacing=0 width=292 height=310 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
            html.append("<tr><td align=center><br>");
            html.append("<table width=280><tr><td align=center valign=center>");
            html.append("<img src=\"L2UI.squaregray\" width=285 height=1/> ");
            html.append("<font name=hs12 color=3399FF>L2Mythras</font>");
            html.append("<img src=\"L2UI.squaregray\" width=285 height=1/>");
            html.append("</td></tr></table><br></td></tr><tr>");
            html.append("<td align=center height=150>");
            html.append("<table width=280><tr><td align=center><img src=\"tut.logo2\" width=240 height=64></td></tr>");
            html.append("<tr><td align=center><br><font color=LEVEL name=hs12>Welcome to L2Mythras ! </font></td></tr>");
            html.append("</table>");
            html.append("<br1>");
            html.append("<table width=280><tr><td align=center>");
            html.append("<font color=00ff99>")
                    .append(player.getName())
                    .append("</font> change your class for <font color=\"LEVEL\">")
                    .append(Util.formatAdena(Config.CLASS_MASTERS_PRICE_LIST[jobLevel]))
                    .append(" Adena</font>!<br1>");
            html.append("</td></tr></table>");
            html.append("<table width=280>");
            for (ClassId cid : ClassId.values()) {
                if (cid != ClassId.inspector && cid.childOf(classId) && cid.occupation() == classId.occupation() + 1) {
                    String name = cid.name().substring(0, 1).toUpperCase() + cid.name().substring(1);
                    html.append("<tr><td align=center><button value=\"")
                            .append(name)
                            .append("\" action=\"bypass -h ChangeTo;")
                            .append(cid.id).append(';')
                            .append(Config.CLASS_MASTERS_PRICE_LIST[jobLevel])
                            .append("\" width=200 height=32 back=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm\"></td></tr>");
                }
            }
            html.append("<tr><td align=center><button value=\"Remind me later\" action=\"bypass CloseTutorial\" width=200 height=28 back=\"L2UI_CT1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Back\"></td></tr>");
            html.append("</table>");
            html.append("</td></tr><tr>");
            html.append("<td align=center><table width=280><tr>");
            html.append("<td align=center valign=center>");
            html.append("<img src=\"L2UI.squaregray\" width=285 height=1/> ");
            html.append("<font name=hs12 color=3399FF>L2Mythras</font>");
            html.append("<img src=\"L2UI.squaregray\" width=285 height=1/> ");
            html.append("</td></tr></table><br></td></tr>");
            html.append("</table></body></html>");

            st.closeTutorial(); // Close the tutorial first so the other html can be shown
            st.showTutorialHTML(html.toString());
        }
    }

    /**
     * Checking if getPlayer have got occupation >= 20, >= 40 or >= 76 and still didn't change class
     *
     * @param player   to check
     * @param jobLevel occupation of the class
     * @return can change class
     */
    private static boolean canChangeClass(Player player, int jobLevel) {
        int level = player.getLevel();

        if (!Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel))
            return false;
        if (level >= 20 && jobLevel == 1)
            return true;
        if (level >= 40 && jobLevel == 2)
            return true;
        return level >= 76 && jobLevel == 3;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        if (player == null)
            return null;

        String html = "";

        if ("CheckPass".equals(event)) {
            String text = HtmCache.INSTANCE.getNotNull("enterworldSecondary.htm", player);
            st.showTutorialHTML(text);
            player.setBlock(true);
            //getPlayer.startAbnormalEffect(AbnormalEffect.FIREROOT_STUN);

            return null;
        } else if ("ProposePass".equals(event)) {
            String text = HtmCache.INSTANCE.getNotNull("enterworldNoSecondary.htm", player);
            st.showTutorialHTML(text);
            //getPlayer.startAbnormalEffect(AbnormalEffect.FIREROOT_STUN);
            return null;
        } else if (event.startsWith("TryPass")) {
            String pass = null;
            boolean correct;
            try {
                pass = event.substring("TryPass ".length());
                pass = pass.trim();
                correct = CCPSecondaryPassword.tryPass(player, pass);
            } catch (IndexOutOfBoundsException e) {
                correct = false;
            }

            if (correct) {
                st.closeTutorial();
                onEvent("UC", st, null);
                player.sendMessage("Your password is correct!");
                if (player.isBlocked()) {
                    //getPlayer.stopAbnormalEffect(AbnormalEffect.FIREROOT_STUN);
                    player.broadcastPacket(new SocialAction(player.objectId(), SocialAction.VICTORY));
                    final MagicSkillUse msu = new MagicSkillUse(player, 23312, 0, 500);
                    player.broadcastPacket(msu);
                    player.broadcastCharInfo();
                    player.setBlock();
                }

                player.setInvul(false);

                // Synerge - Show the premium htm and message
                if (Config.ENTER_WORLD_SHOW_HTML_PREMIUM_BUY) {

                    if (player.getClan() == null) {
                        player.sendPacket(new NpcHtmlMessage(5).setFile("advertise.htm").replace("%playername%", player.getName()));
                    }
                    if (player.getNetConnection() != null) {
                        String msg = "You don't have Premium Account, you can buy it from Community Board.";
                        player.sendPacket(new ExShowScreenMessage(msg, 10000));
                        player.sendMessage(msg);
                    }
                }

                // Synerge - Force ClassMaster check after putting the password just in case
                checkClassMaster(st);

                return null;
            } else {
                // Send a mail to the character telling that his account got a wrong secondary password
                Mail mail = new Mail();
                mail.setSenderId(1);
                mail.setSenderName("System");
                mail.setReceiverId(player.objectId());
                mail.setReceiverName(player.getName());
                mail.setTopic("Wrong Secondary Password");
                mail.setBody("Someone wrote a wrong secondary password (" + pass + ") to enter to your character. This is a warning message, if you didnt entered this password then change it");
                mail.setType(Mail.SenderType.NEWS_INFORMER);
                mail.setUnread(true);
                mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
                mail.save();

                // Logout the character
                //getPlayer.stopAbnormalEffect(AbnormalEffect.FIREROOT_STUN);
                player.logout();

                return null;
            }
        } else if (event.equals("OpenClassMaster")) {
            checkClassMaster(st);
            return null;
        } else if (event.equals("ShowChangeLog")) {
            checkChangeLog(st);
        } else if (event.startsWith("ShowChangeLogPage")) {
            int page = Integer.parseInt(event.substring("ShowChangeLogPage".length()).trim());
            String change = ChangeLogManager.INSTANCE.getChangeLog(page);
            st.showTutorialHTML(change);
        } else if (event.startsWith("ChangeTo")) {
            StringTokenizer tokenizer = new StringTokenizer(event, ";");
            tokenizer.nextToken();
            ClassId newClassId = ClassId.getById(tokenizer.nextToken());
            long price = Long.parseLong(tokenizer.nextToken());

            if (price < 0L)//Somebody cheating
            {
                st.closeTutorial();
                return null;
            }

            if (!newClassId.equalsOrChildOf(player.getActiveClassId())) {//Somebody cheating
                st.closeTutorial();
                return null;
            }

            final int jobLevel = player.getClassId().occupation()+1;
            if (!canChangeClass(player, jobLevel)) {
                st.closeTutorial();
                return null;
            }

            ItemTemplate item = ItemHolder.getTemplate(Config.CLASS_MASTERS_PRICE_ITEM);
            ItemInstance pay = player.getInventory().getItemByItemId(item.itemId());
            if (pay != null && pay.getCount() >= price) {
                player.getInventory().destroyItem(pay, price, "_255_Tutorial");
                if (jobLevel == 3)
                    player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_YOUR_THIRDCLASS_TRANSFER_QUEST);
                else
                    player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER);

                player.setClassId(newClassId, false, false);

                final MagicSkillUse msu = new MagicSkillUse(player, 5103);
                player.broadcastPacket(msu);
                player.broadcastUserInfo(true);
                st.closeTutorial();

                // Synerge - Dont check tutorial events for characters above occupation 70 or in subclass
                if (player.getLevel() < 70 && player.getActiveClassId() == player.getBaseClassId()) {
                    // Synerge - Show a special tutorial htm for weapons after the first class transfer
                    if (jobLevel == 1 && player.getVarInt("lvl") < 21) {
                        player.setVar("lvl", 21);
                        player.sendPacket(new TutorialShowHtml(HtmCache.INSTANCE.getNotNull("SpecialTutorial/Level21.htm", player)));
                    }
                    // Synerge - Show a special tutorial htm after the second class transfer
                    else if (jobLevel == 2 && player.getVarInt("lvl") < 41) {
                        player.setVar("lvl", 41);
                        player.sendPacket(new TutorialShowHtml(HtmCache.INSTANCE.getNotNull("SpecialTutorial/Level41.htm", player)));
                    }
                } else
                    onEvent("OpenClassMaster", st, null);
                return null;
            } else if (Config.CLASS_MASTERS_PRICE_ITEM == ItemTemplate.ITEM_ID_ADENA) {
                player.sendPacket(new SystemMessage2(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
            } else {
                player.sendPacket(new SystemMessage2(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
            }
            st.closeTutorial();
            return null;
        } else if (event.equals("CloseTutorial")) {
            st.closeTutorial();
            return null;
        } else if (event.equals("onTutorialClose")) {
            onTutorialClose(st);
            return null;
        }

        else if (event.startsWith("TE")) {
            st.cancelQuestTimer("TE");
            int event_id = 0;
            if (!"TE".equalsIgnoreCase(event))
                event_id = Integer.valueOf(event.substring(2));
            if (event_id == 0)
                st.closeTutorial();
            else if (event_id == 49) {
                st.closeTutorial();
                return null;
            } else if (event_id == 50)//New Secondary Password
            {
                CCPSecondaryPassword.startSecondaryPasswordSetup(player, "secondaryPassF");
                st.closeTutorial();
                return null;
            }
        }

        // Client Event
        else if (event.startsWith("CE")) {
            // Dont check tutorial events for characters above occupation 70 or in subclass
            if (player.getLevel() >= 70 || player.getActiveClassId() != player.getBaseClassId()) {
                return null;
            }

            int event_id = Integer.valueOf(event.substring(2));

            // Level up event
            if (event_id == 40) {
                // Synerge - On lvl 6 show a html for teleporting the getPlayer to other place
                if (player.getLevel() >= 6 && player.getVarInt("lvl") < 6 && st.getInt("firstexp") == 1) {
                    if (player.getClassId().occupation() == 0) {
                        //getPlayer.setVar("lvl", "6");
                        st.set("firstexp", 2);
                        st.showTutorialHTML(HtmCache.INSTANCE.getNotNull("SpecialTutorial/Level6.htm", player));
                    }
                }
                // Synerge - Show a special tutorial htm for showing a npc in radar
                else if (player.getLevel() >= 32 && player.getVarInt("lvl") < 32) {
                    player.setVar("lvl", 32);
                    st.showTutorialHTML(HtmCache.INSTANCE.getNotNull("SpecialTutorial/Level32.htm", player));
                }
                // Synerge - Show a special tutorial htm for teleporting
                else if (player.getLevel() >= 52 && player.getVarInt("lvl") < 52) {
                    player.setVar("lvl", 52);
                    st.showTutorialHTML(HtmCache.INSTANCE.getNotNull("SpecialTutorial/Level52.htm", player));
                }
            }
            // Exp events
            else if (event_id == 41) {
                // Synerge - When getting the first exp the tutorial should close
                if (st.getInt("firstexp") < 1) {
                    st.set("firstexp");
                    st.closeTutorial();
                }
                // Synerge - Player should get another html after the html7, when he kills a monster
                else if (st.getInt("firstexp") == 3) {
                    player.setVar("lvl", 6);
                    st.set("firstexp", 4);
                    st.showTutorialHTML(HtmCache.INSTANCE.getNotNull("SpecialTutorial/Level8.htm", player));
                }
            }
            // teleport events
            else if (event_id == 42) {
                // Synerge - Shows the level41Ready htm when teleporting after lvl 40
                if (player.getVarInt("lvl") == 41) {
                    player.setVar("lvl", 42);
                    st.showTutorialHTML(HtmCache.INSTANCE.getNotNull("SpecialTutorial/Level41Ready.htm", player));
                }
            }
        }
        // Synerge - Shows the buffer on the community board and the level7.htm in the tutorial
        else if (event.startsWith("ShowBuffer") && st.getInt("firstexp") == 2) {
            st.set("firstexp", 3);

            SchemeBufferInstance.showWindow(player);

            st.showTutorialHTML(HtmCache.INSTANCE.getNotNull("SpecialTutorial/Level7.htm", player));
        }
        // Synerge - Gives the character a certain weapon id and equips it
        else if (event.startsWith("GetWeaponD ") && !player.isVarSet("weapon")) {
            StringTokenizer tokenizer = new StringTokenizer(event, " ");
            tokenizer.nextToken();
            final int itemId = Integer.parseInt(tokenizer.nextToken());

            final ItemInstance createditem = ItemFunctions.createItem(itemId);

            if (createditem == null || createditem.getCrystalType() != Grade.D) {
                player.sendMessage("Wrong weapon");
                st.closeTutorial();
                return null;
            }

            player.setVar("weapon");
            player.getInventory().addItem(createditem, "SpecialTutorial");

            // Also give arrows if the weapon is a bow
            if (createditem.isWeapon() && ((WeaponTemplate) createditem.getTemplate()).getItemType() == WeaponType.BOW) {
                final ItemInstance arrows = ItemFunctions.createItem(1341);
                arrows.setCount(300);
                player.getInventory().addItem(arrows, "SpecialTutorial");
            }

            // Unequip the current getPlayer's weapon
            if (player.getActiveWeaponInstance() != null)
                player.getInventory().unEquipItem(player.getActiveWeaponInstance());

            // Equip the new item
            player.getInventory().equipItem(createditem);

            // Show the equip armor next
            if (player.getRace() == Race.kamael)
                st.showTutorialHTML(HtmCache.INSTANCE.getNotNull("SpecialTutorial/Level21ArmorKamael.htm", player));
            else
                st.showTutorialHTML(HtmCache.INSTANCE.getNotNull("SpecialTutorial/Level21Armors.htm", player));
        }
        // Synerge - Gives the character a certain armor ids and equips it
        else if (event.startsWith("GetArmorD ") && !player.isVarSet("armor")) {
            StringTokenizer tokenizer = new StringTokenizer(event, " ");
            tokenizer.nextToken();

            player.setVar("armor");

            // We have to give and equip each item that is sent through the bypass
            while (tokenizer.hasMoreTokens()) {
                final int itemId = Integer.parseInt(tokenizer.nextToken());
                final ItemInstance createditem = ItemFunctions.createItem(itemId);

                if (createditem == null || createditem.getCrystalType() != Grade.D) {
                    player.sendMessage("Wrong Armor");
                    st.closeTutorial();
                    return null;
                }

                player.getInventory().addItem(createditem, "SpecialTutorial");

                // Unequip the current getPlayer's armor slot
                player.getInventory().unEquipItemInBodySlot(createditem.getBodyPart());

                // Equip the new item
                player.getInventory().equipItem(createditem);
            }

            // Show the soulshots html next
            st.showTutorialHTML(HtmCache.INSTANCE.getNotNull("SpecialTutorial/Level21Soulshots.htm", player));
        }
        // Synerge - Gives the character some shots
        else if (event.startsWith("GetShotsD ") && !player.isVarSet("shots")) {
            StringTokenizer tokenizer = new StringTokenizer(event, " ");
            tokenizer.nextToken();
            final int itemId = Integer.parseInt(tokenizer.nextToken());
            final int itemCount = Integer.parseInt(tokenizer.nextToken());

            final ItemInstance createditem = ItemFunctions.createItem(itemId);

            if (createditem == null || createditem.getCrystalType() != Grade.D) {
                player.sendMessage("Wrong shots");
                st.closeTutorial();
                return null;
            }

            createditem.setCount(itemCount);

            player.setVar("shots");
            player.getInventory().addItem(createditem, "SpecialTutorial");

            // Add the soulshots to a new shortcut
            ShortCut shortCut = new ShortCut(11, 0, ShortCut.TYPE_ITEM, createditem.objectId(), -1, 1);
            player.sendPacket(new ShortCutRegister(player, shortCut));
            player.registerShortCut(shortCut);

            // Show the cruma html next
            st.showTutorialHTML(HtmCache.INSTANCE.getNotNull("SpecialTutorial/Level21Cruma.htm", player));
        }
        // Synerge - Allows to open htmls directly as a bypass. Support for link on tutorials? Should work outside this, but whatever
        else if (event.startsWith("Link ")) {
            StringTokenizer tokenizer = new StringTokenizer(event, " ");
            tokenizer.nextToken();
            final String htm = tokenizer.nextToken();

            st.showTutorialHTML(HtmCache.INSTANCE.getNotNull("SpecialTutorial/" + htm, player));
        }
        // Synerge - Shows a certain npc in the map and radar
        else if (event.startsWith("ShowLocation ")) {
            StringTokenizer tokenizer = new StringTokenizer(event, " ");
            tokenizer.nextToken();
            final int npcId = Integer.parseInt(tokenizer.nextToken());

            final NpcInstance npcLoc = GameObjectsStorage.getByNpcId(npcId);
            if (npcLoc != null)
                player.sendPacket(new RadarControl(2, 2, npcLoc.getLoc()), new RadarControl(0, 1, npcLoc.getLoc()));

            st.closeTutorial();
        }

        if (html.isEmpty())
            return null;
        st.showTutorialPage(html);
        return null;
    }

    @Override
    public void onPlayerEnter(Player player) {
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}