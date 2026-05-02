export type Role = 'ADMIN' | 'DOCTOR' | 'PATIENT';

export type AppointmentStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export interface User {
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  token: string;
}

export interface Patient {
  id: number;
  user: UserBasic;
  dateOfBirth: string;
  gender: string;
  bloodGroup: string;
  address: string;
  emergencyContact: string;
  medicalHistory: string;
}

export interface Doctor {
  id: number;
  user: UserBasic;
  specialization: string;
  qualification: string;
  licenseNumber: string;
  experienceYears: number;
  department: string;
  consultationFee: number;
}

export interface UserBasic {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
}

export interface Appointment {
  id: number;
  patientId: number;
  patientName: string;
  doctorId: number;
  doctorName: string;
  doctorSpecialization: string;
  appointmentDateTime: string;
  status: AppointmentStatus;
  reason: string;
  notes: string;
  createdAt: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phone: string;
  role: Role;
}

export interface AppointmentRequest {
  doctorId: number;
  appointmentDateTime: string;
  reason: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  userId: number;
}
