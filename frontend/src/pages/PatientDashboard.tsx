import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/common/Navbar';
import { appointmentApi, doctorApi } from '../api/services';
import { Appointment, Doctor } from '../types';
import { format } from 'date-fns';

const statusColors: Record<string, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  CONFIRMED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
  COMPLETED: 'bg-gray-100 text-gray-800',
};

const PatientDashboard: React.FC = () => {
  const { user } = useAuth();
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [doctors, setDoctors] = useState<any[]>([]);
  const [showBooking, setShowBooking] = useState(false);
  const [bookForm, setBookForm] = useState({ doctorId: 0, appointmentDateTime: '', reason: '' });
  const [loading, setLoading] = useState(true);
  const [bookingError, setBookingError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    try {
      const [apptRes, docRes] = await Promise.all([
        appointmentApi.getMyAppointments(),
        doctorApi.getAll(),
      ]);
      setAppointments(apptRes.data.data || []);
      setDoctors(docRes.data.data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleBook = async (e: React.FormEvent) => {
    e.preventDefault();
    setBookingError('');
    try {
      await appointmentApi.book(bookForm);
      setShowBooking(false);
      setBookForm({ doctorId: 0, appointmentDateTime: '', reason: '' });
      setSuccessMessage('Appointment booked successfully!');
      setTimeout(() => setSuccessMessage(''), 4000);
      fetchData();
    } catch (err: any) {
      setBookingError(err.response?.data?.message || 'Booking failed');
    }
  };

  const stats = {
    total: appointments.length,
    upcoming: appointments.filter(a => a.status === 'CONFIRMED' || a.status === 'PENDING').length,
    completed: appointments.filter(a => a.status === 'COMPLETED').length,
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-6xl mx-auto px-6 py-8">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900">Welcome back, {user?.firstName}</h1>
          <p className="text-gray-500 mt-1">Manage your appointments and health records</p>
        </div>

        {successMessage && (
          <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-xl text-green-700 font-medium flex items-center gap-2">
            <span>✓</span> {successMessage}
          </div>
        )}

        <div className="grid grid-cols-3 gap-6 mb-8">
          {[
            { label: 'Total Appointments', value: stats.total, color: 'bg-blue-500' },
            { label: 'Upcoming', value: stats.upcoming, color: 'bg-green-500' },
            { label: 'Completed', value: stats.completed, color: 'bg-gray-500' },
          ].map(s => (
            <div key={s.label} className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
              <div className={`w-10 h-10 ${s.color} rounded-lg mb-3`} />
              <p className="text-2xl font-bold text-gray-900">{s.value}</p>
              <p className="text-gray-500 text-sm mt-1">{s.label}</p>
            </div>
          ))}
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">My Appointments</h2>
            <button onClick={() => setShowBooking(!showBooking)}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition">
              + Book Appointment
            </button>
          </div>

          {showBooking && (
            <form onSubmit={handleBook} className="mb-6 p-4 bg-blue-50 rounded-lg border border-blue-100">
              <h3 className="font-medium text-gray-800 mb-3">Book New Appointment</h3>
              {bookingError && <p className="text-red-600 text-sm mb-3">{bookingError}</p>}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Select Doctor</label>
                  <select value={bookForm.doctorId}
                    onChange={e => setBookForm({ ...bookForm, doctorId: Number(e.target.value) })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm outline-none focus:ring-2 focus:ring-blue-500 bg-white"
                    required>
                    <option value={0}>-- Choose Doctor --</option>
                    {doctors.map((d: any) => (
                      <option key={d.id} value={d.id}>
                        Dr. {d.user?.firstName} {d.user?.lastName} — {d.specialization}
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Date & Time</label>
                  <input type="datetime-local" value={bookForm.appointmentDateTime}
                    onChange={e => setBookForm({ ...bookForm, appointmentDateTime: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm outline-none focus:ring-2 focus:ring-blue-500"
                    required />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Reason</label>
                  <input type="text" value={bookForm.reason}
                    onChange={e => setBookForm({ ...bookForm, reason: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Brief reason for visit" />
                </div>
              </div>
              <div className="flex gap-3 mt-4">
                <button type="submit"
                  className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition">
                  Confirm Booking
                </button>
                <button type="button" onClick={() => setShowBooking(false)}
                  className="text-gray-600 hover:text-gray-800 px-4 py-2 rounded-lg text-sm font-medium border border-gray-300 transition">
                  Cancel
                </button>
              </div>
            </form>
          )}

          {loading ? (
            <p className="text-gray-400 text-center py-8">Loading...</p>
          ) : appointments.length === 0 ? (
            <p className="text-gray-400 text-center py-8">No appointments yet. Book your first one!</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-gray-100">
                    <th className="text-left py-3 px-2 text-gray-500 font-medium">Doctor</th>
                    <th className="text-left py-3 px-2 text-gray-500 font-medium">Specialization</th>
                    <th className="text-left py-3 px-2 text-gray-500 font-medium">Date & Time</th>
                    <th className="text-left py-3 px-2 text-gray-500 font-medium">Reason</th>
                    <th className="text-left py-3 px-2 text-gray-500 font-medium">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {appointments.map(a => (
                    <tr key={a.id} className="border-b border-gray-50 hover:bg-gray-50">
                      <td className="py-3 px-2 font-medium text-gray-900">{a.doctorName}</td>
                      <td className="py-3 px-2 text-gray-600">{a.doctorSpecialization}</td>
                      <td className="py-3 px-2 text-gray-600">
                        {format(new Date(a.appointmentDateTime), 'MMM d, yyyy h:mm a')}
                      </td>
                      <td className="py-3 px-2 text-gray-600">{a.reason || '—'}</td>
                      <td className="py-3 px-2">
                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusColors[a.status]}`}>
                          {a.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PatientDashboard;
