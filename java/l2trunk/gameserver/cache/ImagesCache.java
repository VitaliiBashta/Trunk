package l2trunk.gameserver.cache;

import l2trunk.commons.lang.FileUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.PledgeCrest;
import l2trunk.gameserver.taskmanager.AutoImageSenderManager;
import l2trunk.gameserver.utils.Util;
import l2trunk.gameserver.vote.DDSConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ImagesCache {
    private static final Pattern HTML_PATTERN = Pattern.compile("%image:(.*?)%", 32);
    private static final Logger _log = LoggerFactory.getLogger(ImagesCache.class);
    private static final int[] SIZES =
            {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};
    private static final int MAX_SIZE = SIZES[(SIZES.length - 1)];
    private static final String CREST_IMAGE_KEY_WORD = "Crest.crest_";
    private static final Map<Integer, byte[]> _images = new HashMap<>();
    private static final Map<String, Integer> _imagesId = new HashMap<>();

    private ImagesCache() {
    }

    public static void init() {
        loadImages();
    }

    /**
     * Getting Map<Id, File> of all .png files from ./data/images/id_by_name Path
     *
     * @return Getting map of all images
     */
    private static Map<Integer, Path> getImagesToLoad() {
        Map<Integer, Path> files = new HashMap<>();

        Path folder = Config.DATAPACK_ROOT.resolve("data/images");
        if (!Files.exists(folder)) {
            _log.error("Path " + folder.toAbsolutePath() + " doesn't exist!", new FileNotFoundException());
            return files;
        }

        for (Path file : FileUtils.getAllFiles(folder, true, ".png")) {
            file = resizeImage(file);

            int id;
            try {
                String name = file.getFileName().toString();
                id = Integer.parseInt(name);
            } catch (Exception e) {
                id = IdFactory.getInstance().getNextId();
            }

            if (id != -1) {
                files.put(id, file);
            }
        }
        return files;
    }

    private static Path resizeImage(Path file) {
        BufferedImage image;
        try {
            image = ImageIO.read(Files.newInputStream(file));
        } catch (IOException e) {
            _log.error("ImagesChache: Error while resizing " + file.toAbsolutePath() + " image.", e);
            return null;
        }

        if (image == null) {
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();

        boolean resizeWidth = true;
        if (width > MAX_SIZE) {
            image = image.getSubimage(0, 0, MAX_SIZE, height);
            resizeWidth = false;
        }

        boolean resizeHeight = true;
        if (height > MAX_SIZE) {
            image = image.getSubimage(0, 0, width, MAX_SIZE);
            resizeHeight = false;
        }

        int resizedWidth = width;
        if (resizeWidth) {
            for (int size : SIZES) {
                if (size >= width) {
                    resizedWidth = size;
                    break;
                }
            }
        }
        int resizedHeight = height;
        if (resizeHeight) {
            for (int size : SIZES) {
                if (size >= height) {
                    resizedHeight = size;
                    break;
                }
            }
        }
        if ((resizedWidth != width) || (resizedHeight != height)) {
            for (int x = 0; x < resizedWidth; x++) {
                for (int y = 0; y < resizedHeight; y++) {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
            try {
                ImageIO.write(image, FileUtils.getFileExtension(file), Files.newOutputStream(file));
            } catch (IOException e) {
                _log.error("ImagesChache: Error while resizing " + file.toAbsolutePath() + " image.", e);
                return null;
            }
        }
        return file;
    }

    /**
     * Getting end of Image File Name(name is always numbers)
     *
     * @param charArray whole text
     * @param start     place
     * @return whole name
     */
    private static int getFileNameEnd(char[] charArray, int start) {
        int stop = start;
        for (; stop < charArray.length; stop++) {
            if (!Util.isInteger(charArray[stop])) {
                return stop;
            }
        }
        return stop;
    }


    /**
     * Loading all the images from ./data/images/id_by_name Path and adding them to images map
     */
    private static void loadImages() {
        Map<Integer, Path> imagesToLoad = getImagesToLoad();

        for (Map.Entry<Integer, Path> image : imagesToLoad.entrySet()) {
            Path file = image.getValue();
            byte[] data = DDSConverter.convertToDDS(file).array();
            _images.put(image.getKey(), data);
            _imagesId.put(file.getFileName().toString().toLowerCase(), image.getKey());
        }

        _log.info("Loaded " + imagesToLoad.size() + " Images!");
    }

    /**
     * Sending All Images that are needed to open HTML to the getPlayer
     *
     * @param html   page that may contain images
     * @param player that will receive images
     * @return Returns true if images were sent to the getPlayer
     */
    public static String sendUsedImages(String html, Player player) {
        if (!Config.ALLOW_SENDING_IMAGES)
            return html;

        // We must also replace all the crests_1 on the html to fit the current getPlayer serverid, or he wont be able to see the images
        html = html.replaceAll("Crest.crest_1_", "Crest.crest_" + +player.getNetConnection().getServerId() + "_");

        // We must first replace all the images to crests format, things like %image:serverImage% to Crest.crest_1_32423
        Matcher m = HTML_PATTERN.matcher(html);
        while (m.find()) {
            final String imageName = m.group(1);
            final int imageId = _imagesId.get(imageName);
            html = html.replaceAll("%image:" + imageName + "%", "Crest.crest_" + player.getNetConnection().getServerId() + "_" + imageId);
        }

        char[] charArray = html.toCharArray();
        int lastIndex = 0;
        boolean hasSentImages = false;

        // Then we look for crests in the html and send them
        while (lastIndex != -1) {
            lastIndex = html.indexOf(CREST_IMAGE_KEY_WORD, lastIndex);

            if (lastIndex != -1) {
                int start = lastIndex + CREST_IMAGE_KEY_WORD.length() + 2;
                int end = getFileNameEnd(charArray, start);
                lastIndex = end;
                int imageId = Integer.parseInt(html.substring(start, end));

                // Checking if images are sent automatically(then getPlayer needs to wait for sending Thread) or in real time
                if (!AutoImageSenderManager.isImageAutoSendable(imageId)) {
                    sendImageToPlayer(player, imageId);
                    hasSentImages = true;
                }
            }
        }

        // Alexander - To differenciate sent crests we add a CREST in the beggining of the html
        if (hasSentImages)
            html = "CREST" + html;

        return html;
    }

    /**
     * Sending Image as PledgeCrest to a getPlayer If image was already sent once to the getPlayer, it's skipping this part Saved images data is in getPlayer Quick Vars as Key: "Image"+imageId Value: true
     *
     * @param player  that will receive image
     * @param imageId Id of the image
     */
    public static void sendImageToPlayer(Player player, int imageId) {
        if (!Config.ALLOW_SENDING_IMAGES)
            return;

        if (player.wasImageLoaded(imageId))
            return;

        player.addLoadedImage(imageId);

        if (_images.containsKey(imageId)) {
            player.sendPacket(new PledgeCrest(imageId, _images.get(imageId)));
        }
//		else
//		{
//			LOG.warn("Trying to send image that doesn't exist, id:" + imageId);
//		}
    }

}
