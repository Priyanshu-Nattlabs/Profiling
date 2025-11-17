import React, { useEffect, useMemo, useState } from 'react';

import { fetchTemplates } from '../api';

const TEMPLATE_ORDER = [
  'professional',
  'bio',
  'story',
  'industry',
  'modern-professional',
  'executive',
  'cover'
];

const TemplateSelection = ({ onTemplateSelect, onCoverLetterSelect, onBack }) => {
  const [templates, setTemplates] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;

    const loadTemplates = async () => {
      try {
        const result = await fetchTemplates();
        if (!isMounted) {
          return;
        }
        const normalized = Array.isArray(result)
          ? result.map((template) => ({
              id: template.id,
              name: template.name,
              description: template.description,
              icon: template.icon
            }))
          : [];
        setTemplates(normalized);
      } catch (fetchError) {
        if (!isMounted) {
          return;
        }
        const message =
          fetchError.response?.data?.message ||
          fetchError.message ||
          'Unable to load templates right now.';
        setError(message);
        setTemplates([]);
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    };

    loadTemplates();

    return () => {
      isMounted = false;
    };
  }, []);

  const resolvedTemplates = useMemo(() => {
    const fallbackIcon = '🧾';
    const sorted = [...templates].sort((a, b) => {
      const aIndex = TEMPLATE_ORDER.indexOf(a.id);
      const bIndex = TEMPLATE_ORDER.indexOf(b.id);
      if (aIndex === -1 && bIndex === -1) {
        return a.name.localeCompare(b.name);
      }
      if (aIndex === -1) return 1;
      if (bIndex === -1) return -1;
      return aIndex - bIndex;
    });

    return sorted.map((template) => ({
      ...template,
      displayName: template.name || template.id,
      displayDescription: template.description || 'Personalized profile template',
      displayIcon: template.icon || fallbackIcon
    }));
  }, [templates]);

  return (
    <div className="max-w-4xl mx-auto p-6">
      {onBack && (
        <button
          type="button"
          onClick={onBack}
          className="mb-4 px-4 py-2 border border-gray-400 text-gray-700 rounded hover:bg-gray-100 flex items-center gap-2"
        >
          <span>←</span> Back
        </button>
      )}
      <h2 className="text-2xl font-bold mb-6">Choose Your Profile Template</h2>
      <p className="mb-8">Select a template style for your profile</p>

      {isLoading && (
        <div className="mb-4 text-sm text-gray-500">Loading templates…</div>
      )}
      {!isLoading && error && (
        <div className="mb-4 text-sm text-red-600">{error}</div>
      )}
      
      {resolvedTemplates.length === 0 && !isLoading && !error && (
        <div className="text-sm text-gray-500">No templates available.</div>
      )}

      {resolvedTemplates.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {resolvedTemplates.map((template) => (
            <div
              key={template.id}
              onClick={() => {
                if (template.id === 'cover') {
                  if (onCoverLetterSelect) {
                    onCoverLetterSelect();
                  }
                } else {
                  onTemplateSelect(template.id);
                }
              }}
              className="border p-6 rounded cursor-pointer hover:border-blue-500 hover:shadow-lg transition-all"
            >
              <div className="text-4xl mb-4">{template.displayIcon}</div>
              <h3 className="text-xl font-semibold mb-2">{template.displayName}</h3>
              <p className="text-gray-600">{template.displayDescription}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default TemplateSelection;

