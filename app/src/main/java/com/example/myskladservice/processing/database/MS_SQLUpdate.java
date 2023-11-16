package com.example.myskladservice.processing.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MS_SQLUpdate {
    public static void UPDUserATWork (Connection connection, boolean onwork, String email,
                                      String login) throws SQLException {
        String query = "UPDATE MYAppData.Employee SET onwork = ?" +
                " WHERE login = ? AND company_id = (SELECT" +
                " id FROM MYAppData.Company WHERE email = ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setBoolean(1, onwork); statement.setString(2, login);
        statement.setString(3, email);   statement.executeUpdate();
    }

    public static void UpdManagerID(Connection connection, int manager_id, int company_id) throws SQLException {
        String query = "UPDATE MYAppData.[Company] SET [manager_id] = " + manager_id + " WHERE [ID] = " + company_id;
        PreparedStatement statement = connection.prepareStatement(query); statement.executeUpdate();
    }

    public static void UPDTimeAndDays(Connection connection, String SW, String EW, String SH, String EH,
                                                 int employeeId, ArrayList<Boolean> days,
                                      boolean isCo) throws SQLException {
        String updateWorkTimeQuery = "UPDATE MYAppData.[WorkTime" + (isCo ? "CO":"WO") + "] " +
                "SET [startw] = ?, [endw] = ?, [starth] = ?, [endh] = ? " +
                "WHERE [ID] = (SELECT worktime_id FROM MYAppData." +
                (isCo ? "Company":"Employee") + " WHERE id = ?)";
        String updateWorkDaysQuery = "UPDATE MYAppData.[WorkDays" + (isCo ? "CO":"WO") + "] " +
                "SET [mon] = ?, [tue] = ?, [wed] = ?, [thu] = ?, [fri] = ?, [sat] = ?, [san] = ? " +
                "WHERE [ID] = (SELECT workdays_id FROM MYAppData." +
                (isCo ? "Company":"Employee") + " WHERE id = ?)";
        PreparedStatement updateWorkTimeStmt = connection.prepareStatement(updateWorkTimeQuery);
        updateWorkTimeStmt.setString(1, SW); updateWorkTimeStmt.setString(2, EW);
        updateWorkTimeStmt.setString(3, SH); updateWorkTimeStmt.setString(4, EH);
        updateWorkTimeStmt.setInt(5, employeeId); updateWorkTimeStmt.executeUpdate();
        PreparedStatement updateWorkDaysStmt = connection.prepareStatement(updateWorkDaysQuery);
        int i = 1; for (Boolean day : days) {updateWorkDaysStmt.setBoolean(i, day) ;i++;}
        updateWorkDaysStmt.setInt(i, employeeId); updateWorkDaysStmt.executeUpdate();
    }

    public static void UPDUser (Connection connection, String surname, String name, String lastname,
                                String workpost, String workplace, boolean fullacess, int id) throws SQLException {
        String query = "UPDATE MYAppData.[Employee] SET [surname] = ?, [name] = ?, [lastname] = ?," +
                " [workpost] = ?, [workplace] = ?, [fullacess] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, surname);    stmt.setString(2, name);         stmt.setString(3, lastname);
        stmt.setString(4, workpost);   stmt.setString(5, workplace);    stmt.setBoolean(6, fullacess);
        stmt.executeUpdate();
    }

    public static void UPDUserPhone (Connection connection, String phone, int id) throws SQLException {
        String query = "UPDATE MYAppData.[Employee] SET [phnumber] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, phone); stmt.executeUpdate();
    }

    public static void UPDUserPassword (Connection connection, String password, int id) throws SQLException {
        String query = "UPDATE MYAppData.[Employee] SET [password] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, password); stmt.executeUpdate();
    }

    public static void UPDUserLogin (Connection connection, String login, int id) throws SQLException {
        String query = "UPDATE MYAppData.[Employee] SET [login] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, login); stmt.executeUpdate();
    }

    public static void UPDPosition(Connection connection, int company, byte[] image, String s_name,
                                   String s_group, String s_code, float s_kg, int s_sm1, int s_sm2,
                                   int s_sm3, int s_count, String s_provider, String s_comment,
                                   int id) throws SQLException {
        String query = "UPDATE MYAppData.[Product] SET [company_id] = ?, [image] = ?, [name] = ?," +
                " [group] = ?, [code] = ?, [weignt] = ?, [sizeh] = ?, [sizew] = ?, [sized] = ?," +
                " [count] = ?, [provider] = ?, [comment] = ? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, company);    stmt.setBytes(2, image);        stmt.setString(3, s_name);
        stmt.setString(4, s_group); stmt.setString(5, s_code);      stmt.setFloat(6, s_kg);
        stmt.setInt(7, s_sm1);      stmt.setInt(8, s_sm2);          stmt.setInt(9, s_sm3);
        stmt.setInt(10, s_count);   stmt.setString(11, s_provider); stmt.setString(12, s_comment);
        stmt.executeUpdate();
    }

    public static void UPDPosition(Connection connection, int s_count, int id, int type) throws SQLException {
        String addSTR = " [count] "; if (type == 1) addSTR += "+ "; else  if (type == 2) addSTR += "- ";
        String query = "UPDATE MYAppData.[Product] SET [count] =" + (type > 0 ? addSTR: " ") + "? WHERE [ID] = " + id;
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, s_count); stmt.executeUpdate();
    }

    public static ArrayList<Integer> UPDPackingInfo(Connection mssqlConnection, String email, String login,
                                                    String ttnCode, int ordersSumCount, int ordersId)
            throws SQLException { String query = "{CALL UpdateOrdersAndArrive(?, ?, ?, ?, ?, ?, ?, ?)}";
        CallableStatement statement = mssqlConnection.prepareCall(query);
        statement.setString(1, login); statement.setString(2, email); statement.setString(3, ttnCode);
        statement.setInt(4, ordersSumCount); statement.setInt(5, ordersId); statement.setInt(6, 0);
        statement.setInt(7, 0); statement.setInt(8, 0);
        statement.registerOutParameter(6, java.sql.Types.INTEGER);
        statement.registerOutParameter(7, java.sql.Types.INTEGER);
        statement.registerOutParameter(8, java.sql.Types.INTEGER);
        statement.execute(); ArrayList<Integer> returned = new ArrayList<>();
        returned.add(statement.getInt(6)); returned.add(statement.getInt(7));
        returned.add(statement.getInt(8)); return returned;
    }

    public static void UPDAdditionOrOrdersArriveInfo(Connection mssqlConnection, String email, String login,
                                       int state, int getChecker, String table) throws SQLException {
        String query = "UPDATE MYAppData." + table + " SET [performer_id] = (SELECT E.id " +
                "FROM MYAppData.Company C LEFT JOIN MyAppData.Employee E ON C.id = E.company_id " +
                "AND E.login = ? WHERE C.email = ?), [state] = ?  WHERE [ID] = ?";
        PreparedStatement stmt = mssqlConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, login); stmt.setString(2, email);
        stmt.setInt(3, state); stmt.setInt(4, getChecker);
        stmt.executeUpdate();
    }

}