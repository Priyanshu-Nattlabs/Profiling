import React, { useState } from 'react';

const ProfileForm = ({ onSuccess }) => {
  const [formData, setFormData] = useState({
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
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Just pass the form data to parent, don't submit to backend yet
    onSuccess(formData);
  };

  return (
    <div className="max-w-2xl mx-auto p-6">
      <h2 className="text-2xl font-bold mb-6">Profile Form</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block mb-1">Name</label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border rounded"
          />
        </div>

        <div>
          <label className="block mb-1">Email</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border rounded"
          />
        </div>

        <div>
          <label className="block mb-1">Date of Birth (Dob)</label>
          <input
            type="date"
            name="dob"
            value={formData.dob}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border rounded"
          />
        </div>

        <div>
          <label className="block mb-1">LinkedIn</label>
          <input
            type="url"
            name="linkedin"
            value={formData.linkedin}
            onChange={handleChange}
            className="w-full px-3 py-2 border rounded"
          />
        </div>

        <div>
          <label className="block mb-1">Institute</label>
          <input
            type="text"
            name="institute"
            value={formData.institute}
            onChange={handleChange}
            className="w-full px-3 py-2 border rounded"
          />
        </div>

        <div>
          <label className="block mb-1">Current Degree</label>
          <input
            type="text"
            name="currentDegree"
            value={formData.currentDegree}
            onChange={handleChange}
            className="w-full px-3 py-2 border rounded"
          />
        </div>

        <div>
          <label className="block mb-1">Branch</label>
          <input
            type="text"
            name="branch"
            value={formData.branch}
            onChange={handleChange}
            className="w-full px-3 py-2 border rounded"
          />
        </div>

        <div>
          <label className="block mb-1">Year of Study</label>
          <input
            type="text"
            name="yearOfStudy"
            value={formData.yearOfStudy}
            onChange={handleChange}
            className="w-full px-3 py-2 border rounded"
          />
        </div>

        <div>
          <label className="block mb-1">Certifications</label>
          <textarea
            name="certifications"
            value={formData.certifications}
            onChange={handleChange}
            rows="3"
            className="w-full px-3 py-2 border rounded"
          />
        </div>

        <div>
          <label className="block mb-1">Achievements</label>
          <textarea
            name="achievements"
            value={formData.achievements}
            onChange={handleChange}
            rows="3"
            className="w-full px-3 py-2 border rounded"
          />
        </div>

        <div>
          <label className="block mb-1">Technical Skills</label>
          <textarea
            name="technicalSkills"
            value={formData.technicalSkills}
            onChange={handleChange}
            rows="3"
            className="w-full px-3 py-2 border rounded"
          />
        </div>

        <div>
          <label className="block mb-1">Soft Skills</label>
          <textarea
            name="softSkills"
            value={formData.softSkills}
            onChange={handleChange}
            rows="3"
            className="w-full px-3 py-2 border rounded"
          />
        </div>

        <button
          type="submit"
          className="w-full px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          Next: Choose Template
        </button>
      </form>
    </div>
  );
};

export default ProfileForm;

