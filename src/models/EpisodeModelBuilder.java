package models;

import java.time.Instant;

public interface EpisodeModelBuilder {
    void setId(int id);
    void setTitle(String title);
    void setSubtitle(String subtitle);
    void setDescription(String description);
    void setImageURL(String imageURL);
    void setStartTime(Instant startTime);
    void setEndTime(Instant endTime);
    EpisodeModel build();
}
