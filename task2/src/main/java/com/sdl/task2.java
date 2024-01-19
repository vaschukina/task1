package com.sdl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class task2 {

    public static void showVersion() {

        String log_path = System.getenv("LOG_PATH") == null ? "log.txt" : System.getenv("LOG_PATH");
        try {
            System.setErr(new PrintStream(new File(log_path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String DB_URL = System.getenv("DB_URL");
        String USERNAME = System.getenv("USERNAME");
        String PASSWORD = System.getenv("PASSWORD");

        Connection connection = null;
        try {
            connection = DriverManager
                    .getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("Successful connection");

        } catch (SQLException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
            return;
        }

        try {
            PreparedStatement st = connection.prepareStatement("SELECT VERSION();");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                if (!rs.getString(1).contains("PostgreSQL 16")) {
                    System.out.println("Not typical answer:");
                }
                System.out.println(rs.getString(1));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public static void main(String[] argv)  {
        Timer timer = new Timer();
        Long period = System.getenv("PERIOD") == null ? 300000 : Long.parseLong(System.getenv("PERIOD"));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                showVersion();
            }
        }, 5000, period);
    }
}