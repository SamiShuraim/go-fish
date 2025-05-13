import React, { useState } from 'react';

function Lobby({ playerInfo, onStartGame, onLogout }) {
  const [players, setPlayers] = useState([
    { id: 1, name: 'Alice', status: 'Available' },
    { id: 2, name: 'Bob', status: 'In Game' },
    { id: 3, name: playerInfo.playerName, status: 'Available' }
  ]);
  
  const [games, setGames] = useState([
    { id: 1, dealer: 'Charlie', players: 3, status: 'In Progress' }
  ]);
  
  const [numberOfPlayers, setNumberOfPlayers] = useState(1);

  // Simulating the functionality that would connect to the backend
  const handleRefreshPlayers = () => {
    // In the future, this will fetch data from the Java backend
    console.log('Refreshing player list...');
  };

  const handleRefreshGames = () => {
    // In the future, this will fetch data from the Java backend
    console.log('Refreshing games list...');
  };

  const handleStartGame = () => {
    // In the future, this will communicate with the Java backend to start a game
    onStartGame(numberOfPlayers);
  };

  return (
    <div className="lobby-container">
      <h2>Go Fish Game Lobby</h2>
      <p>Welcome, {playerInfo.playerName}!</p>
      <p>Connected to: {playerInfo.serverAddress}:{playerInfo.serverPort}</p>
      
      <div className="lobby-section">
        <div className="section-header">
          <h3>Registered Players</h3>
          <button onClick={handleRefreshPlayers}>Refresh</button>
        </div>
        <table className="player-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {players.map(player => (
              <tr key={player.id}>
                <td>{player.name}</td>
                <td>{player.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      
      <div className="lobby-section">
        <div className="section-header">
          <h3>Active Games</h3>
          <button onClick={handleRefreshGames}>Refresh</button>
        </div>
        <table className="games-table">
          <thead>
            <tr>
              <th>Game ID</th>
              <th>Dealer</th>
              <th>Players</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {games.map(game => (
              <tr key={game.id}>
                <td>{game.id}</td>
                <td>{game.dealer}</td>
                <td>{game.players}</td>
                <td>{game.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      
      <div className="start-game-section">
        <h3>Start New Game</h3>
        <div className="form-group">
          <label htmlFor="playerCount">Number of opponents (1-4):</label>
          <select 
            id="playerCount" 
            value={numberOfPlayers} 
            onChange={(e) => setNumberOfPlayers(parseInt(e.target.value))}
          >
            <option value={1}>1</option>
            <option value={2}>2</option>
            <option value={3}>3</option>
            <option value={4}>4</option>
          </select>
        </div>
        <button onClick={handleStartGame}>Start Game</button>
      </div>
      
      <button className="logout-button" onClick={onLogout}>Disconnect</button>
    </div>
  );
}

export default Lobby; 