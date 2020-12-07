import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.eclipsesource.json.JsonObject;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    private static String devToken = "f3x0jTRfsChuD1KuAbPhKTEuMhpyEvbS";
    private static BoxAPIConnection api;
    private static File zoomFolder;
    private static String name;
    private static String job;
    private static String lastUsed;
    private static ArrayList<File> videoFiles;
    private static Scanner scan;

    public static void main(String[] args) {
        /*
        Program Logic:

        Check if user has a user.json file
        If they don't
            Ask for name
            Ask for job
            Ask for filepath to zoomFolder
            Create JSON obj
            Add info to JSON obj
            Write JSON obj to user.json
        Check if user.json has a lastUsed variable
        If it doesn't
            Ask for a starting date
            Set lastUsed to this date
        For all zoom files since lastUsed (that are CAS Tutoring files, and are bigger than 50MB)
            Rename with proper format
            Add to videoFiles
        Print 'type 'u' to upload, type 'q' to quit
        If user types 'u'
            Upload all files in videoFiles
            Print 'done' when finished or 'error' if interrupted
        If user types 'q'
            Quit
        Else
            Keep asking user for valid input

         */
        scan = new Scanner(System.in);
        if (!new File("user.json").exists()) {
            boolean correctInfo = false;
            while (!correctInfo) {
                name = askForLetters("Please enter your first name and last initial (ex: Ian C)");
                job = askForLetters("Please enter your job (ex: STC Tutoring)");
                lastUsed = askForDate("Please enter the date of the first file you want to upload (ex: 11/16/20)");
                zoomFolder = new File(askForDirectory("Please enter the file directory for your zoom folder: \n(ex: C:\\Users\\ichen\\Documents\\Zoom)"));
                System.out.printf("So, your name is %s, your job is %s, you want to upload \nfiles starting at %s and your zoom directory is %s\n", name, job, lastUsed, zoomFolder.toString());
                System.out.println("Is that correct?");
                String input = scan.next();
                if (input.equals("yes") || input.equals("Yes") || input.equals("YES")) {
                    correctInfo = true;
                }
                scan.nextLine();
            }
            initializeJson();
        } else {
            try {
                Scanner readJson = new Scanner(new File("user.json"));
                StringBuilder stringJson = new StringBuilder();
                while (readJson.hasNextLine()) {
                    stringJson.append(readJson.nextLine());
                }
                JsonObject json = JsonObject.readFrom(stringJson.toString());
                // #TODO figure out why json strings have quotes, and find a better way of replacing quotes
                name = json.get("name").toString().replace("\"", "");
                job = json.get("job").toString().replace("\"", "");
                lastUsed = json.get("lastUsed").toString().replace("\"", "");
                zoomFolder = new File(json.get("directory").toString().replace("\"", ""));
                System.out.printf("Name: %s Job: %s Last Used: %s Folder: %s", name, job, lastUsed, zoomFolder.toString());

            } catch (FileNotFoundException e) {
                System.out.println("Could not find user.json");
            }
        }
        api = new BoxAPIConnection(devToken);
        videoFiles = new ArrayList<File>();
        Date d = new Date(lastUsed);
        /*
        gatherFiles();
        for(File f : videoFiles) {
            uploadFile(f);
        }
        */
    }

    public static String askForLetters(String prompt) {
        String str;
        do {
            System.out.println(prompt);
            str = scan.nextLine();
        } while ((str.equals(""))
                || (str == null)
                || (!str.matches("^[a-zA-Z ]*$")));
        return str;
    }

    public static String askForDirectory(String prompt) {
        String str;
        Boolean isNotValid = false;
        do {
            System.out.println(prompt);
            str = scan.nextLine();
            try {
                Paths.get(str);
            } catch (InvalidPathException | NullPointerException ex) {
                isNotValid = true;
            }
        } while (isNotValid);
        return str;
    }

    public static String askForDate(String prompt) {
        String str;
        do {
            System.out.println(prompt);
            str = scan.nextLine();
        } while ((str.equals(""))
                || (str == null)
                || (!str.matches("^(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{2}$")));
        return str;
    }

    public static void initializeJson() {
        JsonObject json = new JsonObject();
        json.add("name", name);
        json.add("job", job);
        json.add("lastUsed", lastUsed);
        json.add("directory", zoomFolder.toString());
        try {
            FileWriter writer = new FileWriter("user.json");
            json.writeTo(writer);
            writer.close();
            System.out.println("Your info has been stored.");
        } catch (IOException e) {
            System.out.println("Error writing to file");
        }
    }

    public static void gatherFiles() {
        for (File parent : zoomFolder.listFiles()) { // for each folder in directory
            if (parent.getName().contains("CAS Tutoring")) { // if folder is for tutoring
                String date = parent.getName().substring(0, 10); // get the date of the session
                String year = date.substring(2, 4);
                String month = date.substring(5, 7);
                String day = date.substring(8, 10);
                Date d = new Date(String.format("%s/%s/%s", month, day, year));

                date = d.getDotFormat(); // format the date
                for (File child : parent.listFiles()) { // for each file in folder
                    // if file is .mp4 and greater than 50MB
                    if (child.getName().substring(child.getName().length() - 4).equals(".mp4") && child.length() / 1000000 > 50) {
                        // rename the child to Name - Date - Job and add it to the list
                        child.renameTo(new File(child.getParent() + "\\" + name + " - " + date + " - " + job + ".mp4"));
                        videoFiles.add(child);
                    }
                }
            }
        }
    }

    public static boolean uploadFile(File upload) {
        try {
            FileInputStream stream = new FileInputStream(upload);

            BoxFolder rootFolder = new BoxFolder(api, "126166257005");
            BoxFile.Info fileInfo = rootFolder.uploadLargeFile(stream, upload.getName(), upload.length());
            return true;
        } catch (IOException e) {
            System.out.println("Bad file name");
            return false;
        } catch (InterruptedException e) {
            System.out.println("Upload interrupted");
            return false;
        }
    }

    public static void resetFileNames(String type, String newName) { // utility function used to reset names of all files in zoomFolder
        File zoomFolder = new File("C:\\Users\\ichen\\Documents\\Zoom");
        for (File parent : zoomFolder.listFiles()) {
            for (File child : parent.listFiles()) {
                if (child.getName().substring(child.getName().length() - type.length()).equals(type)) {
                    child.renameTo(new File(child.getParent() + "\\" + newName));
                }
            }
        }
    }
}


/*

Documentation:

BoxAPIConnection api = new BoxAPIConnection(devToken);
    connect to API using developer token

BoxFolder rootFolder = BoxFolder.getRootFolder(api);
    get root folder

BoxFile file = new BoxFile(api,id);
    get a file
    id must be a string of numbers

File file = new File(path);
    create a local file

FileInputStream stream = new FileInputStream(file);
    create an inputstream

file.uploadNewVersion(stream);
    upload a new version of a file, given an inputstream
    
 */