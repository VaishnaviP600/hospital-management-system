import api from './axios';
import { ApiResponse, AuthResponse, LoginRequest, RegisterRequest, Appointment, AppointmentRequest, Doctor, Patient } from '../types';

export const authApi = {
  login: (data: LoginRequest) =>
    api.post<ApiResponse<AuthResponse>>('/api/auth/login', data),
  register: (data: RegisterRequest) =>
    api.post<ApiResponse<AuthResponse>>('/api/auth/register', data),
};

export const appointmentApi = {
  book: (data: AppointmentRequest) =>
    api.post<ApiResponse<Appointment>>('/api/appointments', data),
  getMyAppointments: () =>
    api.get<ApiResponse<Appointment[]>>('/api/appointments/my'),
  getDoctorAppointments: () =>
    api.get<ApiResponse<Appointment[]>>('/api/appointments/doctor/my'),
  getAllAppointments: () =>
    api.get<ApiResponse<Appointment[]>>('/api/appointments'),
  updateStatus: (id: number, status: string) =>
    api.patch<ApiResponse<Appointment>>(`/api/appointments/${id}/status?status=${status}`),
};

export const doctorApi = {
  getAll: () => api.get<ApiResponse<any[]>>('/api/doctors'),
  getById: (id: number) => api.get<ApiResponse<any>>(`/api/doctors/${id}`),
  getBySpecialization: (spec: string) =>
    api.get<ApiResponse<any[]>>(`/api/doctors/specialization/${spec}`),
  createProfile: (data: any) => api.post('/api/doctors/profile', data),
};

export const patientApi = {
  getMyProfile: () => api.get<ApiResponse<Patient>>('/api/patients/profile'),
  updateProfile: (data: Partial<Patient>) =>
    api.post<ApiResponse<Patient>>('/api/patients/profile', data),
  getAll: () => api.get<ApiResponse<Patient[]>>('/api/patients'),
  getById: (id: number) => api.get<ApiResponse<Patient>>(`/api/patients/${id}`),
};
