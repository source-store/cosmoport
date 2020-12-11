package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exceptions.BadRequestException;
import com.space.model.ShipType;
import com.space.model.Ship;

import java.util.Date;
import java.util.List;

public interface ShipService {
    Ship saveShip(Ship ship);

    Ship getShip(Long id);

    Ship createShip(Ship ship) throws BadRequestException;

    Ship updateShip(Ship oldShip, Ship newShip) throws IllegalArgumentException;

    void deleteShip(Ship ship);

    List<Ship> getShips(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating
    );

    List<Ship> sortShips(List<Ship> ships, ShipOrder order);

    List<Ship> getPage(List<Ship> ships, Integer pageNumber, Integer pageSize);

    boolean isShipValid(Ship ship);

    Double computeRating(Ship ship);
}
