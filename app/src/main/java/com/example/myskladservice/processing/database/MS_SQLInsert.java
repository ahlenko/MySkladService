package com.example.myskladservice.processing.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class MS_SQLInsert extends Exception {
    public static int AddWorkTime (Connection connection, String SW, String EW, String SH, String EH, boolean isCo) throws SQLException {
        String query;
        if (isCo) query = "INSERT INTO MYAppData.[WorkTimeCO] ([startw], [endw], [starth], [endh]) VALUES (?, ?, ?, ?)";
        else      query = "INSERT INTO MYAppData.[WorkTimeWO] ([startw], [endw], [starth], [endh]) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, SW);  stmt.setString(2, EW);
        stmt.setString(3, SH);  stmt.setString(4, EH);

        stmt.executeUpdate();

        ResultSet resultSet = stmt.getGeneratedKeys();
        if (resultSet.next())
            return resultSet.getInt(1);
        else return -1;
    }

    public static int AddWorkDays (Connection connection, boolean d1, boolean d2, boolean d3, boolean d4, boolean d5, boolean d6, boolean d7, boolean isCo) throws SQLException {
        String query;
        if (isCo) query = "INSERT INTO MYAppData.[WorkDaysCO] ([mon], [tue], [wed], [thu], [fri], [sat], [san]) VALUES (?, ?, ?, ?, ?, ?, ?)";
        else      query = "INSERT INTO MYAppData.[WorkDaysWO] ([mon], [tue], [wed], [thu], [fri], [sat], [san]) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setBoolean(1, d1); stmt.setBoolean(2, d2); stmt.setBoolean(3, d3);
        stmt.setBoolean(4, d4); stmt.setBoolean(5, d5); stmt.setBoolean(6, d6);
        stmt.setBoolean(7, d7);

        stmt.executeUpdate();

        ResultSet resultSet = stmt.getGeneratedKeys();
        if (resultSet.next())
            return resultSet.getInt(1);
        else return -1;
    }

    public static int AddCompany (Connection connection, String name, String adress, String spec, String email, String tel, String code, int WD, int WT) throws SQLException {
        String query = "INSERT INTO MYAppData.[Company] ([name], [adress], [specialization], [email], [phnumber], [reg_number], [workdays_id], [worktime_id]) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, name);    stmt.setString(2, adress);  stmt.setString(3, spec);
        stmt.setString(4, email);   stmt.setString(5, tel);     stmt.setString(6, code);
        stmt.setInt(7, WD);         stmt.setInt(8, WT);

        stmt.executeUpdate();

        ResultSet resultSet = stmt.getGeneratedKeys();
        if (resultSet.next())
            return resultSet.getInt(1);
        else return -1;
    }

    public static int AddUser (Connection connection, String surname, String name, String lastname, String workpost, String workplace, String phnumber,String login,String password , boolean fullacess, int WD, int WT, int CI) throws SQLException {
        String query = "INSERT INTO MYAppData.[Employee] ([surname], [name], [lastname], [workpost], [workplace], [phnumber], [login], [password], [fullacess], [workdays_id], [worktime_id], [company_id]) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, surname);    stmt.setString(2, name);         stmt.setString(3, lastname);
        stmt.setString(4, workpost);   stmt.setString(5, workplace);    stmt.setString(6, phnumber);
        stmt.setString(7, login);      stmt.setString(8, password);     stmt.setBoolean(9, fullacess);
        stmt.setInt(10, WD);           stmt.setInt(11, WT);             stmt.setInt(12, CI);

        stmt.executeUpdate();

        ResultSet resultSet = stmt.getGeneratedKeys();
        if (resultSet.next())
            return resultSet.getInt(1);
        else return -1;
    }
    public static int AddTask(Connection connection, int adresser, int performer, int company, String createDate, String startTime, String endTime, String type, String comment) throws SQLException{
        String query = "INSERT INTO MYAppData.[EmployeeTask] ([adresser_id], [performer_id], [company_id], [createdata], [starttime], [endtime], [type], [text]) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, adresser);       stmt.setInt(2, performer);       stmt.setInt(3, company);
        stmt.setString(4, createDate);  stmt.setString(5, startTime);    stmt.setString(6, endTime);
        stmt.setString(7, type);        stmt.setString(8, comment);

        stmt.executeUpdate();
        ResultSet resultSet = stmt.getGeneratedKeys();
        if (resultSet.next())
            return resultSet.getInt(1);
        else return -1;
    }

    public static int AddPosition(Connection connection, int company, String s_name, String s_group, String s_code, float s_kg, int s_sm1, int s_sm2, int s_sm3, int s_count, String s_provider, String s_comment) throws SQLException {
        String query = "INSERT INTO MYAppData.[Product] ([company_id], [name], [group], [code], [weignt], [sizeh], [sizew], [sized], [count], [provider], [comment]) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        stmt.setInt(1, company);     stmt.setString(2, s_name);
        stmt.setString(3, s_group);  stmt.setString(4, s_code);         stmt.setFloat(5, s_kg);
        stmt.setInt(6, s_sm1);       stmt.setInt(7, s_sm2);             stmt.setInt(8, s_sm3);
        stmt.setInt(9, s_count);    stmt.setString(10, s_provider);    stmt.setString(11, s_comment);

        stmt.executeUpdate();
        ResultSet resultSet = stmt.getGeneratedKeys();
        if (resultSet.next())
            return resultSet.getInt(1);
        else return -1;
    }

    public static int AddPosition(Connection connection, int company, byte[] image, String s_name, String s_group, String s_code, float s_kg, int s_sm1, int s_sm2, int s_sm3, int s_count, String s_provider, String s_comment) throws SQLException {
        String query = "INSERT INTO MYAppData.[Product] ([company_id], [image], [name], [group], [code], [weignt], [sizeh], [sizew], [sized], [count], [provider], [comment]) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, company);     stmt.setBytes(2, image);           stmt.setString(3, s_name);
        stmt.setString(4, s_group);  stmt.setString(5, s_code);         stmt.setFloat(6, s_kg);
        stmt.setInt(7, s_sm1);       stmt.setInt(8, s_sm2);             stmt.setInt(9, s_sm3);
        stmt.setInt(10, s_count);    stmt.setString(11, s_provider);    stmt.setString(12, s_comment);

        stmt.executeUpdate();
        ResultSet resultSet = stmt.getGeneratedKeys();
        if (resultSet.next())
            return resultSet.getInt(1);
        else return -1;
    }


    public static int AddChecking(Connection connection, int company, int performer, int state, int count) throws SQLException {
        String query = "INSERT INTO MYAppData.[Checking] ([company_id], [performer_id], [date], [state], [sum_count]) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, company);     stmt.setInt(2, performer);

        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
            stmt.setDate(3, java.sql.Date.valueOf(String.valueOf(currentDate)));
        }

        stmt.setInt(4, state);     stmt.setInt(5, count);

        stmt.executeUpdate();
        ResultSet resultSet = stmt.getGeneratedKeys();
        if (resultSet.next())
            return resultSet.getInt(1);
        else return -1;
    }


    public static int AddCheckingInfo(Connection connection, int company, int checking_id, int product_id, int count, int count_old) {
        String query = "INSERT INTO MYAppData.[CheckingValues] ([company_id], [product_id], [checking_id], [count], [count_old]) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, company);     stmt.setInt(2, product_id);
            stmt.setInt(3, checking_id);     stmt.setInt(4, count);
            stmt.setInt(5, count_old);

            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next())
                return resultSet.getInt(1);
            else return -1;
        } catch (SQLException e){
            return -1;
        }
    }

    public static int NewArriverAtThatDay(Connection mssqlConnection, int company) throws SQLException {
        String query = "INSERT INTO MYAppData.[OrdersArrive] ([company_id], [date], [state], [sum_count]) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = mssqlConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, company);

        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
            stmt.setDate(2, java.sql.Date.valueOf(String.valueOf(currentDate)));
        }

        stmt.setInt(3, 0);     stmt.setInt(4, 1);

        stmt.executeUpdate();
        ResultSet resultSet = stmt.getGeneratedKeys();
        if (resultSet.next())
            return resultSet.getInt(1);
        else return -1;
    }

    public static void NewArriveOrder(Connection mssqlConnection, int arrive_id, int company, int getChecker, int i) throws SQLException {
        String query = "INSERT INTO MYAppData.[OrdersArriveDetails] ([company_id] ,[orders_id] ,[ordersArrive_id] ,[count]) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = mssqlConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, company);       stmt.setInt(2, getChecker);       stmt.setInt(3, arrive_id);
        stmt.setInt(4, i);

        stmt.executeUpdate();

    }
}
