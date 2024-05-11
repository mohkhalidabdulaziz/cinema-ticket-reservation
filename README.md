# Cinema Ticket Reservation Service

## Overview
This project implements a Cinema Ticket Reservation Service using Maven as the build tool. It is structured as a web application and follows similar patterns to the one demonstrated in the tutorial.

## Getting Started
To get started with this project, follow these steps:

1. Clone the repository to your local machine.
2. Ensure you have Maven installed.
3. Copy the `pom.xml` provided in the `service/` directory to your project root.
4. Copy the `SeatReservation.wsdl` and `SeatReservation.xsd` files into the `src/main/webapp/WEB-INF/wsdl` folder.
5. Run the application. The service will be accessible at `http://localhost:8080/WebService_NEPTUN/Cinema`.

## Functionality Overview

The operations of the service have the following responsibilities:

**Init:**
- Initializes the room with the given number of rows and columns.
- Number of rows: 1 <= rows <= 26
- Number of columns: 1 <= columns <= 100
- All the seats are free, and every previous lock or reservation is deleted.
- If the number of rows or columns is outside of the given interval, a CinemaException must be thrown.

**GetAllSeats:**
- Returns all the seats in the room.
- The rows are denoted by consecutive capital letters of the English alphabet starting from the letter ‘A’.
- Columns are denoted by consecutive integer numbers starting from 1.

**GetSeatStatus:**
- Returns the status (free, locked, reserved, sold) of the given seat.
- If the position of the given seat is invalid, a CinemaException must be thrown.

**Lock:**
- Locks count number of seats in the row starting from the given seat moving forward.
- If the locking cannot be performed (e.g., there are not enough remaining seats in the row or there are not enough free seats), a CinemaException must be thrown, and no seats should be locked.
- The operation must return a unique identifier based on which the service can look up the locked seats.

**Unlock:**
- Releases the lock with the given identifier.
- Every seat belonging to this lock must be freed.
- If the identifier for the lock is invalid, a CinemaException must be thrown.
- Unlock does not release reservations; it only releases locks.

**Reserve:**
- Reserves the seats of the lock with the given identifier.
- If the identifier for the lock is invalid, a CinemaException must be thrown.

**Buy:**
- Sells the seats of the lock or reservation with the given identifier.
- If the identifier is invalid, a CinemaException must be thrown.

## Storage
The service persists data between calls. In a real-world scenario, this would typically involve a database. However, for the purposes of this homework, data is stored using static variables.

## Contribution
Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.

## License
This project is licensed under the [MIT License](LICENSE).
