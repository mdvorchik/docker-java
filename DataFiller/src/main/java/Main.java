import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, CsvException, InterruptedException {
        //init environmental variables
        String dbNameAndPort = System.getenv("SERVICE_PRECONDITION");
        String fileWithDataName = System.getenv("CSV_FILE_NAME");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        //connect to db
        Class.forName("org.postgresql.Driver");
        Connection conn = null;
        int tries = 0;
        while (tries <= 30) {
            try {
                conn = DriverManager
                        .getConnection("jdbc:postgresql://" + dbNameAndPort + "/postgres", dbUser, dbPassword);
                break;
            } catch (Exception ignored) {
                tries++;
                Thread.sleep(1000);
            }
        }
        Statement s = conn.createStatement();
        s.executeUpdate("CREATE TABLE IF NOT EXISTS some_table\n" +
                "(\n" +
                "\tdata1 varchar,\n" +
                "\tdata2 varchar\n" +
                ");");

        //read from csv
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader("data/" + fileWithDataName));
        } catch (Exception ignore) {
            System.out.println("Can't find file:" + fileWithDataName);
            System.out.println("Start search any other file");
            File directory = new File("data");
            File[] files = directory.listFiles();
            System.out.println("Find file:" + files[0].getName());
            reader = new CSVReader(new FileReader("data/" + files[0].getName()));
        }
        List<String[]> records = reader.readAll();

        //insert data to db from csv
        for (String[] record : records) {
            String str = record[0];
            String str2 = record[1];
            s.executeUpdate("INSERT INTO some_table \n" +
                    "VALUES ('" + str + "', '" + str2 + "');");
        }

        //check records
        System.out.println("Filled data:");
        ResultSet res = s.executeQuery("SELECT * FROM some_table;");
        for (int i = 0; ; i++) {
            res.next();
            System.out.println(i + ":" + res.getString("data1")
                    + " " + res.getString("data2"));
            if (res.isLast()) break;
        }
        s.close();
        conn.close();
    }
}
