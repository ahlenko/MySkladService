package com.example.myskladservice.processing.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MS_SQLDelete extends Exception{
    public static void DelWorkDaysCO (Connection connection, int id, boolean isCO) throws SQLException {
        String query;
        if (isCO) query = "DELETE FROM MyAppData.WorkDaysCO WHERE Id =  = " + id;
        else      query = "DELETE FROM MyAppData.WorkDaysWO WHERE Id =  = " + id;
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public static void DelWorkTimeCO (Connection connection, int id, boolean isCO) throws SQLException {
        String query;
        if (isCO) query = "DELETE FROM MyAppData.WorkTimeCO WHERE Id =  = " + id;
        else      query = "DELETE FROM MyAppData.WorkTimeWO WHERE Id =  = " + id;
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public static void DelUserByID (Connection connection, int id) throws SQLException {
        String query = "{CALL DeletEmployee(?)}";
        CallableStatement statement = connection.prepareCall(query);
        statement.setInt(1, id); statement.execute();
    }

    public static void DelRegCompany (Connection connection, String email) throws SQLException {
        String query = "DELETE FROM MyAppData.Company WHERE email = '" + email + "'";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public static void DelTask (Connection connection, int id) throws SQLException {
        String query = "DELETE FROM MyAppData.EmployeeTask WHERE id = " + id;
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public static void DelPos (Connection connection, int id) throws SQLException {
        String query = "DELETE FROM MyAppData.Product WHERE id = " + id;
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public static void DelChecking (Connection connection, int id) throws SQLException {
        String query = "DELETE FROM MyAppData.Checking WHERE id = " + id;
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }
}
