package models;

import java.awt.*;
import java.time.Instant;

/**
 * Used to create EpisodeModel object step by step
 */
public interface EpisodeModelBuilder {
    void setId(int id);
    void setTitle(String title);
    void setSubtitle(String subtitle);
    void setDescription(String description);
    void setImage(Image image);
    void setProgramName(String programName);
    void setStartTime(Instant startTime);
    void setEndTime(Instant endTime);
    EpisodeModel build();
}
