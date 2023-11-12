package com.example.myskladservice.processing.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class MS_SQLInsert extends Exception {
    private static int executeInsertWithTransaction(Connection connection, String query,
                                                    Object ... parameters) throws SQLException {
        connection. setAutoCommit(false); try {
            PreparedStatement stmt = connection.prepareStatement(query, Statement. RETURN_GENERATED_KEYS);
            for (int i = 0; i < parameters. length; i++) {
                if (parameters[i] instanceof String)
                    stmt.setString( i + 1, (String) parameters[i]);
                else if (parameters[i] instanceof Boolean)
                    stmt.setBoolean( i + 1, (Boolean) parameters[i]);
                else if (parameters[i] instanceof Integer)
                    stmt.setInt( i + 1, (Integer) parameters[i]);
                else if (parameters[i] instanceof Float)
                    stmt.setFloat( i + 1, (Float) parameters[i]);
                else if (parameters[i] instanceof byte[])
                    stmt.setBytes( i + 1, (byte[]) parameters[i]);
                else if (parameters[i] instanceof Date)
                    stmt.setDate( i + 1, (Date) parameters[i]);
            } stmt. executeUpdate ();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) { connection. commit();
                return resultSet.getInt(1); }
        } catch (SQLException e) { connection.rollback(); throw e;
        } finally { connection.setAutoCommit(true);} return -1;
    }

    public static int AddWorkTime (Connection connection, String SW, String EW, String SH,
                                   String EH, boolean isCo) throws SQLException {
        String query = "INSERT INTO MYAppData.[WorkTime" + (isCo ? "CO" : "WO") + "] " +
                "([startw], [endw], [starth], [endh]) VALUES (?, ?, ?, ?)";
        return executeInsertWithTransaction(connection, query, SW, EW, SH, EH);
    }

    public static int AddWorkDays (Connection connection, boolean d1, boolean d2,
                                   boolean d3, boolean d4, boolean d5, boolean d6,
                                   boolean d7, boolean isCo) throws SQLException {
        String query = "INSERT INTO MYAppData.[WorkDays" + (isCo ? "CO" : "WO") + "] ([mon], " +
                "[tue], [wed], [thu], [fri], [sat], [san]) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return executeInsertWithTransaction(connection, query, d1, d2, d3, d4, d5, d6, d7);
    }

    public static int AddCompany (Connection connection, String name, String adress, String spec,
                                  String email, String tel, String code, int WD, int WT)
            throws SQLException {
        String query = "INSERT INTO MYAppData.[Company] ([name], [adress], [specialization]," +
                " [email], [phnumber], [reg_number], [workdays_id], [worktime_id])" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return executeInsertWithTransaction(connection, query, name, adress, spec,
                email, tel, code, WD, WT);
    }

    public static int AddUser (Connection connection, String surname, String name, String lastname,
                               String workpost, String workplace, String phnumber,String login,
                               String password , boolean fullacess, int WD, int WT, int CI)
            throws SQLException {
        String query = "INSERT INTO MYAppData.[Employee] ([surname], [name], [lastname]," +
                " [workpost], [workplace], [phnumber], [login], [password], [fullacess]," +
                " [workdays_id], [worktime_id], [company_id])" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return executeInsertWithTransaction(connection, query, surname, name, lastname, workpost,
                workplace, login, password, fullacess, WD, WT, CI);
    }
    public static int AddTask(Connection connection, int adresser, int performer, int company,
                              String createDate, String startTime, String endTime, String type,
                              String comment) throws SQLException{
        String query = "INSERT INTO MYAppData.[EmployeeTask] ([adresser_id], [performer_id]," +
                " [company_id], [createdata], [starttime], [endtime], [type], [text]) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return executeInsertWithTransaction(connection, query, adresser, performer, company,
                createDate, startTime, endTime, type, comment);
    }

    public static int AddPosition(Connection connection, int company, byte[] image, String s_name,
                                  String s_group, String s_code, float s_kg, int s_sm1, int s_sm2,
                                  int s_sm3, int s_count, String s_provider, String s_comment)
            throws SQLException {
        String query = "INSERT INTO MYAppData.[Product] ([company_id], [image], [name], [group], " +
                "[code], [weignt], [sizeh], [sizew], [sized], [count], [provider], [comment]) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return executeInsertWithTransaction(connection, query, company, image, s_name, s_group,
                s_code, s_kg, s_sm1, s_sm2, s_sm3, s_count, s_provider, s_comment);
    }

    public static int AddCheckingInfo(Connection connection, String company, int checking_id,
                                      int product_id, int count, int count_old) throws SQLException {
        String query = "INSERT INTO MYAppData.[CheckingValues] ([company_id], [product_id], " +
                "[checking_id], [count], [count_old]) VALUES ((SELECT id From MYAppData.Company " +
                "WHERE email = ?), ?, ?, ?, ?)";
        return executeInsertWithTransaction(connection, query, company, product_id, checking_id,
                count, count_old);
    }

    public static void NewArriveOrder(Connection connection, int arrive_id, int company,
                                     int getChecker, int i) throws SQLException {
        String query = "INSERT INTO MYAppData.[OrdersArriveDetails] ([company_id], [orders_id], " +
                "[ordersArrive_id], [count]) VALUES (?, ?, ?, ?)";
        executeInsertWithTransaction(connection, query, company, getChecker, arrive_id, i);
    }

    public static int AddChecking(Connection connection, String company, String performer, int state,
                                  int count) throws SQLException {
        String query = "INSERT INTO MYAppData.[Checking] ([company_id], [performer_id], " +
                "[date], [state], [sum_count]) VALUES ((SELECT id From MYAppData.Company WHERE email = ?), " +
                "(SELECT E.id FROM MYAppData.Company C LEFT JOIN MYAppData.Employee E ON C.id = E.company_id " +
                "AND E.login = ? WHERE C.email = ?), ?, ?, ?)";
        Date date = null; if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate curDate = LocalDate.now(); date = java.sql.Date.valueOf(String.valueOf(curDate));}
        return executeInsertWithTransaction(connection, query, company, performer, company, date, state, count);
    }

    public static int NewArriverAtThatDay(Connection connection, int company)
            throws SQLException {
        String query = "INSERT INTO MYAppData.[OrdersArrive] ([company_id], [date], [state], " +
                "[sum_count]) VALUES (?, ?, ?, ?)";
        Date date = null; if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate curDate = LocalDate.now(); date = java.sql.Date.valueOf(String.valueOf(curDate));}
        return executeInsertWithTransaction(connection, query, company, date, 0, 1);
    }


}
