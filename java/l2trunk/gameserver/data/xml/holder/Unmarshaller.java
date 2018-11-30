package l2trunk.gameserver.data.xml.holder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Unmarshaller {

    private Unmarshaller() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T unMarshall(Path path, Class<T> clazz) {
        T result = null;
        try {
            System.setProperty("javax.xml.accessExternalDTD", "file, http");
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            javax.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            result = (T) jaxbUnmarshaller.unmarshal(Files.newInputStream(path));
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
