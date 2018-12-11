package l2trunk.commons.math;

public class SafeMath {


    public static long addAndLimit(long a, long b) {
        return addAndCheck(a, b, "overflow: add", true);
    }

    public static long addAndCheck(long a, long b) throws ArithmeticException {
        return addAndCheck(a, b, "overflow: add", false);
    }

    private static long addAndCheck(long a, long b, String msg, boolean limit) {
        long ret;
        if (a > b)
            // use symmetry to reduce boundary cases
            ret = addAndCheck(b, a, msg, limit);
        else if (a < 0) {
            if (b < 0) {
                // check for negative overflow
                if (Long.MIN_VALUE - b <= a)
                    ret = a + b;
                else if (limit)
                    ret = Long.MIN_VALUE;
                else
                    throw new ArithmeticException(msg);
            } else
                // opposite sign addition is always safe
                ret = a + b;
        } else // check for positive overflow
            if (a <= Long.MAX_VALUE - b)
                ret = a + b;
            else if (limit)
                ret = Long.MAX_VALUE;
            else
                throw new ArithmeticException(msg);
        return ret;
    }

    public static long mulAndCheck(long a, long b) throws ArithmeticException {
        return mulAndCheck(a, b, "overflow: mul", false);
    }

    public static long mulAndLimit(long a, long b) {
        return mulAndCheck(a, b, "overflow: mul", true);
    }

    private static long mulAndCheck(long a, long b, String msg, boolean limit) {
        long ret;
        if (a > b)
            // use symmetry to reduce boundary cases
            ret = mulAndCheck(b, a, msg, limit);
        else if (a < 0) {
            if (b < 0) {
                // check for positive overflow with negative a, negative b
                if (a >= Long.MAX_VALUE / b)
                    ret = a * b;
                else if (limit)
                    ret = Long.MAX_VALUE;
                else
                    throw new ArithmeticException(msg);
            } else if (b > 0) {
                // check for negative overflow with negative a, positive b
                if (Long.MIN_VALUE / b <= a)
                    ret = a * b;
                else if (limit)
                    ret = Long.MIN_VALUE;
                else
                    throw new ArithmeticException(msg);
            } else
                ret = 0;
        } else if (a > 0) {
            // check for positive overflow with positive a, positive b
            if (a <= Long.MAX_VALUE / b)
                ret = a * b;
            else if (limit)
                ret = Long.MAX_VALUE;
            else
                throw new ArithmeticException(msg);
        } else
            ret = 0;
        return ret;
    }

}