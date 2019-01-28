package com.basenko.prefix;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
            Map<String, Integer> words =
                    getWords(line).entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            for (String word : words.keySet()) {
                Integer tempIndex = words.get(word);
                StringBuilder aStringBuilder = new StringBuilder(line);
                aStringBuilder.replace(tempIndex, tempIndex + word.length(), prefix + word);
                line = aStringBuilder.toString();
            }
            fileContent.set(i, line);
        }

        Files.write(p, fileContent, StandardCharsets.UTF_8);
    }

    private static HashMap<String, Integer> getWords(String sentence) {
        HashMap<String, Integer> words = new HashMap<>();
        String wordPattern = "(\\w*__c\\b|\\w*__r\\b)";
        Matcher m = Pattern.compile(wordPattern).matcher(sentence);
        while (m.find()) {
            words.put(m.group(), m.start());
        }
        return words;
    }
}
