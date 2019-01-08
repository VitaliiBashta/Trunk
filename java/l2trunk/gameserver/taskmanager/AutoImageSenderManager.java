package l2trunk.gameserver.taskmanager;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.cache.ImagesCache;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Class containing Thread which sends most important images to the server with {@value #DELAY_BETWEEN_PICTURE} delay between each image
 */
public final class AutoImageSenderManager {
    private static final List<Integer> IMAGES_SENT_ORDER = Arrays.asList(10000, 10001, 10002);

    private static final long DELAY_BETWEEN_PICTURE = 1000L;

    private AutoImageSenderManager() {
    }

    /**
     * Checking if <code>imageId</code> is sent automatically from this class, or it should be sent in real time
     * If image is sent automatically and player didn't receive it yet, he needs to wait.
     *
     * @param imageId Id of requested Image
     * @return should player wait for the Image Thread?
     */
    public static boolean isImageAutoSendable(int imageId) {
        return IMAGES_SENT_ORDER.contains(imageId);
    }

    /**
     * Checking if All Required images to watch Community Board were sent to the Player
     *
     * @param player that could receive Images
     * @return were those images sent already?
     */
    public static boolean wereAllImagesSent(Player player) {
        return !Config.ALLOW_SENDING_IMAGES || player.getLoadedImagesSize() >= IMAGES_SENT_ORDER.size();
    }

    /**
     * Starting a Thread which sends Images to every player that didn't receive them yet
     */
    public static void startSendingImages() {
        ThreadPoolManager.INSTANCE.schedule(new ImageSendThread(), DELAY_BETWEEN_PICTURE);
    }

    private static class ImageSendThread implements Runnable {
        /**
         * If player didn't receive every Image yet, getting next Image Id to receive from {@link #IMAGES_SENT_ORDER} array
         *
         * @param player that will probably receive Image
         * @return next Image Id. In case all images loaded: -1
         */
        private static int getNextPicture(Player player) {
            if (wereAllImagesSent(player))
                return -1;

            for (int imageId : IMAGES_SENT_ORDER) {
                if (!player.wasImageLoaded(imageId))
                    return imageId;
            }

            player.addQuickVar("AllImagesLoaded", true);
            return -1;
        }

        @Override
        public void run() {
            if (Config.ALLOW_SENDING_IMAGES && Config.COMMUNITYBOARD_ENABLED) {
                GameObjectsStorage.getAllPlayersStream()
                        .filter(Player::isOnline)
                        .forEach(player -> {//Check in case of No-Carrier System
                            int pictureToLoad = getNextPicture(player);
                            if (pictureToLoad != -1) {
                                ImagesCache.sendImageToPlayer(player, pictureToLoad);
                            }
                        });
            }
            ThreadPoolManager.INSTANCE.schedule(new ImageSendThread(), DELAY_BETWEEN_PICTURE);
        }
    }
}
