# Elevator System

## Guidelines

1. Clone the project
2. Clone ["elevator-system-front"](https://github.com/delibre/elevator-system-front) and proceed according to the 
guidelines
3. Run "Elevator System"
4. Run "Elevator System Front"

## Source Code Review

The primary class responsible for simulating the elevator's operations is called **"ElevatorSystemService"**. Within 
this class, the pickup and step methods employ a specific algorithm to simulate the elevator's behavior. Here is an 
overview of the algorithm used in these methods:

1. pickup() method:

```java
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
```

* Given the call floor and destination floor, the method finds the closest idle elevator or an elevator that is already 
moving in the desired direction and has capacity.
* It calculates the distance between the elevator's current floor and the call floor.
* If the elevator is idle or meets the criteria for movement in the desired direction, and it is closer or has the same 
distance as the current closest elevator, it becomes the new closest elevator.
* The destination floors of the closest elevator are updated with the call floor and destination floor.
* The direction of the elevator is set based on the current floor and call floor if the elevator is already at the call 
floor, otherwise, it is set based on the current floor and destination floor.
* The destination floors of the elevator are sorted using the sort method, which ensures proper ordering based on the 
current floor.
* If the elevator is already at the call floor, the direction is set again.
* The method returns the updated elevator system.


2. step() method:

```java
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
```

For each elevator in the system:
* If the elevator has destination floors:
  * The current floor and direction of the elevator are retrieved.
  * The destination floors of the elevator are sorted using the sort method to ensure the correct order.
  * If the current floor is the same as the first destination floor, it means the elevator has reached that floor. So, 
  the first destination floor is removed.
    * If there are no more destination floors, the direction is set to idle (0). Otherwise, the direction is updated 
    based on the next destination floor.
  * If the elevator hasn't reached the destination floor, the current floor is updated by adding the direction value 
  to it.
  * The destination floors of the elevator are updated by creating a new sorted set based on the updated destination 
  floors and current floor.
* The method returns the updated elevator system.

The algorithm considers various conditions to assign pickups to the closest available elevator and ensures that the 
elevators move in the desired direction and make stops at the correct floors. The sort method is used to order the 
destination floors based on the current floor to optimize the movement of the elevator.


## Problems

Unfortunately, there is an issue with the sorting functionality that needs to be addressed. In certain cases, the 
sorting operation does not produce the expected results. For instance, when the elevator is located on the 1st floor 
and a call is made from the 0th floor with a destination floor of 1, the sorting algorithm incorrectly swaps the call 
floor and destination floor.# elevator-system
