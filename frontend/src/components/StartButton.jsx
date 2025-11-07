import React from 'react';

const StartButton = ({ onStart }) => {
  return (
    <div className="flex justify-center items-center min-h-screen">
      <button
        onClick={onStart}
        className="px-6 py-3 bg-blue-600 text-white rounded hover:bg-blue-700"
      >
        Start Profiling
      </button>
    </div>
  );
};

export default StartButton;

