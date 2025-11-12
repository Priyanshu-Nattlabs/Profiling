import React, { useEffect, useMemo, useState } from 'react';
import axios from 'axios';

import { fetchTemplates } from '../api';

const emptyProfile = {
  name: '',
  email: '',
  dob: '',
  linkedin: '',
  institute: '',
  currentDegree: '',
  branch: '',
  yearOfStudy: '',
  certifications: '',
  achievements: '',
  technicalSkills: '',
  softSkills: '',
  templateType: '',
  hiringManagerName: '',
  companyName: '',
  companyAddress: '',
  positionTitle: '',
  relevantExperience: '',
  keyAchievement: '',
  strengths: '',
  closingNote: '',
  hasInternship: false,
  internshipDetails: '',
  hasExperience: false,
  experienceDetails: '',
};

const defaultTemplateOptions = [
  { value: 'professional', label: 'Professional' },
  { value: 'bio', label: 'Bio' },
  { value: 'story', label: 'Story' },
  { value: 'industry', label: 'Industry Ready' },
  { value: 'modern-professional', label: 'Modern Professional' },
  { value: 'executive', label: 'Executive Professional Template' },
  { value: 'cover', label: 'Cover Letter' }
];

const ProfileDisplay = ({ profileData }) => {
  const [currentProfileData, setCurrentProfileData] = useState(profileData);
  const [isEditing, setIsEditing] = useState(false);
  const [formValues, setFormValues] = useState(emptyProfile);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [templateOptions, setTemplateOptions] = useState(defaultTemplateOptions);
  const [downloadError, setDownloadError] = useState(null);
  const [isDownloading, setIsDownloading] = useState(false);

  useEffect(() => {
    setCurrentProfileData(profileData);
  }, [profileData]);

  useEffect(() => {
    let isMounted = true;

    const loadTemplates = async () => {
      try {
        const templates = await fetchTemplates();
        if (!isMounted) {
          return;
        }
        if (Array.isArray(templates)) {
          const normalized = templates.map((template) => ({
            value: template.id,
            label: template.name || template.id
          }));
          setTemplateOptions(normalized);
        }
      } catch (error) {
        if (!isMounted) {
          return;
        }
        setTemplateOptions(defaultTemplateOptions);
      }
    };

    loadTemplates();

    return () => {
      isMounted = false;
    };
  }, []);

  const profile = useMemo(() => {
    if (!currentProfileData) {
      return null;
    }
    return currentProfileData.profile || currentProfileData;
  }, [currentProfileData]);

  useEffect(() => {
    if (isEditing && profile) {
      setFormValues({
        name: profile.name || '',
        email: profile.email || '',
        dob: profile.dob || '',
        linkedin: profile.linkedin || '',
        institute: profile.institute || '',
        currentDegree: profile.currentDegree || '',
        branch: profile.branch || '',
        yearOfStudy: profile.yearOfStudy || '',
        certifications: profile.certifications || '',
        achievements: profile.achievements || '',
        technicalSkills: profile.technicalSkills || '',
        softSkills: profile.softSkills || '',
        templateType: profile.templateType || 'professional',
        hiringManagerName: profile.hiringManagerName || '',
        companyName: profile.companyName || '',
        companyAddress: profile.companyAddress || '',
        positionTitle: profile.positionTitle || '',
        relevantExperience: profile.relevantExperience || '',
        keyAchievement: profile.keyAchievement || '',
        strengths: profile.strengths || '',
        closingNote: profile.closingNote || '',
        hasInternship: Boolean(profile.hasInternship),
        internshipDetails: profile.internshipDetails || '',
        hasExperience: Boolean(profile.hasExperience),
        experienceDetails: profile.experienceDetails || '',
      });
    }
  }, [isEditing, profile]);

  if (!profile) {
    return <div className="p-6">No profile data to display</div>;
  }

  const templateText = currentProfileData?.templateText;
  const templateCss = currentProfileData?.templateCss || '';
  const templateName = currentProfileData?.templateName;
  const templateIcon = currentProfileData?.templateIcon;
  const templateDescription = currentProfileData?.templateDescription;
  const templateType = profile?.templateType || currentProfileData?.templateType;
  const profileId = profile?.id || currentProfileData?.id;
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

  const handleDownload = async () => {
    if (!profileId) {
      setDownloadError('Profile ID is required to download PDF');
      return;
    }

    try {
      setIsDownloading(true);
      setDownloadError(null);
      
      const response = await axios.get(`${apiBaseUrl}/api/profiles/${profileId}/download`, {
        responseType: 'blob',
      });

      // Check if the response is actually a PDF by checking content type header
      const contentType = response.headers['content-type'] || response.headers['Content-Type'] || '';
      if (contentType && !contentType.includes('pdf') && !contentType.includes('application/pdf')) {
        // If not a PDF, try to read as text to see the error message
        const text = await response.data.text();
        throw new Error(text || 'Server returned an error');
      }

      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'profile.pdf');
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      // Clear any previous errors on success
      setDownloadError(null);
    } catch (error) {
      console.error('Error downloading profile PDF:', error);
      let errorMessage = 'Failed to download PDF. ';
      
      if (error.response) {
        // Server responded with error status
        if (error.response.status === 404) {
          errorMessage += 'Profile not found.';
        } else if (error.response.status === 500) {
          errorMessage += 'Server error. Please try again later.';
        } else {
          errorMessage += `Server error (${error.response.status}).`;
        }
      } else if (error.request) {
        // Request was made but no response received
        errorMessage += 'Unable to connect to server. Please check your connection.';
      } else {
        // Something else happened
        errorMessage += error.message || 'An unexpected error occurred.';
      }
      
      setDownloadError(errorMessage);
    } finally {
      setIsDownloading(false);
    }
  };

  const handleEditClick = () => {
    if (!profileId) {
      console.error('Profile ID is required to edit profile');
      return;
    }
    setIsEditing(true);
  };

  const handleInputChange = (event) => {
    const { name, value, type, checked } = event.target;
    if (type === 'checkbox') {
      setFormValues((prev) => ({
        ...prev,
        [name]: checked,
        ...(name === 'hasInternship' && !checked ? { internshipDetails: '' } : {}),
        ...(name === 'hasExperience' && !checked ? { experienceDetails: '' } : {}),
      }));
      return;
    }

    setFormValues((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!profileId) {
      return;
    }

    try {
      setIsSubmitting(true);
      const response = await axios.put(
        `${apiBaseUrl}/api/profiles/${profileId}`,
        formValues
      );

      const updatedData = response.data?.data || response.data;
      setCurrentProfileData(updatedData);
      setIsEditing(false);
    } catch (error) {
      console.error('Error updating profile:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const heading = templateType && templateType.toLowerCase() === 'cover'
    ? 'Cover Letter'
    : 'Profile Details';

  useEffect(() => {
    if (!templateCss) {
      return undefined;
    }

    const styleElement = document.createElement('style');
    styleElement.setAttribute('data-template-css', 'profile-display');
    styleElement.innerHTML = templateCss;
    document.head.appendChild(styleElement);

    return () => {
      document.head.removeChild(styleElement);
    };
  }, [templateCss]);

  return (
    <div className="max-w-3xl mx-auto p-6">
      <h2 className="text-2xl font-bold mb-6">{heading}</h2>

      {templateText && (
        <div className="mb-6 p-4 border rounded bg-white shadow-sm">
          {(templateIcon || templateName) && (
            <div className="flex items-center gap-3 mb-3">
              {templateIcon && <span className="text-3xl">{templateIcon}</span>}
              <div>
                {templateName && <h3 className="text-lg font-semibold">{templateName}</h3>}
                {templateDescription && (
                  <p className="text-sm text-gray-600">{templateDescription}</p>
                )}
              </div>
            </div>
          )}
          <p className="whitespace-pre-line">{templateText}</p>
        </div>
      )}

      {!templateText && (
        <div className="p-4 border mb-6">
          <p>No template available.</p>
        </div>
      )}

      <div className="flex flex-col gap-4">
        <div className="flex items-center gap-4">
          <button
            type="button"
            onClick={handleDownload}
            className="px-4 py-2 bg-blue-600 text-white rounded disabled:bg-gray-400 disabled:cursor-not-allowed"
            disabled={!profileId || isDownloading}
          >
            {isDownloading ? 'Downloading...' : 'Download Profile (PDF)'}
          </button>
          <button
            type="button"
            onClick={handleEditClick}
            className="px-4 py-2 bg-gray-700 text-white rounded"
            disabled={!profileId}
          >
            Edit Profile
          </button>
        </div>
        {downloadError && (
          <div className="p-3 bg-red-50 border border-red-200 rounded text-red-700 text-sm">
            {downloadError}
          </div>
        )}
      </div>

      {isEditing && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50 px-4">
          <div className="bg-white rounded shadow-lg w-full max-w-3xl max-h-[90vh] overflow-y-auto p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-xl font-semibold">Edit Profile</h3>
              <button
                type="button"
                onClick={() => setIsEditing(false)}
                className="text-gray-500 hover:text-gray-700"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Name</span>
                  <input
                    type="text"
                    name="name"
                    value={formValues.name}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Email</span>
                  <input
                    type="email"
                    name="email"
                    value={formValues.email}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Date of Birth</span>
                  <input
                    type="date"
                    name="dob"
                    value={formValues.dob}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">LinkedIn</span>
                  <input
                    type="text"
                    name="linkedin"
                    value={formValues.linkedin}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Institute</span>
                  <input
                    type="text"
                    name="institute"
                    value={formValues.institute}
                    onChange={handleInputChange}
                    required
                    className="w-full border rounded px-3 py-2"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Current Degree</span>
                  <input
                    type="text"
                    name="currentDegree"
                    value={formValues.currentDegree}
                    onChange={handleInputChange}
                    required
                    className="w-full border rounded px-3 py-2"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Branch</span>
                  <input
                    type="text"
                    name="branch"
                    value={formValues.branch}
                    onChange={handleInputChange}
                    required
                    className="w-full border rounded px-3 py-2"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Year of Study</span>
                  <input
                    type="text"
                    name="yearOfStudy"
                    value={formValues.yearOfStudy}
                    onChange={handleInputChange}
                    required
                    className="w-full border rounded px-3 py-2"
                  />
                </label>

                <label className="flex flex-col gap-2 md:col-span-2">
                  <span className="text-sm font-medium">Certifications</span>
                  <textarea
                    name="certifications"
                    value={formValues.certifications}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                    rows={2}
                  />
                </label>

                <label className="flex flex-col gap-2 md:col-span-2">
                  <span className="text-sm font-medium">Achievements</span>
                  <textarea
                    name="achievements"
                    value={formValues.achievements}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                    rows={2}
                  />
                </label>

                <label className="flex flex-col gap-2 md:col-span-2">
                  <span className="text-sm font-medium">Technical Skills</span>
                  <textarea
                    name="technicalSkills"
                    value={formValues.technicalSkills}
                    onChange={handleInputChange}
                    required
                    className="w-full border rounded px-3 py-2"
                    rows={2}
                  />
                </label>

                <label className="flex flex-col gap-2 md:col-span-2">
                  <span className="text-sm font-medium">Soft Skills</span>
                  <textarea
                    name="softSkills"
                    value={formValues.softSkills}
                    onChange={handleInputChange}
                    required
                    className="w-full border rounded px-3 py-2"
                    rows={2}
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Template Type</span>
                  <select
                    name="templateType"
                    value={formValues.templateType}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                  >
                    {templateOptions.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Hiring Manager Name</span>
                  <input
                    type="text"
                    name="hiringManagerName"
                    value={formValues.hiringManagerName}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Company Name</span>
                  <input
                    type="text"
                    name="companyName"
                    value={formValues.companyName}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Company Address</span>
                  <textarea
                    name="companyAddress"
                    value={formValues.companyAddress}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                    rows={2}
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Position Title</span>
                  <input
                    type="text"
                    name="positionTitle"
                    value={formValues.positionTitle}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                  />
                </label>

                <label className="flex flex-col gap-2 md:col-span-2">
                  <span className="text-sm font-medium">Relevant Experience</span>
                  <textarea
                    name="relevantExperience"
                    value={formValues.relevantExperience}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                    rows={2}
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Key Achievement</span>
                  <input
                    type="text"
                    name="keyAchievement"
                    value={formValues.keyAchievement}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium">Strengths</span>
                  <textarea
                    name="strengths"
                    value={formValues.strengths}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                    rows={2}
                  />
                </label>

                <label className="flex flex-col gap-2 md:col-span-2">
                  <span className="text-sm font-medium">Closing Note</span>
                  <textarea
                    name="closingNote"
                    value={formValues.closingNote}
                    onChange={handleInputChange}
                    className="w-full border rounded px-3 py-2"
                    rows={2}
                  />
                </label>

                <div className="flex flex-col gap-2 md:col-span-2">
                  <label className="flex items-center gap-2 text-sm font-medium">
                    <input
                      type="checkbox"
                      name="hasInternship"
                      checked={formValues.hasInternship}
                      onChange={handleInputChange}
                      className="h-4 w-4"
                    />
                    <span>Completed Internship</span>
                  </label>
                  {formValues.hasInternship && (
                    <textarea
                      name="internshipDetails"
                      value={formValues.internshipDetails}
                      onChange={handleInputChange}
                      className="w-full border rounded px-3 py-2"
                      rows={2}
                      required={formValues.hasInternship}
                      placeholder="Share internship projects, roles, or key takeaways"
                    />
                  )}
                </div>

                <div className="flex flex-col gap-2 md:col-span-2">
                  <label className="flex items-center gap-2 text-sm font-medium">
                    <input
                      type="checkbox"
                      name="hasExperience"
                      checked={formValues.hasExperience}
                      onChange={handleInputChange}
                      className="h-4 w-4"
                    />
                    <span>Professional Experience</span>
                  </label>
                  {formValues.hasExperience && (
                    <textarea
                      name="experienceDetails"
                      value={formValues.experienceDetails}
                      onChange={handleInputChange}
                      className="w-full border rounded px-3 py-2"
                      rows={2}
                      required={formValues.hasExperience}
                      placeholder="Mention organisations, roles, and key contributions"
                    />
                  )}
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setIsEditing(false)}
                  className="px-4 py-2 bg-gray-200 text-gray-800 rounded"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 text-white rounded"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? 'Saving...' : 'Save Changes'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProfileDisplay;

