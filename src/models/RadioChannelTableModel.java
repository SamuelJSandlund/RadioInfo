package models;

import javax.swing.table.AbstractTableModel;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A table model that defines how information about the episodes on a radio channel
 * should be displayed in a table
 * @author Samuel Sandlund
 * @version 2.0 ability to switch episode list removed, episodes is now final
 * @since 2023-02-07
 */
public class RadioChannelTableModel extends AbstractTableModel {
    private final List<EpisodeModel> episodes;

    public RadioChannelTableModel(List<EpisodeModel> episodes){
        this.episodes = episodes;
    }

    /**
     * @return true if the table does not contain any episodes else false
     */
    public boolean isEmpty(){
        return episodes.isEmpty();
    }

    /**
     * returns a specific episode from the tables list of episodes
     * @param index the index of the desired episode
     * @return EpisodeModel for the selected episode
     */
    public EpisodeModel getEpisode(int index){
        return episodes.get(index);
    }

    @Override
    public int getRowCount() {
        return episodes.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EpisodeModel episode = episodes.get(rowIndex);
        switch (columnIndex){
            case 0 -> {
                //show the program name if it is available, else show the title of the episode
                return episode.getProgramName().equals("") ? episode.getTitle() : episode.getProgramName();
            }
            case 1 -> {
                return episode.getStartTime().atZone(ZoneId.systemDefault()).toLocalDateTime().
                        format(DateTimeFormatter.ofPattern("d MMM HH:mm"));
            }
            case 2 -> {
                return episode.getEndTime().atZone(ZoneId.systemDefault()).toLocalDateTime().
                        format(DateTimeFormatter.ofPattern("d MMM HH:mm"));
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> "Program:";
            case 1 -> "Sändningen startar:";
            case 2 -> "Sändningen slutar:";
            default -> null;
        };
    }
}
