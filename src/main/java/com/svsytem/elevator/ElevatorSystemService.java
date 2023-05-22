package com.svsytem.elevator;

import com.svsytem.dto.ElevatorDTO;
import com.svsytem.dto.ElevatorSystemDTO;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Getter
public class ElevatorSystemService {
    private int numberOfFloors;
    private int numberOfElevators;
    private List<Elevator> elevators;

    public ElevatorSystemDTO initializeElevatorSystem(int numberOfFloors, int numberOfElevators) {
        this.numberOfFloors = numberOfFloors;
        this.numberOfElevators = numberOfElevators;
        this.elevators = new ArrayList<>();
        for (int i = 1; i <= numberOfElevators; i++) {
            Elevator elevator = new Elevator(i, 0, 0, new HashSet<>());
            elevators.add(elevator);
        }

        return createElevatorSystemDTO(elevators);
    }

    public ElevatorSystemDTO pickup(int callFloor, int destinationFloor) {
        Elevator closestElevator = getClosestElevator(callFloor, destinationFloor);

        if (closestElevator != null) {
            closestElevator.getDestinationFloors().add(callFloor);
            closestElevator.getDestinationFloors().add(destinationFloor);

            int currentFloor = closestElevator.getCurrentFloor();
            int destinationDirection;

            if (currentFloor != callFloor) {
                destinationDirection = Integer.compare(callFloor - currentFloor, 0);
            } else {
                destinationDirection = Integer.compare(destinationFloor - currentFloor, 0);
            }

            if (destinationDirection != closestElevator.getDirection()) {
                closestElevator.setDirection(destinationDirection);
            }

            closestElevator.setDestinationFloors(new LinkedHashSet<>(sort(closestElevator.getDestinationFloors(), closestElevator.getCurrentFloor())));

            if (currentFloor == callFloor) {
                closestElevator.setDirection(destinationDirection);
            }
        }


        return createElevatorSystemDTO(elevators);
    }

    private Elevator getClosestElevator(int callFloor, int destinationFloor) {
        Elevator closestElevator = null;
        int minDistance = Integer.MAX_VALUE;
        int desiredDirection = Integer.compare(destinationFloor - callFloor, 0);

        for (Elevator elevator : elevators) {
            int distance = Math.abs(elevator.getCurrentFloor() - callFloor);

            if (isIdle(elevator) ||
                    (goesUp(elevator) && destinationFloor >= callFloor && elevator.getCurrentFloor() <= callFloor) ||
                    (goesDown(elevator) && destinationFloor <= callFloor && elevator.getCurrentFloor() >= callFloor)) {

                if ((distance < minDistance || desiredDirection == elevator.getDirection()) && elevator.getDestinationFloors().size() < numberOfFloors / 2) {
                    minDistance = distance;
                    closestElevator = elevator;
                }
            }
        }

        return closestElevator;
    }

    private boolean isIdle(Elevator elevator) {
        return elevator.getDirection() == 0;
    }

    private boolean goesUp(Elevator elevator) {
        return elevator.getDirection() > 0;
    }

    private boolean goesDown(Elevator elevator) {
        return elevator.getDirection() < 0;
    }

    private List<Integer> sort(Set<Integer> destinationFloors, int currentFloor) {
        List<Integer> destinationFloorsList = new ArrayList<>(destinationFloors);
        Comparator<Integer> customComparator = (a, b) -> {
            if (a <= currentFloor && b <= currentFloor) {
                // Sort descending for values <= x
                return Integer.compare(b, a);
            } else if (a > currentFloor && b > currentFloor) {
                // Sort ascending for values > x
                return Integer.compare(a, b);
            } else if (a <= currentFloor) {
                // Place values <= x before values > x
                return -1;
            } else {
                // Place values > x after values <= x
                return 1;
            }
        };

        // Sort the ArrayList using the custom comparator
        destinationFloorsList.sort(customComparator);

        return destinationFloorsList;
    }

    public ElevatorSystemDTO update(int elevatorId, int currentFloor, int destinationFloor) {
        for (Elevator elevator : elevators) {
            if (elevator.getId() == elevatorId) {
                elevator.setCurrentFloor(currentFloor);
                elevator.setDirection(Integer.compare(destinationFloor - currentFloor, 0));
                elevator.getDestinationFloors().add(destinationFloor);
                Collections.sort(new ArrayList<>(elevator.getDestinationFloors()));
                break;
            }
        }

        return createElevatorSystemDTO(elevators);
    }

    public ElevatorSystemDTO step() {
        elevators.forEach((elevator -> {
            if (!elevator.getDestinationFloors().isEmpty()) {
                int currentFloor = elevator.getCurrentFloor();
                int direction = elevator.getDirection();
                List<Integer> destinationFloors = sort(elevator.getDestinationFloors(), currentFloor);

                if (currentFloor == destinationFloors.get(0)) {
                    destinationFloors.remove(0);
                    if (destinationFloors.isEmpty()) {
                        direction = 0;
                    } else {
                        direction = Integer.compare(destinationFloors.get(0) - currentFloor, 0);
                    }
                } else {
                    currentFloor += direction;
                }

                elevator.setCurrentFloor(currentFloor);
                elevator.setDirection(direction);
                elevator.setDestinationFloors(new LinkedHashSet<>(sort(new HashSet<>(destinationFloors), currentFloor)));
            }
        }));

        return createElevatorSystemDTO(elevators);
    }

    public ElevatorSystemDTO updateFullState(ElevatorDTO elevatorDTO) {
        int elevatorId = elevatorDTO.id();
        int currentFloor = elevatorDTO.currentFloor();
        int direction = elevatorDTO.direction();
        Set<Integer> destinationFloors = new HashSet<>(elevatorDTO.destinationFloors());

        for (Elevator elevator : elevators) {
            if (elevator.getId() == elevatorId) {
                elevator.setCurrentFloor(currentFloor);
                elevator.setDirection(direction);
                elevator.setDestinationFloors(destinationFloors);
                break;
            }
        }

        return createElevatorSystemDTO(elevators);
    }

    public ElevatorSystemDTO createElevatorSystemDTO(List<Elevator> updatedElevators) {
        List<ElevatorDTO> elevatorDTOs = new ArrayList<>();
        for (Elevator elevator : updatedElevators) {
            ElevatorDTO elevatorDTO = new ElevatorDTO(elevator.getId(), elevator.getCurrentFloor(),
                    elevator.getDirection(), new ArrayList<>(sort(elevator.getDestinationFloors(), elevator.getCurrentFloor())));
            elevatorDTOs.add(elevatorDTO);
        }

        return new ElevatorSystemDTO(numberOfFloors, numberOfElevators, elevatorDTOs); // Use numberOfElevators
    }

    public ElevatorSystemDTO restart(int id) {
        Elevator foundElevator = elevators.stream()
                                          .filter(e -> e.getId() == id)
                                          .findFirst()
                                          .orElse(null);

        if (foundElevator != null) {
            foundElevator.setDirection(0);
            foundElevator.setCurrentFloor(0);
            foundElevator.setDestinationFloors(new HashSet<>());
            elevators.removeIf(e -> e.getId() == id);
            elevators.add(foundElevator);
        }

        return createElevatorSystemDTO(elevators);
    }
}
