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

@Path("/spam")
public class SpamResource {

//    your SpamDetector Class responsible for all the SpamDetecting logic
    SpamDetector detector = new SpamDetector();
    private List<TestFile> results=new ArrayList<>();


    SpamResource() throws IOException {
//        TODO: load resources, train and test to improve performance on the endpoint calls
        System.out.print("Training and testing the model, please wait");

//      TODO: call  this.trainAndTest()
      this.results=trainAndTest();


    }
    // In the end delete it, fix getspamResults! using it to make testing esaiser


    @GET
    @Produces("application/json")
    public Response getSpamResults() throws IOException {
//       TODO: return the test results list of TestFile, return in a Response object
        return Response.status(200).header("Access-Control-Allow-Origin","http://localhost:63342")
                .header("Content-Type","application/json").
                entity(results).build();

    }

    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() throws IOException {
//      TODO: return the accuracy of the detector, return in a Response object
        return Response.status(200).header("Access-Control-Allow-Origin","http://localhost:63342")
                .header("Content-Type","application/json").
                entity(getAccuracyAndPrescision(results)[0]).build();
        //TODO: return map converted to json object
    }

    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() throws IOException {
       //      TODO: return the precision of the detector, return in a Response object
        return Response.status(200).header("Access-Control-Allow-Origin","http://localhost:63342")
                .header("Content-Type","application/json").
                entity(getAccuracyAndPrescision(results)[1]).build();
        //TODO: return map converted to json object
    }
    public String TestingProb() throws JsonProcessingException {
        URL url =this.getClass().getClassLoader().getResource("/data");
        File mainDirectory = null;
        try {
            mainDirectory= new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            this.detector.trainAndTest(mainDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TreeMap<String, Double> p=this.detector.gettestingProb();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonArray = objectMapper.writeValueAsString(p);
        return jsonArray;
    };
    public String TestingMaps() throws JsonProcessingException {
        URL url =this.getClass().getClassLoader().getResource("/data");
        File mainDirectory = null;
        try {
            mainDirectory= new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            this.detector.trainAndTest(mainDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<TreeMap<String,Integer>> listOfFiles=this.detector.getWordsInFile();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonArray = objectMapper.writeValueAsString(listOfFiles);
        return jsonArray;
    }
        //         This function basically calls the detector.trainAndTest with a given directory
    private String returnFilesResponse(List<TestFile> results) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonArray = objectMapper.writeValueAsString(results);
        return jsonArray;
    };

   //Previous Working Function split into calculateAccuracy() and calculatePrecision()
    /*
    private double[] getAccuracyAndPrescision(List<TestFile> results) throws IOException {
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
        return new double[]{(double) (trueNegative + truePositive) / results.size(), (truePositive/(double) (falsePositive+truePositive))};
    };
    */


    //Calculates accuracy and returns a Map object with key string "accuracy" and value of calculated accuracy
    private Map<String, double> calculateAccuracy(List<TestFile> results) throws IOException {
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

        Map<String, double> map = new map<>();
        map.put("accuracy" , (double) (trueNegative + truePositive) / results.size() );
        return map;
    }

    //Calculates precision and returns a Map object with key string "precision" and value of calculated precision
    private Map<String Accuracy, Double Value> calculatePrecision(List<TestFile> results) throws IOException {
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

        Map<String, double> map = new map<>();
        map.put("precision" , (truePositive/((double) (falsePositive+truePositive))) );
        return map;

    }

    private List<TestFile> trainAndTest() throws IOException {
        if (this.detector==null){
            this.detector = new SpamDetector();
        }

//        TODO: load the main directory "data" here from the Resources folder
        URL url =this.getClass().getClassLoader().getResource("/data");
        File mainDirectory = null;
        try {
            mainDirectory= new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            return this.detector.trainAndTest(mainDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}