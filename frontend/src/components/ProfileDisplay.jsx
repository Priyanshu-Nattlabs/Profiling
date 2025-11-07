import React from 'react';

const ProfileDisplay = ({ profileData }) => {
  if (!profileData) {
    return <div className="p-6">No profile data to display</div>;
  }

  const profile = profileData.profile || profileData;
  const templateText = profileData.templateText;
  const templateType = profile?.templateType || profileData.templateType;
  const heading = templateType && templateType.toLowerCase() === 'cover'
    ? 'Cover Letter'
    : 'Profile Details';

  return (
    <div className="max-w-2xl mx-auto p-6">
      <h2 className="text-2xl font-bold mb-6">{heading}</h2>
      
      {templateText && (
        <div className="mb-6 p-4 border">
          <p className="whitespace-pre-line">{templateText}</p>
        </div>
      )}

      {!templateText && (
        <div className="p-4 border">
          <p>No template available.</p>
        </div>
      )}
    </div>
  );
};

export default ProfileDisplay;

