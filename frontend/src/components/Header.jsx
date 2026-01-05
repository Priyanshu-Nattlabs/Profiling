import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import { FaSignOutAlt } from 'react-icons/fa';

const Header = ({ onNavigateToStart }) => {
  const { isAuthenticated, logout } = useAuth();

  const handleLogout = () => {
    logout();
    window.location.href = '/';
  };

  const handleBrandClick = () => {
    if (onNavigateToStart) {
      onNavigateToStart();
    }
  };

  return (
    <header className="sticky top-0 z-50 bg-gradient-to-r from-white via-blue-50 to-white shadow-lg backdrop-blur-sm">
      <div className="mx-auto flex h-16 sm:h-20 max-w-7xl items-center justify-between px-4 sm:px-6">
        <button
          onClick={handleBrandClick}
          className="flex items-center gap-2 sm:gap-3 transition hover:scale-105 cursor-pointer"
        >
          <span className="text-xl sm:text-2xl font-bold text-gray-800">
            <span className="text-[#151B54]">Saarthi</span>
            <span className="text-gray-900">X</span>
          </span>
        </button>
        <div className="flex items-center gap-4">
          {isAuthenticated() && (
            <button
              onClick={handleLogout}
              className="inline-flex items-center gap-2 rounded-full bg-gradient-to-r from-[#151B54] to-blue-600 px-6 py-3 font-semibold text-white shadow-lg transition duration-300 hover:shadow-xl hover:scale-105"
            >
              <FaSignOutAlt className="h-4 w-4" />
              <span className="text-sm">Logout</span>
            </button>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;

