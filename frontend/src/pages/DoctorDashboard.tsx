import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/common/Navbar';
import { appointmentApi } from '../api/services';
import { Appointment } from '../types';
import { format } from 'date-fns';

const statusColors: Record<string, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  CONFIRMED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
  COMPLETED: 'bg-gray-100 text-gray-800',
};

const DoctorDashboard: React.FC = () => {
  const { user } = useAuth();
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState<number | null>(null);
  const [toast, setToast] = useState('');

  useEffect(() => { fetchAppointments(); }, []);

  const fetchAppointments = async () => {
    try {
      const res = await appointmentApi.getDoctorAppointments();
      setAppointments(res.data.data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (id: number, status: string) => {
    setUpdating(id);
    try {
      await appointmentApi.updateStatus(id, status);
      fetchAppointments();
      setToast(`Appointment ${status.toLowerCase()} successfully!`);
      setTimeout(() => setToast(''), 3000);
    } catch (err) {
      console.error(err);
    } finally {
      setUpdating(null);
    }
  };

  const stats = {
    total: appointments.length,
    pending: appointments.filter(a => a.status === 'PENDING').length,
    confirmed: appointments.filter(a => a.status === 'CONFIRMED').length,
    completed: appointments.filter(a => a.status === 'COMPLETED').length,
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-6xl mx-auto px-6 py-8">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900">Doctor Dashboard</h1>
          <p className="text-gray-500 mt-1">Dr. {user?.firstName} {user?.lastName} — Manage your appointments</p>
        </div>

        {toast && (
          <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-xl text-green-700 font-medium flex items-center gap-2">
            <span>✓</span> {toast}
          </div>
        )}

        <div className="grid grid-cols-4 gap-5 mb-8">
          {[
            { label: 'Total', value: stats.total, color: 'bg-blue-500' },
            { label: 'Pending', value: stats.pending, color: 'bg-yellow-500' },
            { label: 'Confirmed', value: stats.confirmed, color: 'bg-green-500' },
            { label: 'Completed', value: stats.completed, color: 'bg-gray-500' },
          ].map(s => (
            <div key={s.label} className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
              <div className={`w-8 h-8 ${s.color} rounded-lg mb-3`} />
              <p className="text-2xl font-bold text-gray-900">{s.value}</p>
              <p className="text-gray-500 text-sm mt-1">{s.label}</p>
            </div>
          ))}
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Appointments</h2>
          {loading ? (
            <p className="text-gray-400 text-center py-8">Loading...</p>
          ) : appointments.length === 0 ? (
            <p className="text-gray-400 text-center py-8">No appointments scheduled.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-gray-100">
                    <th className="text-left py-3 px-2 text-gray-500 font-medium">Patient</th>
                    <th className="text-left py-3 px-2 text-gray-500 font-medium">Date & Time</th>
                    <th className="text-left py-3 px-2 text-gray-500 font-medium">Reason</th>
                    <th className="text-left py-3 px-2 text-gray-500 font-medium">Status</th>
                    <th className="text-left py-3 px-2 text-gray-500 font-medium">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {appointments.map(a => (
                    <tr key={a.id} className="border-b border-gray-50 hover:bg-gray-50">
                      <td className="py-3 px-2 font-medium text-gray-900">{a.patientName}</td>
                      <td className="py-3 px-2 text-gray-600">
                        {format(new Date(a.appointmentDateTime), 'MMM d, yyyy h:mm a')}
                      </td>
                      <td className="py-3 px-2 text-gray-600">{a.reason || '—'}</td>
                      <td className="py-3 px-2">
                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusColors[a.status]}`}>
                          {a.status}
                        </span>
                      </td>
                      <td className="py-3 px-2">
                        <div className="flex gap-2">
                          {a.status === 'PENDING' && (
                            <>
                              <button onClick={() => handleStatusUpdate(a.id, 'CONFIRMED')}
                                disabled={updating === a.id}
                                className="px-2 py-1 bg-green-600 hover:bg-green-700 text-white rounded text-xs font-medium transition disabled:opacity-50">
                                Confirm
                              </button>
                              <button onClick={() => handleStatusUpdate(a.id, 'CANCELLED')}
                                disabled={updating === a.id}
                                className="px-2 py-1 bg-red-500 hover:bg-red-600 text-white rounded text-xs font-medium transition disabled:opacity-50">
                                Cancel
                              </button>
                            </>
                          )}
                          {a.status === 'CONFIRMED' && (
                            <button onClick={() => handleStatusUpdate(a.id, 'COMPLETED')}
                              disabled={updating === a.id}
                              className="px-2 py-1 bg-gray-600 hover:bg-gray-700 text-white rounded text-xs font-medium transition disabled:opacity-50">
                              Complete
                            </button>
                          )}
                        </div>
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

export default DoctorDashboard;
