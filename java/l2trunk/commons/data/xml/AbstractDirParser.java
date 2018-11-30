package l2trunk.commons.data.xml;

import l2trunk.commons.lang.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public abstract class AbstractDirParser<H extends AbstractHolder> extends AbstractParser<H> {
    protected AbstractDirParser(H holder) {
        super(holder);
    }

    protected abstract Path getXMLDir();

    protected abstract String getDTDFileName();

    @Override
    protected final void parse() {
        Path dir = getXMLDir();

        if (!Files.exists(dir)) {
            LOG.warn("Dir " + dir.toAbsolutePath() + " not exists");
            return;
        }

        Path dtd = dir.resolve(getDTDFileName());
        if (!Files.exists(dtd)) {
            LOG.warn("DTD file: " + dtd.toAbsolutePath() + " not exists.");
            return;
        }

        initDTD(dtd);

        try {
            Collection<Path> files = FileUtils.getAllFiles(dir, true, ".xml");
            for (Path f : files)
                if (!Files.isHidden(f))
                    try {
                        parseCrypted(f);
                    } catch (Exception e) {
                        LOG.info("Exception: " + e + " in file: " + f.toAbsolutePath(), e);
                    }
        } catch (RuntimeException e) {
            LOG.error("Exception in AbstractDirParser ", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
