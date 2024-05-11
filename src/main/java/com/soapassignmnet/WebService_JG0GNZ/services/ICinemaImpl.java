package com.soapassignmnet.WebService_JG0GNZ.services;

import com.soapassignmnet.WebService_JG0GNZ.entity.SeatKey;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.UUID;
import hu.bme.iit.soi.hw.seatreservation.*;

import java.util.HashMap;
import java.util.Map;

@WebService
@Service
public class ICinemaImpl implements ICinema {


    public static Map<SeatKey, SeatStatus> seatMap = new HashMap<>();
    public static Map<String, SeatKey> lockMap = new HashMap<>();


    @Override
    public void init(int rows, int columns) throws ICinemaInitCinemaException {
        // Initialize the room with the given number of rows and columns
        if (rows < 1 || rows > 26 || columns < 1 || columns > 100) {
            CinemaException exception = new CinemaException();
            exception.setErrorCode(100); // You can define error codes as per your requirement
            exception.setErrorMessage("Invalid number of rows or columns.");
            throw new ICinemaInitCinemaException("Invalid number of rows or columns.", exception);
        }

        // Clear previous data
        seatMap.clear();
        lockMap.clear();

        // Hardcoded initialization of seats with status set to FREE
        for (char row = 'A'; row < 'A' + rows; row++) {
            for (int col = 1; col <= columns; col++) {
                String rowStr = String.valueOf(row);
                String colStr = String.valueOf(col);
                SeatKey seatKey = new SeatKey(rowStr, colStr);
                seatMap.put(seatKey, SeatStatus.FREE);
            }
        }
    }


    @Override
    public ArrayOfSeat getAllSeats() throws ICinemaGetAllSeatsCinemaException {
        try {
            ArrayOfSeat arrayOfSeat = new ArrayOfSeat();

            // Iterate through each seat in the seatMap and add it to the array
            for (Map.Entry<SeatKey, SeatStatus> entry : seatMap.entrySet()) {
                Seat seat = new Seat();
                seat.setRow(entry.getKey().getRow());
                seat.setColumn(entry.getKey().getColumn());
                arrayOfSeat.getSeat().add(seat);
            }

            return arrayOfSeat;
        } catch (Exception e) {
            // If an exception occurs, wrap it in ICinemaGetAllSeatsCinemaException
            throw new ICinemaGetAllSeatsCinemaException("Error occurred while retrieving all seats.", new CinemaException());
        }
    }


    @Override
    public SeatStatus getSeatStatus(Seat seat) throws ICinemaGetSeatStatusCinemaException {
        // Create a SeatKey object from the provided Seat object
        SeatKey seatKey = new SeatKey(seat.getRow(), seat.getColumn());

        // Check if the seat exists in the seatMap
        if (!seatMap.containsKey(seatKey)) {
            // If the seat doesn't exist, throw a CinemaException indicating invalid position
            CinemaException exception = new CinemaException();
            exception.setErrorCode(101); // You can define error codes as per your requirement
            exception.setErrorMessage("Bad seat number.");
            throw new ICinemaGetSeatStatusCinemaException("Bad seat number.", exception);
        }

        // If the seat exists, return its status from the seatMap
        return seatMap.get(seatKey);
    }


    @Override
    public LockResponse lock(Seat seat, int count) throws ICinemaLockCinemaException {
        // Create a LockResponse object with the default lock result "lock0"
        LockResponse lockResponse = new LockResponse();
        lockResponse.setLockResult("lock0");

        // Validate if the seat exists in the seatMap
        SeatKey seatKey = new SeatKey(seat.getRow(), seat.getColumn());
        if (!seatMap.containsKey(seatKey)) {
            throw new ICinemaLockCinemaException("Invalid seat provided.", new CinemaException());
        }

        // Check if the provided seat is already locked
        if (seatMap.get(seatKey) != SeatStatus.FREE) {
            throw new ICinemaLockCinemaException("The provided seat is not available for locking.", new CinemaException());
        }

        // Get the row and column of the provided seat
        char row = seat.getRow().charAt(0);
        int col = Integer.parseInt(seat.getColumn());

        // Check if there are enough remaining seats in the row
        if (col + count - 1 > seatMap.size()) {
            throw new ICinemaLockCinemaException("Not enough remaining seats in the row.", new CinemaException());
        }

        // Generate a unique identifier for the lock operation
        String lockId = UUID.randomUUID().toString();

        // Lock the seats starting from the provided seat
        for (int i = 0; i < count; i++) {
            // Create a SeatKey object for the current seat
            SeatKey currentSeatKey = new SeatKey(String.valueOf(row), String.valueOf(col + i));

            // Add the seat to the lockMap and update its status to locked
            lockMap.put(lockId, currentSeatKey);
            seatMap.put(currentSeatKey, SeatStatus.LOCKED);
        }

        // Update the lock result to the generated lock identifier
        lockResponse.setLockResult(lockId);

        return lockResponse;
    }







    @Override
    public void unlock(String lockId) throws ICinemaUnlockCinemaException {
        try {
            // Check if the lockId is valid (exists in lockMap)
            if (!lockMap.containsKey(lockId)) {
                throw new ICinemaUnlockCinemaException("Invalid lock identifier provided.", new CinemaException());
            }

            // Create an iterator to safely remove elements while iterating
            Iterator<Map.Entry<String, SeatKey>> iterator = lockMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, SeatKey> entry = iterator.next();
                String currentLockId = entry.getKey();
                SeatKey seatKey = entry.getValue();

                // Check if the current lockId matches the provided lockId
                if (currentLockId.equals(lockId)) {
                    // Remove the seat from the lockMap and update its status to FREE
                    iterator.remove(); // Safe removal
                    seatMap.put(seatKey, SeatStatus.FREE);
                }
            }
        } catch (Exception e) {
            // If an exception occurs, wrap it in ICinemaUnlockCinemaException
            throw new ICinemaUnlockCinemaException("Error occurred while unlocking seats.", new CinemaException());
        }
    }


    @Override
    public void reserve(String lockId) throws ICinemaReserveCinemaException {
        try {
            // Check if the lockId is valid
            if (!lockMap.containsKey(lockId)) {
                throw new ICinemaReserveCinemaException("Invalid lock identifier.", new CinemaException());
            }

            // Iterate through each seat in the locked range and update its status to RESERVED
            String[] parts = lockId.split(",");
            for (String part : parts) {
                SeatKey seatKey = lockMap.get(part);
                if (seatKey != null) {
                    seatMap.put(seatKey, SeatStatus.RESERVED);
                }
            }

            // Remove the lockId from the lockMap
           // lockMap.remove(lockId);
        } catch (Exception e) {
            // If an exception occurs, wrap it in ICinemaReserveCinemaException
            throw new ICinemaReserveCinemaException("Error occurred while reserving seats.", new CinemaException());
        }
    }



    @Override
    public void buy(String lockId) throws ICinemaBuyCinemaException {
        try {
            // Check if the lockId is valid
            if (!lockMap.containsKey(lockId)) {
                CinemaException exception = new CinemaException();
                exception.setErrorCode(101); // You can define error codes as per your requirement
                exception.setErrorMessage("Invalid lock identifier.");
                throw new ICinemaBuyCinemaException("Invalid lock identifier.", exception);
            }

            // Iterate through each seat in the locked range and update its status to SOLD
            String[] parts = lockId.split(",");
            for (String part : parts) {
                SeatKey seatKey = lockMap.get(part);
                if (seatKey != null) {
                    SeatStatus status = seatMap.get(seatKey);
                    // Check if the seat is locked or reserved
                    if (status == SeatStatus.LOCKED || status == SeatStatus.RESERVED) {
                        seatMap.put(seatKey, SeatStatus.SOLD);
                    } else {
                        // If the seat is neither locked nor reserved, throw an exception
                        throw new ICinemaBuyCinemaException("Error occurred while buying seats. Seat is not locked or reserved.", new CinemaException());
                    }
                } else {
                    // If the seat key associated with the lockId is not found, throw an exception
                    throw new ICinemaBuyCinemaException("Error occurred while buying seats. Seat key not found.", new CinemaException());
                }
            }

            // Remove the lockId from the lockMap
            lockMap.remove(lockId);
        } catch (Exception e) {
            // If an exception occurs, wrap it in ICinemaBuyCinemaException
            throw new ICinemaBuyCinemaException("Error occurred while buying seats.", new CinemaException());
        }
    }




}
