package classes;

/**
 * Created by Riley on 2016-09-17.
 */
public class Person {

    private double person_lat;  //Latitude of house
    private double person_long; //Longitude of house
    private String person_name;
    private String id;
    private int colour;
    private int group;
    private boolean accepted;

    public Person(){};

    public Person(double _lat, double _long){

        this.person_lat = _lat;
        this.person_long = _long;
        this.accepted = false;
        this.group = 0;

    }

    public double getLat(){
        return person_lat;
    }

    public double getLong(){
        return person_long;
    }

    public String getName(){
        return this.person_name;
    }

    public void setLong(double newLong){
        this.person_long = newLong;
    }

    public void setLat(double newLat){
        this.person_lat = newLat;
    }

    public void setName(String name){
        this.person_name = name;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String newId){
        this.id = newId;
    }

    public void setGroup(int newGroup){
        this.group = newGroup;
    }

    public int getGroup(){
        return this.group;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public boolean getAccepted(){
        return this.accepted;
    }

    public void setAccepted(boolean didAccepted){
        this.accepted = didAccepted;
    }
}
