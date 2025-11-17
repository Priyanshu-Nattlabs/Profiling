import React, { useState, useEffect, useRef } from 'react';
import { generateQuestions, sendChatMessage, evaluateInterests } from '../api';

const SaarthiChatbot = ({ userProfile }) => {
  const [chatState, setChatState] = useState(null);
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isGeneratingQuestions, setIsGeneratingQuestions] = useState(false);
  const [isComplete, setIsComplete] = useState(false);
  const [evaluationResult, setEvaluationResult] = useState(null);
  const [isEvaluating, setIsEvaluating] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    // Initialize chatbot when component mounts
    if (userProfile && !chatState) {
      initializeChatbot();
    }
  }, [userProfile]);

  const initializeChatbot = async () => {
    setIsGeneratingQuestions(true);
    try {
      const result = await generateQuestions(userProfile);
      if (result.success && result.data.questions) {
        const questions = result.data.questions;
        const initialState = {
          currentStage: 1,
          currentQuestionIndex: 0,
          questions: questions,
          answers: {},
          pendingWhyQuestion: null,
          complete: false
        };
        setChatState(initialState);
        
        // Add welcome message and first question
        setMessages([
          {
            type: 'bot',
            text: `Hello! I'm Saarthi, your AI career counselor. I'll ask you ${questions.length} questions to understand your interests better. Let's begin!`
          },
          {
            type: 'bot',
            text: questions[0]
          }
        ]);
      }
    } catch (error) {
      setMessages([{
        type: 'error',
        text: 'Failed to initialize chatbot. Please try again.'
      }]);
    } finally {
      setIsGeneratingQuestions(false);
    }
  };

  const handleSendMessage = async () => {
    if (!inputMessage.trim() || isLoading || !chatState) return;

    const userMsg = inputMessage.trim();
    setInputMessage('');
    
    // Add user message to chat
    setMessages(prev => [...prev, { type: 'user', text: userMsg }]);
    setIsLoading(true);

    try {
      const result = await sendChatMessage(userMsg, chatState);
      
      if (result.success && result.data) {
        const { nextQuestion, conversationState: updatedState, isComplete: completed } = result.data;
        
        setChatState(updatedState);
        
        if (completed) {
          setIsComplete(true);
          setMessages(prev => [...prev, {
            type: 'bot',
            text: 'Great! You\'ve answered all questions. Let me analyze your responses...'
          }]);
          
          // Automatically evaluate
          handleEvaluate();
        } else if (nextQuestion) {
          setMessages(prev => [...prev, { type: 'bot', text: nextQuestion }]);
        }
      }
    } catch (error) {
      setMessages(prev => [...prev, {
        type: 'error',
        text: 'Failed to send message. Please try again.'
      }]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleEvaluate = async () => {
    if (!chatState || !userProfile) return;
    
    setIsEvaluating(true);
    try {
      const result = await evaluateInterests(userProfile, chatState.answers);
      
      if (result.success && result.data) {
        setEvaluationResult(result.data);
        setMessages(prev => [...prev, {
          type: 'bot',
          text: 'Evaluation complete! Scroll down to see your comprehensive results.'
        }]);
      }
    } catch (error) {
      setMessages(prev => [...prev, {
        type: 'error',
        text: 'Failed to evaluate. Please try again.'
      }]);
    } finally {
      setIsEvaluating(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  if (isGeneratingQuestions) {
    return (
      <div className="flex items-center justify-center p-8">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Saarthi is preparing your personalized questions...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="bg-white rounded-lg shadow-lg overflow-hidden">
        {/* Chat Header */}
        <div className="bg-gradient-to-r from-blue-600 to-blue-700 text-white p-4">
          <h2 className="text-xl font-semibold">💬 Chat with Saarthi</h2>
          <p className="text-sm text-blue-100">AI Career Counselor</p>
        </div>

        {/* Messages Area */}
        <div className="h-96 overflow-y-auto p-4 bg-gray-50">
          {messages.map((msg, idx) => (
            <div
              key={idx}
              className={`mb-4 flex ${msg.type === 'user' ? 'justify-end' : 'justify-start'}`}
            >
              <div
                className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${
                  msg.type === 'user'
                    ? 'bg-blue-600 text-white'
                    : msg.type === 'error'
                    ? 'bg-red-100 text-red-800'
                    : 'bg-white text-gray-800 border border-gray-200'
                }`}
              >
                <p className="text-sm whitespace-pre-wrap">{msg.text}</p>
              </div>
            </div>
          ))}
          {isLoading && (
            <div className="flex justify-start mb-4">
              <div className="bg-white border border-gray-200 rounded-lg px-4 py-2">
                <div className="flex space-x-1">
                  <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce"></div>
                  <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0.1s' }}></div>
                  <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
                </div>
              </div>
            </div>
          )}
          {isEvaluating && (
            <div className="flex justify-start mb-4">
              <div className="bg-white border border-gray-200 rounded-lg px-4 py-2">
                <p className="text-sm text-gray-600">Analyzing your responses...</p>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        {/* Input Area */}
        {!isComplete && (
          <div className="border-t border-gray-200 p-4 bg-white">
            <div className="flex gap-2">
              <input
                type="text"
                value={inputMessage}
                onChange={(e) => setInputMessage(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="Type your answer..."
                className="flex-1 border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                disabled={isLoading}
              />
              <button
                onClick={handleSendMessage}
                disabled={isLoading || !inputMessage.trim()}
                className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                Send
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Evaluation Results */}
      {evaluationResult && (
        <div className="mt-6 bg-white rounded-lg shadow-lg p-6">
          <h3 className="text-2xl font-bold mb-4 text-gray-800">Your Interest Evaluation</h3>
          
          {/* Interest Scores */}
          {evaluationResult.interests && (
            <div className="mb-6">
              <h4 className="text-lg font-semibold mb-3">Interest Scores</h4>
              <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
                {Object.entries(evaluationResult.interests).map(([key, value]) => (
                  <div key={key} className="bg-gray-50 p-3 rounded">
                    <p className="text-sm text-gray-600 capitalize">{key}</p>
                    <p className="text-2xl font-bold text-blue-600">{value.toFixed(1)}%</p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Interest Persona */}
          {evaluationResult.interestPersona && (
            <div className="mb-6">
              <h4 className="text-lg font-semibold mb-2">Your Interest Persona</h4>
              <p className="text-gray-700 bg-blue-50 p-4 rounded">{evaluationResult.interestPersona}</p>
            </div>
          )}

          {/* Summary */}
          {evaluationResult.summary && (
            <div className="mb-6">
              <h4 className="text-lg font-semibold mb-2">Summary</h4>
              <p className="text-gray-700">{evaluationResult.summary}</p>
            </div>
          )}

          {/* Strengths & Weaknesses */}
          <div className="grid md:grid-cols-2 gap-4 mb-6">
            {evaluationResult.strengths && evaluationResult.strengths.length > 0 && (
              <div>
                <h4 className="text-lg font-semibold mb-2 text-green-700">Strengths</h4>
                <ul className="list-disc list-inside space-y-1">
                  {evaluationResult.strengths.map((strength, idx) => (
                    <li key={idx} className="text-gray-700">{strength}</li>
                  ))}
                </ul>
              </div>
            )}
            {evaluationResult.weaknesses && evaluationResult.weaknesses.length > 0 && (
              <div>
                <h4 className="text-lg font-semibold mb-2 text-orange-700">Areas to Improve</h4>
                <ul className="list-disc list-inside space-y-1">
                  {evaluationResult.weaknesses.map((weakness, idx) => (
                    <li key={idx} className="text-gray-700">{weakness}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>

          {/* Dos & Don'ts */}
          <div className="grid md:grid-cols-2 gap-4 mb-6">
            {evaluationResult.dos && evaluationResult.dos.length > 0 && (
              <div>
                <h4 className="text-lg font-semibold mb-2 text-green-700">Do's</h4>
                <ul className="list-disc list-inside space-y-1">
                  {evaluationResult.dos.map((doItem, idx) => (
                    <li key={idx} className="text-gray-700">{doItem}</li>
                  ))}
                </ul>
              </div>
            )}
            {evaluationResult.donts && evaluationResult.donts.length > 0 && (
              <div>
                <h4 className="text-lg font-semibold mb-2 text-red-700">Don'ts</h4>
                <ul className="list-disc list-inside space-y-1">
                  {evaluationResult.donts.map((dont, idx) => (
                    <li key={idx} className="text-gray-700">{dont}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>

          {/* Recommended Roles */}
          {evaluationResult.recommendedRoles && evaluationResult.recommendedRoles.length > 0 && (
            <div className="mb-6">
              <h4 className="text-lg font-semibold mb-2">Recommended Roles</h4>
              <div className="flex flex-wrap gap-2">
                {evaluationResult.recommendedRoles.map((role, idx) => (
                  <span key={idx} className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm">
                    {role}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Roadmap */}
          {evaluationResult.roadmap90Days && (
            <div className="mb-6">
              <h4 className="text-lg font-semibold mb-2">90-Day Roadmap</h4>
              <p className="text-gray-700 bg-gray-50 p-4 rounded">{evaluationResult.roadmap90Days}</p>
            </div>
          )}

          {/* Suggested Courses */}
          {evaluationResult.suggestedCourses && evaluationResult.suggestedCourses.length > 0 && (
            <div className="mb-6">
              <h4 className="text-lg font-semibold mb-2">Suggested Courses</h4>
              <ul className="list-disc list-inside space-y-1">
                {evaluationResult.suggestedCourses.map((course, idx) => (
                  <li key={idx} className="text-gray-700">{course}</li>
                ))}
              </ul>
            </div>
          )}

          {/* Project Ideas */}
          {evaluationResult.projectIdeas && evaluationResult.projectIdeas.length > 0 && (
            <div className="mb-6">
              <h4 className="text-lg font-semibold mb-2">Project Ideas</h4>
              <ul className="list-disc list-inside space-y-1">
                {evaluationResult.projectIdeas.map((idea, idx) => (
                  <li key={idx} className="text-gray-700">{idea}</li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default SaarthiChatbot;

