package com.svsytem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class ElevatorSystemDTO {
    private int numberOfFloors;
    private int numberOfElevators;
    private List<ElevatorDTO> elevators;
}


