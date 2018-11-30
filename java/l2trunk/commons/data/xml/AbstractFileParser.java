package l2trunk.commons.data.xml;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractFileParser<H extends AbstractHolder> extends AbstractParser<H> {
    protected AbstractFileParser(H holder) {
        super(holder);
    }

    protected abstract Path getXMLFile();

    protected abstract String getDTDFileName();

    @Override
    protected final void parse() {
        Path file = getXMLFile();

        if (!Files.exists(file)) {
            LOG.warn("file " + file.toAbsolutePath() + " not exists");
            return;
        }

        Path dtd = file.getParent().resolve(getDTDFileName());
        if (!Files.exists(dtd)) {
            LOG.warn("DTD file: " + dtd.toAbsolutePath() + " not exists.");
            return;
        }

        initDTD(dtd);

        try {
            parseCrypted(file);
            //parseDocument(new FileInputStream(file), file.getName());
        } catch (Exception e) {
            LOG.error("Exception in AbstractFileParser ", e);
        }
    }
}
