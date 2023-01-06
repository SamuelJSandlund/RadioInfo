package models;

import java.util.HashMap;
import java.util.List;

public class EpisodeListCache {
    private HashMap<Integer, RadioChannelTableModel> table;

    public HashMap<Integer, RadioChannelTableModel> getTable(){
        return table;
    }
    public EpisodeListCache(){
        table = new HashMap<>();
    }

    public void saveEpisodeList(int channelId, List<EpisodeModel> episodeList){
        table.put(channelId, new RadioChannelTableModel(episodeList));
    }

    public boolean hasSavedEpisodeList(int channelId){
        return table.containsKey(channelId);
    }

    public RadioChannelTableModel getEpisodeList(int channelId){
        return table.get(channelId);
    }
}
