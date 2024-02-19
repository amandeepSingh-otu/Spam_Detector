package com.spamdetector.util;

import com.spamdetector.domain.TestFile;

import java.io.*;
import java.util.*;


/**
 * TODO: This class will be implemented by you
 * You may create more methods to help you organize you strategy and make you code more readable
 */
public class SpamDetector {
    /*
    * Creating HamOccurrences, SpamOccurrences to hold number of files which have the word
    * probOfAllWorld contain prob that file is spam given that the word is in it
    * I am using an arraylist to not include common words or the words which won't provide any value to our estimation
    * */
    TreeMap<String, Integer> hamOccurrences =new TreeMap<>();
    TreeMap<String,Integer> spamOccurrences =new TreeMap<>();
    TreeMap<String,Double> probOfAllWords=new TreeMap<>();
   static ArrayList<String> commonWords= new ArrayList<>();
   String[] commonWordsArray={"I", "you", "he", "she", "it", "we", "they", "me", "him", "her", "us", "them","my","your",
           "his", "its", "our", "their", "a", "an", "the", "and", "in", "of", "on", "with", "to", "for", "from", "at","  "," ",""};

   //So we are being lazy and instead of sorting the common words, we letting java to do that for us
   public SpamDetector(){
       commonWords.addAll(Arrays.asList(commonWordsArray));
       commonWords.sort(Comparator.naturalOrder());
   };

    /*This function take subdirectory train/spam or train/ham and a flag to train the model
        and save results in our attributes containing words and the number of files they are in
     */
    public void numberOfOccurrence(String directory, File[] fileArrays, boolean isHam) throws IOException {
            //going to go through each file in subdirectory and check for numebrs
        for(File file: fileArrays){
            ArrayList<String> lineWords = getStrings(directory,file);
            for(String word:lineWords){
                if(isHam){
                    //check if those are one of common words
                    //add it into ham
                    //if(Collections.binarySearch(commonWords,word)<0){
                        hamOccurrences.put(word,null==hamOccurrences.get(word)?1: hamOccurrences.get(word)+1);


                }
                else{
                    //add it to the spam and check if it is one of common or meaningless words
                    //if(Collections.binarySearch(commonWords,word)<0){
                        spamOccurrences.put(word,null== spamOccurrences.get(word)?1: spamOccurrences.get(word)+1);
                }
            }
        }

    }

    private ArrayList<String> getStrings(String directory, File file) throws IOException {
        ArrayList<String> wordsInFile=new ArrayList<>();
        FileReader fileReader = null;
        directory=directory+"\\"+file.getName();
        try {
            fileReader = new FileReader(directory);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        BufferedReader input = new BufferedReader(fileReader);
        String line=null;
        boolean flag= false;
        String[] lineWords=null;
        while((line=input.readLine())!=null){
                lineWords=line.split("[^a-zA-z]+");
                for(String word: lineWords){
                    if("Subject".equals(word)){
                        flag=true;
                    }
                    if(flag) {
                        //gonna put the words in our map
                        if(Collections.binarySearch(commonWords,word)<0) {
                            boolean add = wordsInFile.add(word);
                        }
                    }
                };
        ;}
        return  wordsInFile;
    }

    ;
    //give prob of word being spam
    public double getProbabilityOfWord(String word){
        double probFileIsSpamHaveThisWord=0.0f;
        int p=(null==spamOccurrences.get(word))?0:spamOccurrences.get(word);
        int z=(null==hamOccurrences.get(word))?0:hamOccurrences.get(word);
        if(p==0 && z==0){
            return 0.0;
        }
        probFileIsSpamHaveThisWord= ((double)p/spamOccurrences.size())/(((double) p/spamOccurrences.size())+((double)((z)/hamOccurrences.size())));
        return probFileIsSpamHaveThisWord;
    }
    public double probFileIsSpam(File file) throws IOException {
        double probFileIsSpamf=0.0f;
        double sumsOfAllProb=0.0f;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        BufferedReader input = new BufferedReader(fileReader);
        String line=null;
        boolean flag= false;
        while((line=input.readLine())!=null){
            String[] lineWords=line.split("[^a-zA-z]+");
            for(String word: lineWords){
                if("Subject".equals(word)){
                    flag=true;
                }
                if(flag) {
                    double p = (null == probOfAllWords.get(word) ? 0 : probOfAllWords.get(word));
                    if(p==0){
                        continue;
                    }
                    sumsOfAllProb += (double) (Math.log(1 - p) - Math.log(p));
                }
            }
        }
        probFileIsSpamf=  (1/(1+Math.pow(Math.E,sumsOfAllProb)));
        return probFileIsSpamf;
    }
    public TreeMap<String, Double> gettestingProb(){
        return probOfAllWords;
    }
    public ArrayList<TreeMap<String,Integer>> getWordsInFile(){
        ArrayList<TreeMap<String,Integer>> g =new ArrayList<>();
        g.add(hamOccurrences);
        g.add(spamOccurrences);
        return g;
    }
    public List<TestFile> trainAndTest(File mainDirectory) throws IOException {
//        TODO: main method of loading the directories and files, training and testing the model
        train(mainDirectory);
        ArrayList<TestFile> help= test(mainDirectory);
        return help ;
    }
    public void train(File mainDirectory) throws IOException {
        String pathTrainHam = mainDirectory + "\\train\\ham";
        File[] testHam1 = new File(pathTrainHam).listFiles();

        String pathTrainSpam = mainDirectory+ "\\train\\spam";
        File[] testSpam1 = new File(pathTrainSpam).listFiles();
        numberOfOccurrence(pathTrainHam,testHam1, true);
        numberOfOccurrence(pathTrainSpam,testSpam1, false);
        hamOccurrences.put("hello",1);

            for (String key : hamOccurrences.keySet()) {
                probOfAllWords.put(key, getProbabilityOfWord(key));
            }
        for (String key : spamOccurrences.keySet()) {
                probOfAllWords.put(key, getProbabilityOfWord(key));
            }
    }

    public ArrayList<TestFile> test(File mainDirectory) throws IOException {
        ArrayList<TestFile> testFileWithProb=new ArrayList<>();
        String pathTrainHam = mainDirectory + "\\test\\ham";
        File[] testHam = new File(pathTrainHam).listFiles();

        String pathTrainSpam = mainDirectory + "\\test\\spam";
        File[] testSpam = new File(pathTrainSpam).listFiles();
        assert testHam != null;
        for(File file: testHam){
            TestFile testFile=new TestFile(file.getName(), probFileIsSpam(file),"Ham");
            testFileWithProb.add(testFile);
        };
        assert testSpam != null;
        for(File file: testSpam){
            TestFile testFile=new TestFile(file.getName(),probFileIsSpam(file),"Spam");
            testFileWithProb.add(testFile);
        };
        return testFileWithProb;
    };


}

