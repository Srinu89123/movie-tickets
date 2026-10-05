CREATE DATABASE movie_ticket_booking;

USE movie_ticket_booking;

CREATE TABLE movies (
    movie_id INT PRIMARY KEY AUTO_INCREMENT,
    movie_name VARCHAR(100),
    language VARCHAR(50),
    duration INT
);

CREATE TABLE theaters (
    theater_id INT PRIMARY KEY AUTO_INCREMENT,
    theater_name VARCHAR(100),
    city VARCHAR(100)
);

CREATE TABLE shows (
    show_id INT PRIMARY KEY AUTO_INCREMENT,
    movie_id INT,
    theater_id INT,
    show_time VARCHAR(50),
    total_seats INT,
    available_seats INT,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id),
    FOREIGN KEY (theater_id) REFERENCES theaters(theater_id)
);

CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    show_id INT,
    customer_name VARCHAR(100),
    seat_number INT,
    ticket_price DOUBLE,
    booking_status VARCHAR(20),
    FOREIGN KEY (show_id) REFERENCES shows(show_id)
);