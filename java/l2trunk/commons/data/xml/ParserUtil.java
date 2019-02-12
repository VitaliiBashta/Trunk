package l2trunk.commons.data.xml;

import l2trunk.commons.crypt.CryptUtil;
import l2trunk.commons.lang.FileUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public enum ParserUtil {
    INSTANCE;
    private final SAXReader reader = new SAXReader();
    private final Logger LOG = LoggerFactory.getLogger(ParserUtil.class);

    ParserUtil() {
        reader.setValidation(true);
    }

    public List<Element> load(Path xmlDir) {
        Path dtd;

        if (Files.isRegularFile(xmlDir)) {
            dtd = xmlDir.getParent().resolve(xmlDir.getFileName().toString().replace(".xml", ".dtd"));
        } else {
            dtd = xmlDir.resolve(xmlDir.getFileName().toString().concat(".dtd"));
        }
        if (!Files.exists(dtd))
            throw new IllegalArgumentException("Can't find dtd file " + dtd.toAbsolutePath());
        return load(xmlDir, dtd);
    }

    private List<Element> load(Path xmlDir, Path dtdFile) {
        if (Files.isRegularFile(xmlDir))
            return List.of(loadFile(xmlDir, dtdFile));
        List<Path> files = FileUtils.getAllFiles(xmlDir, true, ".xml");
        return files.stream()
                .map(f -> loadFile(f, dtdFile))
                .collect(Collectors.toList());
    }

    private Element loadFile(Path xmlFile, Path dtdFile) {
        if (!Files.exists(xmlFile)) {
            LOG.warn("file " + xmlFile.toAbsolutePath() + " not exists");
            throw new IllegalArgumentException("File " + xmlFile + " not found");
        }

        reader.setEntityResolver((publicId, systemId) -> new InputSource(Files.newInputStream(dtdFile.toAbsolutePath())));

        try {
            return reader.read(CryptUtil.decryptOnDemand(xmlFile)).getRootElement();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Can't of file: " + xmlFile);
        }
    }
}
