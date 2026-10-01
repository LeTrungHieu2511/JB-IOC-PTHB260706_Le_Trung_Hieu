package vn.edu.rikkei.Thi;

import utils.ConnectDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//lop truy van du lieu
public class TicketBookingDAO {

    //lay tat ca phieu dat ve = function fn_get_all_bookings
    public List<TicketBooking> getAll() throws SQLException {
        return queryList("SELECT * FROM fn_get_all_bookings()", null);
    }

    //tim phieu theo ten khach hang = function fn_find_bookings_by_customer
    public List<TicketBooking> findByCustomer(String customerName) throws SQLException {
        return queryList("SELECT * FROM fn_find_bookings_by_customer(?)", customerName);
    }

    //tim phieu theo ten phim bang function fn_search_bookings_by_movie
    public List<TicketBooking> searchByMovie(String movieTitle) throws SQLException{
        return queryList("SELECT * FROM fn_search_bookings_by_movie(?)", movieTitle);
    }

    //them phieu moi bang procedure sp_add_booking
    public void addBooking(TicketBooking booking) throws SQLException {
        try (Connection conn = ConnectDB.openConnection();
             CallableStatement cs = conn.prepareCall("CALL sp_add_booking(?, ?, ?, ?, ?, ?)")) {
            cs.setString(1, booking.getMovieTitle());
            cs.setString(2, booking.getCustomerName());
            cs.setTimestamp(3, Timestamp.valueOf(booking.getShowTime()));
            cs.setTimestamp(4, Timestamp.valueOf(booking.getBookingDate()));
            cs.setInt(5, booking.getSeatQuantity());
            cs.setString(6, booking.getStatus());
            cs.execute();
        }
    }

    //cap nhat phieu theo booking_id bang procedure sp_update_booking
    public void updateBooking(TicketBooking booking) throws SQLException {
        try(Connection conn = ConnectDB.openConnection();
            CallableStatement cs = conn.prepareCall("CALL sp_update_booking(?, ?, ?, ?, ?, ?, ?)")) {
            cs.setInt(1, booking.getBookingId());
            cs.setString(2, booking.getMovieTitle());
            cs.setString(3, booking.getCustomerName());
            cs.setTimestamp(4, Timestamp.valueOf(booking.getShowTime()));
            cs.setTimestamp(5, Timestamp.valueOf(booking.getBookingDate()));
            cs.setInt(6, booking.getSeatQuantity());
            cs.setString(7, booking.getStatus());
            cs.execute();
        }
    }

    //xoa phieu theo booking_id bang procedure sp_delete_booking
    public void deleteBooking(int bookingId) throws SQLException {
        try(Connection conn = ConnectDB.openConnection();
            CallableStatement cs = conn.prepareCall("CALL sp_delete_booking(?)")) {
            cs.setInt(1, bookingId);
            cs.execute();
        }
    }

    //chay cau select  goi function roi anh xa sang list
    private List<TicketBooking> queryList(String sql, String keyword) throws SQLException {
        List<TicketBooking> result = new ArrayList<>();
        try (Connection conn = ConnectDB.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (keyword != null) {
                ps.setString(1, keyword);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }
        return result;
    }

    //anh xa mot dong resultset thanh doi tuong TicketBooking
    private TicketBooking mapRow(ResultSet rs) throws SQLException {
        return new TicketBooking(
                rs.getInt("booking_id"),
                rs.getString("movie_title"),
                rs.getString("customer_name"),
                rs.getTimestamp("show_time").toLocalDateTime(),
                rs.getTimestamp("booking_date").toLocalDateTime(),
                rs.getInt("seat_quantity"),
                rs.getString("status")
        );
    }
}
