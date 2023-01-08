package models;

import java.util.HashMap;
import java.util.List;

/**
 * A class to store information about the current episodes on channels that
 * the program has already gotten from the API.
 * Used to reduce the amount of API-calls required.
 * @author Samuel Sandlund
 * @version 1.1 the class now extends HashMap and could be replaced by a HashMap<Integer, RadioChannelTableModel>
 * @since 2023-01-08
 */
public class EpisodeListCache extends HashMap<Integer, RadioChannelTableModel>{

    /**
     * Saves a new channel and its list of episodes
     * @param channelModel the channel on which the episodes are broadcast (the id of this channel is used as key)
     * @param episodeList the list of episodes for the channelModel (a TableModel for this list is saved as value)
     */
    public void saveEpisodeList(ChannelModel channelModel, List<EpisodeModel> episodeList){
        this.put(channelModel.getId(), new RadioChannelTableModel(episodeList, channelModel.getName()));
    }

    /**
     * Checks if data for a channel with the given id has been saved in this object
     * @param channelId id for the channel to check
     * @return true if there is a value for the given channelId, else false
     */
    public boolean hasSavedEpisodeList(int channelId){
        return this.containsKey(channelId);
    }

    /**
     * Gets the tableModel saved for a given channelId
     * @param channelId id of the channel to get RadioChannelTableModel for
     * @return TableModel for the episodes on the channel with the given id.
     */
    public RadioChannelTableModel getEpisodeList(int channelId){
        return this.get(channelId);
    }
}
