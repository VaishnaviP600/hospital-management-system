import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const dashboardLink =
    user?.role === 'ADMIN' ? '/admin' :
    user?.role === 'DOCTOR' ? '/doctor' : '/patient';

  return (
    <nav className="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
      <Link to={dashboardLink} className="flex items-center gap-2">
        <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
          <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
              d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
          </svg>
        </div>
        <span className="font-bold text-gray-900">MediCare HMS</span>
      </Link>

      <div className="flex items-center gap-4">
        <span className="text-sm text-gray-500">
          {user?.firstName} {user?.lastName}
          <span className="ml-2 px-2 py-0.5 bg-blue-100 text-blue-700 rounded-full text-xs font-medium">
            {user?.role}
          </span>
        </span>
        <button onClick={handleLogout}
          className="text-sm text-gray-600 hover:text-red-600 transition font-medium">
          Logout
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
