package com.sweetdum.rssclient;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sweetdum on 2015/4/28.
 */
public class RSSChannel {
    private String sourceURL;
    private String title,link,description;
    private List<RSSItem> itemList;
    public RSSChannel(String source){
        this.sourceURL=source;
        itemList=new ArrayList<RSSItem>();
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RSSItem> getItemList() {
        return itemList;
    }
    public void readData()  {
        final String  TAG="RSSPaser";
        XmlPullParser pullParser= Xml.newPullParser();
        Log.d(TAG,"StartReading..");
        try {
            URL url=new URL(sourceURL);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            InputStream dataIn=connection.getInputStream();
            if (connection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                return ;
            }

            pullParser.setInput(dataIn, "UTF-8");
            int eventType=pullParser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_TAG) {
                    if (pullParser.getName().toUpperCase().equals("RSS")){
                        RSSTagWork(pullParser);
                    }
                }
                eventType = pullParser.next();
            }
        }catch (XmlPullParserException e){
            Log.e(TAG,"XML ERROR",e);
        }
        catch (IOException e){
            Log.e(TAG,"XML ERROR",e);
        }
    }
    private void RSSTagWork(XmlPullParser pullParser) throws IOException,XmlPullParserException{
        int eventType=pullParser.next();
        while (eventType!=XmlPullParser.END_DOCUMENT){
            if (eventType==XmlPullParser.START_TAG){
                if (pullParser.getName().toUpperCase().equals("CHANNEL")){
                    ChannelWork(pullParser);
                }
            }
            if (eventType==XmlPullParser.END_TAG){
                if (pullParser.getName().toUpperCase().equals("RSS")){
                    return ;
                }
            }
            eventType=pullParser.next();
        }
    }
    private void ChannelWork(XmlPullParser pullParser) throws IOException,XmlPullParserException{
        int eventType=pullParser.next();
        while (eventType!=XmlPullParser.END_DOCUMENT){
            if (eventType==XmlPullParser.START_TAG){
                if (pullParser.getName().toUpperCase().equals("TITLE")){
                    this.title=getRSSText(pullParser);
                }
                if (pullParser.getName().toUpperCase().equals("LINK")){
                    this.link=getRSSText(pullParser);
                }
                if (pullParser.getName().toUpperCase().equals("DESCRIPTION")){
                    this.description=getRSSText(pullParser);
                }
                if (pullParser.getName().toUpperCase().equals("ITEM")){
                    ItemWork(pullParser);
                }
            }
            if (eventType==XmlPullParser.END_TAG){
                if (pullParser.getName().toUpperCase().equals("CHANNEL")){
                    return;
                }
            }
            eventType=pullParser.next();
        }
    }
    private void ItemWork(XmlPullParser pullParser) throws IOException, XmlPullParserException{
        int eventType=pullParser.next();
        RSSItem item= new RSSItem();
        while (eventType!=XmlPullParser.END_DOCUMENT){
            if (eventType==XmlPullParser.START_TAG){
                if (pullParser.getName().toUpperCase().equals("TITLE")){
                    item.setTitle(getRSSText(pullParser));
                }
                if (pullParser.getName().toUpperCase().equals("LINK")){
                    item.setLink(getRSSText(pullParser));
                }
                if (pullParser.getName().toUpperCase().equals("DESCRIPTION")){
                    item.setDescription(getRSSText(pullParser));
                }
                if (pullParser.getName().toUpperCase().equals("PUBDATE")){
                    item.setPutDate(getRSSText(pullParser));
                }
                if (pullParser.getName().toUpperCase().equals("AUTHOR")){
                    item.setAuthor(getRSSText(pullParser));
                }
                if (pullParser.getName().toUpperCase().equals("CATEGORY")) {
                    item.setCategory(getRSSText(pullParser));
                }
            }
            if (eventType==XmlPullParser.END_TAG){
                if (pullParser.getName().toUpperCase().equals("ITEM")){
                    itemList.add(item);
                    return;
                }
            }
            eventType=pullParser.next();
        }
    }
    private String getRSSText(XmlPullParser parser) throws IOException, XmlPullParserException{
        int eventType = parser.next();
        String s="";
        while (eventType!=XmlPullParser.END_TAG){
            if (eventType==XmlPullParser.TEXT){
                s=parser.getText();
            }
            eventType=parser.next();
        }
        return s;
    }
}
