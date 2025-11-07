import React from 'react';

const TemplateSelection = ({ onTemplateSelect, onCoverLetterSelect }) => {
  const templates = [
    {
      id: 'professional',
      name: 'Professional',
      description: 'Extremely professional with fluent English',
      icon: '💼'
    },
    {
      id: 'bio',
      name: 'Bio',
      description: 'Casual and friendly bio style',
      icon: '✨'
    },
    {
      id: 'story',
      name: 'Story',
      description: 'Simple story-like narrative',
      icon: '📖'
    },
    {
      id: 'cover',
      name: 'Cover Letter',
      description: 'Generate a tailored cover letter after providing company details',
      icon: '✉️'
    }
  ];

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h2 className="text-2xl font-bold mb-6">Choose Your Profile Template</h2>
      <p className="mb-8">Select a template style for your profile</p>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {templates.map((template) => (
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
            <div className="text-4xl mb-4">{template.icon}</div>
            <h3 className="text-xl font-semibold mb-2">{template.name}</h3>
            <p className="text-gray-600">{template.description}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default TemplateSelection;

