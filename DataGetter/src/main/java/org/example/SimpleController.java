package org.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
public class SimpleController {
    private Connection connection = null;
    String dbNameAndPort = System.getenv("SERVICE_PRECONDITION");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");


    @GetMapping("/")
    public String getData() throws ClassNotFoundException, InterruptedException, SQLException {
        String response = "";
        if (connection == null) {
            connection = getConnection();
        }
        Statement s = connection.createStatement();
        ResultSet res = s.executeQuery("SELECT * FROM some_table;");
        for (int i = 0; ; i++) {
            res.next();
            response += i + ":" + res.getString("data1")
                    + " " + res.getString("data2") + "| ";
            if (res.isLast()) break;
        }
        return response;
    }

    @GetMapping("/health")
    @ResponseBody
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("<html><body><h2>Service is alive!</h2><div>Status code: <b>200</b></div>"
                + "<body></html>", HttpStatus.OK);
    }

    @GetMapping("/error")
    @ResponseBody
    public ResponseEntity<String> errorCheck() {
        return new ResponseEntity<>(String.format("<html><body><h2>Custom Error Page</h2><div>Status code: <b>%s</b></div>"
                        + "<div>Custom Error Controller: <b>%s</b></div><body></html>",
                404, "Sorry, we have only / and /health paths"), HttpStatus.NOT_FOUND);
    }

    private Connection getConnection() throws ClassNotFoundException, InterruptedException {
        Class.forName("org.postgresql.Driver");
        Connection conn = null;
        int tries = 0;
        while (tries <= 30) {
            try {
                conn = DriverManager
                        .getConnection("jdbc:postgresql://" + dbNameAndPort + "/postgres", dbUser, dbPassword);
                break;
            } catch (Exception ignore) {
                tries++;
                Thread.sleep(1000);
            }
        }
        return conn;
    }
}
