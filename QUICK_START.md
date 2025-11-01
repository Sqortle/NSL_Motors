# NSL MOTORS - Liquid Glass Frontend - Quick Start Guide

## Project Overview

NSL MOTORS is a premium car tuning service website featuring a sophisticated Liquid Glass aesthetic. The frontend is built with modern web technologies and is ready for integration with a Spring Boot backend.

## Features

✨ **Liquid Glass Design** - Elegant glassmorphism UI with smooth animations
🎨 **Professional Aesthetic** - British Racing Green accent colors with premium feel
📱 **Fully Responsive** - Optimized for desktop, tablet, and mobile devices
🔐 **Authentication Ready** - JWT token management and protected routes
⚡ **Production Build** - Optimized bundle size (142KB gzipped)
🎯 **Clean Architecture** - Modular TypeScript code for easy backend integration

## Installation

1. **Install dependencies**
```bash
npm install
```

2. **Configure environment**
Update `.env` file with your backend API URL:
```env
VITE_API_URL=http://localhost:8080/api
```

3. **Start development server**
```bash
npm run dev
```
Visit `http://localhost:5173` in your browser

## Build for Production

```bash
npm run build
```

This creates an optimized production build in the `dist/` directory.

## Project Structure

```
src/
├── api/
│   ├── client.ts          # Core HTTP client with auth
│   └── services.ts        # Feature services (auth, cars, appointments, admin)
├── main.tsx               # Entry point
├── App.tsx                # Main component
├── style.css              # Global styles
└── vite-env.d.ts          # Vite types

public/
├── index.html             # Homepage
├── arabalar.html          # Car models & tuning details
├── giris.html             # Login/Registration
├── hakkimizda.html        # About page
├── iletisim.html          # Contact page
├── profilim.html          # User profile
├── rezervasyon.html       # Booking page
├── admin_panel.html       # Admin management
└── employee_panel.html    # Employee dashboard
```

## Pages & Routes

### Public Pages
- **Homepage** (`/`) - Showcase tuning services
- **Car Models** (`/arabalar.html`) - Tuning stages and pricing
- **About** (`/hakkimizda.html`) - Company information
- **Contact** (`/iletisim.html`) - Contact form
- **Login** (`/giris.html`) - Authentication

### Protected Pages
- **Profile** (`/profilim.html`) - User account and appointments
- **Booking** (`/rezervasyon.html`) - Create reservations
- **Admin Panel** (`/admin_panel.html`) - Manage users and appointments
- **Employee Panel** (`/employee_panel.html`) - Technician tasks

## Styling Features

### Color Scheme
- **Primary Accent**: British Racing Green (#004225)
- **Secondary Accent**: Matte Cream (#C3D5C0)
- **Background**: Deep Black (#080808)
- **Glass Background**: rgba(255,255,255,0.1)

### Components
- **Glass Cards** - Frosted glass effect with backdrop blur
- **Buttons** - Primary and secondary with hover animations
- **Forms** - Glass input fields with focus states
- **Animations** - Smooth transitions and shimmer effects

## API Integration

### Quick Start with Services

```typescript
import { authService, appointmentService } from '@/api/services';

// Login
const loginResponse = await authService.login('email@example.com', 'password');

// Get appointments
const appts = await appointmentService.getMyAppointments();

// Create appointment
const booking = await appointmentService.createAppointment(
  'car-id',
  'tuning-stage-id',
  '2025-12-15T10:00:00Z'
);
```

### Response Handling

```typescript
const response = await authService.login(email, password);

if (response.success) {
  console.log('User:', response.data?.user);
} else {
  console.error('Error:', response.error);
}
```

## Backend Requirements

See `BACKEND_INTEGRATION.md` for complete backend API specifications.

### Key Endpoints Needed

- `/api/auth/*` - Authentication
- `/api/cars` - Car management
- `/api/appointments` - Booking system
- `/api/admin/*` - Admin operations

## Development Tips

### Environment Variables
```env
VITE_API_URL=http://localhost:8080/api
VITE_SUPABASE_URL=<your-supabase-url>
VITE_SUPABASE_ANON_KEY=<your-supabase-key>
```

### Useful Commands
```bash
npm run dev          # Development server
npm run build        # Production build
npm run preview      # Preview production build
npm run lint         # Run ESLint
npm run typecheck    # Check TypeScript
```

### Testing API Calls
Use browser console to test services:
```javascript
// Test API connection
fetch('http://localhost:8080/api/cars')
  .then(r => r.json())
  .then(console.log);

// Check if authenticated
localStorage.getItem('auth_token');
```

## Design Highlights

### Liquid Glass Aesthetic
- **Frosted glass cards** with 10px blur backdrop filter
- **Subtle animations** on hover with shimmer effects
- **Color layering** with transparent backgrounds
- **Smooth transitions** using cubic-bezier timing functions

### User Experience
- **Hero section** with compelling headline
- **Service showcase** with stage-based pricing
- **Model showcase** with interactive filtering
- **Responsive forms** with proper validation states
- **Admin dashboard** for efficient management

### Performance
- **Minimal dependencies** - Only React, ReactDOM, and Lucide
- **Optimized CSS** - Efficient selectors and animations
- **Small bundle** - 142KB gzipped JavaScript
- **Fast animations** - 60fps hardware-accelerated transitions

## Mobile Optimization

Responsive breakpoints:
- **1024px** - Tablet adjustments
- **768px** - Mobile adjustments
- **480px** - Small mobile adjustments

All pages are fully functional on mobile with:
- Vertical navigation menu
- Touch-friendly buttons (44px minimum height)
- Optimized font sizes
- Full-width layouts

## Browser Support

- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)
- Mobile browsers

## Troubleshooting

### Build Errors
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
npm run build
```

### API Connection Issues
1. Verify `VITE_API_URL` in `.env`
2. Check backend is running on correct port
3. Check CORS configuration on backend
4. Look for errors in browser console

### Styling Issues
- Clear browser cache (Ctrl+Shift+R)
- Check CSS file is properly linked
- Verify CSS variables are defined

## Next Steps

1. **Setup Backend** - Implement Spring Boot API endpoints
2. **Connect Services** - Test each service module
3. **Add Features** - Customize for your needs
4. **Deploy** - Push to production server
5. **Monitor** - Track user interactions and errors

## Support & Documentation

- See `BACKEND_INTEGRATION.md` for API specifications
- Check individual HTML files for page-specific styling
- Review `src/api/services.ts` for available API methods
- Refer to Tailwind CSS docs for styling customization

## License

© 2025 NSL MOTORS. All rights reserved.

---

**Ready to go live?** 🚀

1. Build production version: `npm run build`
2. Deploy `dist/` folder to your hosting
3. Update `VITE_API_URL` to production backend
4. Configure your Spring Boot backend
5. Launch! 🎉
