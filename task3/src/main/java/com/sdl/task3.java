package com.sdl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;

public class task3 {

    public static String[] tableName = {"user", "type", "status", "report"};
    public static String[] userFields = {"id", "username", "displayname", "email"};
    public static String[] typeFields = {"id", "codename", "description"};
    public static String[] statusFields = {"id", "codename", "description"};
    public static String[] reportFields = {"id", "user_id", "type_id", "description", "status_id", "application_date"};

    public static void main(String[] argv)  {

        if (System.getenv("LOG_PATH") != null) {
            try {
                System.setErr(new PrintStream(new File(System.getenv("LOG_PATH"))));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Scanner sc = new Scanner(System.in);
        Connection connection = null;
        while (connection == null) {
            System.out.print("Input name: ");
            String USER = sc.nextLine();

            System.out.print("Input password: ");
            String PASS = sc.nextLine();

            connection = connect(USER, PASS);
        }

        while (connection != null) {
            try {
                System.out.println("Требуемая операция (1-просмотр, 2-обновление, 3-вставка): ");
                Integer action = Integer.parseInt(sc.nextLine());
                String table = chooseTable();
                String[] table_fields = chooseTableFields(table);

                switch (action) {
                    case (1):
                        show(connection, table, table_fields);
                        break;
                    case (2):
                        update(connection, table, table_fields);
                        break;
                    case (3):
                        insert(connection, table, table_fields, null, null);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Connection connect(String user, String pass) {
        String DB_URL = System.getenv("DB_URL");
        Connection connection = null;
        try {
            connection = DriverManager
                    .getConnection(DB_URL, user, pass);
            System.out.println("Successful connection \n");

        } catch (SQLException e) {
            System.out.println("Connection failed");
        }
        return connection;
    }

    public static void show(Connection connection, String table, String[] table_fields) {

        String sql = "SELECT * from " + table;
        String field = null;
        String field_value = null;
        String[] values = new String[10];

        System.out.println("Фильтрация (1-без фильтров, 2-по одному значению, 3-по нескольким): ");
        Scanner in = new Scanner(System.in);
        Integer type = Integer.parseInt(in.nextLine());

        if (type != 1) {
            System.out.print("Наименование поля. ");
            while (!Arrays.asList(table_fields).contains(field)){
                System.out.println("Возможные поля - " + String.join(", ", table_fields)+":");
                field = in.nextLine();
            }
            sql += " where "+ field;
        }

        if (type == 2) {
            System.out.print("Введите значение: ");
            field_value = in.nextLine();
            sql += " = ? ";
        }

        if (type == 3) {
            int i = 0;
            String line;
            System.out.print("Введите значения (по одному): ");
            while (!(line = in.nextLine()).equals("")){
                values[i] = line;
                i++;
            }
            sql += " = ANY (?) ";
        }

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            if (type == 2){
                if (field.contains("id")) {
                    st.setInt(1, Integer.parseInt(field_value));
                }
                else {
                    st.setString(1, field_value);
                }
            }
            if (type==3){
                String typeName;
                if (field.contains("id")) {
                    typeName = "INTEGER";
                }
                else {
                    typeName = "VARCHAR";
                }
                Array array = connection.createArrayOf(typeName, values);
                st.setArray(1, array);
            }
            ResultSet rs = st.executeQuery();
            System.out.println(String.join("\t", table_fields));
            while (rs.next()) {
                String row = "";
                for (String field_name:
                        table_fields) {
                    row += rs.getString(field_name) + "\t";
                }
                System.out.println(row);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса");
            e.printStackTrace();
            return;
        }
    }

    public static void update(Connection connection, String table, String[] table_fields) {

        String sql = "UPDATE " + table + " SET ";
        String[] fields = new String[10];
        String[] values = new String[10];
        String[] field_values = new String[10];
        Integer id = null;
        String field = null;
        String field_value = null;
        String[] id_values = new String[10];
        String[] res_id = new String[10];

        Scanner in = new Scanner(System.in);
        System.out.print("Тип обновления - 1 (одна запись), 2 (несколько записей): ");
        Integer type = Integer.parseInt(in.nextLine());

        if (type == 1) {
            int i = 0;
            System.out.print("Введите поле: ");
            while (!(field = in.nextLine()).equals("")){
                if (Arrays.asList(table_fields).contains(field) && !field.equals("id")) {
                    System.out.print("Введите значение: ");
                    String value = in.nextLine();
                    fields[i] = field;
                    values[i] = value;
                    i++;
                } else {
                    System.out.println("Возможные поля - " + String.join(", ", table_fields));
                    System.out.println("Кроме поля id.");
                }
                System.out.print("Введите поле: ");
            }
            sql += String.join("=?,", Arrays.copyOfRange(fields, 0, i)) + "=?";
            field_values = Arrays.copyOfRange(values, 0, i);
            System.out.print("Введите идентификатор обновляемой строки: ");
            id = in.nextInt();
            sql += " WHERE id=?";
        }

        if (type == 2) {
            while (!Arrays.asList(table_fields).contains(field) || field.equals("id")) {
                System.out.println("Возможные поля - " + String.join(", ", table_fields));
                System.out.println("Кроме поля id.");
                System.out.print("Введите поле: ");
                field=in.nextLine();
            }
            System.out.print("Введите значение: ");
            field_value = in.nextLine();
            sql += field + "=?";
            int i = 0;
            String line;
            System.out.print("Введите идентификаторы обновляемых строк: ");
            while (!(line = in.nextLine()).equals("")){
                id_values[i] = line;
                i++;
            }
            res_id = Arrays.copyOfRange(id_values, 0, i);
            sql += " WHERE id = ANY (?)";
        }

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            if (type == 1) {
                Integer i = 0;
                for (String value:
                        field_values) {
                    if (fields[i].contains("id")) {
                        st.setInt(i+1, Integer.parseInt(value));
                    }
                    else {
                        st.setString(i+1, value);
                    }
                    i++;
                }
                st.setInt(i+1, id);
            } else {
                if (field.contains("id")) {
                    st.setInt(1, Integer.parseInt(field_value));
                }
                else {
                    st.setString(1, field_value);
                }
                Array array = connection.createArrayOf("INTEGER", res_id);
                st.setArray(2, array);
            }
            st.execute();
            System.out.println("Successful update");

        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса");
            e.printStackTrace();
            return;
        }
    }

    public static String insert(Connection connection, String table, String[] table_fields, Integer type, Integer type2) {
        String sql = "INSERT INTO " + table + " VALUES ";
        String id_related = null;

        String[] strArray = new String[table_fields.length];
        Arrays.fill(strArray, "?");
        String insStr = ("(" + String.join(", ", strArray) + ")");
        Integer amt = 0;
        Scanner in = new Scanner(System.in);

        if (type == null){
            System.out.print("Тип вставки - 1 (одна запись), 2 (несколько записей): ");
            type = Integer.parseInt(in.nextLine());
        }
        if (type == 1) {
            sql += insStr;
        } else {
            System.out.print("Количество вносимых записей: ");
            amt = Integer.parseInt(in.nextLine());
            String[] strInsArray = new String[amt];
            Arrays.fill(strInsArray, insStr);
            sql += String.join(", ", strInsArray);
        }

        if (type2 == null) {
            System.out.print("Тип вставки - 1 (одна таблица), 2 (несколько связанных): ");
            type2 = Integer.parseInt(in.nextLine());
        }
        
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            Integer i = 1;
            if (type == 1) {
                for (String field:
                        table_fields) {
                    System.out.print(field + ": ");
                    String value = null;
                    if (type2 == 2 && field.contains("_id")) {
                            System.out.print("Необходимость вставки связанной сущности - 1 (нет), 2 (да): ");
                            Integer need_ins = Integer.parseInt(in.nextLine());
                            if (need_ins==2){
                                String table2 = field.replace("_id","");
                                String[] table2_fields = chooseTableFields(table2);
                                value = insert(connection,table2,table2_fields,1,1);
                            } else {
                                System.out.print("Значение: ");
                                value = in.nextLine();
                            }
                    } else {
                        value = in.nextLine();
                    }
                    if (i==1) {
                        id_related = value;
                    }
                    if (field.contains("id")) {
                        st.setInt(i, Integer.parseInt(value));
                    }
                    else {
                        st.setString(i, value);
                    }
                    i++;
                }
            } else {
                for (int j=1; j<=amt; j++){
                    for (String field:
                            table_fields) {
                        System.out.print(field + ": ");
                        String value = null;
                        if (type2 == 2 && field.contains("_id")) {
                            System.out.print("Необходимость вставки связанной сущности - 1 (нет), 2 (да): ");
                            Integer need_ins = Integer.parseInt(in.nextLine());
                            if (need_ins==2){
                                String table2 = field.replace("_id","");
                                String[] table2_fields = chooseTableFields(table2);
                                value = insert(connection,table2,table2_fields,1,1);
                            } else {
                                System.out.print("Значение: ");
                                value = in.nextLine();
                            }
                        } else {
                            value = in.nextLine();
                        }
                        if (field.contains("id")) {
                            st.setInt(i, Integer.parseInt(value));
                        }
                        else {
                            st.setString(i, value);
                        }
                        i++;
                    }
                }
            }
            st.execute();
            System.out.println("Successful insert");
            return id_related;

        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса");
            e.printStackTrace();
            return null;
        }
    }

    public static String chooseTable () {
        Scanner sc = new Scanner(System.in);
        System.out.print("Наименование таблицы. ");
        String table = null;
        while (!Arrays.asList(tableName).contains(table)){
            System.out.println("Возможные значения - " + String.join(", ", tableName)+":");
            table = sc.nextLine();
        }
        if (table.equals("user")) {
            table = "\"user\"";
        }
        return table;
    }

    public static String[] chooseTableFields (String table) {
        String[] table_fields = null;
        switch (table){
            case ("\"user\""):
                table_fields = userFields;
                break;
            case ("type"):
                table_fields = typeFields;
                break;
            case ("status"):
                table_fields = statusFields;
                break;
            case ("report"):
                table_fields = reportFields;
                break;
        }
        return table_fields;
    }
}
