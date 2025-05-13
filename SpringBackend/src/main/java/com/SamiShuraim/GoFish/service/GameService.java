package com.SamiShuraim.GoFish.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.SamiShuraim.GoFish.model.Card;
import com.SamiShuraim.GoFish.model.GameObj;
import com.SamiShuraim.GoFish.model.PlayerObj;

@Service
public class GameService {

    private final Map<String, PlayerObj> players = new ConcurrentHashMap<>();
    private final Map<String, GameObj> games = new ConcurrentHashMap<>();
    private int gameIdCounter = 1;

    // Player management
    public PlayerObj registerPlayer(String name, String address, int mPort) throws UnknownHostException {
        int rPort = mPort + 1;
        int pPort = mPort + 2;
        
        // Check if player name already exists
        for (PlayerObj player : players.values()) {
            if (player.getName().equals(name)) {
                return null; // Player name already taken
            }
        }
        
        PlayerObj player = new PlayerObj(name, address, mPort, rPort, pPort);
        players.put(player.getId(), player);
        return player;
    }

    public boolean unregisterPlayer(String playerId) {
        if (players.containsKey(playerId)) {
            players.remove(playerId);
            return true;
        }
        return false;
    }

    public List<PlayerObj> getAllPlayers() {
        return new ArrayList<>(players.values());
    }

    public PlayerObj getPlayerById(String playerId) {
        return players.get(playerId);
    }

    public PlayerObj getPlayerByName(String name) {
        return players.values().stream()
                .filter(player -> player.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    // Game management
    public GameObj createGame(String dealerId, int numberOfOpponents) {
        PlayerObj dealer = players.get(dealerId);
        if (dealer == null) {
            return null;
        }

        // Get available players
        List<PlayerObj> availablePlayers = players.values().stream()
                .filter(player -> !player.getId().equals(dealerId) && "Available".equals(player.getStatus()))
                .collect(Collectors.toList());

        if (availablePlayers.size() < numberOfOpponents) {
            return null; // Not enough players available
        }

        // Select random opponents
        Random random = new Random();
        ArrayList<PlayerObj> gamePlayers = new ArrayList<>();
        gamePlayers.add(dealer);

        for (int i = 0; i < numberOfOpponents; i++) {
            if (availablePlayers.isEmpty()) {
                break;
            }
            int index = random.nextInt(availablePlayers.size());
            PlayerObj opponent = availablePlayers.remove(index);
            gamePlayers.add(opponent);
        }

        // Create game with random seed
        int seed = random.nextInt(1000);
        GameObj game = new GameObj(gamePlayers, dealer, gameIdCounter++, seed);
        
        // Update player status
        for (PlayerObj player : gamePlayers) {
            player.setStatus("In Game");
        }
        
        games.put(game.getId(), game);
        return game;
    }

    public List<GameObj> getAllGames() {
        return new ArrayList<>(games.values());
    }

    public GameObj getGameById(String gameId) {
        return games.get(gameId);
    }

    public boolean endGame(String gameId) {
        GameObj game = games.get(gameId);
        if (game == null) {
            return false;
        }
        
        // Update player status
        for (PlayerObj player : game.getPlayers()) {
            player.setStatus("Available");
        }
        
        game.setStatus("Completed");
        return true;
    }

    // Game actions
    public boolean askForCard(String gameId, String askingPlayerId, String targetPlayerId, String rank) {
        GameObj game = games.get(gameId);
        PlayerObj askingPlayer = players.get(askingPlayerId);
        PlayerObj targetPlayer = players.get(targetPlayerId);
        
        if (game == null || askingPlayer == null || targetPlayer == null) {
            return false;
        }
        
        // Check if asking player has the rank
        boolean hasRank = askingPlayer.getHand().stream()
                .anyMatch(card -> card.getRank().equals(rank));
        
        if (!hasRank) {
            return false;
        }
        
        // Check if target player has the rank
        List<Card> matchingCards = targetPlayer.getHand().stream()
                .filter(card -> card.getRank().equals(rank))
                .collect(Collectors.toList());
        
        if (matchingCards.isEmpty()) {
            // Go fish
            if (!game.getDeck().getDeck().isEmpty()) {
                Card drawnCard = game.getDeck().draw();
                askingPlayer.getHand().add(drawnCard);
            }
            return false;
        } else {
            // Transfer cards
            for (Card card : matchingCards) {
                targetPlayer.getHand().remove(card);
                askingPlayer.getHand().add(card);
            }
            
            // Check for books
            askingPlayer.checkHand(rank);
            game.checkBooks(askingPlayer.checkHand(rank));
            
            return true;
        }
    }
} 