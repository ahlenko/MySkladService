package com.example.myskladservice.processing.database;

import android.content.Context;
import android.os.Build;

import com.example.myskladservice.processing.datastruct.PositionData;
import com.example.myskladservice.processing.datastruct.TaskData;
import com.example.myskladservice.processing.datastruct.UserData;
import com.example.myskladservice.processing.datastruct.Worktime;
import com.example.myskladservice.processing.shpreference.AppTableChecker;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class MS_SQLSelect extends Exception {
    public static ResultSet IsCurrectLogin(Connection connection, String email, String login) throws SQLException {
        String query = "SELECT C.email AS company_email, C.id AS company_id, E.id AS employee_id, " +
                "E.fullacess, E.password, E.phnumber, E.login FROM MYAppData.Company C " +
                "LEFT JOIN MYAppData.Employee E ON C.id = E.company_id AND " +
                "E.login = ? WHERE C.email = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, email);

        return preparedStatement.executeQuery();
    }

    public static ResultSet  HasCompanyEmail (Connection connection, String email) throws SQLException {
        String query = "SELECT id FROM MYAppData.Company WHERE email = '" + email + "'";
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet  CompanyManager (Connection connection, String email) throws SQLException {
        String query = "SELECT id, manager_id FROM MYAppData.Company WHERE email = '" + email + "'";
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet HasUserLogin (Connection connection, String login, int id) throws SQLException {
        String query = "SELECT id, fullacess, password, phnumber, login FROM MYAppData.Employee WHERE login = '" + login + "' AND company_id = " + id;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet UserATWork (Connection connection, String login, int id) throws SQLException {
        String query = "SELECT id, onwork FROM MYAppData.Employee WHERE login = '" + login + "' AND company_id = " + id;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet SelectUser (Connection connection, int id) throws SQLException {
        String query = "SELECT id, surname, name, lastname, workpost, workplace, onwork, fullacess, login FROM MYAppData.Employee WHERE company_id = " + id + " ORDER BY surname";
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet SelectUserTime (Connection connection, int id) throws SQLException {
        String query = "SELECT workdays_id, worktime_id FROM MYAppData.Employee WHERE id = " + id;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet HasUserPhone (Connection connection, String phone, int id) throws SQLException {
        String query = "SELECT fullacess, password FROM MYAppData.Employee WHERE login = '" + phone + "' AND company_id = " + id;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet HasBarcode (Connection connection, String code, int id) throws SQLException {
        String query = "SELECT id, code FROM MYAppData.Product WHERE code = '" + code + "' AND company_id = " + id;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet HasCompanyPhoneNumber (Connection connection, String phone) throws SQLException {
        String query = "SELECT id FROM MYAppData.Company WHERE phnumber = '" + phone + "'";
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet HasCompanyRegNumber (Connection connection, String code) throws SQLException {
        String query = "SELECT id FROM MYAppData.Company WHERE reg_number = " + code;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static void WorkTimetable (Connection connection, String email, int id) throws SQLException {
        String query0, query1, query2;
        ResultSet resultSet;
        if (id == -1 ) query0 = "SELECT workdays_id, worktime_id FROM MYAppData.Company WHERE email = '" + email + "'";
        else           query0 = "SELECT workdays_id, worktime_id FROM MYAppData.Employee WHERE id = '" + id + "'";
        Statement statement = connection.createStatement();
        resultSet = statement.executeQuery(query0); resultSet.next();
        if (id == -1 ){
            query1 = "SELECT * FROM MYAppData.WorkDaysCO WHERE id = " + resultSet.getInt("workdays_id");
            query2 = "SELECT * FROM MYAppData.WorkTimeCO WHERE id = " + resultSet.getInt("worktime_id");
        } else {
            query1 = "SELECT * FROM MYAppData.WorkDaysWO WHERE id = " + resultSet.getInt("workdays_id");
            query2 = "SELECT * FROM MYAppData.WorkTimeWO WHERE id = " + resultSet.getInt("worktime_id");
        }
        resultSet = statement.executeQuery(query1); resultSet.next();
        Worktime.mon = resultSet.getBoolean("mon");
        Worktime.tue = resultSet.getBoolean("tue");
        Worktime.wed = resultSet.getBoolean("wed");
        Worktime.thu = resultSet.getBoolean("thu");
        Worktime.fri = resultSet.getBoolean("fri");
        Worktime.sat = resultSet.getBoolean("sat");
        Worktime.san = resultSet.getBoolean("san");
        resultSet = statement.executeQuery(query2); resultSet.next();
        Worktime.startw = resultSet.getString("startw").substring(0, resultSet.getString("startw").length() - 11);
        Worktime.endw = resultSet.getString("endw").substring(0, resultSet.getString("endw").length() - 11);
        Worktime.starth = resultSet.getString("starth").substring(0, resultSet.getString("starth").length() - 11);
        Worktime.endh = resultSet.getString("endh").substring(0, resultSet.getString("endh").length() - 11);
    }

    public static void ReadUser (Connection connection, int id) throws SQLException{
        String query = "SELECT * FROM MYAppData.Employee WHERE id = " + id;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query); resultSet.next();
        UserData.surname = resultSet.getString("surname");
        UserData.name = resultSet.getString("name");
        UserData.lastname = resultSet.getString("lastname");
        UserData.workpost = resultSet.getString("workpost");
        UserData.workplace = resultSet.getString("workplace");
        UserData.phnumber = resultSet.getString("phnumber");
        UserData.login = resultSet.getString("login");
        UserData.password = resultSet.getString("password");
        UserData.fullaccess = resultSet.getBoolean("fullacess");
        WorkTimetable(connection, "", id);
    }

    public static void ReadTask (Connection connection, int id) throws  SQLException{
        String query = "SELECT * FROM MYAppData.EmployeeTask WHERE id = " + id;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query); resultSet.next();
        TaskData.type = resultSet.getString("type");
        TaskData.comment = resultSet.getString("text");
        TaskData.endtime = resultSet.getString("endtime");
        TaskData.starttime = resultSet.getString("starttime").substring(0, resultSet.getString("starttime").length() - 11);
        query = "SELECT surname, name, lastname FROM MYAppData.Employee WHERE id = " + resultSet.getInt("performer_id");
        resultSet = statement.executeQuery(query); resultSet.next();
        TaskData.pefrormer = resultSet.getString("surname") + " " +
                resultSet.getString("name") + " " +
                resultSet.getString("lastname");
    }

    public static ResultSet ReadTaskPrinted (Connection connection, int idCompany, int idAdresser) throws  SQLException{
        String query = "SELECT * FROM MYAppData.EmployeeTask WHERE company_id = " + idCompany + " AND adresser_id = " + idAdresser;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet ReadTaskPrintedPR (Connection connection, int idCompany, int idPerformer) throws  SQLException{
        String query = "SELECT * FROM MYAppData.EmployeeTask WHERE company_id = " + idCompany + " AND performer_id = " + idPerformer;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static String ReadUserName (Connection connection, int idUser) throws  SQLException{
        String query = "SELECT surname, name, lastname FROM MYAppData.Employee WHERE id = " + idUser;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next())
        return resultSet.getString("surname") + " " +
                resultSet.getString("name").charAt(0) + ". " +
                resultSet.getString("lastname").charAt(0) + ".";
        else return "_____________";
    }

    public static ResultSet ReadProducts(Connection connection, int company) throws SQLException {
        String query = "SELECT * FROM MYAppData.Product WHERE company_id = " + company;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet ReadChecking(Connection connection, int company, LocalDate date) throws SQLException {
        String query = "SELECT * FROM MYAppData.Checking WHERE company_id = ? AND MONTH(date) = ? AND YEAR(date) = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, company);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            statement.setInt(2, date.getMonthValue());
            statement.setInt(3, date.getYear());
        }
        return statement.executeQuery();
    }

    public static ResultSet ReadInput(Connection connection, int company, LocalDate date) throws SQLException {
        String query = "SELECT * FROM MYAppData.Addition WHERE company_id = ? AND MONTH(date) = ? AND YEAR(date) = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, company);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            statement.setInt(2, date.getMonthValue());
            statement.setInt(3, date.getYear());
        }
        return statement.executeQuery();
    }

    public static ResultSet ReadOutput(Connection connection, int company, LocalDate date) throws SQLException {
        String query = "SELECT * FROM MYAppData.OrdersArrive WHERE company_id = ? AND MONTH(date) = ? AND YEAR(date) = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, company);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            statement.setInt(2, date.getMonthValue());
            statement.setInt(3, date.getYear());
        }
        return statement.executeQuery();
    }

    public static ResultSet ReadOrders(Connection connection, int company, LocalDate date) throws SQLException {
        String query = "SELECT * FROM MYAppData.Orders WHERE company_id = ? AND DAY(date) = ? AND MONTH(date) = ? AND YEAR(date) = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, company);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            statement.setInt(2, date.getDayOfMonth());
            statement.setInt(3, date.getMonthValue());
            statement.setInt(4, date.getYear());
        }
        return statement.executeQuery();
    }

    public static ResultSet ReadProductsWith(Connection connection, int company, String name) throws SQLException {
        String query = "SELECT * FROM MYAppData.Product WHERE company_id = " + company + " AND name LIKE '" + name + "%'";
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static void ReadProduct(Connection connection, int id) throws SQLException {
        String query = "SELECT * FROM MYAppData.Product WHERE id = " + id;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query); resultSet.next();
        PositionData.image = resultSet.getBytes("image");
        PositionData.name = resultSet.getString("name");
        PositionData.group = resultSet.getString("group");
        PositionData.code = resultSet.getString("code");
        PositionData.weight = resultSet.getFloat("weignt");
        PositionData.size_1 = resultSet.getInt("sizeh");
        PositionData.size_2 = resultSet.getInt("sizew");
        PositionData.size_3 = resultSet.getInt("sized");
        PositionData.count = resultSet.getInt("count");
        PositionData.provider = resultSet.getString("provider");
        PositionData.comment = resultSet.getString("comment");
    }

    public static ResultSet ReadProduct(Connection connection, int id, int i) throws SQLException {
        String query = "SELECT * FROM MYAppData.Product WHERE id = " + id;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet ReadCheckingInfo(Connection connection, int id_checking) throws SQLException{
        String query = "SELECT * FROM MYAppData.CheckingValues WHERE checking_id = " + id_checking;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet ReadOrderCode(Connection mssqlConnection, int getChecker) throws SQLException {
        String query = "SELECT * FROM MYAppData.Orders WHERE id = " + getChecker;
        Statement statement = mssqlConnection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet ReadAddition(Connection mssqlConnection, int getChecker) throws SQLException {
        String query = "SELECT * FROM MYAppData.Addition WHERE id = " + getChecker;
        Statement statement = mssqlConnection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet ReadOrderArrive(Connection mssqlConnection, int id) throws SQLException {
        String query = "SELECT * FROM MYAppData.OrdersArrive WHERE id = " + id;
        Statement statement = mssqlConnection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet ReadOrderElements(Connection mssqlConnection, int getChecker) throws SQLException {
        String query = "SELECT * FROM MYAppData.OrdersDetails WHERE orders_id = " + getChecker;
        Statement statement = mssqlConnection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet ReadAdditionElements(Connection mssqlConnection, int getChecker) throws SQLException {
        String query = "SELECT * FROM MYAppData.AdditionValues WHERE addition_id = " + getChecker;
        Statement statement = mssqlConnection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet ReadOrderArriveElements(Connection mssqlConnection, int id) throws SQLException {
        String query = "SELECT * FROM MYAppData.OrdersArriveDetails WHERE orders_id = " + id;
        Statement statement = mssqlConnection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet GetProdNameById(Connection connection, int id) throws SQLException {
        String query = "SELECT id, name, code FROM MYAppData.Product WHERE id = " + id;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet GetOrderArriveInfo(Connection connection, int id) throws SQLException {
        String query = "SELECT * FROM MYAppData.Orders WHERE id = " + id;
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static ResultSet ArriverAtThatDay(Connection mssqlConnection, int company) throws SQLException {
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();}

        String query = "SELECT * FROM MYAppData.OrdersArrive WHERE company_id = " + company + " AND date = ?";
        PreparedStatement statement = mssqlConnection.prepareStatement(query);
        statement.setDate(1, java.sql.Date.valueOf(String.valueOf(currentDate)));
        return statement.executeQuery();
    }

    public static int GetProdCountById(Connection mssqlConnection, Integer integer) throws SQLException {
        String query = "SELECT count FROM MYAppData.Product WHERE id = " + integer;
        Statement statement = mssqlConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();
        return resultSet.getInt("count");
    }
}
