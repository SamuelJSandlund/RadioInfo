package models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class APIHandler {

    /**
     * Gets all radio channels from the Swedish public radio (SR) API
     * @return List of available channels
     */
    public List<ChannelModel> getChannels(){
        ArrayList<ChannelModel> results = new ArrayList<>();
        try{
            //send request to API
            URL endpoint = new URL("http://api.sr.se/api/v2/channels");
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if(connection.getResponseCode() != 200){
                throw new RuntimeException("Non \"OK\" response code received: " + connection.getResponseCode());
            }
            connection.disconnect();

            //parse document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(endpoint.openStream());
            //normalize
            doc.getDocumentElement().normalize();


            //get page count
            NodeList pageCountList = doc.getElementsByTagName("totalpages");
            Node noPagesNode = pageCountList.item(0);
            Element noPagesElement = (Element) noPagesNode;
            int pageCount = Integer.parseInt(noPagesElement.getTextContent());

            //get document for all pages
            ArrayList<Document> documentList = new ArrayList<>();
            documentList.add(doc);
            for(int i = 2 ; i <= pageCount ; i++){
                endpoint = new URL("http://api.sr.se/api/v2/channels?page=" + i);
                connection = (HttpURLConnection) endpoint.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if(connection.getResponseCode() != 200){
                    throw new RuntimeException("Non \"OK\" response code received: " + connection.getResponseCode());
                }
                connection.disconnect();
                doc = documentBuilder.parse(endpoint.openStream());
                documentList.add(doc);
            }

            //parse channel models from all documents
            for(Document d : documentList){
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
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    /**
     *
     * @param channel
     * @return
     */
    public List<EpisodeModel> getScheduledEpisodes(ChannelModel channel){
        ArrayList<EpisodeModel> results = new ArrayList<>();
        try {
            URL endpoint = new URL("http://api.sr.se/v2/scheduledepisodes?channelid=" + channel.getId());
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if(connection.getResponseCode() != 200){
                throw new RuntimeException("Non \"OK\" response code received: " + connection.getResponseCode());
            }
            connection.disconnect();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(endpoint.openStream());
            doc.getDocumentElement().normalize();

            NodeList pageCountList = doc.getElementsByTagName("totalpages");
            Node noPagesNode = pageCountList.item(0);
            Element noPagesElement = (Element) noPagesNode;
            int pageCount = Integer.parseInt(noPagesElement.getTextContent());

            ArrayList<Document> documentList = new ArrayList<>();
            documentList.add(doc);
            for(int i = 2 ; i <= pageCount ; i++){
                endpoint = new URL("http://api.sr.se/v2/scheduledepisodes?channelid=" + channel.getId() + "&page=" + i);
                connection = (HttpURLConnection) endpoint.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if(connection.getResponseCode() != 200){
                    throw new RuntimeException("Non \"OK\" response code received: " + connection.getResponseCode());
                }
                connection.disconnect();
                doc = documentBuilder.parse(endpoint.openStream());
                documentList.add(doc);
            }

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
                            case "imageurl" -> episodeBuilder.setImageURL(episodeInfo.getTextContent());
                            default -> {
                                continue;//skip attributes we are not interested in
                            }
                        }
                    }
                    results.add(episodeBuilder.build());
                }
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        return results;
    }
}
