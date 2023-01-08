package models;

/**
 * Representation of a radio channel from the Swedish public radio
 * @author Samuel Sandlund
 * @version 1.0
 * @since 2023-01-08
 */
public class ChannelModel {
    private int id;
    private String name;

    public ChannelModel(int id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * @return the id of this radio channel
     */
    public int getId(){
        return id;
    }

    /**
     * @return the name of this radio channel
     */
    public String getName(){
        return name;
    }
}



