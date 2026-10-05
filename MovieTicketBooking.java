
package movieticket;

import java.sql.*;
import java.util.Scanner;

public class MovieTicketBooking {

    static final String URL = "jdbc:mysql://localhost:3306/movie_ticket_booking";
    static final String USERNAME = "root";
    static final String PASSWORD = "0907";

    static Scanner sc = new Scanner(System.in);


    // Database Connection
    
    public static Connection getConnection() {

        Connection con = null;

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection( URL, USERNAME, PASSWORD);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return con;
    }


    // 1. ADD MOVIE
    
    public static void addMovie() {

        try {

            Connection con = getConnection();

            System.out.print("Enter Movie Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Language: ");
            String language = sc.nextLine();

            System.out.print("Enter Duration in minutes: ");
            int duration = sc.nextInt();
            sc.nextLine();

            String sql ="INSERT INTO movies " +"(movie_name, language, duration) " +"VALUES (?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, language);
            ps.setInt(3, duration);

            ps.executeUpdate();

            System.out.println("Movie Added Successfully");

            con.close();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    // 2. VIEW MOVIES
    
    public static void viewMovies() {

        try {

            Connection con = getConnection();

            String sql = "SELECT * FROM movies";

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nMOVIES");

            while (rs.next()) {

                System.out.println( "ID: " + rs.getInt("movie_id") + " | Movie: " + rs.getString("movie_name") +" | Language: " +
                                     rs.getString("language") +  " | Duration: " + rs.getInt("duration"));
            }

            con.close();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    // 3. ADD THEATER
    
    public static void addTheater() {

        try {

            Connection con = getConnection();

            System.out.print("Enter Theater Name: ");
            String name = sc.nextLine();

            System.out.print("Enter City: ");
            String city = sc.nextLine();

            String sql ="INSERT INTO theaters " + "(theater_name, city) " + "VALUES (?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, city);

            ps.executeUpdate();

            System.out.println( "Theater Added Successfully" );

            con.close();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    // 4. VIEW THEATERS
    
    public static void viewTheaters() {

        try {

            Connection con = getConnection();

            String sql = "SELECT * FROM theaters";

            PreparedStatement ps =con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nTHEATERS");

            while (rs.next()) 
            {

                System.out.println( "ID: " + rs.getInt("theater_id") + " | Theater: "+  rs.getString("theater_name") + " | City: " + rs.getString("city"));
            }

            con.close();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    // 5. ADD SHOW
    
    public static void addShow() {

        try {

            Connection con = getConnection();

            System.out.print("Enter Movie ID: ");
            int movieId = sc.nextInt();

            System.out.print("Enter Theater ID: ");
            int theaterId = sc.nextInt();

            sc.nextLine();

            System.out.print("Enter Show Time: ");
            String time = sc.nextLine();

            System.out.print("Enter Total Seats: ");
            int seats = sc.nextInt();
            sc.nextLine();

            String sql = "INSERT INTO shows " + "(movie_id, theater_id, show_time, " + "total_seats, available_seats) " + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, movieId);
            ps.setInt(2, theaterId);
            ps.setString(3, time);
            ps.setInt(4, seats);
            ps.setInt(5, seats);

            ps.executeUpdate();

            System.out.println("Show Added Successfully");

            con.close();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    // 6. VIEW SHOWS
    
    public static void viewShows() {

        try
        {

            Connection con = getConnection();

            String sql = "SELECT shows.show_id, " + "movies.movie_name, " + "theaters.theater_name, " + "shows.show_time, " +
                         "shows.available_seats " + "FROM shows " + "JOIN movies " + "ON shows.movie_id = movies.movie_id " +
                         "JOIN theaters " + "ON shows.theater_id = theaters.theater_id";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nSHOWS");

            while (rs.next()) 
            {

                System.out.println("Show ID: " + rs.getInt("show_id") + " | Movie: " + rs.getString("movie_name") + " | Theater: " +
                                    rs.getString("theater_name") + " | Time: " +  rs.getString("show_time") +
                                   " | Available Seats: " + rs.getInt("available_seats"));
            }

            con.close();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    // 7. BOOK TICKET
    
    public static void bookTicket() {

        Connection con = null;

        try {

            con = getConnection();

            // START TRANSACTION
            
            con.setAutoCommit(false);

            System.out.print("Enter Show ID: ");
            int showId = sc.nextInt();

            System.out.print("Enter Seat Number: ");
            int seatNumber = sc.nextInt();

            sc.nextLine();

            System.out.print("Enter Customer Name: ");
            String customer = sc.nextLine();

            // CHECK SEAT
            
            String check = "SELECT available_seats " + "FROM shows " + "WHERE show_id = ? " + "FOR UPDATE";

            PreparedStatement ps =
                    con.prepareStatement(check);

            ps.setInt(1, showId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {

                System.out.println("Show Not Found");

                con.rollback();
                con.close();
                return;
            }

            int available = rs.getInt("available_seats");

            // SEAT VALIDATION
            
            if (seatNumber <= 0 || seatNumber > available)
            	
            {

            	System.out.println( "Invalid Seat Number");

                con.rollback();
                con.close();
                return;
            }

            // CHECK WHETHER SEAT ALREADY BOOKED
            
            String seatCheck = "SELECT * FROM bookings " + "WHERE show_id = ? " + "AND seat_number = ? " + "AND booking_status = 'BOOKED'";

            PreparedStatement ps2 = con.prepareStatement(seatCheck);

            ps2.setInt(1, showId);
            ps2.setInt(2, seatNumber);

            ResultSet rs2 = ps2.executeQuery();

            if (rs2.next()) 
            {

                System.out.println( "Seat Already Booked" );

                con.rollback();
                con.close();
                return;
            }

            // INSERT BOOKING
            
            String insert = "INSERT INTO bookings " + "(show_id, customer_name, seat_number, " + "ticket_price, booking_status) " + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps3 = con.prepareStatement(insert);

            ps3.setInt(1, showId);
            ps3.setString(2, customer);
            ps3.setInt(3, seatNumber);
            ps3.setDouble(4, 200);
            ps3.setString(5, "BOOKED");

            ps3.executeUpdate();

            // REDUCE AVAILABLE SEATS
            
            String update = "UPDATE shows " + "SET available_seats = available_seats - 1 " + "WHERE show_id = ?";
            
            PreparedStatement ps4 = con.prepareStatement(update);

            ps4.setInt(1, showId);

            ps4.executeUpdate();

            // COMMIT
            
            con.commit();

            System.out.println( "Ticket Booked Successfully");

            con.close();

        } catch (Exception e) {

            try {

                if (con != null) {
                    con.rollback();
                }

            } catch (Exception ex) {

                ex.printStackTrace();

            }

            e.printStackTrace();
        }
    }


    // 8. VIEW BOOKINGS
    
    public static void viewBookings() {

        try {

            Connection con = getConnection();

            String sql ="SELECT bookings.booking_id, " + "movies.movie_name, " + "theaters.theater_name, " + "bookings.customer_name, " +
                        "bookings.seat_number, " +  "bookings.ticket_price, " +  "bookings.booking_status " + "FROM bookings " +
                        "JOIN shows " + "ON bookings.show_id = shows.show_id " + "JOIN movies " + "ON shows.movie_id = movies.movie_id " +
                        "JOIN theaters " + "ON shows.theater_id = theaters.theater_id";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nBOOKINGS");

            while (rs.next()) {

                System.out.println( "Booking ID: " + rs.getInt("booking_id") + " | Movie: " + rs.getString("movie_name") + " | Theater: " +
                                     rs.getString("theater_name") + " | Customer: " + rs.getString("customer_name") + " | Seat: " +
                                     rs.getInt("seat_number") + " | Price: " + rs.getDouble("ticket_price") + " | Status: " +
                                     rs.getString("booking_status"));
            }

            con.close();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    // 9. MAIN MENU
    
    public static void main(String[] args) {

        int choice;

        do {

            System.out.println("\n====================================");
            System.out.println(" MOVIE TICKET BOOKING SYSTEM");
            System.out.println( "====================================");

            System.out.println("1. Add Movie");
            System.out.println("2. View Movies");
            System.out.println("3. Add Theater");
            System.out.println("4. View Theaters");
            System.out.println("5. Add Show");
            System.out.println("6. View Shows");
            System.out.println("7. Book Ticket");
            System.out.println("8. View Bookings");
            System.out.println("9. Exit");

            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    addMovie();
                    break;

                case 2:
                    viewMovies();
                    break;

                case 3:
                    addTheater();
                    break;

                case 4:
                    viewTheaters();
                    break;

                case 5:
                    addShow();
                    break;

                case 6:
                    viewShows();
                    break;

                case 7:
                    bookTicket();
                    break;

                case 8:
                    viewBookings();
                    break;

                case 9:
                    System.out.println("Thank You 🙏");
                    break;

                default:
                    System.out.println("Invalid Choice");
            }

        } while (choice != 9);

        sc.close();
    }
}