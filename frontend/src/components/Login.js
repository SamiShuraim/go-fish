import React, { useState } from 'react';

function Login({ onLogin }) {
  const [playerName, setPlayerName] = useState('');
  const [serverAddress, setServerAddress] = useState('localhost');
  const [serverPort, setServerPort] = useState('7000');
  const [clientPort, setClientPort] = useState('8001');

  const handleSubmit = (e) => {
    e.preventDefault();
    
    // In the future, this will connect to the Java backend
    // For now, just pass the data to the parent component
    onLogin({
      playerName,
      serverAddress,
      serverPort,
      clientPort
    });
  };

  return (
    <div className="login-container">
      <h2>Go Fish Game - Player Registration</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="playerName">Your Name:</label>
          <input
            type="text"
            id="playerName"
            value={playerName}
            onChange={(e) => setPlayerName(e.target.value)}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="serverAddress">Server Address:</label>
          <input
            type="text"
            id="serverAddress"
            value={serverAddress}
            onChange={(e) => setServerAddress(e.target.value)}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="serverPort">Server Port:</label>
          <input
            type="text"
            id="serverPort"
            value={serverPort}
            onChange={(e) => setServerPort(e.target.value)}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="clientPort">Client Base Port:</label>
          <input
            type="text"
            id="clientPort"
            value={clientPort}
            onChange={(e) => setClientPort(e.target.value)}
            required
          />
          <small>This port and the next two ports must be available.</small>
        </div>
        
        <button type="submit" className="login-button">Connect to Server</button>
      </form>
    </div>
  );
}

export default Login; 