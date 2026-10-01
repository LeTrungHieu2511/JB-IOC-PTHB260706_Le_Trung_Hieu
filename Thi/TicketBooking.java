package vn.edu.rikkei.Thi;

import java.time.LocalDateTime;

//entity anh xa bang ticket_booking
public class TicketBooking {
    private final int bookingId;
    private String movieTitle;
    private String customerName;
    private LocalDateTime showTime;
    private LocalDateTime bookingDate;
    private int seatQuantity;
    private String status;

    //contructor day du tham so
    public TicketBooking ( int bookingId, String movieTitle, String customerName, LocalDateTime showTime, LocalDateTime bookingDate, int seatQuantity, String status) {
        this.bookingId = bookingId;
        this.movieTitle = movieTitle;
        this.customerName = customerName;
        this.showTime = showTime;
        this.bookingDate = bookingDate;
        this.seatQuantity = seatQuantity;
        this.status = status;
    }

    //getter
    public int getBookingId() {
        return bookingId;
    }
    public String getMovieTitle() {
        return movieTitle;
    }
    public String getCustomerName() {
        return customerName;
    }
    public LocalDateTime getShowTime() {
        return showTime;
    }
    public  LocalDateTime getBookingDate() {
        return bookingDate;
    }
    public  int getSeatQuantity() {
        return seatQuantity;
    }
    public String getStatus() {
        return status;
    }

    //setter
    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }
    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }
    public void setSeatQuantity(int seatQuantity ) {
        this.seatQuantity = seatQuantity;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    //chuoi mo ta phieu dat ve
    @Override
    public String toString() {
        return "TicketBooking{BookingID=" + bookingId + ", movieTitle=" + movieTitle + ",customerName=" + customerName + ", showTime=" + showTime + ", bookingDate=" + bookingDate + ", seatQuantity=" + seatQuantity
                + ", status=" + status + "}";
    }
}
