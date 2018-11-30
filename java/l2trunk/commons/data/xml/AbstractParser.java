package l2trunk.commons.data.xml;

import l2trunk.commons.crypt.CryptUtil;
import l2trunk.commons.data.xml.helpers.ErrorHandlerImpl;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;


public abstract class AbstractParser<H extends AbstractHolder> {
    public static final Logger LOG = LoggerFactory.getLogger(AbstractParser.class);
    private final H holder;

    private String currentFile;
    private final SAXReader reader;

    AbstractParser(H holder) {
        this.holder = holder;
        reader = new SAXReader();
        reader.setValidation(true);
        reader.setErrorHandler(new ErrorHandlerImpl(this));
    }

    void initDTD(Path f) {
        reader.setEntityResolver((publicId, systemId) -> new InputSource(Files.newInputStream(f.toAbsolutePath())));
    }

    private void parseDocument(InputStream f, String name) throws Exception {
        currentFile = name;
        Document document = reader.read(f);
        readData(document.getRootElement());
    }

    void parseCrypted(Path file) throws Exception {
        parseDocument(CryptUtil.decryptOnDemand(file), file.toString());
    }

    protected abstract void readData(Element rootElement) throws Exception;

    protected abstract void parse();

    protected H getHolder() {
        return holder;
    }

    public String getCurrentFileName() {
        return currentFile;
    }

    public void load() {
        parse();
        LOG.info(String.format("loaded %d %s(s) count.", holder.size(), getClass().getSimpleName()));
    }

    public void reload() {
        LOG.info("reload start...");
        holder.clear();
        load();
    }
}
