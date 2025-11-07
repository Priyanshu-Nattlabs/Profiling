import React from 'react';

const ProfileDisplay = ({ profileData }) => {
  if (!profileData) {
    return <div className="p-6">No profile data to display</div>;
  }

  const renderField = (label, value) => {
    if (value === null || value === undefined || value === '') {
      return null;
    }
    return (
      <div className="mb-4">
        <h3 className="font-semibold mb-1">{label}</h3>
        <p className="text-gray-700">{value}</p>
      </div>
    );
  };

  return (
    <div className="max-w-2xl mx-auto p-6">
      <h2 className="text-2xl font-bold mb-6">Profile Details</h2>
      <div className="space-y-4">
        {renderField('Name', profileData.name)}
        {renderField('Email', profileData.email)}
        {renderField('Date of Birth', profileData.dob)}
        {renderField('LinkedIn', profileData.linkedin)}
        {renderField('Institute', profileData.institute)}
        {renderField('Current Degree', profileData.currentDegree)}
        {renderField('Branch', profileData.branch)}
        {renderField('Year of Study', profileData.yearOfStudy)}
        {renderField('Certifications', profileData.certifications)}
        {renderField('Achievements', profileData.achievements)}
        {renderField('Technical Skills', profileData.technicalSkills)}
        {renderField('Soft Skills', profileData.softSkills)}
      </div>
    </div>
  );
};

export default ProfileDisplay;

