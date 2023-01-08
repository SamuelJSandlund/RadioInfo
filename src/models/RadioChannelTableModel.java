package models;

import javax.swing.table.AbstractTableModel;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A table model that defines how information about the episodes on a radio channel
 * should be displayed in a table
 * @author Samuel Sandlund
 * @version 1.0
 * @since 2023-01-08
 */
public class RadioChannelTableModel extends AbstractTableModel {
    private List<EpisodeModel> episodes;
    private String channelName;

    public RadioChannelTableModel(List<EpisodeModel> episodes, String channelName){
        this.episodes = episodes;
        this.channelName = channelName;
    }

    /**
     * Updates the list of episodes that this table model has
     * This model is synchronized since multiple threads could be trying to update the
     * table at the same time
     * @param newEpisodes the new list of episodes
     */
    public synchronized void updateEpisodeList(List<EpisodeModel> newEpisodes){
        episodes = newEpisodes;
        this.fireTableDataChanged();
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

    /**
     * @return the name of the channel that the episodes in the table are broadcast on
     */
    public String getChannelName(){
        return channelName;
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
