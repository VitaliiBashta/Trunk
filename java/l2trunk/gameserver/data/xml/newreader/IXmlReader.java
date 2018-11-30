package l2trunk.gameserver.data.xml.newreader;

import l2trunk.commons.lang.FileUtils;
import l2trunk.gameserver.Config;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

public interface IXmlReader {
    Logger LOGGER = Logger.getLogger(IXmlReader.class.getName());

    String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    /**
     * The default file filter, ".xml" files only.
     */
    PathMatcher XML_FILTER = new XMLFilter();

    /**
     * This method can be used to load/reload the data.<br>
     * It's highly recommended to clear the data storage, either the list or map.
     */
    void load();

    default void parseDatapackFile(String path) {
        parseFile(Config.DATAPACK_ROOT.resolve(path));
    }

    /**
     * Parses a single XML file.<br>
     * If the file was successfully parsed, call parseDocument(Document, File)} for the parsed document.<br>
     * <b>Validation is enforced.</b>
     *
     * @param f the XML file to parse.
     */
    default void parseFile(Path f) {
        if (!getCurrentFileFilter().matches(f)) {
            LOGGER.warning(getClass().getSimpleName() + ": Could not parse " + f.toString() + " is not a file or it doesn't exist!");
            return;
        }

        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setValidating(true);
        dbf.setIgnoringComments(true);
        try {
            dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            final DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new XMLErrorHandler());
            parseDocument(db.parse(Files.newInputStream(f)), f);
        } catch (SAXParseException e) {
            LOGGER.warning(getClass().getSimpleName() + ": Could not parse file " + f.toString() + " at line " + e.getLineNumber() + ", column " + e.getColumnNumber() + ": " + e.getMessage());
        } catch (Exception e) {
            LOGGER.warning(getClass().getSimpleName() + ": Could not parse file " + f.toString() + ": " + e.getMessage());
        }
    }

    /**
     * @param path the path to the directory where the XML files are.
     * @return {@code false} if it fails to find the directory, {@code true} otherwise.
     */
    default boolean parseDirectory(String path) {
        return parseDirectory(Paths.get(path), false);
    }

    /**
     * @param path      the path to the directory where the XML files are.
     * @param recursive parses all sub folders if there is.
     * @return {@code false} if it fails to find the directory, {@code true} otherwise.
     */
    default boolean parseDirectory(String path, boolean recursive) {
        return parseDirectory(Paths.get(path), recursive);
    }

    default boolean parseDirectory(Path dir, boolean recursive) {
        if (!Files.exists(dir)) {
            LOGGER.warning(getClass().getSimpleName() + ": Folder " + dir.toAbsolutePath() + " doesn't exist!");
            return false;
        }

        final List<Path> files = FileUtils.getAllFiles(dir, recursive, "");
        files.forEach(this::parseFile);
        return true;
    }

    default boolean parseDatapackDirectory(String path, boolean recursive) {
        return parseDirectory(Config.DATAPACK_ROOT.resolve(path), recursive);
    }

    /**
     * @param doc the current document to parse
     * @param f   the current file
     */
    default void parseDocument(Document doc, Path f) {
        parseDocument(doc);
    }

    /**
     * @param doc the current document to parse
     */
    void parseDocument(Document doc);
//    {
//        LOGGER.severe("Parser not implemented!");
//    }

    /**
     * Parses a boolean value.
     *
     * @param node         the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Boolean parseBoolean(Node node, Boolean defaultValue) {
        return node != null ? Boolean.valueOf(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a boolean value.
     *
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Boolean parseBoolean(Node node) {
        return parseBoolean(node, null);
    }

    /**
     * Parses a boolean value.
     *
     * @param attrs the attributes
     * @param name  the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Boolean parseBoolean(NamedNodeMap attrs, String name) {
        return parseBoolean(attrs.getNamedItem(name));
    }

    /**
     * Parses a boolean value.
     *
     * @param attrs        the attributes
     * @param name         the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Boolean parseBoolean(NamedNodeMap attrs, String name, Boolean defaultValue) {
        return parseBoolean(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a byte value.
     *
     * @param node         the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Byte parseByte(Node node, Byte defaultValue) {
        return node != null ? Byte.valueOf(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a byte value.
     *
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Byte parseByte(Node node) {
        return parseByte(node, null);
    }

    /**
     * Parses a byte value.
     *
     * @param attrs the attributes
     * @param name  the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Byte parseByte(NamedNodeMap attrs, String name) {
        return parseByte(attrs.getNamedItem(name));
    }

    /**
     * Parses a byte value.
     *
     * @param attrs        the attributes
     * @param name         the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Byte parseByte(NamedNodeMap attrs, String name, Byte defaultValue) {
        return parseByte(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a short value.
     *
     * @param node         the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Short parseShort(Node node, Short defaultValue) {
        return node != null ? Short.valueOf(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a short value.
     *
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Short parseShort(Node node) {
        return parseShort(node, null);
    }

    /**
     * Parses a short value.
     *
     * @param attrs the attributes
     * @param name  the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Short parseShort(NamedNodeMap attrs, String name) {
        return parseShort(attrs.getNamedItem(name));
    }

    /**
     * Parses a short value.
     *
     * @param attrs        the attributes
     * @param name         the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Short parseShort(NamedNodeMap attrs, String name, Short defaultValue) {
        return parseShort(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses an int value.
     *
     * @param node         the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default int parseInt(Node node, Integer defaultValue) {
        return node != null ? Integer.parseInt(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses an int value.
     *
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default int parseInt(Node node) {
        return parseInt(node, -1);
    }

    /**
     * Parses an integer value.
     *
     * @param node         the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Integer parseInteger(Node node, Integer defaultValue) {
        return node != null ? Integer.valueOf(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses an integer value.
     *
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Integer parseInteger(Node node) {
        return parseInteger(node, null);
    }

    /**
     * Parses an integer value.
     *
     * @param attrs the attributes
     * @param name  the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Integer parseInteger(NamedNodeMap attrs, String name) {
        return parseInteger(attrs.getNamedItem(name));
    }

    /**
     * Parses an integer value.
     *
     * @param attrs        the attributes
     * @param name         the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Integer parseInteger(NamedNodeMap attrs, String name, Integer defaultValue) {
        return parseInteger(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a long value.
     *
     * @param node         the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Long parseLong(Node node, Long defaultValue) {
        return node != null ? Long.valueOf(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a long value.
     *
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Long parseLong(Node node) {
        return parseLong(node, null);
    }

    /**
     * Parses a long value.
     *
     * @param attrs the attributes
     * @param name  the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Long parseLong(NamedNodeMap attrs, String name) {
        return parseLong(attrs.getNamedItem(name));
    }

    /**
     * Parses a long value.
     *
     * @param attrs        the attributes
     * @param name         the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Long parseLong(NamedNodeMap attrs, String name, Long defaultValue) {
        return parseLong(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a float value.
     *
     * @param node         the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Float parseFloat(Node node, Float defaultValue) {
        return node != null ? Float.valueOf(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a float value.
     *
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Float parseFloat(Node node) {
        return parseFloat(node, null);
    }

    /**
     * Parses a float value.
     *
     * @param attrs the attributes
     * @param name  the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Float parseFloat(NamedNodeMap attrs, String name) {
        return parseFloat(attrs.getNamedItem(name));
    }

    /**
     * Parses a float value.
     *
     * @param attrs        the attributes
     * @param name         the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Float parseFloat(NamedNodeMap attrs, String name, Float defaultValue) {
        return parseFloat(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a double value.
     *
     * @param node         the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Double parseDouble(Node node, Double defaultValue) {
        return node != null ? Double.valueOf(node.getNodeValue()) : defaultValue;
    }

    /**
     * Parses a double value.
     *
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Double parseDouble(Node node) {
        return parseDouble(node, null);
    }

    /**
     * Parses a double value.
     *
     * @param attrs the attributes
     * @param name  the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default Double parseDouble(NamedNodeMap attrs, String name) {
        return parseDouble(attrs.getNamedItem(name));
    }

    /**
     * Parses a double value.
     *
     * @param attrs        the attributes
     * @param name         the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default Double parseDouble(NamedNodeMap attrs, String name, Double defaultValue) {
        return parseDouble(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses a string value.
     *
     * @param node         the node to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default String parseString(Node node, String defaultValue) {
        return node != null ? node.getNodeValue() : defaultValue;
    }

    /**
     * Parses a string value.
     *
     * @param node the node to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default String parseString(Node node) {
        return parseString(node, null);
    }

    /**
     * Parses a string value.
     *
     * @param attrs the attributes
     * @param name  the name of the attribute to parse
     * @return if the node is not null, the value of the parsed node, otherwise null
     */
    default String parseString(NamedNodeMap attrs, String name) {
        return parseString(attrs.getNamedItem(name));
    }

    /**
     * Parses a string value.
     *
     * @param attrs        the attributes
     * @param name         the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null, the value of the parsed node, otherwise the default value
     */
    default String parseString(NamedNodeMap attrs, String name, String defaultValue) {
        return parseString(attrs.getNamedItem(name), defaultValue);
    }

    /**
     * Parses an enumerated value.
     *
     * @param <T>          the enumerated type
     * @param node         the node to parse
     * @param clazz        the class of the enumerated
     * @param defaultValue the default value
     * @return if the node is not null and the node value is valid the parsed value, otherwise the default value
     */
    default <T extends Enum<T>> T parseEnum(Node node, Class<T> clazz, T defaultValue) {
        if (node == null) {
            return defaultValue;
        }

        try {
            return Enum.valueOf(clazz, node.getNodeValue());
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid value specified for node: " + node.getNodeName() + " specified value: " + node.getNodeValue() + " should be enum value of \"" + clazz.getSimpleName() + "\" using default value: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Parses an enumerated value.
     *
     * @param <T>   the enumerated type
     * @param node  the node to parse
     * @param clazz the class of the enumerated
     * @return if the node is not null and the node value is valid the parsed value, otherwise null
     */
    default <T extends Enum<T>> T parseEnum(Node node, Class<T> clazz) {
        return parseEnum(node, clazz, null);
    }

    /**
     * Parses an enumerated value.
     *
     * @param <T>   the enumerated type
     * @param attrs the attributes
     * @param clazz the class of the enumerated
     * @param name  the name of the attribute to parse
     * @return if the node is not null and the node value is valid the parsed value, otherwise null
     */
    default <T extends Enum<T>> T parseEnum(NamedNodeMap attrs, Class<T> clazz, String name) {
        return parseEnum(attrs.getNamedItem(name), clazz);
    }

    /**
     * Parses an enumerated value.
     *
     * @param <T>          the enumerated type
     * @param attrs        the attributes
     * @param clazz        the class of the enumerated
     * @param name         the name of the attribute to parse
     * @param defaultValue the default value
     * @return if the node is not null and the node value is valid the parsed value, otherwise the default value
     */
    default <T extends Enum<T>> T parseEnum(NamedNodeMap attrs, Class<T> clazz, String name, T defaultValue) {
        return parseEnum(attrs.getNamedItem(name), clazz, defaultValue);
    }

    /**
     * Gets the current file filter.
     *
     * @return the current file filter
     */
    default PathMatcher getCurrentFileFilter() {
        return XML_FILTER;
    }

    /**
     * Simple XML error handler.
     *
     * @author Zoey76
     */
    class XMLErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException e) throws SAXParseException {
            throw e;
        }

        @Override
        public void error(SAXParseException e) throws SAXParseException {
            throw e;
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXParseException {
            throw e;
        }
    }
}
