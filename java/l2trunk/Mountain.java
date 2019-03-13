package l2trunk;

import org.json.JSONArray;
import org.json.JSONObject;


public class Mountain {

    public static void main(String[] args) {
        JSONObject obj = new JSONObject();
        obj.put("Novel Name", "Godaan");
        obj.put("Author", "Munshi Premchand");

        JSONArray novelDetails = new JSONArray();
        novelDetails.put("Language: Hindi");
        novelDetails.put("Year of Publication: 1936");
        novelDetails.put("Publisher: Lokmanya Press");

        obj.put("Novel Details", novelDetails);
        System.out.print(obj);
    }
}

