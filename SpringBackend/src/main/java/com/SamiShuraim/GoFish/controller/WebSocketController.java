package com.SamiShuraim.GoFish.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.SamiShuraim.GoFish.model.GameObj;
import com.SamiShuraim.GoFish.model.PlayerObj;
import com.SamiShuraim.GoFish.service.GameService;
import com.SamiShuraim.GoFish.service.WebSocketService;

@Controller
public class WebSocketController {

    private final GameService gameService;
    private final WebSocketService webSocketService;

    @Autowired
    public WebSocketController(GameService gameService, WebSocketService webSocketService) {
        this.gameService = gameService;
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/join-game/{gameId}")
    @SendTo("/topic/game/{gameId}")
    public GameObj joinGame(@DestinationVariable String gameId, Map<String, String> message, 
                           SimpMessageHeaderAccessor headerAccessor) {
        // Store the game ID in the WebSocket session
        headerAccessor.getSessionAttributes().put("gameId", gameId);
        headerAccessor.getSessionAttributes().put("playerId", message.get("playerId"));
        
        return gameService.getGameById(gameId);
    }

    @MessageMapping("/game/{gameId}/ask-card")
    public void askForCard(@DestinationVariable String gameId, Map<String, Object> message) {
        String askingPlayerId = (String) message.get("askingPlayerId");
        String targetPlayerId = (String) message.get("targetPlayerId");
        String rank = (String) message.get("rank");
        
        GameObj game = gameService.getGameById(gameId);
        PlayerObj askingPlayer = gameService.getPlayerById(askingPlayerId);
        PlayerObj targetPlayer = gameService.getPlayerById(targetPlayerId);
        
        if (game != null && askingPlayer != null && targetPlayer != null) {
            boolean success = gameService.askForCard(gameId, askingPlayerId, targetPlayerId, rank);
            
            // Send card request result to all players in the game
            webSocketService.sendCardRequestResult(game, askingPlayer, targetPlayer, rank, success);
            
            // Send updated game state
            webSocketService.sendGameUpdate(game);
        }
    }

    @MessageMapping("/game/{gameId}/chat")
    @SendTo("/topic/game/{gameId}/chat")
    public Map<String, String> sendChatMessage(@DestinationVariable String gameId, Map<String, String> message) {
        return message;
    }
} 