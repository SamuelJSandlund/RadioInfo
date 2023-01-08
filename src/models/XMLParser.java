package models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A class used to parse models for radio channels and episodes from XML documents
 * from the Swedish public radio API
 */
public class XMLParser {

    /**
     * Parses models for radio channels from the given list of documents
     * @param documentList XML documents from the Swedish public radio API
     * @return a list of ChannelModels, if no channels could be parsed this list is empty.
     */
    public List<ChannelModel> parseChannels(List<Document> documentList){
        ArrayList<ChannelModel> results = new ArrayList<>();
        //parse channel models from all documents
        for(Document d : documentList){
            d.normalize();
            NodeList channelList = d.getElementsByTagName("channel");
            for(int i = 0 ; i < channelList.getLength() ; i++){
                Node channelNode = channelList.item(i);
                if(channelNode.getNodeType() != Node.ELEMENT_NODE){continue;}//skip non-element nodes
                Element channelElement = (Element) channelNode;
                int channelId = Integer.parseInt(channelElement.getAttribute("id"));
                String channelName = channelElement.getAttribute("name");
                ChannelModel channel = new ChannelModel(channelId, channelName);
                results.add(channel);
            }
        }
        return results;
    }

    /**
     * Parses models for radio-program-episodes from the given list of documents
     * @param documentList XML documents from the Swedish public radio API
     * @return a list of EpisodeModels, if no episodes could be parsed this list is empty.
     */
    public List<EpisodeModel> parseEpisodes(List<Document> documentList){
        APIHandler imageFetcher = new APIHandler();
        ArrayList<EpisodeModel> results = new ArrayList<>();
        for(Document d : documentList){
            NodeList episodeList = d.getElementsByTagName("scheduledepisode");
            for(int i = 0 ; i < episodeList.getLength() ; i++){
                EpisodeModelBuilder episodeBuilder = new EpisodeModel.Builder();
                Node episodeNode = episodeList.item(i);
                if(episodeNode.getNodeType() != Node.ELEMENT_NODE){continue;}
                Element episode = (Element) episodeNode;
                //get info from side nodes
                NodeList episodeInfoList = episode.getChildNodes();
                for(int j = 0 ; j < episodeInfoList.getLength() ; j++){
                    Node episodeInfoNode = episodeInfoList.item(j);
                    if(episodeInfoNode.getNodeType() != Node.ELEMENT_NODE){continue;}
                    Element episodeInfo = (Element) episodeInfoNode;
                    switch (episodeInfo.getTagName()){
                        case "episodeid" -> episodeBuilder.setId(Integer.parseInt(episodeInfo.getTextContent()));
                        case "title" -> episodeBuilder.setTitle(episodeInfo.getTextContent());
                        case "subtitle" -> episodeBuilder.setSubtitle(episodeInfo.getTextContent());
                        case "description" -> episodeBuilder.setDescription(episodeInfo.getTextContent());
                        case "starttimeutc" -> episodeBuilder.setStartTime(Instant.parse(episodeInfo.getTextContent()));
                        case "endtimeutc" -> episodeBuilder.setEndTime(Instant.parse(episodeInfo.getTextContent()));
                        case "imageurl" -> episodeBuilder.setImage(imageFetcher.getImage(episodeInfo.getTextContent()));
                        case "program" -> episodeBuilder.setProgramName(episodeInfo.getAttribute("name"));
                        default -> {
                            continue;//skip other attributes
                        }
                    }
                }
                results.add(episodeBuilder.build());
            }
        }
        return results;
    }
}
