package com.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This is a class that test markovchain for text generation I recently felt interested.
 *
 * I mainly use it to get familiar with Java 8's stream feature
 *
 * Please don't use it in your homework!
 *
 * TODO: consider puncturation!
 *
 * @author Zeng Dai
 */

public class MarkovChain {
    static final int ORDER = 2;
    static final String DELIMITER = "+";

    static public void main(String[] args) {
        try (Stream<String> lines = Files.lines(Paths.get("./data/obama08.txt")) ) {
            

            // This collection should not change afterward!
            final List<String> words = 
            lines.flatMap(line -> {
                    if(line.isEmpty()) {
                        return null;
                    }
                    return Arrays.stream(line.split("\\W+"));
                 })
                 .collect(Collectors.toList());

            lines.close();

            // Trained text statistics
            Map<String, List<String>> trained = 
            IntStream.range(0, words.size())
                     .boxed()
                     .collect(Collectors.toMap(
                     i -> {
                         String key =
                         Stream.iterate(i-ORDER, e -> e+1)
                               .map(j -> {
                                   if(j < 0 || j >= words.size()) {
                                       return "";
                                   }
                                   else {
                                       return words.get(j);
                                   }
                               })
                               .limit(ORDER)
                               .collect(Collectors.joining(DELIMITER));
                         //System.out.println(i+ "th key is: " + key);
                         return key;

                     },
                     i -> {
                         //System.out.println(i+ "th value is: " + words.get(i));
                         List<String> alist = new ArrayList<String>();
                         alist.add(words.get(i));
                         return alist;
                     },
                     // merge
                     (a, b) -> {
                         List<String> newlist = new ArrayList<String>(a);
                         newlist.addAll(b);
                         return newlist;
                     }
                     ));

            //System.out.println(trained);

            // I guess stream party is over! we have to access adjacent element and update states
            int yourBoringThreshold = 1000;
            Random rndm = new Random();
            List<String> prev = new LinkedList<String>();
            for(int i = 0; i< ORDER; ++i) {
                prev.add("");
            }
            for(int i = 0;i < yourBoringThreshold; ++i) {
                String key = String.join(DELIMITER, prev);
                List<String> knowledge = trained.get(key);

                String obamaSay = knowledge.get(rndm.nextInt(knowledge.size()));

                System.out.print(obamaSay + " ");

                prev.remove(0);
                prev.add(obamaSay);
            }

        }
        catch(Exception e) {
            System.out.println("Something terrible happened...");
        }
    }
}
