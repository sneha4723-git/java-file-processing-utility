package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileProcessor {

    private static final Logger logger =
            LogManager.getLogger(
                    FileProcessor.class);

    public static void main(
            String[] args) {

        String inputFolder =
                ConfigReader.getProperty("Input");

        String outputFolder =
                ConfigReader.getProperty("Output");

        String rejectFolder =
                ConfigReader.getProperty("Reject");

        String backupFolder =
                ConfigReader.getProperty("Backup");

        File inputDir = new File(inputFolder);

        File[] files = inputDir.listFiles(
                (dir, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {

            logger.info("No files found in Input Folder");
            return;
        }

        for (File file : files) {

            processFile(
                    file,
                    outputFolder,
                    rejectFolder,
                    backupFolder);
        }

    }
    private static void processFile(
            File inputFile,
            String outputFolder,
            String rejectFolder,
            String backupFolder) {

        int totalRecords = 0;
        int validRecords = 0;
        int invalidRecords = 0;

        String fileName = inputFile.getName();

        String outputFile =
                outputFolder
                        + File.separator
                        + "output_" + fileName;

        String rejectFile =
                rejectFolder
                        + File.separator
                        + "reject_" + fileName;

        try (

                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(inputFile));

                BufferedWriter outputWriter =
                        new BufferedWriter(
                                new FileWriter(outputFile));

                BufferedWriter rejectWriter =
                        new BufferedWriter(
                                new FileWriter(rejectFile))

        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                totalRecords++;

                line = line.trim();

                if (Validator.isValid(line)) {

                    outputWriter.write(line);
                    outputWriter.newLine();

                    validRecords++;

                } else {

                    rejectWriter.write(line);
                    rejectWriter.newLine();

                    invalidRecords++;
                }
            }

            logger.info(
                    "File={} Total={} Valid={} Invalid={}",
                    fileName,
                    totalRecords,
                    validRecords,
                    invalidRecords);

        } catch (Exception e) {

            logger.error(
                    "Error while processing file : {}",
                    fileName,
                    e);

            return;
        }

// TRY BLOCK KHATAM HONE KE BAAD
        try {

            backupFile(
                    inputFile,
                    backupFolder);

        } catch (Exception e) {

            logger.error(
                    "Backup failed for file : {}",
                    fileName,
                    e);
        }
    }
    private static void backupFile(
            File inputFile,
            String backupFolder)
            throws IOException {

        String timestamp =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyyMMdd_HHmmss"));

        String backupFileName =
                inputFile.getName()
                        .replace(
                                ".txt",
                                "_" + timestamp + ".txt");

        Path backupPath =
                Paths.get(
                        backupFolder,
                        backupFileName);

        Files.move(
                inputFile.toPath(),
                backupPath,
                StandardCopyOption.REPLACE_EXISTING);

        logger.info(
                "File moved to backup : {}",
                backupFileName);

    }
}
