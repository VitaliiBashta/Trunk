package l2trunk;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class Mountain {

    public static void main(String[] args) {
        List<String>  list = new ArrayList<>(Arrays.asList("Arnie","Chuck", "Slay"));
        list.forEach(x -> {
            if (x.equals("Chuck")) {
                list.remove(x);
            }
        });
    }
}
