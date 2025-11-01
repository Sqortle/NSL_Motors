import { apiClient, setAuthToken, clearAuthToken } from './client';

export interface User {
  id: string;
  email: string;
  name: string;
  role: 'CUSTOMER' | 'TECHNICIAN' | 'MASTER' | 'ADMIN';
  createdAt: string;
}

export interface Car {
  id: string;
  make: string;
  model: string;
  stockHp: number;
}

export interface TuningStage {
  id: string;
  carId: string;
  stage: number;
  hp: number;
  torque: number;
  price: number;
  description: string;
}

export interface Appointment {
  id: string;
  customerId: string;
  carId: string;
  stageId: string;
  status: 'PENDING' | 'CONFIRMED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  appointmentDate: string;
  createdAt: string;
}

export const authService = {
  async login(email: string, password: string) {
    const response = await apiClient.post<{ token: string; user: User }>(
      '/auth/login',
      { email, password }
    );

    if (response.success && response.data?.token) {
      setAuthToken(response.data.token);
    }

    return response;
  },

  async register(email: string, password: string, name: string) {
    const response = await apiClient.post<{ token: string; user: User }>(
      '/auth/register',
      { email, password, name }
    );

    if (response.success && response.data?.token) {
      setAuthToken(response.data.token);
    }

    return response;
  },

  async logout() {
    clearAuthToken();
    return { success: true };
  },

  async getCurrentUser() {
    return apiClient.get<User>('/auth/me');
  },

  async updateProfile(name: string, phone?: string) {
    return apiClient.put<User>(
      '/auth/profile',
      { name, phone }
    );
  },

  async changePassword(currentPassword: string, newPassword: string) {
    return apiClient.post(
      '/auth/change-password',
      { currentPassword, newPassword }
    );
  },
};

export const carsService = {
  async getAllCars() {
    return apiClient.get<Car[]>('/cars');
  },

  async getCarById(id: string) {
    return apiClient.get<Car>(`/cars/${id}`);
  },

  async getTuningStages(carId: string) {
    return apiClient.get<TuningStage[]>(`/cars/${carId}/tuning-stages`);
  },

  async createCar(make: string, model: string, stockHp: number) {
    return apiClient.post<Car>(
      '/cars',
      { make, model, stockHp }
    );
  },

  async updateCar(id: string, make: string, model: string, stockHp: number) {
    return apiClient.put<Car>(
      `/cars/${id}`,
      { make, model, stockHp }
    );
  },

  async deleteCar(id: string) {
    return apiClient.delete(`/cars/${id}`);
  },
};

export const appointmentService = {
  async getMyAppointments() {
    return apiClient.get<Appointment[]>('/appointments/my');
  },

  async getAllAppointments() {
    return apiClient.get<Appointment[]>('/appointments');
  },

  async getAppointmentById(id: string) {
    return apiClient.get<Appointment>(`/appointments/${id}`);
  },

  async createAppointment(
    carId: string,
    stageId: string,
    appointmentDate: string
  ) {
    return apiClient.post<Appointment>(
      '/appointments',
      { carId, stageId, appointmentDate }
    );
  },

  async updateAppointmentStatus(id: string, status: Appointment['status']) {
    return apiClient.patch<Appointment>(
      `/appointments/${id}/status`,
      { status }
    );
  },

  async assignTechnician(appointmentId: string, technicianId: string) {
    return apiClient.patch<Appointment>(
      `/appointments/${appointmentId}/assign`,
      { technicianId }
    );
  },

  async cancelAppointment(id: string) {
    return apiClient.delete(`/appointments/${id}`);
  },
};

export const adminService = {
  async createUser(email: string, password: string, name: string, role: User['role']) {
    return apiClient.post<User>(
      '/admin/users',
      { email, password, name, role }
    );
  },

  async getAllUsers() {
    return apiClient.get<User[]>('/admin/users');
  },

  async getUserById(id: string) {
    return apiClient.get<User>(`/admin/users/${id}`);
  },

  async updateUser(id: string, updates: Partial<User>) {
    return apiClient.put<User>(
      `/admin/users/${id}`,
      updates
    );
  },

  async deleteUser(id: string) {
    return apiClient.delete(`/admin/users/${id}`);
  },

  async getAppointmentStats() {
    return apiClient.get<{
      total: number;
      pending: number;
      inProgress: number;
      completed: number;
    }>('/admin/stats/appointments');
  },
};
