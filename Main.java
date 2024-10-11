// Projekta autors:
// 231RDB363 Maksims Koļcovs 14. grupa

import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.util.*;
import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;


public class Main {


// Informācija par ceļojumiem tiek glabāta tekstā failā db.csv
// Izstrādājot programmu pieņemt, ka fails db.csv atrodas tekošā mapē

    private static final String filename = "db.csv"; 

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> database = readDatabase(filename);
        String command;
    
        try {
            while (true) {
                System.out.print("Ievadiet komandu (add, print, edit, del, sort, find, avg, exit): ");
                String input = scanner.nextLine().trim();
                String[] words = input.split(" ", 2);
                command = words[0];
    
                switch (command) {
                    case "add":
                        if (words.length > 1) {
                            String tripToAdd = words[1];
                            addTrip(database, tripToAdd);
                        } else {
                            System.out.println("wrong format");
                        }
                        break;
                    case "print":
                        printTrips(database);
                        break;
                    case "edit":
                        if (words.length > 1) {
                            String tripToEdit = words[1];
                            editTrip(database, tripToEdit);
                        } else {
                            System.out.println("wrong format");
                        }
                        break;
                    case "del":
                        if (words.length > 1) {
                            String tripToDelete = words[1];
                            deleteTrip(database, tripToDelete);
                        } else {
                            System.out.println("wrong format");
                        }
                        break;
                    case "sort":
                        sortTripsByDate(database);
                        break;
                    case "find":
                        if (words.length > 1) {
                            String priceToFind = words[1];
                            findPrice(database, priceToFind);
                        } else {
                            System.out.println("wrong format");
                        }
                        break;
                    case "avg":
                        avgPrice(database);
                        break;
                    case "exit":
                        saveDatabase(filename, database); // saglābā izmaiņas pirms aizveras programma
                        System.out.println("Izmaiņas ir saglabātas un programmas darbība pabeigta.");
                        return;
                    default:
                        System.out.println("Nepareiza komanda. Lūdzu, mēģiniet vēlreiz.");
  
                }
            }
        } finally {
            scanner.close();
        }
    }
    
    



            // Šī metode nolasa datubāzes failu un atgriež to kā sarakstu ar rindiņām

    public static ArrayList<String> readDatabase(String filename) {
        ArrayList<String> lines = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileReader(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Nevarēja nolasīt failu " + filename); 
            e.printStackTrace();
        }
        return lines;
    }
        
// metode addTrip

public static void addTrip(ArrayList<String> database, String tripToAdd) {

    String[] fields = tripToAdd.split(";"); // Sadala ievadītas rindas ar semikolu

    if (fields.length != 6) { // ja uzrakstīti vairāk par 6 datiem, tad darbība add neizpildās
        System.out.println("wrong field count");
        return;
    }

    String id = fields[0]; 
    if (id.length() != 3 || !id.matches("\\d+")) { // Pārbauda, vai identifikators sastāv no trim cipariem un vai tas ir skaitlis
        System.out.println("wrong id"); // Ja identifikators nav pareizs, izvada kļūdas ziņojumu un beidz metodes izpildi
        return;
    }

    for (String trip : database) { // Pārbauda, vai datubāzē jau ir ceļojums ar šādu identifikatoru
        if (trip.startsWith(id + ";")) {
            System.out.println("wrong id"); // Ja ir, izvada kļūdas ziņojumu un beidz metodes izpildi
            return;
        }
    }

        // Pārveido pilsētas nosaukumu tā, lai visi burti būtu mazie, bet katra vārda pirmais burts būtu liels

        String city = fields[1].toLowerCase();

        final StringBuilder capitalizedCity = new StringBuilder(city.length());
        String[] cityParts = city.split("\s");
        for (int i=0,l=cityParts.length;i<l;++i) {
            if (i>0) {
                capitalizedCity.append(" ");
            }
            capitalizedCity.append(Character.toUpperCase(cityParts[i].charAt(0))).append(cityParts[i].substring(1));
        }
        city = capitalizedCity.toString();
        
        // https://stackoverflow.com/questions/3904579/how-to-capitalize-the-first-letter-of-a-string-in-java 

        // Pārbauda datumu
        String date = "";
        if (!fields[2].isEmpty()) {
            date = fields[2];
            String[] dateMasivs = date.split("/");
            String month = dateMasivs[1];
            String day = dateMasivs[0];

            try {

                if (Integer.parseInt(day) < 1 || Integer.parseInt(day) > 31 || Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12) {
                System.out.println("wrong date");
                return;
           }

        } catch (NumberFormatException e) {
            System.out.println("wrong date");
            return;
        }

           if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) { 

              System.out.println("wrong date");
              return;
          }
    }

    // https://stackoverflow.com/questions/69806492/regex-d4-d2-d2 

    // Pārbauda dienu skaitu
    String days = fields[3];
    if (!days.matches("\\d+")) { // pārbauda vai tas ir cipars
        System.out.println("wrong day count");
        return;
    }

    // https://stackoverflow.com/questions/13843688/java-regex-finding-digits-in-a-string 


    // Pārbauda cenu
    String price = fields[4];
    if (!price.matches("\\d+(\\.\\d{1,2})?")) { // pārbauda vai ir 1 vai 2 cipari aiz komata
        System.out.println("wrong price");
        return;
    }

    double parsedPrice = Double.parseDouble(price);

    price = String.format("%.2f", parsedPrice);

    // https://stackoverflow.com/questions/1547574/regex-for-prices


// Pārbauda transportlīdzekļa veidu

String vehicle = fields[5].toUpperCase();
if (!vehicle.equals("TRAIN") && !vehicle.equals("PLANE") && !vehicle.equals("BUS") && !vehicle.equals("BOAT")) {
    System.out.println("wrong vehicle");
    return;
}
    tripToAdd = id + ";" + city + ";" + date + ";" + days + ";" + price + ";" + vehicle;
    
    database.add(tripToAdd); // Ja visas pārbaudes ir veiksmīgas, tad pievieno ceļojumu datubāzei
    System.out.println("added");
}


// metode DeleteTrip

public static void deleteTrip(ArrayList<String> database, String tripToDelete) {
    
    int id = 1;
    try {
        id = Integer.parseInt(tripToDelete);
    } catch (NumberFormatException e) {
        System.out.println("wrong id");
    } 

     // Pārbauda, vai identifikators ir pareizs

    if (id < 100 || id > 999) {
        System.out.println("wrong id");
        return;
    }
    // Mēģina izdzēst ceļojumu no datubāzes
    String StringId = String.valueOf(id);

    boolean deleted = database.removeIf(trip -> trip.startsWith(StringId + ";"));

    if (deleted) {
        System.out.println("deleted");
    } else {
        System.out.println("wrong id");
    }
}

 // metode editTrip

 public static void editTrip(ArrayList<String> database, String tripToEdit) {
    String[] fields = tripToEdit.split(";", -1);
    // Pārbauda, vai ir pareizs lauku skaits

    if (fields.length != 6) {
        System.out.println("wrong field count"); // Izvada kļūdas paziņojumu, ja lauku skaits nav pareizs
        return;
    }

    // Pārbauda, vai identifikators ir pareizs (jābūt 3 skaitļiem) un vai tāds eksistē 

    String id = fields[0];
    if (id.length() != 3 || !id.matches("\\d+")) {
        System.out.println("wrong id"); // Izvada kļūdas paziņojumu, ja ID nav pareizs
        return;
    }

     // aizvieto pilsētu ar lielo burtu

    if (!fields[1].isEmpty()) {

        String city = fields[1].toLowerCase();

        final StringBuilder capitalizedCity = new StringBuilder(city.length());
        String[] cityParts = city.split("\s");
        for (int i=0,l=cityParts.length;i<l;++i) {
            if (i>0) {
                capitalizedCity.append(" ");
            }
            capitalizedCity.append(Character.toUpperCase(cityParts[i].charAt(0))).append(cityParts[i].substring(1));
        }
        city = capitalizedCity.toString();
        fields[1] = city;
    } 


    // Pārbauda datumu

    if (!fields[2].isEmpty()) {
         String date = fields[2];
         String[] dateMasivs = date.split("/");
         String month = dateMasivs[1];
         String day = dateMasivs[0];

         try {

            if (Integer.parseInt(day) < 1 || Integer.parseInt(day) > 31 || Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12) {
                System.out.println("wrong date");
                return;
            }

        } catch (NumberFormatException e) {
            System.out.println("wrong date");
            return;
        }

            if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) { 

               System.out.println("wrong date");
               return;
           }
           fields[2] = date;
    }
    // Pārbaudu dienu skaitu

    if (!fields[3].isEmpty()) {
        String days = fields[3];
        if (!days.matches("\\d+")) { // pārbauda vai tas ir cipars
            System.out.println("wrong day count");
            return;
        }
        fields[3] = days;
    }

    // Pārbauda cenu
    if (!fields[4].isEmpty()) {
        String price = fields[4];

        if (!price.matches("\\d+(\\.\\d{1,2})?")) { // pārbauda vai ir 1 vai 2 cipari aiz komata
            System.out.println("wrong price");
            return;
        }
        double parsedPrice = Double.parseDouble(price);

        price = String.format("%.2f", parsedPrice);
        fields[4] = price;
    }


    // Pārbauda vehicle veidu

    if (!fields[5].isEmpty()) {
        String vehicle = fields[5].toUpperCase();
        if (!vehicle.equals("TRAIN") && !vehicle.equals("PLANE") && !vehicle.equals("BUS") && !vehicle.equals("BOAT")) {
        System.out.println("wrong vehicle");
        return;
    }
    fields[5] = vehicle;
    }
    
    // Atrod ceļojumu datubāzē ar norādīto ID

    for (int index = 0; index < database.size(); index++) {
        String tripData = database.get(index);
        String[] tripFields = tripData.split(";");
        if (tripFields[0].equals(id)) {
            // Ja ceļojums ar norādīto ID tika atrasts, tad atjaunina ceļojuma informāciju
            for (int i = 1; i < fields.length; i++) {
                if (!fields[i].isEmpty()) {
                    tripFields[i] = fields[i]; // Atjaunina lauku, ja tā vērtība nav tukša
                }
            }
            database.set(index, String.join(";", tripFields)); // Atjaunina ceļojumu datubāzē
            System.out.println("changed"); // Izvada paziņojumu, ka ceļojums ir mainīts
            return;
        }
    }

    // Ja ceļojums ar norādīto ID netika atrasts, tad izvada kļūdas paziņojumu
    System.out.println("wrong id");
}

// printTrips metode

private static final String format = "\n%-4s%-21s%-11s%6s%10s %-8s";
// "\n%-4s%-21s%-11s%6s%10s %-8s", "ID", "City", "Date", "Days", "Price", "Vehicle"
public static void printTrips(ArrayList<String> database) {
    System.out.print("\n------------------------------------------------------------");
    System.out.printf(format, "ID", "City","Date", "Days", "Price", "Vehicle");
    System.out.print("\n------------------------------------------------------------");

    for (String tripData : database) {
        String[] tripFields = tripData.split(";");
        String id = tripFields[0];
        String city = tripFields[1];
        String date = tripFields[2];
        String days = tripFields[3];
        String price = tripFields[4];
        String vehicle = tripFields[5];
        System.out.printf(format, id, city, date,  days, price, vehicle, "ID", "City", "Date", "Days", "Price", "Vehicle");
    }

    System.out.println("\n------------------------------------------------------------");

}

// sortTripsByDate metode

    public static void sortTripsByDate(ArrayList<String> database) {
        int listSize = database.size();

        for (int i = 0; i < listSize - 1; i++) {
            for (int j = 0; j < listSize - i - 1; j++) {
                String list1 = database.get(j);
                String list2 = database.get(j + 1);

                Date dateOfList1 = extractDateFunction(list1);
                Date dateOfList2 = extractDateFunction(list2);

                if (dateOfList1 != null && dateOfList2 != null && dateOfList1.compareTo(dateOfList2) > 0) {
                   database.set(j, list2);
                   database.set(j + 1, list1);
                }
            }
        }
        System.out.println("sorted");
    }

    public static Date extractDateFunction(String list) {
        
        String[] parts = list.split(";");
        String dateString = parts[2];
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
        } catch (ParseException e) {
            System.out.println("wrong date");
        }
        return null;
    }

// metode SaveDatabase


    public static void saveDatabase(String filename, ArrayList<String> database) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (String tripData : database) {
                writer.write(tripData + "\n");
            }
            System.out.println("Datubāze ir saglabāta failā: " + filename);
        } catch (IOException e) {
            System.err.println("Kļūda, nevarēja saglabāt datubāzi failā " + filename);
            e.printStackTrace();
        }
    }

// metode avgPrice


    public static void avgPrice (List<String> tripsDatabase) {
        double totalCost = 0; // Kopējā ceļojumu cena
        int tripsCount = tripsDatabase.size();   // Ceļojumu skaits

        for (String tripData : tripsDatabase) {
            String[] tripFields = tripData.split(";");

            // Izgūst ceļojuma cenu no 5 lauka 
            double tripPrice = Double.parseDouble(tripFields[4]);

            totalCost += tripPrice; // Pievieno ceļojuma cenu kopējai summai
        }

        // Aprēķina vidējo cenu, ja ir vismaz viens ceļojums

        if (tripsCount > 0) {
            double averagePrice = totalCost / tripsCount;
            System.out.printf("average=%.2f%n", averagePrice);
        } else {
            System.out.println("Nav datu, lai aprēķinātu vidējo ceļojumu cenu.");
        }
    }

// metode findPrice


public static void findPrice(ArrayList<String> database, String priceInput) {

    double priceToFind;

    try {
        priceToFind = Double.parseDouble(priceInput);
    } catch (NumberFormatException e) {
        System.out.println("wrong price");
        return;
    }

    System.out.print("\n------------------------------------------------------------");

    System.out.printf(format, "ID", "City","Date", "Days", "Price", "Vehicle");

    System.out.print("\n------------------------------------------------------------");

     // Meklē un izvada ceļojumus ar cenu, kas nepārsniedz norādīto cenu
     for (String tripData : database) {
        String[] tripFields = tripData.split(";");
        double tripPrice = Double.parseDouble(tripFields[4]);
        if (tripPrice <= priceToFind) {
            System.out.printf(format, tripFields[0], tripFields[1], tripFields[2], tripFields[3], tripFields[4], tripFields[5]);
        }
    }

    System.out.println("\n------------------------------------------------------------"); }

 }