package models;

import javax.swing.*;
import java.time.Instant;

/**
 * Used to create EpisodeModel object step by step
 */
public interface EpisodeModelBuilder {
    void setId(int id);
    void setTitle(String title);
    void setSubtitle(String subtitle);
    void setDescription(String description);
    void setImage(ImageIcon image);
    void setProgramName(String programName);
    void setStartTime(Instant startTime);
    void setEndTime(Instant endTime);
    EpisodeModel build();
}
