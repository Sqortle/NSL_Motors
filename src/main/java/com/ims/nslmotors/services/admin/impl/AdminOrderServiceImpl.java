package com.ims.nslmotors.services.admin.impl;

import com.ims.nslmotors.dto.admin.DtoAdminOrder;
import com.ims.nslmotors.dto.admin.DtoAdminOrderIU;
import com.ims.nslmotors.model.*; // Order, Customer, Employee, CarModel
import com.ims.nslmotors.repository.admin.AdminOrderRepository;
import com.ims.nslmotors.repository.admin.AdminCarRepository;
import com.ims.nslmotors.repository.admin.AdminCustomerRepository;
import com.ims.nslmotors.repository.admin.AdminEmployeeRepository;
import com.ims.nslmotors.services.admin.IAdminOrderService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements IAdminOrderService {

    private final AdminOrderRepository orderRepository;
    private final AdminCustomerRepository customerRepository; // FK i?in
    private final AdminEmployeeRepository employeeRepository; // FK i?in
    private final AdminCarRepository carModelRepository; // FK i?in

    // --- Helper: Benzersiz 12 Haneli Sipariş Numarası Üretimi ---
    private String generateUniqueOrderNumber() {
        String orderNumber;
        do {
            // Rastgele 12 basamakl? (10^11 ile 10^12 - 1 aras?) say? ?retilir
            long randomLong = (long) (Math.random() * (999999999999L - 100000000000L) + 100000000000L);
            orderNumber = String.valueOf(randomLong);
            // Benzersiz de?ilse tekrar ?ret
        } while (orderRepository.findByOrderNumber(orderNumber).isPresent());

        return orderNumber;
    }

    // --- Helper: Entity'den DTO'ya Dönüşüm ---
    private DtoAdminOrder convertToDto(Order order) {
        DtoAdminOrder dto = new DtoAdminOrder();
        BeanUtils.copyProperties(order, dto);

        // İlişkisel ID'leri ve Ekstra Bilgileri Doldur
        dto.setCustomerId(order.getCustomer().getId());
        dto.setCustomerFullName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());

        if (order.getTechnician() != null) {
            dto.setTechnicianId(order.getTechnician().getId());
        }

        dto.setCarModelId(order.getCarModel().getId());
        dto.setCarModelMakeAndModel(order.getCarModel().getMake() + " " + order.getCarModel().getModel());

        if (order.getInvoice() != null) {
            dto.setInvoiceId(order.getInvoice().getId());
        }
        return dto;
    }

    // --- CRUD: READ (Sayfaland?rma ve Esnek Arama) ---
    @Override
    public Page<DtoAdminOrder> getOrders(DtoAdminOrder criteria, Pageable pageable) {
        Specification<Order> specification = buildSpecification(criteria);
        Page<Order> orderPage = orderRepository.findAll(specification, pageable);
        return orderPage.map(this::convertToDto);
    }

    // Specification olu?turma (OrderNumber, CustomerID, Status vb. i?in)
    private Specification<Order> buildSpecification(DtoAdminOrder criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Sipari? Numaras?na g?re arama (Tam E?le?me veya LIKE)
            if (criteria.getOrderNumber() != null && !criteria.getOrderNumber().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("orderNumber"), "%" + criteria.getOrderNumber() + "%"));
            }

            // M??teri ID'sine g?re arama
            if (criteria.getCustomerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), criteria.getCustomerId()));
            }

            // Durum (Status) filtresi
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // Daha fazla filtre (Stage, Tarih aral???) buraya eklenebilir.

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // --- CRUD: CREATE (Tekil) ---
    @Override
    public DtoAdminOrder createOrder(DtoAdminOrderIU orderCreationDto) {

        // 1. Gerekli Entity'leri ID'ler ile bul (Foreign Keys)
        Customer customer = customerRepository.findById(orderCreationDto.getCustomerId())
                .orElseThrow(() -> new NoSuchElementException("Müşteri ID " + orderCreationDto.getCustomerId() + " bulunamadı."));

        Car carModel = carModelRepository.findById(orderCreationDto.getCarModelId())
                .orElseThrow(() -> new NoSuchElementException("Araba Modeli ID " + orderCreationDto.getCarModelId() + " bulunamadı."));

        Employee technician = null;
        if (orderCreationDto.getTechnicianId() != null) {
            technician = employeeRepository.findById(orderCreationDto.getTechnicianId())
                    .orElseThrow(() -> new NoSuchElementException("Teknisyen ID " + orderCreationDto.getTechnicianId() + " bulunamadı."));
        }

        // 2. Order Entity'sini oluştur ve zorunlu alanları ata
        Order order = new Order();
        BeanUtils.copyProperties(orderCreationDto, order, "id"); // ID hariç kopyala

        order.setOrderNumber(generateUniqueOrderNumber()); // Otomatik benzersiz numara ata
        order.setCustomer(customer);
        order.setCarModel(carModel);
        order.setTechnician(technician);
        order.setOrderDate(LocalDateTime.now());

        // 3. FATURA ENTITY'SİNİ OLUŞTUR VE ZİNCİRİ KUR (KRİTİK ÇÖZÜM)
        Invoice invoice = createDefaultInvoice(order);

        // Faturayı Siparişe Ekle (Bu, CascadeType.ALL sayesinde Faturanın kaydedilmesini sağlar)
        order.setInvoice(invoice);

        // 4. SADECE Siparişi Kaydet (Invoice otomatik olarak kaydedilir)
        Order savedOrder = orderRepository.save(order);

        return convertToDto(savedOrder);
    }

    private Invoice createDefaultInvoice(Order order) {
        Invoice invoice = new Invoice();

        // Fatura ve Sipari?in ID'si ayn? olmal? (Çünkü @MapsId kullan?l?yor)
        // Ancak ID, Order kaydedildikten sonra DB taraf?ndan ?retildi?i i?in,
        // Order'in ID'sini almak yerine, ?imdilik s?f?r b?rak?p yaln?zca Order objesini set edece?iz.
        // Hibernate save s?ras?nda bunu d?zeltecektir.

        // Önemli: Invoice.id'nin ?retimini hibernate'e b?rak?yoruz.

        invoice.setOrder(order);
        invoice.setInvoiceDate(LocalDateTime.now());

        // Varsayılan Muhasebe Değerleri (Sıfır olarak başlatılır)
        invoice.setSubtotalAmount(BigDecimal.ZERO);
        invoice.setTaxRate(new BigDecimal("0.00")); // %0 Vergi
        invoice.setTaxAmount(BigDecimal.ZERO);
        invoice.setTotalAmount(BigDecimal.ZERO);

        // Diğer alanlar
        invoice.setPaymentMethod("UNPAID");
        invoice.setStatus("PENDING");

        return invoice;
    }

    // --- CRUD: UPDATE ---
    @Override
    public DtoAdminOrder updateOrder(Long id, DtoAdminOrderIU updateDto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID " + id + " ile sipari? bulunamad?."));

        // Sadece DTO'da gelen ve Entity'ye ait alanlar? g?ncelle
        // Sipari? numaras?, m??teri ve olu?turulma tarihi G?NCELLENMEZ
        existingOrder.setStatus(updateDto.getStatus());
        existingOrder.setStageSelected(updateDto.getStageSelected());
        existingOrder.setAppointmentDate(updateDto.getAppointmentDate());

        // Teknisyen güncelleme
        if (updateDto.getTechnicianId() != null) {
            Employee newTechnician = employeeRepository.findById(updateDto.getTechnicianId())
                    .orElseThrow(() -> new NoSuchElementException("Teknisyen ID " + updateDto.getTechnicianId() + " bulunamad?."));
            existingOrder.setTechnician(newTechnician);
        } else {
            existingOrder.setTechnician(null);
        }

        Order updatedOrder = orderRepository.save(existingOrder);
        return convertToDto(updatedOrder);
    }

    // --- CRUD: DELETE ---
    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NoSuchElementException("ID " + id + " ile sipari? bulunamad??? i?in silinemedi.");
        }
        orderRepository.deleteById(id);
    }

    // ... (createOrdersBulk metodu buraya eklenebilir) ...
}