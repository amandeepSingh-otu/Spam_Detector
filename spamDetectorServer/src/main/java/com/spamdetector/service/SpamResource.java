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


    SpamResource(){
//        TODO: load resources, train and test to improve performance on the endpoint calls
        System.out.print("Training and testing the model, please wait");

//      TODO: call  this.trainAndTest();


    }
    // In the end delete it, fix getspamResults! using it to make testing esaiser

    @GET
    @Produces("text/string")
    public String get() throws IOException {
//       TODO: return the test results list of TestFile, return in a Response object
        URL url =this.getClass().getClassLoader().getResource("/data");
        File mainDirectory = null;
        try {
            assert url != null;
            mainDirectory= new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String p=(mainDirectory+"\\train\\ham");
        File[] fileArrays=new File(p).listFiles();
       ArrayList<String> wordsInFile=new ArrayList<>();

            FileReader fileReader = null;
            try {
                fileReader = new FileReader(fileArrays[1]);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            String g="";
       for(File file: fileArrays){
            g=g+file.getName()+"\n";
       };
       return g;
    }

    @GET
    @Path("/it")
    @Produces("application/json")
    public Response getSpamResults() throws IOException {
//       TODO: return the test results list of TestFile, return in a Response object
        return Response.status(200).header("Access-Control-Allow-Origin","http://localhost:63342")
                .header("Content-Type","application/json").
                entity(TestingProb()).build();

    }

    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() {
//      TODO: return the accuracy of the detector, return in a Response object

        return null;
    }

    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() {
       //      TODO: return the precision of the detector, return in a Response object

        return null;
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
    private String returnFilesResponse() throws IOException {
            List<TestFile> listOfFiles=trainAndTest();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonArray = objectMapper.writeValueAsString(listOfFiles);
        return jsonArray;
    };
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