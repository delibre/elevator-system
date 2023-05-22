package com.svsytem.elevator;

import com.svsytem.dto.ElevatorDTO;
import com.svsytem.dto.ElevatorSystemDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/elevator-system/api/v1")
public class ElevatorSystemController {
    private final ElevatorSystemService elevatorSystemService;

    public ElevatorSystemController(ElevatorSystemService elevatorSystemService) {
        this.elevatorSystemService = elevatorSystemService;
    }

    @GetMapping("/initialize")
    public ElevatorSystemDTO initialize(@RequestParam int numberOfFloors, @RequestParam int numberOfElevators) {
        return elevatorSystemService.initializeElevatorSystem(numberOfFloors, numberOfElevators);
    }

    @GetMapping("/pickup")
    public ElevatorSystemDTO pickup(@RequestParam int callFloor, @RequestParam int destinationFloor) {
        return elevatorSystemService.pickup(callFloor, destinationFloor);
    }

    @GetMapping("/update")
    public ElevatorSystemDTO update(@RequestParam int id, @RequestParam int currentFloor,
                                    @RequestParam int destinationFloor) {
        return elevatorSystemService.update(id, currentFloor, destinationFloor);
    }

    @GetMapping("/restart")
    public ElevatorSystemDTO update(@RequestParam int id) {
        return elevatorSystemService.restart(id);
    }

    @GetMapping("/step")
    public ElevatorSystemDTO step() {
        return elevatorSystemService.step();
    }

    @GetMapping("/update-full-state")
    public ElevatorSystemDTO updateFullState(@RequestBody ElevatorDTO elevatorFullStateDTO) {
        return elevatorSystemService.updateFullState(elevatorFullStateDTO);
    }
}

