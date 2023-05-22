package com.svsytem.elevator;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class Elevator {
    private int id;
    private int currentFloor;
    private int direction;
    private Set<Integer> destinationFloors;

    public Elevator(int id, int currentFloor, int direction, Set<Integer> destinationFloors) {
        this.id = id;
        this.currentFloor = currentFloor;
        this.direction = direction;
        this.destinationFloors = destinationFloors;
    }
}

