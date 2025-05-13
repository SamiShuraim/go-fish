package com.SamiShuraim.GoFish.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.SamiShuraim.GoFish.model.Card;
import com.SamiShuraim.GoFish.model.GameObj;
import com.SamiShuraim.GoFish.model.PlayerObj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Send updated player list to all connected clients
    public void broadcastPlayerList(List<PlayerObj> players) {
        messagingTemplate.convertAndSend("/topic/players", players);
    }

    // Send updated game list to all connected clients
    public void broadcastGameList(List<GameObj> games) {
        messagingTemplate.convertAndSend("/topic/games", games);
    }

    // Send game update to all players in a specific game
    public void sendGameUpdate(GameObj game) {
        messagingTemplate.convertAndSend("/topic/game/" + game.getId(), game);
    }

    // Send private message to a specific player
    public void sendPrivateMessage(String playerId, String message) {
        messagingTemplate.convertAndSendToUser(playerId, "/queue/private", message);
    }

    // Send a card request result to all players in a game
    public void sendCardRequestResult(GameObj game, PlayerObj askingPlayer, PlayerObj targetPlayer, String rank, boolean success) {
        Map<String, Object> result = new HashMap<>();
        result.put("gameId", game.getId());
        result.put("askingPlayer", askingPlayer.getName());
        result.put("targetPlayer", targetPlayer.getName());
        result.put("rank", rank);
        result.put("success", success);
        
        messagingTemplate.convertAndSend("/topic/game/" + game.getId() + "/card-request", result);
    }

    // Send a notification when a player makes a book
    public void sendBookNotification(GameObj game, PlayerObj player, String rank) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("gameId", game.getId());
        notification.put("player", player.getName());
        notification.put("rank", rank);
        
        messagingTemplate.convertAndSend("/topic/game/" + game.getId() + "/book", notification);
    }

    // Send a notification when the game ends
    public void sendGameEndNotification(GameObj game, PlayerObj winner) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("gameId", game.getId());
        notification.put("winner", winner.getName());
        notification.put("books", winner.getBasket().size());
        
        messagingTemplate.convertAndSend("/topic/game/" + game.getId() + "/end", notification);
    }
} 