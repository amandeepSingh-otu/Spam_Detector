package com.spamdetector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spamdetector.domain.TestFile;
import com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import jakarta.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

@Path("/spam")
public class SpamResource {

    //    your SpamDetector Class responsible for all the SpamDetecting logic
    SpamDetector detector = new SpamDetector();
   //We're running spamDetector just once to get the list of results and then store into this variable
    private List<TestFile> results=new ArrayList<>();


    SpamResource() throws IOException {
        // TODO: load resources, train and test to improve performance on the endpoint calls
        System.out.print("Training and testing the model, please wait");

        //TODO: call  this.trainAndTest()
      this.results=trainAndTest();


    }


    @GET
    @Produces("application/json")
    public Response getSpamResults() throws IOException {

        //  TODO: return the test results list of TestFile, return in a Response object
        return Response.status(200).header("Access-Control-Allow-Origin","http://localhost:63342")
                .header("Content-Type","application/json").
                entity(results).build();

    }

    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() throws IOException {
        //return the accuracy of the detector, return in a Response object
        return Response.status(200).header("Access-Control-Allow-Origin","http://localhost:63342")
                .header("Content-Type","application/json").
                entity(calculateAccuracy()).build();
    }

    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() throws IOException {
        //return the precision of the detector, return in a Response object
        return Response.status(200).header("Access-Control-Allow-Origin","http://localhost:63342")
                .header("Content-Type","application/json").
                entity(calculatePrecision()).build();

    }



    //Calculates accuracy and returns a Map object with key string "accuracy" and value of calculated accuracy
    private TreeMap<String, Double> calculateAccuracy() throws IOException {
        double accuracy=0;
        int truePositive=0;
        int trueNegative=0;
        int falsePositive=0;
        for( TestFile file: results){
            if(file.getSpamProbability()<0.5 && file.getActualClass().equals("Ham")){
                truePositive+=1;
            }
            if(file.getSpamProbability()>0.5 && file.getActualClass().equals("Spam")){
                trueNegative+=1;
            }
            if(file.getSpamProbability()<0.5 && file.getActualClass().equals("Spam")){
                falsePositive+=1;
            }
        };

        TreeMap<String, Double> map = new TreeMap<>();
        map.put("accuracy" , (double) (trueNegative + truePositive) / results.size() );
        return map;
    }

    //Calculates precision and returns a Map object with key string "precision" and value of calculated precision
    private TreeMap<String, Double> calculatePrecision() throws IOException {
        int truePositive=0;
        int trueNegative=0;
        int falsePositive=0;
        for( TestFile file: results){
            if(file.getSpamProbability()<0.5 && file.getActualClass().equals("Ham")){
                truePositive+=1;
            }
            if(file.getSpamProbability()>0.5 && file.getActualClass().equals("Spam")){
                trueNegative+=1;
            }
            if(file.getSpamProbability()<0.5 && file.getActualClass().equals("Spam")){
                falsePositive+=1;
            }
        };

        TreeMap<String, Double> map = new TreeMap<>();
        map.put("precision" , (truePositive/((double) (falsePositive+truePositive))) );
        return map;

    }

    // This function get the URL of our data and teh call testAndTrain from detetcor storing results in the result
    private List<TestFile> trainAndTest() throws IOException {
        if (this.detector==null){
            this.detector = new SpamDetector();
        }

        //TODO: load the main directory "data" here from the Resources folder
        URL url =this.getClass().getClassLoader().getResource("/data");
        File mainDirectory = null;
        try {
            assert url != null;
            mainDirectory= new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            //we're calling trainAndTest function of detector, passing it the directory opf our data resources
            return this.detector.trainAndTest(mainDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}