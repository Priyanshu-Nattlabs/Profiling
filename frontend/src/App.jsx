import React, { useState } from 'react';
import StartButton from './components/StartButton';
import ProfileForm from './components/ProfileForm';
import ProfileDisplay from './components/ProfileDisplay';

function App() {
  const [currentView, setCurrentView] = useState('start'); // 'start', 'form', 'display'
  const [profileData, setProfileData] = useState(null);
  const [error, setError] = useState(null);

  const handleStart = () => {
    setCurrentView('form');
    setError(null);
    setProfileData(null);
  };

  const handleFormSuccess = (data) => {
    setProfileData(data);
    setCurrentView('display');
    setError(null);
  };

  const handleFormError = (errorMessage) => {
    setError(errorMessage);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {currentView === 'start' && <StartButton onStart={handleStart} />}
      
      {currentView === 'form' && (
        <div>
          {error && (
            <div className="max-w-2xl mx-auto p-4 mt-4 bg-red-100 border border-red-400 text-red-700 rounded">
              Error: {error}
            </div>
          )}
          <ProfileForm onSuccess={handleFormSuccess} onError={handleFormError} />
        </div>
      )}
      
      {currentView === 'display' && profileData && (
        <ProfileDisplay profileData={profileData} />
      )}
    </div>
  );
}

export default App;

