package com.eventmanagement.backend;

import com.eventmanagement.backend.model.Event;
import com.eventmanagement.backend.model.User;
import com.eventmanagement.backend.model.VendorProfile;
import com.eventmanagement.backend.repository.EventRepository;
import com.eventmanagement.backend.repository.UserRepository;
import com.eventmanagement.backend.repository.VendorProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@EnableMongoAuditing
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Event Management Backend is RUNNING!");
        System.out.println("  Server: http://localhost:8080");
        System.out.println("========================================\n");
    }

    @Bean
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            VendorProfileRepository vendorProfileRepository,
            EventRepository eventRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Seed Admin if not exists
            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                User admin = User.builder()
                        .name("Administrator")
                        .email("admin@example.com")
                        .passwordHash(passwordEncoder.encode("password123"))
                        .role(User.Role.ADMIN)
                        .phoneNo("+91 9988776655")
                        .gender("male")
                        .build();
                userRepository.save(admin);
                System.out.println("Seeded admin account: admin@example.com / password123");
            }

            // 2. Seed Planner if not exists
            User planner = userRepository.findByEmail("planner@example.com").orElse(null);
            if (planner == null) {
                planner = User.builder()
                        .name("Rajani Event Planner")
                        .email("planner@example.com")
                        .passwordHash(passwordEncoder.encode("password123"))
                        .role(User.Role.PLANNER)
                        .phoneNo("+91 8877665544")
                        .gender("female")
                        .build();
                planner = userRepository.save(planner);
                System.out.println("Seeded planner account: planner@example.com / password123");
            }

            // 3. Seed Client Rishika if not exists
            User clientUser = userRepository.findByEmail("rishika@example.com").orElse(null);
            if (clientUser == null) {
                clientUser = User.builder()
                        .name("Rishika")
                        .email("rishika@example.com")
                        .passwordHash(passwordEncoder.encode("password123"))
                        .role(User.Role.CLIENT)
                        .phoneNo("+91 9876543210")
                        .gender("female")
                        .build();
                clientUser = userRepository.save(clientUser);
                System.out.println("Seeded client account: rishika@example.com / password123");
            }

            // 4. Seed Vendors if empty
            if (vendorProfileRepository.count() == 0) {
                System.out.println("Seeding mock vendors with detailed portfolios, phone numbers, and categories...");

                // Vendor 1: Venue
                User venueUser = User.builder()
                        .name("Grand Palace Venues")
                        .email("venue@example.com")
                        .passwordHash(passwordEncoder.encode("password123"))
                        .role(User.Role.VENDOR)
                        .phoneNo("+91 7766554433")
                        .gender("male")
                        .build();
                venueUser = userRepository.save(venueUser);

                VendorProfile venueProfile = VendorProfile.builder()
                        .userId(venueUser.getId())
                        .businessName("Royal Palace & Celebration Hall")
                        .category(VendorProfile.Category.VENUE)
                        .priceRange(new VendorProfile.PriceRange(50000, 250000))
                        .description("Luxurious sprawling event venue with built-in stage setups, central AC, champagne drape arches, and crystal chandeliers. Perfect for weddings, receptions, birthday soirees, and corporate galas.")
                        .phoneNo("+91 7766554433")
                        .gender("male")
                        .location("Bangalore Central")
                        .experienceYears(8)
                        .available(true)
                        .servicesOffered(Arrays.asList("Indoor AC Banquet Halls", "Outdoor Lawn Spaces", "Valet Parking Service", "Bridal Dressing Suite"))
                        .portfolioImages(Arrays.asList(
                                "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=600&q=80",
                                "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=600&q=80",
                                "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=600&q=80"
                        ))
                        .status(VendorProfile.Status.APPROVED)
                        .build();
                vendorProfileRepository.save(venueProfile);

                // Vendor 2: Catering
                User cateringUser = User.builder()
                        .name("Gourmet Kitchens")
                        .email("catering@example.com")
                        .passwordHash(passwordEncoder.encode("password123"))
                        .role(User.Role.VENDOR)
                        .phoneNo("+91 6655443322")
                        .gender("female")
                        .build();
                cateringUser = userRepository.save(cateringUser);

                VendorProfile cateringProfile = VendorProfile.builder()
                        .userId(cateringUser.getId())
                        .businessName("Nourish Feast Caterers")
                        .category(VendorProfile.Category.CATERING)
                        .priceRange(new VendorProfile.PriceRange(20000, 120000))
                        .description("Bespoke event dining planners, crafting premium multi-cuisine menus. We specialize in live Mughlai grills, Italian pasta live counters, organic salad bars, and custom dessert fountains for birthdays and weddings.")
                        .phoneNo("+91 6655443322")
                        .gender("female")
                        .location("Indiranagar, Bangalore")
                        .experienceYears(12)
                        .available(true)
                        .servicesOffered(Arrays.asList("Live Food Counters", "Mocktail & Cocktail Bars", "Formal Table Service", "Birthday Cake Displays"))
                        .portfolioImages(Arrays.asList(
                                "https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=600&q=80",
                                "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?auto=format&fit=crop&w=600&q=80",
                                "https://images.unsplash.com/photo-1467003909585-2f8a72700288?auto=format&fit=crop&w=600&q=80"
                        ))
                        .status(VendorProfile.Status.APPROVED)
                        .build();
                vendorProfileRepository.save(cateringProfile);

                // Vendor 3: Decor
                User decorUser = User.builder()
                        .name("Floral & Sparkle Decor")
                        .email("decor@example.com")
                        .passwordHash(passwordEncoder.encode("password123"))
                        .role(User.Role.VENDOR)
                        .phoneNo("+91 5544332211")
                        .gender("female")
                        .build();
                decorUser = userRepository.save(decorUser);

                VendorProfile decorProfile = VendorProfile.builder()
                        .userId(decorUser.getId())
                        .businessName("Prism Themes & Birthday Decorators")
                        .category(VendorProfile.Category.DECOR)
                        .priceRange(new VendorProfile.PriceRange(15000, 95000))
                        .description("Creating magical event spaces using premium fresh roses, pastel balloon arches, fairy lights, custom neon name signs, and glassmorphic photo booths. Ideal for birthdays, baby showers, and receptions.")
                        .phoneNo("+91 5544332211")
                        .gender("female")
                        .location("Whitefield, Bangalore")
                        .experienceYears(6)
                        .available(true)
                        .servicesOffered(Arrays.asList("Pastel Balloon Archways", "Neon Name Board Displays", "Stage Flower Walls", "Table Floral Styling"))
                        .portfolioImages(Arrays.asList(
                                "https://images.unsplash.com/photo-1513151233558-d860c5398176?auto=format&fit=crop&w=600&q=80",
                                "https://images.unsplash.com/photo-1523438885200-e635ba2c371e?auto=format&fit=crop&w=600&q=80",
                                "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?auto=format&fit=crop&w=600&q=80"
                        ))
                        .status(VendorProfile.Status.APPROVED)
                        .build();
                vendorProfileRepository.save(decorProfile);

                // Vendor 4: AV & DJ
                User avUser = User.builder()
                        .name("Dynamic Sound Systems")
                        .email("av@example.com")
                        .passwordHash(passwordEncoder.encode("password123"))
                        .role(User.Role.VENDOR)
                        .phoneNo("+91 4433221100")
                        .gender("male")
                        .build();
                avUser = userRepository.save(avUser);

                VendorProfile avProfile = VendorProfile.builder()
                        .userId(avUser.getId())
                        .businessName("Sonic Boom AV & DJ Nights")
                        .category(VendorProfile.Category.AV)
                        .priceRange(new VendorProfile.PriceRange(25000, 150000))
                        .description("Concert-grade Line Array speakers, architectural laser shows, LED backdrop screens, CO2 cold pyros, and celebrity DJ setups for birthday parties, DJ nights, and corporate galas.")
                        .phoneNo("+91 4433221100")
                        .gender("male")
                        .location("Koramangala, Bangalore")
                        .experienceYears(10)
                        .available(true)
                        .servicesOffered(Arrays.asList("Celebrity DJ Performance", "Concert Line Array Speakers", "LED Stage Background Screen", "Smoke & Cold Pyro Machines"))
                        .portfolioImages(Arrays.asList(
                                "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=600&q=80",
                                "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=600&q=80",
                                "https://images.unsplash.com/photo-1506157786151-b8491531f063?auto=format&fit=crop&w=600&q=80"
                        ))
                        .status(VendorProfile.Status.APPROVED)
                        .build();
                vendorProfileRepository.save(avProfile);

                System.out.println("Successfully seeded all mock vendors into MongoDB Atlas!");
            }

            // 5. Ensure Ready-to-Book Packages (Events) exist for Client (Rishika) & Planner
            if (eventRepository.findByClientLinkToken("bday-fiesta-token-123").isEmpty() && planner != null) {
                System.out.println("Seeding sample packages (Birthday & Wedding)...");

                VendorProfile vVenue = vendorProfileRepository.findByCategoryAndStatus(VendorProfile.Category.VENUE, VendorProfile.Status.APPROVED).stream().findFirst().orElse(null);
                VendorProfile vCatering = vendorProfileRepository.findByCategoryAndStatus(VendorProfile.Category.CATERING, VendorProfile.Status.APPROVED).stream().findFirst().orElse(null);
                VendorProfile vDecor = vendorProfileRepository.findByCategoryAndStatus(VendorProfile.Category.DECOR, VendorProfile.Status.APPROVED).stream().findFirst().orElse(null);
                VendorProfile vAV = vendorProfileRepository.findByCategoryAndStatus(VendorProfile.Category.AV, VendorProfile.Status.APPROVED).stream().findFirst().orElse(null);

                // Package 1: Grand Birthday Soiree Package
                Event bdayEvent = Event.builder()
                        .plannerId(planner.getId())
                        .clientEmail("rishika@example.com")
                        .title("Grand Birthday Soiree & DJ Night Fiesta")
                        .targetBudget(150000.0)
                        .totalCost(115000.0)
                        .status(Event.Status.APPROVED)
                        .clientLinkToken("bday-fiesta-token-123")
                        .vendors(Arrays.asList(
                                new Event.EventVendor(vDecor != null ? vDecor.getId() : "1", 35000.0, true),
                                new Event.EventVendor(vCatering != null ? vCatering.getId() : "2", 45000.0, true),
                                new Event.EventVendor(vAV != null ? vAV.getId() : "3", 35000.0, true)
                        ))
                        .build();
                eventRepository.save(bdayEvent);

                // Package 2: Royal Wedding & Reception Gala
                Event weddingEvent = Event.builder()
                        .plannerId(planner.getId())
                        .clientEmail("rishika@example.com")
                        .title("Royal Palace Wedding & Reception Gala")
                        .targetBudget(350000.0)
                        .totalCost(285000.0)
                        .status(Event.Status.PENDING_APPROVAL)
                        .clientLinkToken("wedding-gala-token-456")
                        .vendors(Arrays.asList(
                                new Event.EventVendor(vVenue != null ? vVenue.getId() : "1", 120000.0, true),
                                new Event.EventVendor(vCatering != null ? vCatering.getId() : "2", 85000.0, true),
                                new Event.EventVendor(vDecor != null ? vDecor.getId() : "3", 50000.0, true),
                                new Event.EventVendor(vAV != null ? vAV.getId() : "4", 30000.0, true)
                        ))
                        .build();
                eventRepository.save(weddingEvent);

                System.out.println("Seeded ready-to-book Birthday and Wedding Packages for client Rishika!");
            }
        };
    }
}
