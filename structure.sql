CREATE DATABASE db_cinema_booking;

-- bang phieu dat ve
CREATE TABLE IF NOT EXISTS ticket_bookings (
    booking_id SERIAL PRIMARY KEY,
    movie_title VARCHAR(150) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    show_time TIMESTAMP NOT NULL,
    booking_date TIMESTAMP NOT NULL,
    seat_quantity INT NOT NULL,
    status VARCHAR(30) NOT NULL
);

-- function thuc hien lay danh sach tat ca cac phieu dat ve
CREATE OR REPLACE FUNCTION fn_get_all_bookings()
    RETURNS TABLE (
        booking_id INT, movie_title VARCHAR(150), customer_name VARCHAR(100),
        show_time TIMESTAMP, booking_date TIMESTAMP, seat_quantity INT, status VARCHAR(30)
                  )
    LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY SELECT tb.booking_id, tb.movie_title, tb.customer_name, tb.show_time, tb.booking_date, tb.seat_quantity, tb.status
        FROM ticket_bookings tb
        ORDER BY tb.booking_id;
END;
$$;

-- procedure thuc hien them moi motmphieu dat ve
CREATE OR REPLACE PROCEDURE sp_add_booking(
    p_movie_title VARCHAR, p_customer_name VARCHAR, p_show_time TIMESTAMP,
    p_booking_date TIMESTAMP, p_seat_quantity INT, p_status VARCHAR
)
    LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO ticket_bookings (movie_title, customer_name, show_time, booking_date, seat_quantity, status)
    VALUES (p_movie_title, p_customer_name, p_show_time, p_booking_date, p_seat_quantity, p_status);
END;
$$;

-- function thuc hien lay danh sach phieu dat ve theo ten khach hang customer_name
CREATE OR REPLACE FUNCTION fn_find_bookings_by_customer(p_customer_name VARCHAR)
    RETURNS TABLE (
        booking_id INT, movie_title VARCHAR(150), customer_name VARCHAR(100),
        show_time TIMESTAMP, booking_date TIMESTAMP, seat_quantity INT, status VARCHAR(30)
                  )
    LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT tb.booking_id, tb.movie_title, tb.customer_name, tb.show_time, tb.booking_date, tb.seat_quantity, tb.status
        FROM ticket_bookings tb
        WHERE tb.customer_name ILIKE '%' || p_customer_name || '%'
        ORDER BY tb.booking_id;
END;
$$;

-- procedure thuc hien cap nhat thong tin phieu dat ve theo booking_id
CREATE OR REPLACE PROCEDURE sp_update_booking(
    p_booking_id INT, p_movie_title VARCHAR, p_customer_name VARCHAR,
    p_show_time TIMESTAMP, p_booking_date TIMESTAMP, p_seat_quantity INT, p_status VARCHAR
)
    LANGUAGE plpgsql AS $$
BEGIN
    UPDATE ticket_bookings
    SET movie_title = p_movie_title,
        customer_name = p_customer_name,
        show_time = p_show_time,
        booking_date = p_booking_date,
        seat_quantity = p_seat_quantity,
        status = p_status
    WHERE booking_id = p_booking_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy phiếu đặt vé có mã %', p_booking_id;
    END IF;
END;
$$;

-- procedure thuc hien xoa phieu dat ve theo booking_id
CREATE OR REPLACE PROCEDURE sp_delete_booking(p_booking_id INT)
    LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM ticket_bookings WHERE booking_id = p_booking_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy phiếu đặt vé có mã %', p_booking_id;
    END IF;
END;
$$;

-- function thuc hien tim kiem phieu dat ve theo ten phim movie_title(tim gan dung, khong phan biet hoa thuong)
CREATE OR REPLACE FUNCTION fn_search_bookings_by_movie(p_movie_title VARCHAR)
    RETURNS TABLE (
        booking_id INT, movie_title VARCHAR(150), customer_name VARCHAR(100),
        show_time TIMESTAMP, booking_date TIMESTAMP, seat_quantity INT, status VARCHAR(30)
                  )
    LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT tb.booking_id, tb.movie_title, tb.customer_name, tb.show_time,
               tb.booking_date, tb.seat_quantity, tb.status
        FROM ticket_bookings tb
        WHERE tb.movie_title ILIKE '%' || p_movie_title || '%'
        ORDER BY tb.booking_id;
END;
$$;

-- du lieu mau
INSERT INTO ticket_bookings (movie_title, customer_name, show_time, booking_date, seat_quantity, status)
VALUES
    ('Avengers: Endgame', 'Nguyễn Văn An', '2026-10-05 19:30', '2026-10-01 09:15', 2, 'Booked'),
    ('Inception', 'Trần Khánh Vy', '2026-10-06 21:00', '2026-10-02 14:00', 4, 'CheckedIn'),
    ('Interstellar', 'Lê Trường Giang', '2026-10-07 18:00', '2026-10-03 10:30', 3, 'Cancelled'),
    ('Avatar', 'Phạm Minh Tuấn', '2026-10-08 20:15', '2026-10-04 16:45', 5, 'Booked'),
    ('Your Name', 'Hoàng Thu Hà', '2026-10-09 17:30', '2026-10-05 08:00', 1, 'Booked');