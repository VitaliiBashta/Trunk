//package l2trunk.commons.logging;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public abstract class LoggerObject {
//    protected final Logger _log = LoggerFactory.getLogger(getClass());
//
//    protected void error(String st, Exception e) {
//        _log.error(getClass().getSimpleName() + ": " + st, e);
//    }
//
//    public void error(String st) {
//        _log.error(getClass().getSimpleName() + ": " + st);
//    }
//
//    protected void warn(String st, Exception e) {
//        _log.warn(getClass().getSimpleName() + ": " + st, e);
//    }
//
//    public void warn(String st) {
//        _log.warn(getClass().getSimpleName() + ": " + st);
//    }
//
//    protected void info(String st, Exception e) {
//        _log.info(getClass().getSimpleName() + ": " + st, e);
//    }
//
//    protected void info(String st) {
//        _log.info(getClass().getSimpleName() + ": " + st);
//    }
//}
