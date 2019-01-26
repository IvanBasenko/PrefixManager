package com.basenko.prefix;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PrefixManager {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Main Directory: ");
        String mainPath = sc.nextLine();
        System.out.print("Prefix with 2 underscores: ");
        String prefix = sc.nextLine();
        try (Stream<Path> paths = Files.walk(Paths.get(mainPath))) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            addPrefix(path, prefix);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    private static void addPrefix(Path p, String prefix) throws IOException {
        ArrayList<String> fileContent = new ArrayList<>(Files.readAllLines(p, StandardCharsets.UTF_8));

        for (int i = 0; i < fileContent.size(); i++) {
            String line = fileContent.get(i);
            ArrayList<String> words = getWords(line);
            for (String word : words) {
                if (line.contains(word)) {
                    line = line.replace(word, prefix + word);
                }
            }
            fileContent.set(i, line);
        }

        Files.write(p, fileContent, StandardCharsets.UTF_8);
    }

    private static ArrayList<String> getWords(String sentence) {
        HashSet<String> words = new HashSet<>();
        String wordPattern = "(\\w*__c\\b|\\w*__r\\b)";
        Matcher m = Pattern.compile(wordPattern).matcher(sentence);
        while (m.find()) {
            words.add(m.group());
        }
        return new ArrayList<>(words);
    }
}
