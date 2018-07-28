package com.greguniverse.dynamicsapi;

public class Main {
    public static void  main(String args[]) {
        System.out.println("Hello, World");
        MicrosoftAuth microsoftAuth = new MicrosoftAuth("", "");
        microsoftAuth.auth();
    }
}
