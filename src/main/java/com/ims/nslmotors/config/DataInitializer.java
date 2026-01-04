package com.ims.nslmotors.config;

import com.ims.nslmotors.model.Employee;
import com.ims.nslmotors.model.Shop;
import com.ims.nslmotors.repository.EmployeeRepository;
import com.ims.nslmotors.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final ShopRepository shopRepository;
    private final EmployeeRepository employeeRepository;
    
    @Override
    public void run(String... args) {
        // Eğer zaten shop varsa, initialization'ı atla
        if (shopRepository.count() > 0) {
            log.info("Shops already initialized, skipping data initialization");
            return;
        }
        
        log.info("Initializing sample shop and employee data...");
        
        // Dükkanları oluştur
        Shop merkez = new Shop();
        merkez.setName("NSL Motors Merkez");
        merkez.setAddress("Atatürk Caddesi No:123, Kadıköy");
        merkez.setCity("İstanbul");
        merkez.setPhoneNumber("0216 555 0123");
        merkez.setEmail("merkez@nslmotors.com");
        merkez.setIsActive(true);
        merkez.setWorkingHours("09:00-18:00");
        
        Shop anadolu = new Shop();
        anadolu.setName("NSL Motors Anadolu");
        anadolu.setAddress("Bağdat Caddesi No:456, Maltepe");
        anadolu.setCity("İstanbul");
        anadolu.setPhoneNumber("0216 555 0456");
        anadolu.setEmail("anadolu@nslmotors.com");
        anadolu.setIsActive(true);
        anadolu.setWorkingHours("09:00-18:00");
        
        Shop avrupa = new Shop();
        avrupa.setName("NSL Motors Avrupa");
        avrupa.setAddress("Barbaros Bulvarı No:789, Beşiktaş");
        avrupa.setCity("İstanbul");
        avrupa.setPhoneNumber("0212 555 0789");
        avrupa.setEmail("avrupa@nslmotors.com");
        avrupa.setIsActive(true);
        avrupa.setWorkingHours("09:00-18:00");
        
        List<Shop> shops = shopRepository.saveAll(Arrays.asList(merkez, anadolu, avrupa));
        log.info("Created {} shops", shops.size());
        
        // Her dükkana teknisyen ekle
        // Merkez Teknisyenleri
        createTechnician("Ahmet", "Yılmaz", "ahmet.yilmaz@nslmotors.com", "MASTER", shops.get(0));
        createTechnician("Mehmet", "Demir", "mehmet.demir@nslmotors.com", "TECHNICIAN", shops.get(0));
        createTechnician("Can", "Öztürk", "can.ozturk@nslmotors.com", "TECHNICIAN", shops.get(0));
        
        // Anadolu Teknisyenleri
        createTechnician("Deniz", "Kaya", "deniz.kaya@nslmotors.com", "MASTER", shops.get(1));
        createTechnician("Emre", "Şahin", "emre.sahin@nslmotors.com", "TECHNICIAN", shops.get(1));
        
        // Avrupa Teknisyenleri
        createTechnician("Burak", "Aydın", "burak.aydin@nslmotors.com", "MASTER", shops.get(2));
        createTechnician("Oğuz", "Çelik", "oguz.celik@nslmotors.com", "TECHNICIAN", shops.get(2));
        createTechnician("Cem", "Yıldız", "cem.yildiz@nslmotors.com", "TECHNICIAN", shops.get(2));
        
        log.info("Sample data initialization completed!");
    }
    
    private void createTechnician(String firstName, String lastName, String email, String role, Shop shop) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setRole(role);
        employee.setPassword("$2a$10$DummyPasswordHashForTechnician123"); // Dummy hash
        employee.setPhoneNumber("05" + (int)(Math.random() * 100000000));
        employee.setShop(shop);
        employee.setShopName(shop.getName());
        employeeRepository.save(employee);
        log.info("Created technician: {} {} at {}", firstName, lastName, shop.getName());
    }
}
