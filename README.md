# Back-end Tech Challenge

## Problem
An underwater volcano formed a new small island in the Pacific Ocean last month. All the conditions on the island seems perfect and it was
decided to open it up for the general public to experience the pristine uncharted territory.
The island is big enough to host a single campsite so everybody is very excited to visit. In order to regulate the number of people on the island, it
was decided to come up with an online web application to manage the reservations. You are responsible for design and development of a REST
API service that will manage the campsite reservations.
To streamline the reservations a few constraints need to be in place -
- The campsite will be free for all.
- The campsite can be reserved for max 3 days.
- The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.
- Reservations can be cancelled anytime.
- For sake of simplicity assume the check-in & check-out time is 12:00 AM
###### System Requirements
- The users will need to find out when the campsite is available. So the system should expose an API to provide information of the
availability of the campsite for a given date range with the default being 1 month.
- Provide an end point for reserving the campsite. The user will provide his/her email & full name at the time of reserving the campsite
along with intended arrival date and departure date. Return a unique booking identifier back to the caller if the reservation is successful.
- The unique booking identifier can be used to modify or cancel the reservation later on. Provide appropriate end point(s) to allow
modification/cancellation of an existing reservation
- Due to the popularity of the island, there is a high likelihood of multiple users attempting to reserve the campsite for the same/overlapping
date(s). Demonstrate with appropriate test cases that the system can gracefully handle concurrent requests to reserve the campsite.
- Provide appropriate error messages to the caller to indicate the error cases.
- In general, the system should be able to handle large volume of requests for getting the campsite availability.
- There are no restrictions on how reservations are stored as as long as system constraints are not violated.

## Tools used

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/)
* [Java 11](https://www.java.com)
* [MariaDB](https://mariadb.org/)

## Solution design
Some points about the architecture:
1. Large volume of requests: Considering that this API should support multiple users attempting to reserve, microservices architecture comes up, this same module can be duplicated or deployed many times in order to support big loads.
2. Concurrency: Multiple users may attempt to reserve the campsite at the same time, for this I created a stored procedure responsible to create a unique identifier and to prevent duplicated reservations.
3. Error messages: I used standard API validations for common business logic and returned to the caller an HTTP Status and message based on the validation.

## How to test
In order to run all tests, you will be required to set these properties in `application-test.properties` file:
```
spring.datasource.url=jdbc:mysql://localhost/upgrade_techchallenge?useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=CHANGEIT
spring.datasource.password=CHANGEIT
```

After these properties are set, you can run:
```
mvn clean package
```

### Reservation tests
1. Get all reservations: `ReservationService.getReservations`
2. Simple campsite reservation: `ReservationService.bookCampsite`
3. Prevent duplicated reservations: `ReservationService.bookCampsiteSameOverlappingDates`
4. Cancel reservation: `ReservationService.cancelReservation`
5. Try to cancel wrong reservation: `ReservationService.tryToCancelWrongReservation`
6. Modify reservation: `ReservationService.modifyReservation`
7. Attempt concurrent reservations: `ReservationService.concurrentReservations`

## Docker file
This is a very simple microservice, it only creates a Docker image containing the JAR file and its responsible to pass all parameters to the execution.

## Autor
J. Milton Chambi M.

