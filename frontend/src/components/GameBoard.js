import React, { useState } from 'react';

function GameBoard({ playerInfo, numberOfPlayers, onEndGame }) {
  const [hand, setHand] = useState([
    { id: 1, rank: '2', suit: 'H' },
    { id: 2, rank: '5', suit: 'D' },
    { id: 3, rank: '5', suit: 'S' },
    { id: 4, rank: '8', suit: 'C' },
    { id: 5, rank: 'K', suit: 'H' },
    { id: 6, rank: 'A', suit: 'S' },
    { id: 7, rank: 'J', suit: 'D' }
  ]);
  
  const [players, setPlayers] = useState([
    { id: 1, name: playerInfo.playerName, cards: hand.length, books: 0 },
    { id: 2, name: 'Player 2', cards: 5, books: 1 },
    { id: 3, name: 'Player 3', cards: 7, books: 0 }
  ]);
  
  const [selectedRank, setSelectedRank] = useState('');
  const [selectedPlayer, setSelectedPlayer] = useState(null);
  const [gameLog, setGameLog] = useState([
    'Game started. You have been dealt 7 cards.',
    'It\'s your turn!'
  ]);
  
  const [isYourTurn, setIsYourTurn] = useState(true);
  const [deckSize, setDeckSize] = useState(31);
  const [books, setBooks] = useState([]);

  // Simulated game actions
  const handleAskForCard = () => {
    if (!selectedRank || !selectedPlayer) {
      alert('Please select a card rank and a player to ask.');
      return;
    }
    
    // In the future, this will communicate with the Java backend
    const message = `You asked ${selectedPlayer.name} for ${selectedRank}s.`;
    setGameLog([...gameLog, message]);
    
    // Simulate response (would come from backend)
    setTimeout(() => {
      const gotCard = Math.random() > 0.5;
      
      if (gotCard) {
        const newCard = { id: hand.length + 1, rank: selectedRank, suit: ['H', 'D', 'C', 'S'][Math.floor(Math.random() * 4)] };
        setHand([...hand, newCard]);
        setGameLog([...gameLog, message, `${selectedPlayer.name} gave you a ${selectedRank}.`]);
        
        // Check if we made a book
        const rankCount = hand.filter(card => card.rank === selectedRank).length + 1;
        if (rankCount === 4) {
          setBooks([...books, selectedRank]);
          setHand(hand.filter(card => card.rank !== selectedRank));
          setGameLog([...gameLog, message, `${selectedPlayer.name} gave you a ${selectedRank}.`, `You completed a book of ${selectedRank}s!`]);
        }
      } else {
        setGameLog([...gameLog, message, `${selectedPlayer.name} says "Go Fish!"`]);
        
        // Draw from deck
        if (deckSize > 0) {
          const newCard = { id: hand.length + 1, rank: ['2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K', 'A'][Math.floor(Math.random() * 13)], suit: ['H', 'D', 'C', 'S'][Math.floor(Math.random() * 4)] };
          setHand([...hand, newCard]);
          setDeckSize(deckSize - 1);
          setGameLog([...gameLog, message, `${selectedPlayer.name} says "Go Fish!"`, `You drew a ${newCard.rank}${newCard.suit}.`]);
        } else {
          setGameLog([...gameLog, message, `${selectedPlayer.name} says "Go Fish!"`, `The deck is empty.`]);
        }
        
        // End turn
        setIsYourTurn(false);
        setTimeout(() => {
          setGameLog([...gameLog, message, `${selectedPlayer.name} says "Go Fish!"`, `It's ${players[1].name}'s turn.`]);
        }, 1500);
      }
      
      setSelectedRank('');
      setSelectedPlayer(null);
    }, 1000);
  };

  const renderCard = (card) => {
    const suitSymbol = {
      'H': '♥',
      'D': '♦',
      'C': '♣',
      'S': '♠'
    };
    
    const isRed = card.suit === 'H' || card.suit === 'D';
    
    return (
      <div 
        key={card.id} 
        className={`card ${selectedRank === card.rank ? 'selected' : ''} ${isRed ? 'red' : 'black'}`}
        onClick={() => setSelectedRank(card.rank)}
      >
        <div className="card-corner top-left">
          <div className="card-rank">{card.rank}</div>
          <div className="card-suit">{suitSymbol[card.suit]}</div>
        </div>
        <div className="card-center">{suitSymbol[card.suit]}</div>
        <div className="card-corner bottom-right">
          <div className="card-rank">{card.rank}</div>
          <div className="card-suit">{suitSymbol[card.suit]}</div>
        </div>
      </div>
    );
  };

  return (
    <div className="game-board">
      <div className="game-header">
        <h2>Go Fish Game</h2>
        <div className="game-info">
          <span>Deck: {deckSize} cards</span>
          <span>Your Books: {books.length}</span>
        </div>
        <button className="exit-button" onClick={onEndGame}>Exit Game</button>
      </div>
      
      <div className="players-container">
        {players.filter(p => p.id !== 1).map(player => (
          <div 
            key={player.id}
            className={`player ${selectedPlayer?.id === player.id ? 'selected' : ''}`}
            onClick={() => setSelectedPlayer(player)}
          >
            <div className="player-name">{player.name}</div>
            <div className="player-cards">{player.cards} cards</div>
            <div className="player-books">{player.books} books</div>
          </div>
        ))}
      </div>
      
      <div className="game-log">
        <h3>Game Log</h3>
        <div className="log-entries">
          {gameLog.map((entry, index) => (
            <div key={index} className="log-entry">{entry}</div>
          ))}
        </div>
      </div>
      
      <div className="your-hand">
        <h3>Your Hand</h3>
        <div className="cards-container">
          {hand.map(card => renderCard(card))}
        </div>
      </div>
      
      <div className="game-actions">
        <button 
          onClick={handleAskForCard}
          disabled={!isYourTurn || !selectedRank || !selectedPlayer}
        >
          Ask for Card
        </button>
        
        {selectedRank && (
          <div className="selection-info">
            Asking for: {selectedRank}s from {selectedPlayer?.name || 'Select a player'}
          </div>
        )}
      </div>
    </div>
  );
}

export default GameBoard; 