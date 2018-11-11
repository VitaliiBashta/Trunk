package l2trunk.loginserver.crypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordHash {
    private final static Logger _log = LoggerFactory.getLogger(PasswordHash.class);

    private final String name;

    public PasswordHash(String name) {
        this.name = name;
    }

    public boolean compare(String password, String expected) {
            return password.equals(expected);

    }

}