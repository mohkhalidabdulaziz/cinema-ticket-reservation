package com.soapassignmnet.WebService_JG0GNZ.endpoint;


import com.soapassignmnet.WebService_JG0GNZ.entity.SeatKey;
import jakarta.xml.bind.JAXBElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import hu.bme.iit.soi.hw.seatreservation.*;

import javax.xml.namespace.QName;
import java.util.Map;

import static com.soapassignmnet.WebService_JG0GNZ.services.ICinemaImpl.lockMap;
import static com.soapassignmnet.WebService_JG0GNZ.services.ICinemaImpl.seatMap;

@Endpoint
public class ICinemaEndpoint {

    private static final String NAMESPACE_URI = "http://www.iit.bme.hu/soi/hw/SeatReservation";

    private final ICinema cinemaService;

    @Autowired
    public ICinemaEndpoint(ICinema cinemaService) {
        this.cinemaService = cinemaService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "Init")
    @ResponsePayload
    public JAXBElement<InitResponse> init(@RequestPayload Init init) throws ICinemaInitCinemaException {
        cinemaService.init(init.getRows(), init.getColumns());
        return new JAXBElement<>(new QName(InitResponse.class.getSimpleName()), InitResponse.class, new InitResponse());
    }

    @PayloadRoot(namespace = "http://www.iit.bme.hu/soi/hw/SeatReservation", localPart = "GetAllSeats")
    @ResponsePayload
    public JAXBElement<GetAllSeatsResponse> getAllSeats() throws ICinemaGetAllSeatsCinemaException {
        GetAllSeatsResponse response = new GetAllSeatsResponse();
        response.setGetAllSeatsResult(cinemaService.getAllSeats());
        return new JAXBElement<>(new QName(GetAllSeatsResponse.class.getSimpleName()), GetAllSeatsResponse.class, response);
    }


    @PayloadRoot(namespace = "http://www.iit.bme.hu/soi/hw/SeatReservation", localPart = "GetSeatStatus")
    @ResponsePayload
    public JAXBElement<GetSeatStatusResponse> getSeatStatus(@RequestPayload GetSeatStatus req) throws ICinemaGetSeatStatusCinemaException {
        GetSeatStatusResponse response = new GetSeatStatusResponse();
        response.setGetSeatStatusResult(cinemaService.getSeatStatus(req.getSeat()));
        return new JAXBElement<>(new QName(GetSeatStatusResponse.class.getSimpleName()), GetSeatStatusResponse.class, response);
    }


    @PayloadRoot(namespace = "http://www.iit.bme.hu/soi/hw/SeatReservation", localPart = "Lock")
    @ResponsePayload
    public JAXBElement<LockResponse> lock(@RequestPayload Lock lock) throws ICinemaLockCinemaException {
        // Call the lock method from the cinemaService to perform the locking operation
        LockResponse lockResponse = cinemaService.lock(lock.getSeat(), lock.getCount());

        // Create a JAXBElement containing the LockResponse
        QName qName = new QName("http://www.iit.bme.hu/soi/hw/SeatReservation", "LockResponse");
        JAXBElement<LockResponse> jaxbElement = new JAXBElement<>(qName, LockResponse.class, lockResponse);

        return jaxbElement;
    }


    @PayloadRoot(namespace = "http://www.iit.bme.hu/soi/hw/SeatReservation", localPart = "Unlock")
    @ResponsePayload
    public JAXBElement<UnlockResponse> unlock(@RequestPayload Lock lock) throws ICinemaUnlockCinemaException {
        // Extract row and column from the Seat object
        String row = lock.getSeat().getRow();
        String column = lock.getSeat().getColumn();

        // Construct a SeatKey object from row and column
        SeatKey seatKey = new SeatKey(row, column);

        // Find the lockId associated with the provided seat from seatMap
        String lockId = null;
        for (Map.Entry<String, SeatKey> entry : lockMap.entrySet()) {
            if (entry.getValue().equals(seatKey)) {
                lockId = entry.getKey();
                break;
            }
        }

        // If lockId is null, throw an exception
        if (lockId == null) {
            throw new ICinemaUnlockCinemaException("No lock found for the provided seat.", new CinemaException());
        }

        // Call the unlock method with the found lockId
        cinemaService.unlock(lockId);

        return new JAXBElement<>(new QName(UnlockResponse.class.getSimpleName()), UnlockResponse.class, new UnlockResponse());
    }


    @PayloadRoot(namespace = "http://www.iit.bme.hu/soi/hw/SeatReservation", localPart = "Reserve")
    @ResponsePayload
    public JAXBElement<ReserveResponse> reserve(@RequestPayload Lock lock) throws ICinemaReserveCinemaException {
        // Extract row and column from the Seat object
        String row = lock.getSeat().getRow();
        String column = lock.getSeat().getColumn();

        // Construct a SeatKey object from row and column
        SeatKey seatKey = new SeatKey(row, column);

        // Find the lockId associated with the provided seat from seatMap
        String lockId = null;
        for (Map.Entry<String, SeatKey> entry : lockMap.entrySet()) {
            if (entry.getValue().equals(seatKey)) {
                lockId = entry.getKey();
                break;
            }
        }

        // If lockId is null, throw an exception
        if (lockId == null) {
            throw new ICinemaReserveCinemaException("No lock found for the provided seat.", new CinemaException());
        }

        // Call the reserve method with the found lockId
        cinemaService.reserve(lockId);

        return new JAXBElement<>(new QName(ReserveResponse.class.getSimpleName()), ReserveResponse.class, new ReserveResponse());
    }


    @PayloadRoot(namespace = "http://www.iit.bme.hu/soi/hw/SeatReservation", localPart = "Buy")
    @ResponsePayload
    public JAXBElement<BuyResponse> buy(@RequestPayload Lock lock) throws ICinemaBuyCinemaException {
        // Extract row and column from the Seat object
        String row = lock.getSeat().getRow();
        String column = lock.getSeat().getColumn();

        // Construct a SeatKey object from row and column
        SeatKey seatKey = new SeatKey(row, column);

        // Check if the seat is locked or reserved
        SeatStatus status = seatMap.get(seatKey);
        if (status != SeatStatus.LOCKED && status != SeatStatus.RESERVED) {
            throw new ICinemaBuyCinemaException("Seat is not locked or reserved.", new CinemaException());
        }

        // Find the lockId associated with the provided seat from seatMap
        String lockId = null;
        for (Map.Entry<String, SeatKey> entry : lockMap.entrySet()) {
            if (entry.getValue().equals(seatKey)) {
                lockId = entry.getKey();
                break;
            }
        }

        // If lockId is null, throw an exception
        if (lockId == null) {
            throw new ICinemaBuyCinemaException("No lock found for the provided seat.", new CinemaException());
        }

        // Call the buy method with the found lockId
        cinemaService.buy(lockId);

        return new JAXBElement<>(new QName(BuyResponse.class.getSimpleName()), BuyResponse.class, new BuyResponse());
    }

}
