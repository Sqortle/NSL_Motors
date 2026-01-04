// Admin Panel JavaScript

// Global state
let currentPage = {
    customers: 0,
    employees: 0,
    cars: 0,
    orders: 0,
    invoices: 0
};

const pageSize = 10;

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    initializeTabs();
    loadInitialData();
});

// Tab Management
function initializeTabs() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabName = btn.getAttribute('data-tab');
            switchTab(tabName);
        });
    });
}

function switchTab(tabName) {
    // Update active tab button
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');

    // Update active tab content
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    document.getElementById(`${tabName}-tab`).classList.add('active');

    // Load data for the active tab
    loadTabData(tabName);
}

function loadInitialData() {
    loadTabData('customers');
}

function loadTabData(tabName) {
    switch(tabName) {
        case 'customers':
            loadCustomers();
            break;
        case 'employees':
            loadEmployees();
            break;
        case 'cars':
            loadCars();
            break;
        case 'orders':
            loadOrders();
            break;
        case 'invoices':
            loadInvoices();
            break;
    }
}

// Customers Management
async function loadCustomers(page = 0) {
    try {
        const response = await fetch(`/api/admin/customers/list?page=${page}&size=${pageSize}`);
        const data = await response.json();
        currentPage.customers = page;
        renderCustomersTable(data.content);
        renderPagination('customers-pagination', data, loadCustomers);
    } catch (error) {
        console.error('Error loading customers:', error);
        showError('Müşteriler yüklenirken bir hata oluştu.');
    }
}

function renderCustomersTable(customers) {
    const tbody = document.getElementById('customers-tbody');
    if (customers.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="loading">Kayıt bulunamadı.</td></tr>';
        return;
    }
    
    tbody.innerHTML = customers.map(customer => `
        <tr>
            <td>${customer.id}</td>
            <td>${customer.firstName || ''}</td>
            <td>${customer.lastName || ''}</td>
            <td>${customer.email || ''}</td>
            <td>${customer.phoneNumber || '-'}</td>
            <td class="btn-actions">
                <button class="btn btn-edit" onclick="editCustomer(${customer.id})">Düzenle</button>
                <button class="btn btn-delete" onclick="deleteCustomer(${customer.id})">Sil</button>
            </td>
        </tr>
    `).join('');
}

function openCustomerModal(customerId = null) {
    const modal = createModal(
        customerId ? 'Kullanıcı Düzenle' : 'Yeni Kullanıcı Ekle',
        `
            <form id="customer-form">
                <div class="form-group">
                    <label>Ad *</label>
                    <input type="text" id="customer-firstName" required>
                </div>
                <div class="form-group">
                    <label>Soyad *</label>
                    <input type="text" id="customer-lastName" required>
                </div>
                <div class="form-group">
                    <label>Email *</label>
                    <input type="email" id="customer-email" required>
                </div>
                <div class="form-group">
                    <label>Şifre ${customerId ? '(Boş bırakırsanız değişmez)' : '*'}</label>
                    <input type="password" id="customer-password" ${customerId ? '' : 'required'}>
                </div>
                <div class="form-group">
                    <label>Telefon</label>
                    <input type="text" id="customer-phoneNumber">
                </div>
            </form>
        `,
        () => saveCustomer(customerId)
    );

    if (customerId) {
        // Load customer data - search by ID
        fetch(`/api/admin/customers/list?id=${customerId}&page=0&size=1`)
            .then(res => res.json())
            .then(data => {
                const customer = data.content.find(c => c.id === customerId);
                if (customer) {
                    document.getElementById('customer-firstName').value = customer.firstName || '';
                    document.getElementById('customer-lastName').value = customer.lastName || '';
                    document.getElementById('customer-email').value = customer.email || '';
                    document.getElementById('customer-phoneNumber').value = customer.phoneNumber || '';
                }
            })
            .catch(error => {
                console.error('Error loading customer:', error);
                showError('Müşteri bilgileri yüklenirken bir hata oluştu.');
            });
    }
}

async function saveCustomer(customerId) {
    const formData = {
        id: customerId || null,
        firstName: document.getElementById('customer-firstName').value,
        lastName: document.getElementById('customer-lastName').value,
        email: document.getElementById('customer-email').value,
        phoneNumber: document.getElementById('customer-phoneNumber').value
    };

    const password = document.getElementById('customer-password').value;
    if (password || !customerId) {
        formData.password = password;
    }

    try {
        const url = customerId 
            ? `/api/admin/customers/update/${customerId}`
            : '/api/admin/customers/add';
        const method = customerId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            closeModal();
            loadCustomers(currentPage.customers);
            showSuccess(customerId ? 'Kullanıcı güncellendi.' : 'Kullanıcı eklendi.');
        } else {
            const error = await response.text();
            showError('Hata: ' + error);
        }
    } catch (error) {
        console.error('Error saving customer:', error);
        showError('Kayıt sırasında bir hata oluştu.');
    }
}

function editCustomer(id) {
    openCustomerModal(id);
}

async function deleteCustomer(id) {
    if (!confirm('Bu kullanıcıyı silmek istediğinize emin misiniz?')) return;

    try {
        const response = await fetch(`/api/admin/customers/delete/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadCustomers(currentPage.customers);
            showSuccess('Kullanıcı silindi.');
        } else {
            showError('Silme işlemi başarısız.');
        }
    } catch (error) {
        console.error('Error deleting customer:', error);
        showError('Silme sırasında bir hata oluştu.');
    }
}

// Employees Management
let employeeFilters = {
    role: '',
    firstName: '',
    email: '',
    sort: ''
};

async function loadEmployees(page = 0) {
    try {
        // Build query parameters
        let queryParams = `page=${page}&size=${pageSize}`;
        
        if (employeeFilters.role) {
            queryParams += `&role=${encodeURIComponent(employeeFilters.role)}`;
        }
        if (employeeFilters.firstName) {
            queryParams += `&firstName=${encodeURIComponent(employeeFilters.firstName)}`;
        }
        if (employeeFilters.email) {
            queryParams += `&email=${encodeURIComponent(employeeFilters.email)}`;
        }
        if (employeeFilters.sort) {
            const [property, direction] = employeeFilters.sort.split(',');
            queryParams += `&sort=${property},${direction}`;
        }
        
        const response = await fetch(`/api/admin/employees/list?${queryParams}`);
        const data = await response.json();
        currentPage.employees = page;
        renderEmployeesTable(data.content);
        renderPagination('employees-pagination', data, loadEmployees);
    } catch (error) {
        console.error('Error loading employees:', error);
        showError('Ustalar yüklenirken bir hata oluştu.');
    }
}

function applyEmployeeFilters() {
    employeeFilters.role = document.getElementById('employee-filter-role').value.trim();
    employeeFilters.firstName = document.getElementById('employee-filter-firstName').value.trim();
    employeeFilters.email = document.getElementById('employee-filter-email').value.trim();
    employeeFilters.sort = document.getElementById('employee-sort-by').value;
    loadEmployees(0);
}

function clearEmployeeFilters() {
    document.getElementById('employee-filter-role').value = '';
    document.getElementById('employee-filter-firstName').value = '';
    document.getElementById('employee-filter-email').value = '';
    document.getElementById('employee-sort-by').value = '';
    employeeFilters = { role: '', firstName: '', email: '', sort: '' };
    loadEmployees(0);
}

function renderEmployeesTable(employees) {
    const tbody = document.getElementById('employees-tbody');
    if (employees.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="loading">Kayıt bulunamadı.</td></tr>';
        return;
    }
    
    tbody.innerHTML = employees.map(emp => `
        <tr>
            <td>${emp.id}</td>
            <td>${emp.role || ''}</td>
            <td>${emp.firstName || ''}</td>
            <td>${emp.lastName || ''}</td>
            <td>${emp.email || ''}</td>
            <td>${emp.phoneNumber || '-'}</td>
            <td>${emp.shopName || '-'}</td>
            <td class="btn-actions">
                <button class="btn btn-edit" onclick="editEmployee(${emp.id})">Düzenle</button>
                <button class="btn btn-delete" onclick="deleteEmployee(${emp.id})">Sil</button>
            </td>
        </tr>
    `).join('');
}

function openEmployeeModal(employeeId = null) {
    // Kullanıcının rolüne göre uygun rol seçeneklerini belirle
    const isOwner = (currentUserRole === 'OWNER');
    const isOwnerOrMaster = isOwner || isMasterUser;
    let roleOptions = '<option value="">Seçiniz</option>';

    // Sadece OWNER, OWNER rolünü görebilir ve atayabilir
    if (isOwner) {
        roleOptions += '<option value="OWNER">OWNER</option>';
    }

    // OWNER veya master ADMIN rolünü görebilir ve atayabilir
    if (isOwnerOrMaster) {
        roleOptions += '<option value="ADMIN">ADMIN</option>';
    }

    // Herkes TECHNICIAN ve MASTER ekleyebilir
    roleOptions += `
        <option value="TECHNICIAN">TECHNICIAN</option>
        <option value="MASTER">MASTER</option>
    `;

    const modal = createModal(
        employeeId ? 'Usta Düzenle' : 'Yeni Usta Ekle',
        `
            <form id="employee-form">
                <div class="form-group">
                    <label>Rol *</label>
                    <select id="employee-role" required>
                        ${roleOptions}
                    </select>
                </div>
                <div class="form-group">
                    <label>Ad *</label>
                    <input type="text" id="employee-firstName" required>
                </div>
                <div class="form-group">
                    <label>Soyad *</label>
                    <input type="text" id="employee-lastName" required>
                </div>
                <div class="form-group">
                    <label>Email *</label>
                    <input type="email" id="employee-email" required>
                </div>
                <div class="form-group">
                    <label>Şifre ${employeeId ? '(Boş bırakırsanız değişmez)' : '*'}</label>
                    <input type="password" id="employee-password" ${employeeId ? '' : 'required'}>
                </div>
                <div class="form-group">
                    <label>Telefon</label>
                    <input type="text" id="employee-phoneNumber">
                </div>
                <div class="form-group">
                    <label>Dükkan Adı</label>
                    <input type="text" id="employee-shopName">
                </div>
                <div class="form-group">
                    <label>Adres</label>
                    <textarea id="employee-address"></textarea>
                </div>
                <div class="form-group">
                    <label>TC Kimlik No (11 haneli)</label>
                    <input type="text" id="employee-tcKimlikNo" maxlength="11">
                </div>
            </form>
        `,
        () => saveEmployee(employeeId)
    );

    if (employeeId) {
        fetch(`/api/admin/employees/list?page=0&size=100`)
            .then(res => res.json())
            .then(data => {
                const employee = data.content.find(e => e.id === employeeId);
                if (employee) {
                    document.getElementById('employee-role').value = employee.role || '';
                    document.getElementById('employee-firstName').value = employee.firstName || '';
                    document.getElementById('employee-lastName').value = employee.lastName || '';
                    document.getElementById('employee-email').value = employee.email || '';
                    document.getElementById('employee-phoneNumber').value = employee.phoneNumber || '';
                    document.getElementById('employee-shopName').value = employee.shopName || '';
                    document.getElementById('employee-address').value = employee.address || '';
                    document.getElementById('employee-tcKimlikNo').value = employee.tcKimlikNo || '';
                }
            });
    }
}

async function saveEmployee(employeeId) {
    const formData = {
        id: employeeId || null,
        role: document.getElementById('employee-role').value,
        firstName: document.getElementById('employee-firstName').value,
        lastName: document.getElementById('employee-lastName').value,
        email: document.getElementById('employee-email').value,
        phoneNumber: document.getElementById('employee-phoneNumber').value,
        shopName: document.getElementById('employee-shopName').value,
        address: document.getElementById('employee-address').value,
        tcKimlikNo: document.getElementById('employee-tcKimlikNo').value
    };

    const password = document.getElementById('employee-password').value;
    if (password || !employeeId) {
        formData.password = password;
    }

    try {
        const url = employeeId 
            ? `/api/admin/employees/update/${employeeId}`
            : '/api/admin/employees/add';
        const method = employeeId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            closeModal();
            loadEmployees(currentPage.employees);
            showSuccess(employeeId ? 'Usta güncellendi.' : 'Usta eklendi.');
        } else {
            const error = await response.text();
            showError('Hata: ' + error);
        }
    } catch (error) {
        console.error('Error saving employee:', error);
        showError('Kayıt sırasında bir hata oluştu.');
    }
}

function editEmployee(id) {
    openEmployeeModal(id);
}

async function deleteEmployee(id) {
    if (!confirm('Bu ustayı silmek istediğinize emin misiniz?')) return;

    try {
        const response = await fetch(`/api/admin/employees/delete/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadEmployees(currentPage.employees);
            showSuccess('Usta silindi.');
        } else {
            showError('Silme işlemi başarısız.');
        }
    } catch (error) {
        console.error('Error deleting employee:', error);
        showError('Silme sırasında bir hata oluştu.');
    }
}

// Cars Management
let carFilters = {
    make: '',
    model: '',
    year: '',
    sort: ''
};

async function loadCars(page = 0) {
    try {
        // Build query parameters
        let queryParams = `page=${page}&size=${pageSize}`;
        
        if (carFilters.make) {
            queryParams += `&make=${encodeURIComponent(carFilters.make)}`;
        }
        if (carFilters.model) {
            queryParams += `&model=${encodeURIComponent(carFilters.model)}`;
        }
        if (carFilters.year) {
            queryParams += `&year=${encodeURIComponent(carFilters.year)}`;
        }
        if (carFilters.sort) {
            const [property, direction] = carFilters.sort.split(',');
            queryParams += `&sort=${property},${direction}`;
        }
        
        const response = await fetch(`/api/admin/cars/list?${queryParams}`);
        const data = await response.json();
        currentPage.cars = page;
        renderCarsTable(data.content);
        renderPagination('cars-pagination', data, loadCars);
    } catch (error) {
        console.error('Error loading cars:', error);
        showError('Arabalar yüklenirken bir hata oluştu.');
    }
}

function applyCarFilters() {
    carFilters.make = document.getElementById('car-filter-make').value.trim();
    carFilters.model = document.getElementById('car-filter-model').value.trim();
    carFilters.year = document.getElementById('car-filter-year').value.trim();
    carFilters.sort = document.getElementById('car-sort-by').value;
    loadCars(0);
}

function clearCarFilters() {
    document.getElementById('car-filter-make').value = '';
    document.getElementById('car-filter-model').value = '';
    document.getElementById('car-filter-year').value = '';
    document.getElementById('car-sort-by').value = '';
    carFilters = { make: '', model: '', year: '', sort: '' };
    loadCars(0);
}

function renderCarsTable(cars) {
    const tbody = document.getElementById('cars-tbody');
    if (cars.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="loading">Kayıt bulunamadı.</td></tr>';
        return;
    }
    tbody.innerHTML = cars.map(car => `
        <tr>
            <td>${car.id}</td>
            <td>${car.make || ''}</td>
            <td>${car.model || ''}</td>
            <td>${car.year || ''}</td>
            <td>${car.stockHp || '-'}</td>
            <td>${car.stage1Hp || '-'}</td>
            <td>${car.stage2Hp || '-'}</td>
            <td>${car.stage3Hp || '-'}</td>
            <td class="btn-actions">
                <button class="btn btn-edit" onclick="editCar(${car.id})">Düzenle</button>
                <button class="btn btn-delete" onclick="deleteCar(${car.id})">Sil</button>
            </td>
        </tr>
    `).join('');
}

function openCarModal(carId = null) {
    createModal(
        carId ? 'Araba Düzenle' : 'Yeni Araba Ekle',
        `
            <form id="car-form">
                <div class="form-group">
                    <label>Marka *</label>
                    <input type="text" id="car-make" required>
                </div>
                <div class="form-group">
                    <label>Model *</label>
                    <input type="text" id="car-model" required>
                </div>
                <div class="form-group">
                    <label>Yıl *</label>
                    <input type="number" id="car-year" min="1900" required>
                </div>
                <div class="form-group">
                    <label>Stok HP *</label>
                    <input type="number" id="car-stockHp" min="50" required>
                </div>
                <div class="form-group">
                    <label>Stage 1 HP</label>
                    <input type="number" id="car-stage1Hp" min="0">
                </div>
                <div class="form-group">
                    <label>Stage 2 HP</label>
                    <input type="number" id="car-stage2Hp" min="0">
                </div>
                <div class="form-group">
                    <label>Stage 3 HP</label>
                    <input type="number" id="car-stage3Hp" min="0">
                </div>
                <div class="form-group">
                    <label>Stage 1 Fiyat *</label>
                    <input type="number" id="car-stage1Price" step="0.01" min="0" required>
                </div>
                <div class="form-group">
                    <label>Stage 2 Fiyat *</label>
                    <input type="number" id="car-stage2Price" step="0.01" min="0" required>
                </div>
                <div class="form-group">
                    <label>Stage 3 Fiyat *</label>
                    <input type="number" id="car-stage3Price" step="0.01" min="0" required>
                </div>
                <div class="form-group">
                    <label>Araba Resim URL</label>
                    <input type="text" id="car-carImageUrl">
                </div>
                <div class="form-group">
                    <label>Marka Resim URL</label>
                    <input type="text" id="car-makeImageUrl" placeholder="Marka logosu/fotoğrafı için URL">
                </div>
            </form>
        `,
        () => saveCar(carId)
    );

    if (carId) {
        fetch(`/api/admin/cars/list?page=0&size=100`)
            .then(res => res.json())
            .then(data => {
                const car = data.content.find(c => c.id === carId);
                if (car) {
                    document.getElementById('car-make').value = car.make || '';
                    document.getElementById('car-model').value = car.model || '';
                    document.getElementById('car-year').value = car.year || '';
                    document.getElementById('car-stockHp').value = car.stockHp || '';
                    document.getElementById('car-stage1Hp').value = car.stage1Hp || '';
                    document.getElementById('car-stage2Hp').value = car.stage2Hp || '';
                    document.getElementById('car-stage3Hp').value = car.stage3Hp || '';
                    document.getElementById('car-stage1Price').value = car.stage1Price || '';
                    document.getElementById('car-stage2Price').value = car.stage2Price || '';
                    document.getElementById('car-stage3Price').value = car.stage3Price || '';
                    document.getElementById('car-carImageUrl').value = car.carImageUrl || '';
                    document.getElementById('car-makeImageUrl').value = car.makeImageUrl || '';
                }
            });
    }
}

async function saveCar(carId) {
    const formData = {
        id: carId || null,
        make: document.getElementById('car-make').value,
        model: document.getElementById('car-model').value,
        year: parseInt(document.getElementById('car-year').value),
        stockHp: parseInt(document.getElementById('car-stockHp').value),
        stage1Hp: parseInt(document.getElementById('car-stage1Hp').value) || null,
        stage2Hp: parseInt(document.getElementById('car-stage2Hp').value) || null,
        stage3Hp: parseInt(document.getElementById('car-stage3Hp').value) || null,
        stage1Price: parseFloat(document.getElementById('car-stage1Price').value),
        stage2Price: parseFloat(document.getElementById('car-stage2Price').value),
        stage3Price: parseFloat(document.getElementById('car-stage3Price').value),
        carImageUrl: document.getElementById('car-carImageUrl').value || null,
        makeImageUrl: document.getElementById('car-makeImageUrl').value || null
    };

    try {
        const url = carId 
            ? `/api/admin/cars/update/${carId}`
            : '/api/admin/cars/add';
        const method = carId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            closeModal();
            loadCars(currentPage.cars);
            showSuccess(carId ? 'Araba güncellendi.' : 'Araba eklendi.');
        } else {
            const error = await response.text();
            showError('Hata: ' + error);
        }
    } catch (error) {
        console.error('Error saving car:', error);
        showError('Kayıt sırasında bir hata oluştu.');
    }
}

function editCar(id) {
    openCarModal(id);
}

async function deleteCar(id) {
    if (!confirm('Bu arabayı silmek istediğinize emin misiniz?')) return;

    try {
        const response = await fetch(`/api/admin/cars/delete/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadCars(currentPage.cars);
            showSuccess('Araba silindi.');
        } else {
            showError('Silme işlemi başarısız.');
        }
    } catch (error) {
        console.error('Error deleting car:', error);
        showError('Silme sırasında bir hata oluştu.');
    }
}

// Orders Management
async function loadOrders(page = 0) {
    const tbody = document.getElementById('orders-tbody');
    if (!tbody) {
        console.error('orders-tbody element not found');
        return;
    }
    
    try {
        tbody.innerHTML = '<tr><td colspan="9" class="loading">Yükleniyor...</td></tr>';
        
        const response = await fetch(`/api/admin/orders/list?page=${page}&size=${pageSize}`);
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Error loading orders:', response.status, errorText);
            showError(`Siparişler yüklenirken bir hata oluştu. (${response.status})`);
            tbody.innerHTML = '<tr><td colspan="9" class="loading">Siparişler yüklenemedi. Status: ' + response.status + '</td></tr>';
            return;
        }
        
        const data = await response.json();
        console.log('Orders data:', data);
        currentPage.orders = page;
        renderOrdersTable(data.content || []);
        renderPagination('orders-pagination', data, loadOrders);
    } catch (error) {
        console.error('Error loading orders:', error);
        showError('Siparişler yüklenirken bir hata oluştu: ' + error.message);
        tbody.innerHTML = '<tr><td colspan="9" class="loading">Siparişler yüklenemedi: ' + error.message + '</td></tr>';
    }
}

function renderOrdersTable(orders) {
    const tbody = document.getElementById('orders-tbody');
    if (orders.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="loading">Kayıt bulunamadı.</td></tr>';
        return;
    }
    tbody.innerHTML = orders.map(order => {
        const statusClass = getStatusClass(order.status);
        const statusText = getStatusText(order.status);
        return `
        <tr>
            <td>${order.id}</td>
            <td>${order.orderNumber || ''}</td>
            <td>${order.customerFullName || '-'}</td>
            <td>${order.carModelMakeAndModel || '-'}</td>
            <td>${order.stageSelected || ''}</td>
            <td>${order.orderDate ? new Date(order.orderDate).toLocaleDateString('tr-TR') : '-'}</td>
            <td>${order.appointmentDate ? new Date(order.appointmentDate).toLocaleDateString('tr-TR') : '-'}</td>
            <td><span class="status-badge ${statusClass}">${statusText}</span></td>
            <td class="btn-actions">
                <button class="btn btn-sm btn-primary" onclick="updateOrderStatus(${order.id}, '${order.status || ''}')">Durum Güncelle</button>
                <button class="btn btn-edit" onclick="editOrder(${order.id})">Düzenle</button>
                <button class="btn btn-delete" onclick="deleteOrder(${order.id})">Sil</button>
            </td>
        </tr>
    `;
    }).join('');
}

function openOrderModal(orderId = null) {
    // Note: This is a simplified version. You may need to load customers and cars for dropdowns
    createModal(
        orderId ? 'Sipariş Düzenle' : 'Yeni Sipariş Ekle',
        `
            <form id="order-form">
                <div class="form-group">
                    <label>Müşteri ID *</label>
                    <input type="number" id="order-customerId" required>
                </div>
                <div class="form-group">
                    <label>Teknisyen ID</label>
                    <input type="number" id="order-technicianId">
                </div>
                <div class="form-group">
                    <label>Araba Model ID *</label>
                    <input type="number" id="order-carModelId" required>
                </div>
                <div class="form-group">
                    <label>Stage * (STAGE1 veya STAGE2)</label>
                    <select id="order-stageSelected" required>
                        <option value="">Seçiniz</option>
                        <option value="STAGE1">STAGE1</option>
                        <option value="STAGE2">STAGE2</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Randevu Tarihi</label>
                    <input type="datetime-local" id="order-appointmentDate">
                </div>
                <div class="form-group">
                    <label>Durum *</label>
                    <input type="text" id="order-status" required>
                </div>
            </form>
        `,
        () => saveOrder(orderId)
    );

    if (orderId) {
        fetch(`/api/admin/orders/list?page=0&size=100`)
            .then(res => res.json())
            .then(data => {
                const order = data.content.find(o => o.id === orderId);
                if (order) {
                    document.getElementById('order-customerId').value = order.customerId || '';
                    document.getElementById('order-technicianId').value = order.technicianId || '';
                    document.getElementById('order-carModelId').value = order.carModelId || '';
                    document.getElementById('order-stageSelected').value = order.stageSelected || '';
                    document.getElementById('order-status').value = order.status || '';
                    if (order.appointmentDate) {
                        const date = new Date(order.appointmentDate);
                        document.getElementById('order-appointmentDate').value = date.toISOString().slice(0, 16);
                    }
                }
            });
    }
}

async function saveOrder(orderId) {
    const formData = {
        id: orderId || null,
        customerId: parseInt(document.getElementById('order-customerId').value),
        technicianId: parseInt(document.getElementById('order-technicianId').value) || null,
        carModelId: parseInt(document.getElementById('order-carModelId').value),
        stageSelected: document.getElementById('order-stageSelected').value,
        status: document.getElementById('order-status').value
    };

    const appointmentDate = document.getElementById('order-appointmentDate').value;
    if (appointmentDate) {
        formData.appointmentDate = new Date(appointmentDate).toISOString();
    }

    try {
        const url = orderId 
            ? `/api/admin/orders/update/${orderId}`
            : '/api/admin/orders/add';
        const method = orderId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            closeModal();
            loadOrders(currentPage.orders);
            showSuccess(orderId ? 'Sipariş güncellendi.' : 'Sipariş eklendi.');
        } else {
            const error = await response.text();
            showError('Hata: ' + error);
        }
    } catch (error) {
        console.error('Error saving order:', error);
        showError('Kayıt sırasında bir hata oluştu.');
    }
}

function editOrder(id) {
    openOrderModal(id);
}

function getStatusClass(status) {
    if (!status) return 'status-beklemede';
    const s = status.toUpperCase();
    if (s === 'BEKLEMEDE' || s === 'PENDING') return 'status-beklemede';
    if (s === 'ONAYLANDI' || s === 'CONFIRMED') return 'status-onaylandi';
    if (s === 'HAZIRLANIYOR' || s === 'IN_PROGRESS') return 'status-hazirlaniyor';
    if (s === 'TAMAMLANDI' || s === 'COMPLETED') return 'status-tamamlandi';
    if (s === 'IPTAL' || s === 'CANCELLED') return 'status-iptal';
    return 'status-beklemede';
}

function getStatusText(status) {
    if (!status) return 'Beklemede';
    const s = status.toUpperCase();
    if (s === 'BEKLEMEDE' || s === 'PENDING') return 'Beklemede';
    if (s === 'ONAYLANDI' || s === 'CONFIRMED') return 'Onaylandı';
    if (s === 'HAZIRLANIYOR' || s === 'IN_PROGRESS') return 'Hazırlanıyor';
    if (s === 'TAMAMLANDI' || s === 'COMPLETED') return 'Tamamlandı';
    if (s === 'IPTAL' || s === 'CANCELLED') return 'İptal';
    return status;
}

function updateOrderStatus(orderId, currentStatus) {
    createModal(
        'Sipariş Durumu Güncelle',
        `
            <form id="status-form">
                <div class="form-group">
                    <label>Yeni Durum:</label>
                    <select id="order-status-select" required>
                        <option value="">Seçiniz</option>
                        <option value="BEKLEMEDE" ${currentStatus === 'BEKLEMEDE' ? 'selected' : ''}>Beklemede</option>
                        <option value="ONAYLANDI" ${currentStatus === 'ONAYLANDI' ? 'selected' : ''}>Onaylandı</option>
                        <option value="HAZIRLANIYOR" ${currentStatus === 'HAZIRLANIYOR' ? 'selected' : ''}>Hazırlanıyor</option>
                        <option value="TAMAMLANDI" ${currentStatus === 'TAMAMLANDI' ? 'selected' : ''}>Tamamlandı</option>
                        <option value="IPTAL" ${currentStatus === 'IPTAL' ? 'selected' : ''}>İptal</option>
                    </select>
                </div>
            </form>
        `,
        () => saveOrderStatus(orderId)
    );
}

async function saveOrderStatus(orderId) {
    const newStatus = document.getElementById('order-status-select').value;
    if (!newStatus) {
        showError('Lütfen bir durum seçin.');
        return;
    }

    try {
        // Önce mevcut order'ı al
        const orderResponse = await fetch(`/api/admin/orders/list?page=0&size=100`);
        const orderData = await orderResponse.json();
        const order = orderData.content.find(o => o.id === orderId);
        
        if (!order) {
            showError('Sipariş bulunamadı.');
            return;
        }

        // Status'u güncelle
        const updateData = {
            customerId: order.customerId,
            technicianId: order.technicianId,
            carModelId: order.carModelId,
            stageSelected: order.stageSelected,
            status: newStatus,
            appointmentDate: order.appointmentDate
        };

        const response = await fetch(`/api/admin/orders/update/${orderId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updateData)
        });

        if (response.ok) {
            closeModal();
            loadOrders(currentPage.orders);
            showSuccess('Sipariş durumu güncellendi.');
        } else {
            const error = await response.text();
            showError('Hata: ' + error);
        }
    } catch (error) {
        console.error('Error updating order status:', error);
        showError('Durum güncellenirken bir hata oluştu.');
    }
}

async function deleteOrder(id) {
    if (!confirm('Bu siparişi silmek istediğinize emin misiniz?')) return;

    try {
        const response = await fetch(`/api/admin/orders/delete/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadOrders(currentPage.orders);
            showSuccess('Sipariş silindi.');
        } else {
            showError('Silme işlemi başarısız.');
        }
    } catch (error) {
        console.error('Error deleting order:', error);
        showError('Silme sırasında bir hata oluştu.');
    }
}

// Invoices Management
async function loadInvoices(page = 0) {
    const tbody = document.getElementById('invoices-tbody');
    if (!tbody) {
        console.error('invoices-tbody element not found');
        return;
    }
    
    try {
        tbody.innerHTML = '<tr><td colspan="9" class="loading">Yükleniyor...</td></tr>';
        
        const response = await fetch(`/api/admin/invoices/list?page=${page}&size=${pageSize}`);
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Error loading invoices:', response.status, errorText);
            showError(`Faturalar yüklenirken bir hata oluştu. (${response.status})`);
            tbody.innerHTML = '<tr><td colspan="9" class="loading">Faturalar yüklenemedi. Status: ' + response.status + '</td></tr>';
            return;
        }
        
        const data = await response.json();
        console.log('Invoices data:', data);
        currentPage.invoices = page;
        renderInvoicesTable(data.content || []);
        renderPagination('invoices-pagination', data, loadInvoices);
    } catch (error) {
        console.error('Error loading invoices:', error);
        showError('Faturalar yüklenirken bir hata oluştu: ' + error.message);
        tbody.innerHTML = '<tr><td colspan="9" class="loading">Faturalar yüklenemedi: ' + error.message + '</td></tr>';
    }
}


function renderInvoicesTable(invoices) {
    const tbody = document.getElementById('invoices-tbody');
    if (invoices.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="loading">Kayıt bulunamadı.</td></tr>';
        return;
    }
    tbody.innerHTML = invoices.map(invoice => `
        <tr>
            <td>${invoice.id}</td>
            <td>${invoice.orderNumber || '-'}</td>
            <td>${invoice.invoiceDate ? new Date(invoice.invoiceDate).toLocaleDateString('tr-TR') : '-'}</td>
            <td>${invoice.subtotalAmount ? invoice.subtotalAmount.toFixed(2) : '-'} ₺</td>
            <td>${invoice.taxAmount ? invoice.taxAmount.toFixed(2) : '-'} ₺</td>
            <td>${invoice.totalAmount ? invoice.totalAmount.toFixed(2) : '-'} ₺</td>
            <td>${invoice.paymentDate ? new Date(invoice.paymentDate).toLocaleDateString('tr-TR') : '-'}</td>
            <td>${invoice.paymentMethod || '-'}</td>
            <td>${invoice.status || '-'}</td>
        </tr>
    `).join('');
}

// Modal Management
function createModal(title, body, onSave) {
    const modalContainer = document.getElementById('modal-container');
    const modalId = 'modal-' + Date.now();
    modalContainer.innerHTML = `
        <div class="modal-overlay" id="${modalId}" onclick="if(event.target === this) closeModal()">
            <div class="modal">
                <div class="modal-header">
                    <h3>${title}</h3>
                    <button class="modal-close" onclick="closeModal()">&times;</button>
                </div>
                <div class="modal-body">
                    ${body}
                </div>
                <div class="modal-footer">
                    <button class="btn btn-secondary" onclick="closeModal()">İptal</button>
                    <button class="btn btn-primary" id="modal-save-btn">Kaydet</button>
                </div>
            </div>
        </div>
    `;
    // Fix the save button
    const saveBtn = modalContainer.querySelector('#modal-save-btn');
    saveBtn.onclick = () => {
        onSave();
    };
}

function closeModal() {
    document.getElementById('modal-container').innerHTML = '';
}

// Pagination
function renderPagination(containerId, pageData, loadFunction) {
    const container = document.getElementById(containerId);
    if (!container) return;

    const totalPages = pageData.totalPages;
    const currentPageNum = pageData.number;

    let html = '';
    
    if (totalPages > 1) {
        html += `<button ${currentPageNum === 0 ? 'disabled' : ''} onclick="${loadFunction.name}(0)">İlk</button>`;
        html += `<button ${currentPageNum === 0 ? 'disabled' : ''} onclick="${loadFunction.name}(${currentPageNum - 1})">Önceki</button>`;
        html += `<span class="page-info">Sayfa ${currentPageNum + 1} / ${totalPages}</span>`;
        html += `<button ${currentPageNum >= totalPages - 1 ? 'disabled' : ''} onclick="${loadFunction.name}(${currentPageNum + 1})">Sonraki</button>`;
        html += `<button ${currentPageNum >= totalPages - 1 ? 'disabled' : ''} onclick="${loadFunction.name}(${totalPages - 1})">Son</button>`;
    }

    container.innerHTML = html;
}

// Alert Messages
function showSuccess(message) {
    showAlert(message, 'success');
}

function showError(message) {
    showAlert(message, 'error');
}

function showAlert(message, type) {
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    alert.style.position = 'fixed';
    alert.style.top = '20px';
    alert.style.right = '20px';
    alert.style.zIndex = '10000';
    alert.style.minWidth = '300px';
    
    document.body.appendChild(alert);
    
    setTimeout(() => {
        alert.remove();
    }, 5000);
}

