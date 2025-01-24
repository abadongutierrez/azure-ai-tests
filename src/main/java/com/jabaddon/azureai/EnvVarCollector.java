package com.jabaddon.azureai;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class EnvVarCollector {
    public static List<String> collectEnv(String... args) {
        Stream<String> stringStream = Stream.of(args).map(System::getenv);

        if (stringStream.anyMatch(Objects::isNull)) {
            System.out.println("Missing some environment variables: " + Stream.of(args).toList());
            System.out.println("Set them before running this sample.");
            System.exit(1);
        }

        return Stream.of(args).map(System::getenv).toList();
    }
}
