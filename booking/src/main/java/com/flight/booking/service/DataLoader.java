package com.flight.booking.service;

import com.flight.booking.entity.Flist;
import com.flight.booking.entity.User;
import com.flight.booking.repository.FlistRepository;
import com.flight.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlistRepository flistRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Admin User
        if (!userRepository.findByEmail("admin@gmail.com").isPresent()) {
            User admin = User.builder()
                    .name("Deccan Admin")
                    .email("admin@gmail.com")
                    .password("admin@123")
                    .role("ADMIN")
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user seeded successfully: admin@gmail.com / admin@123");
        }

        // 2. Seed 50 Flights if flights database is empty
        if (flistRepository.count() == 0) {
            List<Flist> flights = new ArrayList<>();
            
            String[] airlines = {
                "Deccan Airways", "Air India", "IndiGo", "Vistara", 
                "Emirates", "Qatar Airways", "Singapore Airlines", "British Airways"
            };

            String[] airlineCodes = {
                "DA", "AI", "6E", "UK", "EK", "QR", "SQ", "BA"
            };

            String[] cities = {
                "Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai",
                "Dubai", "London", "New York", "Singapore", "Paris", "Tokyo"
            };

            String[] imgUrls = {
                "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=500&auto=format&fit=crop&q=60", // Flight in air
                "https://images.unsplash.com/photo-1540962351504-03099e0a754b?w=500&auto=format&fit=crop&q=60", // Wings
                "https://images.unsplash.com/photo-1483450388369-9ed95738483c?w=500&auto=format&fit=crop&q=60", // Jet engine
                "https://images.unsplash.com/photo-1506012787146-f92b2d7d6d96?w=500&auto=format&fit=crop&q=60", // Window view
                "https://images.unsplash.com/photo-1517999144091-3d9dca6d1e43?w=500&auto=format&fit=crop&q=60", // Airplane nose
                "https://images.unsplash.com/photo-1473877950242-1830d57fc7e7?w=500&auto=format&fit=crop&q=60", // High sky
                "https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=500&auto=format&fit=crop&q=60", // City view
                "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?w=500&auto=format&fit=crop&q=60"  // Travel
            };

            LocalDateTime baseTime = LocalDateTime.now().plusDays(1).withHour(6).withMinute(0).withSecond(0).withNano(0);

            int flightCount = 0;
            // Generate 50 flights using combinations
            for (int i = 0; i < airlines.length; i++) {
                String airline = airlines[i];
                String code = airlineCodes[i];
                
                for (int j = 0; j < cities.length; j++) {
                    String src = cities[j];
                    
                    for (int k = 0; k < cities.length; k++) {
                        String dest = cities[k];
                        if (src.equals(dest)) continue; // source and destination must be different
                        
                        // Let's control the combinations to make exactly 50 flights
                        if (flightCount >= 50) break;

                        // Check if domestic or international
                        boolean isSrcIndian = isIndianCity(src);
                        boolean isDestIndian = isIndianCity(dest);
                        String flightType = (isSrcIndian && isDestIndian) ? "DOMESTIC" : "INTERNATIONAL";

                        // Limit international airlines to their routes or allow general
                        if (airline.equals("Emirates") && !src.equals("Dubai") && !dest.equals("Dubai")) {
                            continue; // Emirates route usually through Dubai
                        }
                        if (airline.equals("Singapore Airlines") && !src.equals("Singapore") && !dest.equals("Singapore")) {
                            continue;
                        }
                        if (airline.equals("Qatar Airways") && !src.equals("Dubai") && !dest.equals("Dubai")) {
                            // Let's assume Doha/Dubai route
                            continue;
                        }

                        int flightNum = 100 + flightCount + 1;
                        String fNumber = code + "-" + flightNum;
                        
                        // Increment time progressively for diversity
                        LocalDateTime dep = baseTime.plusHours(flightCount * 3L + (j * 2));
                        LocalDateTime arr = dep.plusHours(flightType.equals("DOMESTIC") ? 2 : 7);

                        // Price calculation: higher for international, distance factor
                        double price = flightType.equals("DOMESTIC") ? 99.0 + (flightNum % 50) * 4 : 450.0 + (flightNum % 100) * 8;

                        Flist flight = Flist.builder()
                                .flightNumber(fNumber)
                                .airline(airline)
                                .source(src)
                                .destination(dest)
                                .departureTime(dep)
                                .arrivalTime(arr)
                                .price(price)
                                .totalSeats(180)
                                .availableSeats(180)
                                .flightType(flightType)
                                .imageUrl(imgUrls[flightCount % imgUrls.length])
                                .build();

                        flights.add(flight);
                        flightCount++;
                    }
                    if (flightCount >= 50) break;
                }
                if (flightCount >= 50) break;
            }

            flistRepository.saveAll(flights);
            System.out.println("Seeded " + flights.size() + " flights into the flights database.");
        }
    }

    private boolean isIndianCity(String city) {
        return city.equals("Mumbai") || city.equals("Delhi") || city.equals("Bangalore") || city.equals("Hyderabad") || city.equals("Chennai");
    }
}
