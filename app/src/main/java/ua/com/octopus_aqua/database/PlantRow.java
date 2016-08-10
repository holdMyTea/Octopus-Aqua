package ua.com.octopus_aqua.database;

import org.json.JSONException;
import org.json.JSONObject;

public class PlantRow {

    private int id;
    private String name;
    private String info;
    private String type;
    private String pic;
    private String group;

    public static final String    ID = "id";
    public static final String  NAME = "name";
    public static final String  INFO = "info";
    public static final String  TYPE = "type";
    public static final String   PIC = "pic";
    public static final String GROUP = "group";

    public static final String NO_GROUP = "none";


    public PlantRow(int id, String name, String info, String type, String pic, String group){
        this.id = id;
        this.name = name;
        this.info = info;
        this.type = type;
        this.pic = pic;
        this.group = group;
    }

    public PlantRow(String objJSON){
        this(decodeJSON(objJSON));
    }

    private PlantRow(PlantRow row){
        this.id = row.getId();

        this.name = row.getName();
        this.info = row.getInfo();
        this.type = row.getType();
        this.pic = row.getPic();
        this.group= row.getGroup();
    }

    public String encodeJSON(){
        JSONObject row = new JSONObject();

        try {
            row.put(ID,id);
            row.put(NAME,name);
            row.put(INFO,info);
            row.put(TYPE,type);
            row.put(PIC,pic);
            row.put(GROUP,group);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return row.toString();
    }

    public static PlantRow decodeJSON(String objJSON){
        try {
            JSONObject row = new JSONObject(objJSON);

            int id;
            String name, info ,type, pic, group;

            id = row.getInt(ID);
            name = row.getString(NAME);
            info = row.getString(INFO);
            type = row.getString(TYPE);
            pic = row.getString(PIC);
            group = row.getString(GROUP);

            return new PlantRow(id,name,info,type,pic,group);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getType() {
        return type;
    }

    public String getPic() {
        return pic;
    }

    public String getGroup() {
        return group;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
