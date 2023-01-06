package models;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RadioChannelTableModel extends AbstractTableModel {
    private List<EpisodeModel> episodes;
    public RadioChannelTableModel(List<EpisodeModel> episodes){
        this.episodes = episodes;
    }

    public void updateEpisodeList(List<EpisodeModel> newEpisodes){
        episodes = newEpisodes;
        this.fireTableDataChanged();
    }

    public boolean isEmpty(){
        return episodes.isEmpty();
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
                return episode.getProgramName().equals("") ? "NAMNLÖST PROGRAM" : episode.getProgramName();
            }
            case 1 -> {
                return episode.getStartTime().atZone(ZoneId.systemDefault()).toLocalDateTime().
                        format(DateTimeFormatter.ofPattern("d MMM hh:mm"));
            }
            case 2 -> {
                return episode.getEndTime().atZone(ZoneId.systemDefault()).toLocalDateTime().
                        format(DateTimeFormatter.ofPattern("d MMM hh:mm"));
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
