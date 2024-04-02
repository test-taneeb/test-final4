package wedding.Planner;
import java.util.*;
import java.util.stream.*;
import javax.swing.*;
import static java.lang.System.*;
public class UserManager {
  //  private Map<String, Package> requestedPackages = new HashMap<>();
   // private Map<String, String> negotiatedContracts = new HashMap<>(); // Assuming simple representation for demo
  //  private Map<String, List<Booking>> userBookings = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    private Map<String, List<ServiceProvider>> serviceProviders = new HashMap<>();
    private EventMediaManager mediaManager = new EventMediaManager(); // Media manager instance

    private static User user;
    private static final PackageList list = new PackageList();

    public User getUserById(String username) {
        return users.get(username);
    }
    public void registerUser(String username, String password, String role, String hallnumber) {
        User newUser;
        if ("ADMIN".equalsIgnoreCase(role)) {
            newUser = new Admin(username, password, hallnumber);
        } else {
            newUser = new RegularUser(username, password, hallnumber);
        }
        users.put(username, newUser);
        System.out.println(role + " registered successfully.");
    }
    public void registerUser(String username, String password, String role, String hallNumber, String serviceType, String location, double pricing, double rating) {
        hallNumber = (hallNumber == null || hallNumber.equalsIgnoreCase("none")) ? "" : hallNumber;
        ServiceProvider serviceProvider = new ServiceProvider(username, password, hallNumber, serviceType, location, pricing, rating);
        users.put(username, serviceProvider);
        serviceProviders.computeIfAbsent(serviceType.toLowerCase(), k -> new ArrayList<>()).add(serviceProvider); // Add to service providers
        System.out.println("Service provider registered successfully.");
    }
    public void printActiveEvents() {
        boolean hasActiveEvents = false;
        System.out.println("Active Events:");
        for (Map.Entry<String, User> entry : this.users.entrySet()) {
            User user = entry.getValue();
            if (user.getHallnumber() != null && !user.getHallnumber().isEmpty()) {
                System.out.println("Username: " + entry.getKey() + " - Event Hall: " + user.getHallnumber());
                hasActiveEvents = true;
            }
        }
        if (!hasActiveEvents) {
            System.out.println("No active events at the moment.");
        }
    }

    public boolean loginUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) { // Password should be checked with a hashed value in real scenarios
            System.out.println("Login successful for " + user.getRole() + ": " + username +" " + user.getHallnumber());
            return true;
        }
        return false;
    }
    public void addMediaToUserEvent(String username, Media media) {
        User user = users.get(username);
        if (user != null && user.getHallnumber() != null) {
            mediaManager.addMediaToEvent(user.getHallnumber(), media);
            System.out.println("Media added to event.");
        } else {
            System.out.println("User does not have an active event to add media.");
        }
    }
    public List<Media> getMediaForUserEvent(String username) {
        User user = users.get(username);
        if (user != null && user.getHallnumber() != null) {
            return mediaManager.getMediaForEvent(user.getHallnumber());
        } else {
            System.out.println("User does not have an active event.");
            return null;
        }
    }
    public void removeMediaFromUserEvent(String username, Media media) {
        User user = users.get(username);
        if (user != null && user.getHallnumber() != null) {
            mediaManager.removeMediaFromEvent(user.getHallnumber(), media);
            System.out.println("Media removed from event.");
        } else {
            System.out.println("User does not have an active event.");
        }
    }

    public List<ServiceProvider> searchServiceProviders(String type, String location, double maxPricing, double minRating) {
        System.out.println("Searching for: Type=" + type + ", Location=" + location + ", Max Pricing=" + maxPricing + ", Min Rating=" + minRating);
        return serviceProviders.getOrDefault(type.toLowerCase(), Collections.emptyList()).stream()
                .filter(provider -> provider.getLocation().equalsIgnoreCase(location))
                .filter(provider -> provider.getPricing() <= maxPricing)
                .filter(provider -> provider.getRating() >= minRating)
                .collect(Collectors.toList());
    }
    
}
