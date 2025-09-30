import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RailwayReservation {
    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Train> trains = new ArrayList<>();
    private static int passengerID = 1;

    public static void main(String[] args) {
        initializeTrains();
        while (true) {
            System.out.println("\n--- Railway Reservation System ---");
            System.out.println("1. Book Ticket");
            System.out.println("2. Cancel Ticket");
            System.out.println("3. Print Train Details");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int ch = readInt();

            switch (ch) {
                case 1: handleBooking(); break;
                case 2: handleCancellation(); break;
                case 3: printAllTrains(); break;
                case 4:
                    System.out.println("Exiting... Thank you for using Railway Reservation!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void initializeTrains() {
        for (int i = 0; i < 2; i++) {
            trains.add(new Train());
        }
    }

    private static void handleBooking() {
        System.out.print("Enter Train ID: ");
        int tid = readInt();

        Train currentTrain = getTrainById(tid);
        if (currentTrain == null) return;

        System.out.print("Enter number of tickets to book: ");
        int ticketCount = readInt();

        if (ticketCount <= 0) {
            System.out.println("Invalid ticket count.");
            return;
        }

        if (ticketCount > currentTrain.seats) {
            System.out.println("Not enough seats available.");
            return;
        }

        List<String[]> passengerInfos = new ArrayList<>();
        sc.nextLine();

        for (int i = 1; i <= ticketCount; i++) {
            System.out.println("\nEnter details for Passenger " + i + ":");

            System.out.print("Name: ");
            String name = sc.nextLine();

            System.out.print("Age: ");
            int age = readInt();
            sc.nextLine(); 

            System.out.print("Gender: ");
            String gender = sc.nextLine();

            passengerInfos.add(new String[]{name, String.valueOf(age), gender});
        }

        currentTrain.addPassengerDetails(passengerID, passengerInfos);
        System.out.println("Booking Successful! Passenger Group ID: " + passengerID);
        currentTrain.trainSummary();

        passengerID++;
    }

    private static void handleCancellation() {
        System.out.print("Enter Train ID: ");
        int tid = readInt();

        Train currentTrain = getTrainById(tid);
        if (currentTrain == null) return;

        System.out.print("Enter Passenger Group ID to cancel: ");
        int pid = readInt();

        currentTrain.cancelTicket(pid);
        currentTrain.trainSummary();
    }

    private static void printAllTrains() {
        for (Train t : trains) {
            if (t.passengerDetails.isEmpty()) {
                System.out.println("No passengers for Train " + t.trainID);
            } else {
                t.printDetails();
            }
        }
    }

    private static Train getTrainById(int tid) {
        for (Train t : trains) {
            if (t.trainID == tid) return t;
        }
        System.out.println("Invalid Train ID.");
        return null;
    }

    private static int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid number: ");
            sc.next();
        }
        return sc.nextInt();
    }

    static class Train {
        public static int trainCount = 1;

        public int trainID;
        public int seats;
        public double price;
        public HashMap<Integer, String> passengerDetails;
        private PriorityQueue<Integer> freeSeats;
        private int nextSeatNumber;

        public Train() {
            this.trainID = trainCount++;
            this.seats = 120;
            this.price = 450.0;
            this.passengerDetails = new HashMap<>();
            this.freeSeats = new PriorityQueue<>();
            this.nextSeatNumber = 1;
        }

        public void addPassengerDetails(int passengerID, List<String[]> passengerInfos) {
            StringBuilder detail = new StringBuilder();

            for (String[] info : passengerInfos) {
                String name = info[0];
                int age = Integer.parseInt(info[1]);
                String gender = info[2];
                int seatNo = !freeSeats.isEmpty() ? freeSeats.poll() : nextSeatNumber++;

                detail.append("Passenger Group ID: ").append(passengerID)
                      .append(", Name: ").append(name)
                      .append(", Age: ").append(age)
                      .append(", Gender: ").append(gender)
                      .append(", Seat No: ").append(seatNo)
                      .append(", Fare: ₹ ").append(price)
                      .append("\n");
            }

            passengerDetails.put(passengerID, detail.toString());
            seats -= passengerInfos.size();

            writeToFile("BOOKED", passengerID, detail.toString());
        }

        public void cancelTicket(int passengerID) {
            if (passengerDetails.containsKey(passengerID)) {
                String details = passengerDetails.get(passengerID);
                int ticketCount = countBookedTickets(details);

        
                for (String line : details.split("\n")) {
                    try {
                        String[] parts = line.split("Seat No: ");
                        int seatNo = Integer.parseInt(parts[1].split(",")[0]);
                        freeSeats.offer(seatNo);
                    } catch (Exception e) {
                        
                    }
                }

                seats += ticketCount;
                passengerDetails.remove(passengerID);

                System.out.println("Passenger Group ID " + passengerID + " booking cancelled.");
                writeToFile("CANCELLED", passengerID, details);
            } else {
                System.out.println("Passenger Group ID " + passengerID + " not found.");
            }
        }

        private int countBookedTickets(String details) {
            return details.split("\n").length;
        }

        public void trainSummary() {
            System.out.println("Train " + trainID + " Summary:");
            System.out.println("Available Seats: " + seats);
            System.out.println("Ticket Fare: ₹" + price);
        }

        public void printDetails() {
            System.out.println("\n--- Train " + trainID + " Passenger Details ---");
            for (String detail : passengerDetails.values()) {
                System.out.print(detail);
            }
        }

        private void writeToFile(String action, int passengerID, String content) {
            try (FileWriter writer = new FileWriter("railway_bookings.txt", true)) {
                writer.write("[" + action + "] Train ID: " + trainID + ", Passenger Group ID: " + passengerID + "\n");
                writer.write(content);
                writer.write("------------------------------------------------------------\n");
            } catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }
        }
    }
}
