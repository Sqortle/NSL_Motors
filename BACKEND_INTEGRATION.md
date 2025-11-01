# Spring Boot Backend Integration Guide

## Overview
This frontend is built with a modular API client structure designed to seamlessly integrate with a Spring Boot backend. The project uses TypeScript and provides a clean abstraction layer for all API communications.

## API Client Architecture

### Main Files
- **`src/api/client.ts`** - Core HTTP client with authentication
- **`src/api/services.ts`** - Service modules for different features

### Setup

#### 1. Configure Backend URL
Update `.env` file with your Spring Boot backend URL:

```env
VITE_API_URL=http://localhost:8080/api
```

For production, update to your production server:
```env
VITE_API_URL=https://api.nslmotors.com/api
```

#### 2. Authentication Flow

The client automatically handles JWT token management:

```typescript
import { authService, setAuthToken, isAuthenticated } from '@/api/services';

// Login
const response = await authService.login('user@example.com', 'password');
if (response.success) {
  // Token is automatically stored in localStorage
  console.log('Logged in:', response.data?.user);
}

// Check authentication status
if (isAuthenticated()) {
  // User is authenticated
}

// Logout
await authService.logout();
```

### API Services

All services use the `apiClient` which automatically handles:
- Authorization headers (Bearer token)
- Error handling
- Authentication state management
- Automatic re-authentication on 401 responses

#### Authentication Service
```typescript
authService.login(email, password)
authService.register(email, password, name)
authService.logout()
authService.getCurrentUser()
authService.updateProfile(name, phone)
authService.changePassword(currentPassword, newPassword)
```

#### Cars Service
```typescript
carsService.getAllCars()
carsService.getCarById(id)
carsService.getTuningStages(carId)
carsService.createCar(make, model, stockHp)
carsService.updateCar(id, make, model, stockHp)
carsService.deleteCar(id)
```

#### Appointments Service
```typescript
appointmentService.getMyAppointments()
appointmentService.getAllAppointments()
appointmentService.getAppointmentById(id)
appointmentService.createAppointment(carId, stageId, appointmentDate)
appointmentService.updateAppointmentStatus(id, status)
appointmentService.assignTechnician(appointmentId, technicianId)
appointmentService.cancelAppointment(id)
```

#### Admin Service
```typescript
adminService.createUser(email, password, name, role)
adminService.getAllUsers()
adminService.getUserById(id)
adminService.updateUser(id, updates)
adminService.deleteUser(id)
adminService.getAppointmentStats()
```

## Backend API Endpoints Required

### Authentication (`/api/auth`)
- `POST /auth/login` - User login
- `POST /auth/register` - New user registration
- `GET /auth/me` - Get current user
- `PUT /auth/profile` - Update user profile
- `POST /auth/change-password` - Change password

### Cars (`/api/cars`)
- `GET /cars` - List all cars
- `GET /cars/{id}` - Get car details
- `GET /cars/{id}/tuning-stages` - Get tuning stages for a car
- `POST /cars` - Create new car (Admin)
- `PUT /cars/{id}` - Update car (Admin)
- `DELETE /cars/{id}` - Delete car (Admin)

### Appointments (`/api/appointments`)
- `GET /appointments/my` - Get user's appointments
- `GET /appointments` - Get all appointments (Admin/Technician)
- `GET /appointments/{id}` - Get appointment details
- `POST /appointments` - Create new appointment
- `PATCH /appointments/{id}/status` - Update appointment status
- `PATCH /appointments/{id}/assign` - Assign technician
- `DELETE /appointments/{id}` - Cancel appointment

### Admin (`/api/admin`)
- `POST /admin/users` - Create new user
- `GET /admin/users` - List all users
- `GET /admin/users/{id}` - Get user details
- `PUT /admin/users/{id}` - Update user
- `DELETE /admin/users/{id}` - Delete user
- `GET /admin/stats/appointments` - Get appointment statistics

## Expected Response Format

All endpoints should return JSON responses in this format:

```json
{
  "success": true,
  "data": { /* endpoint-specific data */ },
  "message": "Success message"
}
```

Error responses:
```json
{
  "success": false,
  "error": "Error description"
}
```

## Data Types

### User
```typescript
interface User {
  id: string;
  email: string;
  name: string;
  role: 'CUSTOMER' | 'TECHNICIAN' | 'MASTER' | 'ADMIN';
  createdAt: string;
}
```

### Car
```typescript
interface Car {
  id: string;
  make: string;
  model: string;
  stockHp: number;
}
```

### TuningStage
```typescript
interface TuningStage {
  id: string;
  carId: string;
  stage: number;
  hp: number;
  torque: number;
  price: number;
  description: string;
}
```

### Appointment
```typescript
interface Appointment {
  id: string;
  customerId: string;
  carId: string;
  stageId: string;
  status: 'PENDING' | 'CONFIRMED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  appointmentDate: string;
  createdAt: string;
}
```

## CORS Configuration

Ensure your Spring Boot backend is configured to accept requests from this frontend:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000", "http://localhost:5173", "https://yourdomain.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

## Using the API Client

### Example: Create an Appointment

```typescript
import { appointmentService } from '@/api/services';

async function bookAppointment() {
  const response = await appointmentService.createAppointment(
    'car-id-123',
    'tuning-stage-id',
    '2025-12-15T10:00:00Z'
  );

  if (response.success) {
    console.log('Appointment created:', response.data);
  } else {
    console.error('Failed to create appointment:', response.error);
  }
}
```

### Example: Admin Panel - Update Appointment Status

```typescript
import { appointmentService } from '@/api/services';

async function confirmAppointment(appointmentId: string) {
  const response = await appointmentService.updateAppointmentStatus(
    appointmentId,
    'CONFIRMED'
  );

  if (response.success) {
    console.log('Appointment confirmed');
  }
}
```

## Error Handling

The API client automatically handles common errors:

- **401 Unauthorized**: User is redirected to login page
- **Network Errors**: Returned as error response with message
- **JSON Parse Errors**: Caught and returned as error response

### Custom Error Handling

```typescript
const response = await authService.login(email, password);

if (!response.success) {
  // Handle error
  alert(`Login failed: ${response.error}`);
} else {
  // Success
  console.log('Logged in as:', response.data?.user.name);
}
```

## Frontend Pages Structure

### Public Pages
- `index.html` - Homepage with tuning services showcase
- `arabalar.html` - Car models and tuning stages
- `hakkimizda.html` - About company
- `iletisim.html` - Contact form
- `giris.html` - Login/registration

### Protected Pages (Require Authentication)
- `profilim.html` - User profile and appointments
- `rezervasyon.html` - Booking form
- `admin_panel.html` - Admin management console
- `employee_panel.html` - Employee/technician panel

## Development vs Production

### Development
```bash
npm run dev
```
Backend URL: `http://localhost:8080/api`

### Production Build
```bash
npm run build
```
Update `VITE_API_URL` in `.env` to production API URL before building.

## Testing the Integration

1. **Verify API connectivity**:
   ```javascript
   const response = await apiClient.get('/cars');
   console.log(response);
   ```

2. **Test authentication flow**:
   - Navigate to login page
   - Enter test credentials
   - Verify token is stored
   - Check protected pages are accessible

3. **Test admin features**:
   - Create test car models
   - Create test appointments
   - Verify admin panel shows data

## Troubleshooting

### CORS Errors
- Verify backend CORS configuration
- Check that frontend URL is in allowed origins
- Ensure credentials are properly configured

### 401 Unauthorized
- Verify token is being sent
- Check token expiration
- Ensure backend validates JWT correctly

### Network Errors
- Check backend is running
- Verify `VITE_API_URL` is correct
- Check browser console for detailed errors

## Next Steps

1. **Implement backend endpoints** following the API specifications
2. **Set up authentication** with JWT token generation
3. **Create database schema** for users, cars, and appointments
4. **Test each endpoint** before integrating with frontend
5. **Deploy frontend** to production with correct API URL
