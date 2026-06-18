package org.example;

public class Validator {

    private Validator() {
    }

    public static boolean isValid(
            String record) {

        return record.matches(
                "^[a-zA-Z0-9]+$");
    }
}