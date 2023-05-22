package com.svsytem.dto;

import java.util.List;

public record ElevatorDTO(int id, int currentFloor, int direction, List<Integer> destinationFloors) {
}
