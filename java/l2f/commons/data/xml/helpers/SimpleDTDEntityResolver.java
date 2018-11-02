package l2f.commons.data.xml.helpers;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.File;

public class SimpleDTDEntityResolver implements EntityResolver {
    private String _fileName;

    public SimpleDTDEntityResolver(File f) {
        _fileName = f.getAbsolutePath();
    }

    @SuppressWarnings("unused")
    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        return new InputSource(_fileName);
    }
}
