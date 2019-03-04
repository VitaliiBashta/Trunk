//package l2trunk.commons.logging;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public abstract class LoggerObject {
//    protected final Logger LOG = LoggerFactory.getLogger(getClass());
//
//    protected void error(String st, Exception e) {
//        LOG.error(getClass().getSimpleName() + ": " + st, e);
//    }
//
//    public void error(String st) {
//        LOG.error(getClass().getSimpleName() + ": " + st);
//    }
//
//    protected void warn(String st, Exception e) {
//        LOG.warn(getClass().getSimpleName() + ": " + st, e);
//    }
//
//    public void warn(String st) {
//        LOG.warn(getClass().getSimpleName() + ": " + st);
//    }
//
//    protected void info(String st, Exception e) {
//        LOG.info(getClass().getSimpleName() + ": " + st, e);
//    }
//
//    protected void info(String st) {
//        LOG.info(getClass().getSimpleName() + ": " + st);
//    }
//}
