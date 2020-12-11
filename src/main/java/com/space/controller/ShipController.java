package com.space.controller;

import com.space.exceptions.BadRequestException;
import com.space.exceptions.BadShipIdException;
import com.space.exceptions.ShipErrorResponse;
import com.space.exceptions.ShipNotFoundException;
import com.space.model.ShipType;
import com.space.model.Ship;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class ShipController {

    private ShipService shipActions;

    public ShipController() {
    }

    @Autowired
    public ShipController(ShipService shipActions) {
        this.shipActions = shipActions;
    }

    @GetMapping("/ships")
    public List<Ship> getAllShips(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", required = false) ShipOrder order,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        final List<Ship> ships = shipActions.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating);

        final List<Ship> sortedShips = shipActions.sortShips(ships, order);

        return shipActions.getPage(sortedShips, pageNumber, pageSize);
    }

    @GetMapping("/ships/count")
    public Integer getShipsCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating
    ) {
        return shipActions.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating).size();
    }

    @PostMapping("/ships")
    @ResponseBody
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        if (!shipActions.isShipValid(ship)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (ship.getUsed() == null) ship.setUsed(false);
        ship.setSpeed(ship.getSpeed());
        final double rating = shipActions.computeRating(ship);
        ship.setRating(rating);

        final Ship savedShip = shipActions.saveShip(ship);

        return new ResponseEntity<>(savedShip, HttpStatus.OK);
    }

    @GetMapping("/ships/{id}")
    @ResponseBody
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") String pathId) {
        final Long id = convertIdToLong(pathId);
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final Ship ship = shipActions.getShip(id);
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @PostMapping("/ships/{id}")
    @ResponseBody
    public ResponseEntity<Ship> updateShip(
            @PathVariable(value = "id") String pathId,
            @RequestBody Ship ship
    ) {
        final ResponseEntity<Ship> entity = getShip(pathId);
        final Ship savedShip = entity.getBody();
        if (savedShip == null) {
            return entity;
        }

        final Ship result;
        try {
            result = shipActions.updateShip(savedShip, ship);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/ships/{id}")
    public ResponseEntity<Ship> deleteShip(@PathVariable(value = "id") String pathId) {
        final ResponseEntity<Ship> entity = getShip(pathId);
        final Ship savedShip = entity.getBody();
        if (savedShip == null) {
            return entity;
        }
        shipActions.deleteShip(savedShip);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Long convertIdToLong(String pathId) {
        if (pathId == null) {
            return null;
        } else try {
            return Long.parseLong(pathId);
        } catch (NumberFormatException e) {
            return null;
        }
    }


    @ExceptionHandler
    public ResponseEntity<ShipErrorResponse> handleShipNotFoundException(ShipNotFoundException exc) {
        ShipErrorResponse error = new ShipErrorResponse(HttpStatus.NOT_FOUND.value(), "No ship whith this id");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ShipErrorResponse> handleBadShipIdException(BadShipIdException exc) {
        ShipErrorResponse error = new ShipErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invaliв id number");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ShipErrorResponse> handleShipBadRequestException(BadRequestException exc) {
        ShipErrorResponse error = new ShipErrorResponse(HttpStatus.BAD_REQUEST.value(), "Unknown error");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}