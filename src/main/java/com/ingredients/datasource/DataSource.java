package com.ingredients.datasource;


import java.sql.Connection;
import java.sql.DriverManager;

public class DataSource {

        private static final String URL = "jdbc:postgresql://localhost:5432/mini_dish_db";
        private static final String USER = "mini_dish_db_manager";
        private static final String PASSWORD = "123456";

        public Connection getConnection() throws Exception {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }
