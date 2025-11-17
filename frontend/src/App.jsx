import React, { useState } from 'react';
import StartButton from './components/StartButton';
import ProfileForm from './components/ProfileForm';
import TemplateSelection from './components/TemplateSelection';
import CoverLetterForm from './components/CoverLetterForm';
import ProfileDisplay from './components/ProfileDisplay';
import { submitProfile } from './api';

function App() {
  const [currentView, setCurrentView] = useState('start'); // 'start', 'form', 'template', 'cover', 'display'
  const [formData, setFormData] = useState(null);
  const [profileData, setProfileData] = useState(null);
  const [error, setError] = useState(null);

  const handleStart = () => {
    setCurrentView('form');
    setError(null);
    setFormData(null);
    setProfileData(null);
  };

  const handleFormSuccess = (data) => {
    setFormData(data);
    setCurrentView('template');
    setError(null);
  };

  const handleTemplateSelect = async (templateType) => {
    if (!formData) {
      setError('Profile details are missing. Please complete the form again.');
      setCurrentView('form');
      return;
    }

    if (templateType === 'cover') {
      setCurrentView('cover');
      return;
    }

    // Submit to backend with selected template type
    const result = await submitProfile(formData, templateType);
    
    if (result.success) {
      setProfileData(result.data);
      setCurrentView('display');
      setError(null);
    } else {
      setError(result.error);
      setCurrentView('template');
    }
  };

  const handleCoverSubmit = async (coverDetails) => {
    if (!formData) {
      setError('Profile details are missing. Please complete the form again.');
      setCurrentView('form');
      return;
    }

    const combinedData = {
      ...formData,
      ...coverDetails
    };

    const result = await submitProfile(combinedData, 'cover');

    if (result.success) {
      setProfileData(result.data);
      setCurrentView('display');
      setError(null);
    } else {
      setError(result.error);
      setCurrentView('cover');
    }
  };

  const handleBackToTemplates = () => {
    setError(null);
    setCurrentView('template');
  };

  const handleBackToStart = () => {
    setError(null);
    setFormData(null);
    setProfileData(null);
    setCurrentView('start');
  };

  const handleBackToForm = () => {
    setError(null);
    setCurrentView('form');
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
          <ProfileForm onSuccess={handleFormSuccess} onBack={handleBackToStart} initialData={formData} />
        </div>
      )}
      
      {currentView === 'template' && (
        <div>
          {error && (
            <div className="max-w-4xl mx-auto p-4 mt-4 bg-red-100 border border-red-400 text-red-700 rounded">
              Error: {error}
            </div>
          )}
          <TemplateSelection
            onTemplateSelect={handleTemplateSelect}
            onCoverLetterSelect={() => handleTemplateSelect('cover')}
            onBack={handleBackToForm}
          />
        </div>
      )}

      {currentView === 'cover' && (
        <div>
          {error && (
            <div className="max-w-2xl mx-auto p-4 mt-4 bg-red-100 border border-red-400 text-red-700 rounded">
              Error: {error}
            </div>
          )}
          <CoverLetterForm onSubmit={handleCoverSubmit} onBack={handleBackToTemplates} />
        </div>
      )}
      
      {currentView === 'display' && profileData && (
        <ProfileDisplay profileData={profileData} />
      )}
    </div>
  );
}

export default App;

