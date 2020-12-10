package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.ShipType;
import com.space.model.SpaceShip;

import java.util.Date;
import java.util.List;

public interface ShipActions {
    SpaceShip saveShip(SpaceShip ship);

    SpaceShip getShip(Long id);

    SpaceShip updateShip(SpaceShip oldShip, SpaceShip newShip) throws IllegalArgumentException;

    void deleteShip(SpaceShip ship);

    List<SpaceShip> getShips(
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

    List<SpaceShip> sortShips(List<SpaceShip> ships, ShipOrder order);

    List<SpaceShip> getPage(List<SpaceShip> ships, Integer pageNumber, Integer pageSize);

    boolean isShipValid(SpaceShip ship);

    double computeRating(double speed, boolean isUsed, Date prod);
}
