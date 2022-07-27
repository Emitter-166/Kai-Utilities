package org.example.captionMe;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class counter extends ListenerAdapter {
    int counter = 0;
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        counter++;
        if(counter > 500){
            counter = 0;


        }
    }

    static String getImageUrl() throws UnirestException {
        HttpResponse<String> response = Unirest.get("https://contextualwebsearch-websearch-v1.p.rapidapi.com/api/Search/ImageSearchAPI?q=Weird%20Animals&pageNumber=1&pageSize=10&autoCorrect=true")
                .header("X-RapidAPI-Key", "1c6bd68f50msh8668cb6aeb0ec76p1865d3jsnbeb97c4a99d9")
                .header("X-RapidAPI-Host", "contextualwebsearch-websearch-v1.p.rapidapi.com")
                .asString();

        return response.getBody();
    }

    public static void main(String[] args) throws UnirestException {
        System.out.println(getImageUrl());
    }
}
