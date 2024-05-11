package com.soapassignmnet.WebService_JG0GNZ;


import com.soapassignmnet.WebService_JG0GNZ.services.ICinemaImpl;
import org.junit.jupiter.api.BeforeEach;
import hu.bme.iit.soi.hw.seatreservation.*;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class ICinemaImplTest {

    private ICinemaImpl cinema;

    @BeforeEach
    void setUp() {
        cinema = new ICinemaImpl();
    }

    @Test
    void testInit() {
        // Create an instance of ICinemaImpl
        ICinemaImpl cinema = new ICinemaImpl();

        // Test with valid number of rows and columns
        try {
            cinema.init(26, 100); // Test with 5 rows and 10 columns
        } catch (ICinemaInitCinemaException e) {
            fail("Exception thrown for valid input: " + e.getMessage());
        }

        // Test with invalid number of rows and columns
        assertThrows(ICinemaInitCinemaException.class, () -> cinema.init(30, 10)); // Test with 30 rows (invalid)
        assertThrows(ICinemaInitCinemaException.class, () -> cinema.init(5, 110)); // Test with 110 columns (invalid)
    }






























}
