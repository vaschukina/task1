import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class task1 {

    public static void main(String[] argv) throws SQLException, IOException {

        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/db.properties"));

        String DB_URL = properties.getProperty("DB_URL");

        Scanner in = new Scanner(System.in);
        System.out.print("Input name: ");
        String USER = in.nextLine();

        System.out.print("Input password: ");
        String PASS = in.nextLine();
        in.close();

        Connection connection = null;
        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        PreparedStatement st = connection.prepareStatement("SELECT VERSION();");
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
        rs.close();
        st.close();
    }
}
