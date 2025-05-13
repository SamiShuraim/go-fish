package com.SamiShuraim.GoFish.controller;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SamiShuraim.GoFish.model.GameObj;
import com.SamiShuraim.GoFish.model.PlayerObj;
import com.SamiShuraim.GoFish.service.GameService;
import com.SamiShuraim.GoFish.service.WebSocketService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow requests from any origin for development
public class GameController {

    private final GameService gameService;
    private final WebSocketService webSocketService;

    @Autowired
    public GameController(GameService gameService, WebSocketService webSocketService) {
        this.gameService = gameService;
        this.webSocketService = webSocketService;
    }

    // Player management endpoints
    @PostMapping("/players")
    public ResponseEntity<?> registerPlayer(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String address = (String) request.get("address");
            int mPort = Integer.parseInt(request.get("mPort").toString());
            
            PlayerObj player = gameService.registerPlayer(name, address, mPort);
            if (player == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Player name already exists");
            }
            
            // Broadcast updated player list
            webSocketService.broadcastPlayerList(gameService.getAllPlayers());
            
            return ResponseEntity.ok(player);
        } catch (UnknownHostException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid address");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering player: " + e.getMessage());
        }
    }

    @DeleteMapping("/players/{playerId}")
    public ResponseEntity<?> unregisterPlayer(@PathVariable String playerId) {
        boolean success = gameService.unregisterPlayer(playerId);
        if (success) {
            // Broadcast updated player list
            webSocketService.broadcastPlayerList(gameService.getAllPlayers());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/players")
    public ResponseEntity<List<PlayerObj>> getAllPlayers() {
        return ResponseEntity.ok(gameService.getAllPlayers());
    }

    @GetMapping("/players/{playerId}")
    public ResponseEntity<?> getPlayerById(@PathVariable String playerId) {
        PlayerObj player = gameService.getPlayerById(playerId);
        if (player != null) {
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Game management endpoints
    @PostMapping("/games")
    public ResponseEntity<?> createGame(@RequestBody Map<String, Object> request) {
        try {
            String dealerId = (String) request.get("dealerId");
            int numberOfOpponents = Integer.parseInt(request.get("numberOfOpponents").toString());
            
            GameObj game = gameService.createGame(dealerId, numberOfOpponents);
            if (game == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not create game");
            }
            
            // Broadcast updated game and player lists
            webSocketService.broadcastGameList(gameService.getAllGames());
            webSocketService.broadcastPlayerList(gameService.getAllPlayers());
            
            return ResponseEntity.ok(game);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating game: " + e.getMessage());
        }
    }

    @GetMapping("/games")
    public ResponseEntity<List<GameObj>> getAllGames() {
        return ResponseEntity.ok(gameService.getAllGames());
    }

    @GetMapping("/games/{gameId}")
    public ResponseEntity<?> getGameById(@PathVariable String gameId) {
        GameObj game = gameService.getGameById(gameId);
        if (game != null) {
            return ResponseEntity.ok(game);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/games/{gameId}/end")
    public ResponseEntity<?> endGame(@PathVariable String gameId) {
        boolean success = gameService.endGame(gameId);
        if (success) {
            // Broadcast updated game and player lists
            webSocketService.broadcastGameList(gameService.getAllGames());
            webSocketService.broadcastPlayerList(gameService.getAllPlayers());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Game action endpoints
    @PostMapping("/games/{gameId}/ask")
    public ResponseEntity<?> askForCard(
            @PathVariable String gameId,
            @RequestBody Map<String, Object> request) {
        try {
            String askingPlayerId = (String) request.get("askingPlayerId");
            String targetPlayerId = (String) request.get("targetPlayerId");
            String rank = (String) request.get("rank");
            
            GameObj game = gameService.getGameById(gameId);
            PlayerObj askingPlayer = gameService.getPlayerById(askingPlayerId);
            PlayerObj targetPlayer = gameService.getPlayerById(targetPlayerId);
            
            if (game == null || askingPlayer == null || targetPlayer == null) {
                return ResponseEntity.notFound().build();
            }
            
            boolean success = gameService.askForCard(gameId, askingPlayerId, targetPlayerId, rank);
            
            // Send card request result to all players in the game
            webSocketService.sendCardRequestResult(game, askingPlayer, targetPlayer, rank, success);
            
            // Send updated game state
            webSocketService.sendGameUpdate(game);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing card request: " + e.getMessage());
        }
    }
} 