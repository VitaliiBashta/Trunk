package l2trunk;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.LongUnaryOperator;

class Mountain {
    static String name = "Himalaya";
    static Mountain getMountain() {
        System.out.println("getting name");
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getMountain().name);
    }


}
