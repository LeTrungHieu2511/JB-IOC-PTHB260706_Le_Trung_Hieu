package vn.edu.rikkei.Thi;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String[] STATUSES = {"Booked", "Cancelled", "CheckedIn"};
    private static final String ROW_FORMAT = "%-5s %-28s %-22s %-17s %-17s %-6s %-10s%n";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);
    private static final Scanner scanner = new Scanner(System.in);
    private static final TicketBookingDAO dao = new TicketBookingDAO();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = inputMenuChoice();
            try {
                switch (choice) {
                    case 1 -> showAll();
                    case 2 -> addBooking();
                    case 3 -> updateBooking();
                    case 4 -> deleteBooking();
                    case 5 -> searchByCustomer();
                    case 6 -> searchByMovie();
                    case 7 -> running = false;
                    default -> System.out.println("[LỖI] Lựa chọn không hợp lệ, vui lòng chọn từ 1 đến 7!");
                }
            } catch (SQLException | RuntimeException e) {
                System.out.println("[LỖI] " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Đã thoát chương trình.");
    }

    //in menu
    private static void printMenu() {
        System.out.println();
        System.out.println("=".repeat(58));
        System.out.println("              CINEMA TICKET BOOKING MANAGEMENT");
        System.out.println("=".repeat(58));
        System.out.println("""
                1.Danh sách tất cả phiếu đặt vé
                2.Thêm mới phiếu đặt vé
                3.Cập nhật thông tin phiếu đặt vé
                4.Xóa phiếu đặt vé
                5.Tìm kiếm phiếu đặt vé theo tên khách hàng
                6.Tìm kiếm phiếu đặt vé theo tên phim
                7.Thoát
                Lựa chọn của bạn :""");
    }

    //doc lua chon menu, tra ve -1 neu khong phai so
    private static int inputMenuChoice() {
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            choice = -1;
        }

        return choice;
    }

    //chuc nang 1
    private static void showAll() throws SQLException {
        printBookings("DANH SÁCH TẤT CẢ PHIẾU ĐẶT VÉ", dao.getAll());
    }

    //chuc nang 2
    private static void addBooking() throws SQLException {
        System.out.println("--- THÊM MỚI PHIẾU ĐẶT VÉ ---");
        String movieTitle = inputText("Tên phim", 150, null);
        String customerName = inputText("Tên khách hàng", 100, null);
        LocalDateTime bookingDate = inputDateTime("Ngày đặt vé", null, null);
        LocalDateTime showTime = inputDateTime("Suất chiếu", null, bookingDate);
        int seatQuantity = inputSeats(null);
        String status = inputStatus(null);
        dao.addBooking(new TicketBooking(0, movieTitle, customerName, showTime, bookingDate, seatQuantity, status));
        System.out.println("[THÀNH CÔNG] Đã thêm phiếu đặt vé mới!");
    }

    //chuc nang 3
    private static void updateBooking() throws SQLException {
        System.out.println("--- CẬP NHẬT PHIẾU ĐẶT VÉ ---");
        int id = inputId("Nhập mã phiếu cần cập nhật");
        TicketBooking old = findById(id);
        if (old == null) {
            System.out.println("[LỖI] Không tìm thấy phiếu đặt vé có mã " + id + "!");
            return;
        }


        printBookings("THÔNG TIN PHIẾU HIỆN TẠI", List.of(old));
        System.out.println("(Nhấn Enter để giữ nguyên giá trị hiện tại, mã phiếu không thể sửa)");
        String movieTitle = inputText("Tên phim", 150, old.getMovieTitle());
        String customerName = inputText("Tên khách hàng", 100, old.getCustomerName());
        LocalDateTime bookingDate = inputDateTime("Ngày đặt vé", old.getBookingDate(), null);
        LocalDateTime showTime = inputDateTime("Suất chiếu", old.getShowTime(), bookingDate);
        int seatQuantity = inputSeats(old.getSeatQuantity());
        String status = inputStatus(old.getStatus());
        dao.updateBooking(new TicketBooking(id, movieTitle, customerName, showTime, bookingDate, seatQuantity, status));
        System.out.println("[THÀNH CÔNG] Đã cập nhật phiếu đặt vé " + id + "!");
    }

    //chuc nang 4
    private static void deleteBooking() throws SQLException {
        System.out.println("--- XÓA PHIẾU ĐẶT VÉ ---");
        int id = inputId("Nhập mã phiếu cần xóa");
        TicketBooking booking = findById(id);
        if (booking == null) {
            System.out.println("[LỖI] Không tìm thấy phiếu đặt vé có mã " + id + "!");
            return;
        }

        printBookings("PHIẾU SẼ BỊ XÓA", List.of(booking));
        if (confirm("Bạn có chắc chắn muốn xóa phiếu này? (Y/N): ")) {
            dao.deleteBooking(id);
            System.out.println("[THÀNH CÔNG] Đã xóa phiếu đặt vé " + id + "!");
        } else {
            System.out.println("Đã hủy thao tác xóa.");
        }
    }

    //chuc nang 5
    private static void searchByCustomer() throws SQLException {
        String keyword = inputText("Nhập tên khách hàng cần tìm", 100, null);
        printBookings("KẾT QUẢ TÌM THEO KHÁCH HÀNG: " + keyword, dao.findByCustomer(keyword));
    }

    //chuc nang 6
    private static void searchByMovie() throws SQLException {
        String keyword = inputText("Nhập tên phim cần tìm", 150, null);
        printBookings("KẾT QUẢ TÌM THEO TÊN PHIM: " + keyword, dao.searchByMovie(keyword));
    }

    //tim phieu theo ma lay tu CSDL, tra ve null neu khong co
    private static TicketBooking findById(int id) throws SQLException {
        TicketBooking found = null;

        for (TicketBooking booking : dao.getAll()) {
            if (booking.getBookingId() == id) {
                found = booking;
            }
        }
        return found;
    }

    //in danh sach cot kem tong so phieu
    private static void printBookings(String title, List<TicketBooking> bookings) {
        System.out.println();
        System.out.println("=== " + title + " ===");
        System.out.printf(ROW_FORMAT, "ID", "TÊN PHIM", "KHÁCH HÀNG", "SUẤT CHIẾU", "NGÀY ĐẶT", "GHẾ", "TRẠNG THÁI");
        System.out.println("-".repeat(115));
        if (bookings.isEmpty()) {
            System.out.println("Không có dữ liệu phù hợp.");
        }
        for (TicketBooking b : bookings) {
            System.out.printf(ROW_FORMAT,
                    b.getBookingId(),
                    b.getMovieTitle(),
                    b.getCustomerName(),
                    formatDateTime(b.getShowTime()),
                    formatDateTime(b.getBookingDate()),
                    b.getSeatQuantity(),
                    b.getStatus());
        }
        System.out.println("Tổng số phiếu: " + bookings.size());
    }

    //in cau hoi roi doc 1 dong nhap
    private static String readLine(String label, String currentText) {
        if (currentText == null) {
            System.out.print(label + ": ");
        } else {
            System.out.print(label + " [" + currentText + "]: ");
        }
        return scanner.nextLine().trim();
    }

    //nhap chuoi khong rong, khong vuot do dai toi da
    private static String inputText(String label, int maxLength, String current) {
        while (true) {
            String input = readLine(label, current);
            if (input.isEmpty() && current != null) {
                return current;
            }
            if (input.isEmpty()) {
                System.out.println("[LỖI] " + label + " không được để trống!");
            } else if (input.length() > maxLength) {
                System.out.println("[LỖI] " + label + " tối đa " + maxLength + " ký tự!");
            } else {
                return input;
            }
        }
    }

    //nhap ma phieu, phai la so >0
    private static int inputId(String label) {
        while (true) {
            String input = readLine(label, null);
            try {
                int id = Integer.parseInt(input);
                if (id > 0) {
                    return id;
                }
                System.out.println("[LỖI] Mã phiếu phải là số nguyên dương!");
            } catch (NumberFormatException e) {
                System.out.println("[LỖI] Mã phiếu phải là số nguyên!");
            }
        }
    }

    //nhap ngay gio dung dinh dang, khong duoc truoc moc min
    private static LocalDateTime inputDateTime(String label, LocalDateTime current, LocalDateTime min) {
        String currentText = formatDateTime(current);
        while (true) {
            String input = readLine(label + " (yyyy-MM-dd HH:mm)", currentText);
            LocalDateTime value;

            if (input.isEmpty() && current != null) {
                value = current;
            } else {
                try {
                    value = LocalDateTime.parse(input, FORMATTER);
                } catch (DateTimeParseException e) {
                    System.out.println("[LỖI] Sai định dạng hoặc ngày giờ không tồn tại (mẫu: 2026-10-05 19:30)!");
                    continue;
                }
            }

            if (min != null && value.isBefore(min)) {
                System.out.println("[LỖI] " + label + " không được trước " + formatDateTime(min) + "!");
            } else {
                return value;
            }
        }
    }

    //nhap so ghe la so nguyen duong
    private static int inputSeats(Integer current) {
        String currentText = null;
        if (current != null) {
            currentText = String.valueOf(current);
        }

        while (true) {
            String input = readLine("Số lượng ghế", currentText);
            if (input.isEmpty() && current != null) {
                return current;
            }
            try {
                int seats = Integer.parseInt(input);
                if (seats >= 1 ) {
                    return seats;
                }
                System.out.println("[LỖI] Số ghế phải từ lớn hơn 0!");
            } catch (NumberFormatException e) {
                System.out.println("[LỖI] Số ghế phải là số nguyên!");
            }
        }
    }

    //nhap trang thai thuoc Booked/Cancelled/CheckedIn khong phan biet hoa thuong
    private static String inputStatus(String current) {
        while (true) {
            String input = readLine("Trạng thái (Booked/Cancelled/CheckedIn)", current);
            if (input.isEmpty() && current != null) {
                return current;
            }
            for (String status : STATUSES) {
                if (status.equalsIgnoreCase(input)) {
                    return status;
                }
            }
            System.out.println("[LỖI] Trạng thái chỉ được là Booked, Cancelled hoặc CheckedIn!");
        }
    }

    //hoi xac nhan Yes No, lap lai cho den khi hop le
    private static boolean confirm(String question) {
        while (true) {
            System.out.print(question);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Y")) {
                return true;
            }
            if (input.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.println("[LỖI] Vui lòng nhập Y hoặc N!");
        }
    }

    //doi ngay gio sang chuoi, tra ve null neu khong co gia tri
    private static String formatDateTime(LocalDateTime dateTime) {
        String text = null;
        if (dateTime != null) {
            text = dateTime.format(FORMATTER);
        }
        return text;
    }
}