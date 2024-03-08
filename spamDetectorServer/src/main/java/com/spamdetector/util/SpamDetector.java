package com.spamdetector.util;

import com.spamdetector.domain.TestFile;

import java.io.*;
import java.util.*;


/**
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

   //Array of comonWords which don't provide any meaning in terms of spam prob.
   String[] commonWordsArray={"I", "you", "he", "she", "it", "we", "they", "me", "him", "her", "us", "them","my","your",
           "his", "its", "our", "their", "a", "an", "the", "and", "in", "of", "on", "with", "to", "for", "from", "at","  "," ",""};

   //So we are being lazy and instead of sorting the common words, we're letting java do that for us
   public SpamDetector(){
       //sorting the common words so we can find them easily
       commonWords.addAll(Arrays.asList(commonWordsArray));
       commonWords.sort(Comparator.naturalOrder());

   };


    /*This function take subdirectory train/spam or train/ham and a flag (to check if it's training ham or spam data) to train the model
        and save results in our attributes containing words and the number of files they are in
     */
    public void numberOfOccurrence(String directory, File[] fileArrays, boolean isHam) throws IOException {
            //going to go through each file in subdirectory and check for numbers
        for(File file: fileArrays){
            ArrayList<String> lineWords = getStrings(directory,file);
            for(String word:lineWords){
                if(isHam){
                    //check if we already have those words im map, if we do it increase the count, otherwise it add that word with count 1
                        hamOccurrences.put(word, null == hamOccurrences.get(word) ? 1 : hamOccurrences.get(word) + 1);
                }
                else{
                    //check if we already have those words im map, if we do it increase the count, otherwise it add that word with count 1
                        spamOccurrences.put(word, null == spamOccurrences.get(word) ? 1 : spamOccurrences.get(word) + 1);
                }
            }
        }

    }


    // This function take directory and file name, the locate the files, read it, split it using regex and return all the words found in file
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
                //splitting words where on spaces, number or anything other than words
                lineWords=line.split("[^a-zA-z]+");
                for(String word: lineWords){
                    //start reading from Subject keyword to avoid reading extra
                    if("Subject".equals(word)){
                        flag=true;
                    }
                    if(flag) {
                        //going to put the words in our map, after we passed Subject. It also checks if our word is one of common words
                        if(Collections.binarySearch(commonWords,word)<0) {
                            boolean add = wordsInFile.add(word);
                        }
                    }
                };
        ;}
        return  wordsInFile;
    }


    //give prob of word being spam, word, total count of words in spamOccurances and hamOccurances
    public double getProbabilityOfWord(String word,double totalSpam, double totalHam){
        double probFileIsSpamHaveThisWord=0.0;
        double probOfSpam=(null==spamOccurrences.get(word))?0:spamOccurrences.get(word);
        double probOfHam=(null==hamOccurrences.get(word))?0:hamOccurrences.get(word);
        if(probOfHam==0 || probOfSpam==0){
            return 0.0;
        }
        /* calculate the prob. of word using bayes' theorem if the word exist in our training data */
        probFileIsSpamHaveThisWord=(probOfSpam/totalSpam)/((probOfHam/totalHam)+(probOfSpam/totalSpam));
        return probFileIsSpamHaveThisWord;
    }


    //this function takes a file and return the prob. of it being spam
    public double probFileIsSpam(String directory,File file) throws IOException {
        double probFileIsSpamf=0.0;
        double sumsOfAllProb=0.0;
        FileReader fileReader = null;
        String path=directory+"\\"+file.getName();
        try {
            fileReader = new FileReader(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        BufferedReader input = new BufferedReader(fileReader);
        String line=null;
        boolean flag= false;

        //same logic we're going to start reading from subject and get prob of files being spam for each word, the add those prob. using our expression
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
                    sumsOfAllProb += (Math.log(1 - p) - Math.log(p));
                }
            }
        }
        probFileIsSpamf=  (1/(1+Math.pow(Math.E,sumsOfAllProb)));
        return probFileIsSpamf;
    }


    //This trainAndTest is where the whole process starts, This function takes a directory and run training function and then test on our test Files
    public List<TestFile> trainAndTest(File mainDirectory) throws IOException {
    //TODO: main method of loading the directories and files, training and testing the model
        train(mainDirectory);
        return test(mainDirectory);
    }

    //This function takes a directory, get list of all the files from that directory and run training logic
    public void train(File mainDirectory) throws IOException {
        String pathTrainHam = mainDirectory + "\\train\\ham";
        File[] testHam1 = new File(pathTrainHam).listFiles();

        String pathTrainSpam = mainDirectory+ "\\train\\spam";
        File[] testSpam1 = new File(pathTrainSpam).listFiles();

        assert testHam1 != null;
        //we run the helper function which takes all the ham files and calculate number of occurrences to store in hamOccurances
        numberOfOccurrence(pathTrainHam,testHam1, true);

        assert testSpam1 != null;
        //we run the helper function which takes all the spam files and calculate number of occurrences to store in spamOccurances
        numberOfOccurrence(pathTrainSpam,testSpam1, false);

            //now we use the ham and spam occurrences maps to calculate the prob of file being spam given a word is in it for each words in spam and ham
            for (String key : hamOccurrences.keySet()) {
                if(0!=getProbabilityOfWord(key, testSpam1.length,testHam1.length)) {
                    probOfAllWords.put(key, getProbabilityOfWord(key, testSpam1.length, testHam1.length));
                };
            }
            for (String key : spamOccurrences.keySet()) {
                if(0!=getProbabilityOfWord(key, testSpam1.length,testHam1.length)) {
                    probOfAllWords.put(key, getProbabilityOfWord(key, testSpam1.length, testHam1.length));
                }
            }
    }

    public ArrayList<TestFile> test(File mainDirectory) throws IOException {
        //swe read the directory for files
        ArrayList<TestFile> testFileWithProb=new ArrayList<>();
        String pathTrainHam = mainDirectory + "\\test\\ham";
        File[] testHam = new File(pathTrainHam).listFiles();

        String pathTrainSpam = mainDirectory + "\\test\\spam";
        File[] testSpam = new File(pathTrainSpam).listFiles();
        assert testHam != null;

        /* for each file, we get the prob. of it being spam, then make a new object of testFile with that data, which would be added to the ArrayList
            which will be returned in the end */
        for(File file: testHam){
            TestFile testFile=new TestFile(file.getName(), probFileIsSpam(pathTrainHam,file),"Ham");
            testFileWithProb.add(testFile);
        };
        assert testSpam != null;
        /* We do same for the spam files*/
        for(File file: testSpam){
            TestFile testFile=new TestFile(file.getName(),probFileIsSpam(pathTrainSpam,file),"Spam");
            testFileWithProb.add(testFile);
        };
        return testFileWithProb;
    };


}

