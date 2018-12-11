package l2trunk.commons.lang;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileUtils {
    public static List<Path> getAllFiles(Path path, boolean recursive, String fileFilter) {
        List<Path> result = new ArrayList<>();
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(path);

            for (Path entry : stream) {
                if (Files.isDirectory(entry) && recursive) {
                    result.addAll(getAllFiles(entry, recursive, fileFilter));
                } else if (entry.toString().endsWith(fileFilter)) {
                    result.add(entry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String getFileExtension(Path file) {
        String filename = file.toString();
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1)).orElse("");
    }

    public static String readFileToString(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String readFileToString(String fileName) {
        return readFileToString(Paths.get(fileName));
    }


    public static void main(String[] args) {
        Path path = Paths.get("./data/doors");
        StringBuilder result = new StringBuilder();
        List<Path> allFiles = getAllFiles(path, true, ".xml");
        for (Path curFile :allFiles) {
            result.append(readFileToString(curFile));
        }

        Path outputFile = path.resolve("allDoors.xml");
        try {
            Files.write(outputFile, result.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(path.toAbsolutePath());
    }
}
