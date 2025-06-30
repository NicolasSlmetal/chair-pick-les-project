package com.chairpick.ecommerce.e2e.utils;

import java.io.*;

public class ContainerInitializer {
    private static final String DIRNAME = System.getProperty("user.dir");

    public static void up() throws IOException, InterruptedException {
        Process process = new ProcessBuilder("docker", "compose", "-f", "docker-compose-test.yaml", "up", "-d", "--build")
                .directory(new File(DIRNAME))
                .start();


        process.waitFor();

        if (process.exitValue() != 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            throw new IllegalStateException("Could not start the application");
        }
    }

    public static void down() throws InterruptedException, IOException {
        Process process = new ProcessBuilder("docker", "compose", "-f", "docker-compose-test.yaml", "down", "-v")
                .directory(new File(DIRNAME))
                .start();
        process.waitFor();

        if (process.exitValue() != 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            throw new IllegalStateException("Could not stop the application");
        }
    }
}
