import React, { useState } from 'react';
import './App.css';
import GameBoard from './components/GameBoard';
import Lobby from './components/Lobby';
import Login from './components/Login';

function App() {
  const [currentScreen, setCurrentScreen] = useState('login'); // 'login', 'lobby', or 'game'
  const [playerInfo, setPlayerInfo] = useState(null);
  const [numberOfPlayers, setNumberOfPlayers] = useState(1);

  const handleLogin = (info) => {
    setPlayerInfo(info);
    setCurrentScreen('lobby');
  };

  const handleLogout = () => {
    setPlayerInfo(null);
    setCurrentScreen('login');
  };

  const handleStartGame = (numPlayers) => {
    setNumberOfPlayers(numPlayers);
    setCurrentScreen('game');
  };

  const handleEndGame = () => {
    setCurrentScreen('lobby');
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Go Fish Card Game</h1>
      </header>
      
      <main>
        {currentScreen === 'login' && <Login onLogin={handleLogin} />}
        
        {currentScreen === 'lobby' && playerInfo && (
          <Lobby 
            playerInfo={playerInfo} 
            onStartGame={handleStartGame} 
            onLogout={handleLogout} 
          />
        )}
        
        {currentScreen === 'game' && playerInfo && (
          <GameBoard 
            playerInfo={playerInfo} 
            numberOfPlayers={numberOfPlayers} 
            onEndGame={handleEndGame} 
          />
        )}
      </main>
      
      <footer className="App-footer">
        <p>&copy; 2025 Go Fish Game - CSE434 Socket Programming Project</p>
      </footer>
    </div>
  );
}

export default App;
