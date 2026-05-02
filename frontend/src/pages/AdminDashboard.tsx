import React, { useEffect, useState } from 'react';
import Navbar from '../components/common/Navbar';
import { appointmentApi, doctorApi, patientApi } from '../api/services';
import { Appointment, Doctor, Patient } from '../types';
import { format } from 'date-fns';

const statusColors: Record<string, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  CONFIRMED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
  COMPLETED: 'bg-gray-100 text-gray-800',
};

type Tab = 'overview' | 'appointments' | 'doctors' | 'patients';

const AdminDashboard: React.FC = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<Tab>('overview');

  useEffect(() => { fetchAll(); }, []);

  const fetchAll = async () => {
    try {
      const [apptRes, docRes, patRes] = await Promise.all([
        appointmentApi.getAllAppointments(),
        doctorApi.getAll(),
        patientApi.getAll(),
      ]);
      setAppointments(apptRes.data.data || []);
      setDoctors(docRes.data.data || []);
      setPatients(patRes.data.data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (id: number, status: string) => {
    try {
      await appointmentApi.updateStatus(id, status);
      fetchAll();
    } catch (err) { console.error(err); }
  };

  const stats = [
    { label: 'Total Appointments', value: appointments.length, color: 'bg-blue-500' },
    { label: 'Registered Doctors', value: doctors.length, color: 'bg-green-500' },
    { label: 'Registered Patients', value: patients.length, color: 'bg-purple-500' },
    { label: 'Pending Appointments', value: appointments.filter(a => a.status === 'PENDING').length, color: 'bg-yellow-500' },
  ];

  const tabs: { key: Tab; label: string }[] = [
    { key: 'overview', label: 'Overview' },
    { key: 'appointments', label: 'Appointments' },
    { key: 'doctors', label: 'Doctors' },
    { key: 'patients', label: 'Patients' },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-7xl mx-auto px-6 py-8">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900">Admin Dashboard</h1>
          <p className="text-gray-500 mt-1">Full system overview and management</p>
        </div>

        {/* Tabs */}
        <div className="flex gap-1 mb-6 bg-gray-100 p-1 rounded-xl w-fit">
          {tabs.map(t => (
            <button key={t.key} onClick={() => setActiveTab(t.key)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
                activeTab === t.key ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'
              }`}>
              {t.label}
            </button>
          ))}
        </div>

        {/* Overview Tab */}
        {activeTab === 'overview' && (
          <div>
            <div className="grid grid-cols-4 gap-5 mb-8">
              {stats.map(s => (
                <div key={s.label} className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
                  <div className={`w-10 h-10 ${s.color} rounded-lg mb-3`} />
                  <p className="text-2xl font-bold text-gray-900">{s.value}</p>
                  <p className="text-gray-500 text-sm mt-1">{s.label}</p>
                </div>
              ))}
            </div>
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">Recent Appointments</h2>
              <AppointmentsTable
                appointments={appointments.slice(0, 5)}
                onStatusUpdate={handleStatusUpdate}
              />
            </div>
          </div>
        )}

        {/* Appointments Tab */}
        {activeTab === 'appointments' && (
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">All Appointments ({appointments.length})</h2>
            {loading ? <p className="text-gray-400 text-center py-8">Loading...</p> :
              <AppointmentsTable appointments={appointments} onStatusUpdate={handleStatusUpdate} />}
          </div>
        )}

        {/* Doctors Tab */}
        {activeTab === 'doctors' && (
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Registered Doctors ({doctors.length})</h2>
            {loading ? <p className="text-gray-400 text-center py-8">Loading...</p> : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {doctors.map(d => (
                  <div key={d.id} className="border border-gray-100 rounded-xl p-4">
                    <div className="flex items-center gap-3 mb-2">
                      <div className="w-10 h-10 bg-green-100 text-green-700 rounded-full flex items-center justify-center font-semibold text-sm">
                        {d.user?.firstName?.[0]}{d.user?.lastName?.[0]}
                      </div>
                      <div>
                        <p className="font-semibold text-gray-900 text-sm">
                          Dr. {d.user?.firstName} {d.user?.lastName}
                        </p>
                        <p className="text-xs text-gray-500">{d.user?.email}</p>
                      </div>
                    </div>
                    <p className="text-xs text-blue-600 font-medium">{d.specialization}</p>
                    <p className="text-xs text-gray-500 mt-1">{d.department} • {d.experienceYears} yrs exp</p>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Patients Tab */}
        {activeTab === 'patients' && (
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Registered Patients ({patients.length})</h2>
            {loading ? <p className="text-gray-400 text-center py-8">Loading...</p> : (
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-gray-100">
                      <th className="text-left py-3 px-2 text-gray-500 font-medium">Name</th>
                      <th className="text-left py-3 px-2 text-gray-500 font-medium">Email</th>
                      <th className="text-left py-3 px-2 text-gray-500 font-medium">Blood Group</th>
                      <th className="text-left py-3 px-2 text-gray-500 font-medium">Gender</th>
                    </tr>
                  </thead>
                  <tbody>
                    {patients.map(p => (
                      <tr key={p.id} className="border-b border-gray-50 hover:bg-gray-50">
                        <td className="py-3 px-2 font-medium text-gray-900">
                          {p.user?.firstName} {p.user?.lastName}
                        </td>
                        <td className="py-3 px-2 text-gray-600">{p.user?.email}</td>
                        <td className="py-3 px-2 text-gray-600">{p.bloodGroup || '—'}</td>
                        <td className="py-3 px-2 text-gray-600">{p.gender || '—'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

const AppointmentsTable: React.FC<{
  appointments: Appointment[];
  onStatusUpdate: (id: number, status: string) => void;
}> = ({ appointments, onStatusUpdate }) => {
  if (appointments.length === 0)
    return <p className="text-gray-400 text-center py-8">No appointments found.</p>;

  return (
    <div className="overflow-x-auto">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-gray-100">
            <th className="text-left py-3 px-2 text-gray-500 font-medium">Patient</th>
            <th className="text-left py-3 px-2 text-gray-500 font-medium">Doctor</th>
            <th className="text-left py-3 px-2 text-gray-500 font-medium">Date & Time</th>
            <th className="text-left py-3 px-2 text-gray-500 font-medium">Status</th>
            <th className="text-left py-3 px-2 text-gray-500 font-medium">Actions</th>
          </tr>
        </thead>
        <tbody>
          {appointments.map(a => (
            <tr key={a.id} className="border-b border-gray-50 hover:bg-gray-50">
              <td className="py-3 px-2 font-medium text-gray-900">{a.patientName}</td>
              <td className="py-3 px-2 text-gray-600">{a.doctorName}</td>
              <td className="py-3 px-2 text-gray-600">
                {format(new Date(a.appointmentDateTime), 'MMM d, yyyy h:mm a')}
              </td>
              <td className="py-3 px-2">
                <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusColors[a.status]}`}>
                  {a.status}
                </span>
              </td>
              <td className="py-3 px-2">
                <div className="flex gap-2">
                  {a.status === 'PENDING' && (
                    <>
                      <button onClick={() => onStatusUpdate(a.id, 'CONFIRMED')}
                        className="px-2 py-1 bg-green-600 hover:bg-green-700 text-white rounded text-xs font-medium transition">
                        Confirm
                      </button>
                      <button onClick={() => onStatusUpdate(a.id, 'CANCELLED')}
                        className="px-2 py-1 bg-red-500 hover:bg-red-600 text-white rounded text-xs font-medium transition">
                        Cancel
                      </button>
                    </>
                  )}
                  {a.status === 'CONFIRMED' && (
                    <button onClick={() => onStatusUpdate(a.id, 'COMPLETED')}
                      className="px-2 py-1 bg-gray-600 hover:bg-gray-700 text-white rounded text-xs font-medium transition">
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
  );
};

export default AdminDashboard;
