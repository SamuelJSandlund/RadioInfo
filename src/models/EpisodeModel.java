package models;

import java.awt.*;
import java.time.Instant;

/**
 * Representation of a radio program from the Swedish public radio
 * @author Samuel Sandlund
 * @version 1.1 image type changed from swing ImageIcon to abstract Image
 * @since 2023-02-07
 */
public class EpisodeModel {
    private final int id;
    private final String title;
    private final String subTitle;
    private final String description;
    private final Image image;
    private final String programName;
    private final Instant startTime;
    private final Instant endTime;


    private EpisodeModel(Builder builder){
        this.id = builder.id;
        this.title = builder.title;
        this.subTitle = builder.subTitle;
        this.description = builder.description;
        this.image = builder.image;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.programName = builder.programName;
    }

    public EpisodeModel(int id,
                        String title,
                        String subTitle,
                        String description,
                        Image image,
                        String programName,
                        Instant startTime,
                        Instant endTime){
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.image = image;
        this.startTime = startTime;
        this.endTime = endTime;
        this.programName = programName;
    }

    /**
     * @return the id of this episode
     */
    public int getId(){
        return id;
    }

    /**
     * @return the title of this episode
     */
    public String getTitle(){
        return title;
    }

    /**
     * @return the subtitle of this episode
     */
    public String getSubTitle() {
        return subTitle;
    }

    /**
     * @return a description of this episode
     */
    public String getDescription(){
        return description;
    }

    /**
     * @return the cover image for this episode
     */
    public Image getImage(){return image;}

    /**
     * @return the time (UTC) at which the broadcast starts
     */
    public Instant getStartTime(){
        return startTime;
    }

    /**
     * @return the time (UTC) at which the broadcast ends
     */
    public Instant getEndTime(){
        return endTime;
    }

    /**
     * @return the name of the program that this episode is a part of
     */
    public String getProgramName(){return programName;}


    public static class Builder implements EpisodeModelBuilder{
        private int id;
        private String title = "";
        private String subTitle = "";
        private String description = "";
        private Image image = null;
        private String programName = "";
        private Instant startTime = null;
        private Instant endTime = null;


        @Override
        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public void setSubtitle(String subtitle) {
            this.subTitle = subtitle;
        }

        @Override
        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public void setImage(Image image) {
            this.image = image;
        }

        @Override
        public void setStartTime(Instant startTime) {
            this.startTime = startTime;
        }

        @Override
        public void setEndTime(Instant endTime) {
            this.endTime = endTime;
        }

        @Override
        public void setProgramName(String programName){
            this.programName = programName;
        }

        @Override
        public EpisodeModel build() {
            return new EpisodeModel(this);
        }
    }
}