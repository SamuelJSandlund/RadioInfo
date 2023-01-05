package models;


import java.time.Instant;

public class EpisodeModel {
    private final int id;

    private final String title;
    private final String subTitle;
    private final String description;
    private final String imageURL;

    private final Instant startTime;
    private final Instant endTime;

    private EpisodeModel(Builder builder){
        this.id = builder.id;
        this.title = builder.title;
        this.subTitle = builder.subTitle;
        this.description = builder.description;
        this.imageURL = builder.imageURL;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
    }

    public EpisodeModel(int id, String title, String subTitle, String description, String imageURL, Instant startTime, Instant endTime){
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.imageURL = imageURL;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getSubTitle() {
        return subTitle;
    }
    public String getDescription(){
        return description;
    }
    public String getImageURL(){return description;}
    public Instant getStartTime(){
        return startTime;
    }
    public Instant getEndTime(){
        return endTime;
    }


    public static class Builder implements EpisodeModelBuilder{
        private int id;

        private String title = "";
        private String subTitle = "";
        private String description = "";
        private String imageURL = "";

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
        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
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
        public EpisodeModel build() {
            return new EpisodeModel(this);
        }
    }
}
