package com.example.myskladservice.processing.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

public class MS_SQLUpdate {
    public static void UPDUserATWork (Connection connection, boolean onwork, String email, String login) throws SQLException {
        String query = "UPDATE MYAppData.Employee SET onwork = ?" +
                " WHERE login = ? AND company_id = (SELECT" +
                " id FROM MYAppData.Company WHERE email = ?)";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setBoolean(1, onwork);
        statement.setString(2, login);
        statement.setString(3, email);

        statement.executeUpdate();
    }

    public static void UpdManagerID(Connection connection, int manager_id, int company_id) throws SQLException {
        String query = "UPDATE MYAppData.[Company] SET [manager_id] = " + manager_id + " WHERE [ID] = " + company_id;
        PreparedStatement statement = connection.prepareStatement(query);
        statement.executeUpdate();
    }

    public static void UPDWorkTime (Connection connection, String SW, String EW, String SH, String EH, boolean isCo, int id) throws SQLException {
        String query;
        if (isCo) query = "UPDATE MYAppData.[WorkTimeCO] SET [startw] = ?, [endw] = ?, [starth] = ?, [endh] = ? WHERE [ID] = " + id;
        else      query = "UPDATE MYAppData.[WorkTimeWO] SET [startw] = ?, [endw] = ?, [starth] = ?, [endh] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, SW);  stmt.setString(2, EW);
        stmt.setString(3, SH);  stmt.setString(4, EH);
        stmt.executeUpdate();
    }

    public static void UPDWorkDays (Connection connection, boolean d1, boolean d2, boolean d3, boolean d4, boolean d5, boolean d6, boolean d7, boolean isCo, int id) throws SQLException {
        String query;
        if (isCo) query = "UPDATE MYAppData.[WorkDaysCO] SET [mon] = ?, [tue] = ?, [wed] = ?, [thu] = ?, [fri] = ?, [sat] = ?, [san] = ? WHERE [ID] = " + id;
        else      query = "UPDATE MYAppData.[WorkDaysWO] SET [mon] = ?, [tue] = ?, [wed] = ?, [thu] = ?, [fri] = ?, [sat] = ?, [san] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setBoolean(1, d1); stmt.setBoolean(2, d2); stmt.setBoolean(3, d3);
        stmt.setBoolean(4, d4); stmt.setBoolean(5, d5); stmt.setBoolean(6, d6);
        stmt.setBoolean(7, d7);
        stmt.executeUpdate();

    }

    public static void UPDUser (Connection connection, String surname, String name, String lastname, String workpost, String workplace, boolean fullacess, int id) throws SQLException {
        String query = "UPDATE MYAppData.[Employee] SET [surname] = ?, [name] = ?, [lastname] = ?, [workpost] = ?, [workplace] = ?, [fullacess] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, surname);    stmt.setString(2, name);         stmt.setString(3, lastname);
        stmt.setString(4, workpost);   stmt.setString(5, workplace);    stmt.setBoolean(6, fullacess);
        stmt.executeUpdate();
    }

    public static void UPDUserPhone (Connection connection, String phone, int id) throws SQLException {
        String query = "UPDATE MYAppData.[Employee] SET [phnumber] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, phone);
        stmt.executeUpdate();
    }

    public static void UPDUserPassword (Connection connection, String password, int id) throws SQLException {
        String query = "UPDATE MYAppData.[Employee] SET [password] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, password);
        stmt.executeUpdate();
    }

    public static void UPDUserLogin (Connection connection, String login, int id) throws SQLException {
        String query = "UPDATE MYAppData.[Employee] SET [login] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, login);
        stmt.executeUpdate();
    }



    public static void UPDPosition(Connection connection, int company, String s_name, String s_group, String s_code, float s_kg, int s_sm1, int s_sm2, int s_sm3, int s_count, String s_provider, String s_comment, int id) throws SQLException {
        String query = "UPDATE MYAppData.[Product] SET [company_id] = ?, [name] = ?, [group] = ?, [code] = ?, [weignt] = ?, [sizeh] = ?, [sizew] = ?, [sized] = ?, [count] = ?, [provider] = ?, [comment] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        stmt.setInt(1, company);     stmt.setString(2, s_name);
        stmt.setString(3, s_group);  stmt.setString(4, s_code);         stmt.setFloat(5, s_kg);
        stmt.setInt(6, s_sm1);       stmt.setInt(7, s_sm2);             stmt.setInt(8, s_sm3);
        stmt.setInt(9, s_count);    stmt.setString(10, s_provider);    stmt.setString(11, s_comment);
        stmt.executeUpdate();
    }

    public static void UPDPosition(Connection connection, int company, byte[] image, String s_name, String s_group, String s_code, float s_kg, int s_sm1, int s_sm2, int s_sm3, int s_count, String s_provider, String s_comment, int id) throws SQLException {
        String query = "UPDATE MYAppData.[Product] SET [company_id] = ?, [image] = ?, [name] = ?, [group] = ?, [code] = ?, [weignt] = ?, [sizeh] = ?, [sizew] = ?, [sized] = ?, [count] = ?, [provider] = ?, [comment] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, company);     stmt.setBytes(2, image);           stmt.setString(3, s_name);
        stmt.setString(4, s_group);  stmt.setString(5, s_code);         stmt.setFloat(6, s_kg);
        stmt.setInt(7, s_sm1);       stmt.setInt(8, s_sm2);             stmt.setInt(9, s_sm3);
        stmt.setInt(10, s_count);    stmt.setString(11, s_provider);    stmt.setString(12, s_comment);
        stmt.executeUpdate();
    }

    public static void UPDPosition(Connection connection, int s_count, int id) throws SQLException {
        String query = "UPDATE MYAppData.[Product] SET [count] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, s_count);
        stmt.executeUpdate();
    }

    public static void AddArriver(Connection mssqlConnection, int arrive_id, int sum_count) throws SQLException {
        String query = "UPDATE MYAppData.[OrdersArrive] SET [sum_count] = ? WHERE [ID] = " + arrive_id;
        PreparedStatement stmt = mssqlConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, sum_count);
        stmt.executeUpdate();
    }

    public static void UPDPackingInfo(Connection mssqlConnection, int getChecker, int user_id, String s, int i, AtomicInteger allItemCounter) throws SQLException {
        String query = "UPDATE MYAppData.[Orders] SET [performer_id] = ?, [ttn_code] = ?, [state] = ?, [sum_count] = ? WHERE [ID] = " + getChecker;
        PreparedStatement stmt = mssqlConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, user_id); stmt.setString(2, s);
        stmt.setInt(3, i); stmt.setInt(4, allItemCounter.get());
        stmt.executeUpdate();
    }

    public static void UPDAdditionInfo(Connection mssqlConnection, int performer, int state, int getChecker) throws SQLException {
        String query = "UPDATE MYAppData.[Addition] SET [performer_id] = ?, [state] = ?  WHERE [ID] = " + getChecker;
        PreparedStatement stmt = mssqlConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, performer); stmt.setInt(2, state);
        stmt.executeUpdate();
    }

    public static void UPDProdCountById(Connection mssqlConnection, Integer integer, int i) throws SQLException {
        String query = "UPDATE MYAppData.[Product] SET [count] = ? WHERE [ID] = " + integer;
        PreparedStatement stmt = mssqlConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, i);
        stmt.executeUpdate();
    }

    public static void UPDOrdersArrive(Connection mssqlConnection, int id, int state, int performerId) throws SQLException {
        String query = "UPDATE MYAppData.[OrdersArrive] SET [performer_id] = ?, [state] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = mssqlConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, performerId); stmt.setInt(2, state);
        stmt.executeUpdate();
    }
}
