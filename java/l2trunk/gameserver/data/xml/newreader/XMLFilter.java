package l2trunk.gameserver.data.xml.newreader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class XMLFilter implements PathMatcher {
    @Override
    public boolean matches(Path path) {
        if ((path == null) || !Files.isRegularFile(path)) {
            return false;
        }
        return path.toString().endsWith(".xml");
    }

}
