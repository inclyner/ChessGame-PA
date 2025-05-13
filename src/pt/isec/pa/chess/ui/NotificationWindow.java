package pt.isec.pa.chess.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.ModelLog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class NotificationWindow extends Stage implements PropertyChangeListener {
    
    private final ListView<String> notificationsList;
    private final LinkedList<String> notifications; // Para limitar o número de notificações
    private final int MAX_NOTIFICATIONS = 100;
    
    private final ToggleButton btnLogs;
    private final ToggleButton btnGameEvents;
    private final Label lblStatus;
    
    private boolean showLogs = true;
    private boolean showGameEvents = true;
    
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public NotificationWindow() {
        setTitle("Notificações do Sistema");
        initStyle(StageStyle.DECORATED);
        
        // Inicializar a lista de notificações
        notifications = new LinkedList<>();
        notificationsList = new ListView<>();
        
        // Criar labels de cabeçalho
        Label lblTitle = new Label("Monitor de Notificações");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        lblStatus = new Label("Monitorando: Logs e Eventos de Jogo");
        lblStatus.setFont(Font.font("System", FontWeight.NORMAL, 12));
        lblStatus.setTextFill(Color.GRAY);
        
        // Botões de filtro
        btnLogs = new ToggleButton("Logs");
        btnLogs.setSelected(true);
        btnLogs.setOnAction(e -> toggleLogsFilter());
        
        btnGameEvents = new ToggleButton("Eventos de Jogo");
        btnGameEvents.setSelected(true);
        btnGameEvents.setOnAction(e -> toggleGameEventsFilter());
        
        // Layout dos botões
        HBox filterButtons = new HBox(10, btnLogs, btnGameEvents);
        filterButtons.setAlignment(Pos.CENTER_LEFT);
        filterButtons.setPadding(new Insets(5));
        
        // Layout do cabeçalho
        VBox headerBox = new VBox(5, lblTitle, lblStatus, filterButtons);
        headerBox.setPadding(new Insets(10));
        
        // Layout principal
        BorderPane root = new BorderPane();
        root.setTop(headerBox);
        root.setCenter(notificationsList);
        BorderPane.setMargin(notificationsList, new Insets(0, 10, 10, 10));
        HBox.setHgrow(notificationsList, Priority.ALWAYS);
        
        Scene scene = new Scene(root, 500, 400);
        setScene(scene);
        
        // Registrar esta janela como listener para notificações
        ModelLog.getInstance().addPropertyChangeListener(this);
        
        // Tornar a janela redimensionável
        setResizable(true);
        
        // Adicionar uma mensagem inicial
        addNotification("[Sistema] Monitor de notificações iniciado");
        
        // Limpar listeners quando a janela for fechada
        setOnCloseRequest(e -> {
            ModelLog.getInstance().removePropertyChangeListener(this);
        });
    }
    

    //Adiciona uma notificação à lista
    private void addNotification(String message) {
        String timestamp = LocalDateTime.now().format(timeFormatter);
        String notification = timestamp + " - " + message;
        
        // Adicionar à lista interna com limite de tamanho
        notifications.addLast(notification);
        while (notifications.size() > MAX_NOTIFICATIONS) {
            notifications.removeFirst();
        }
        
        // Atualizar a ListView
        updateNotificationsList();
    }
    
    // Atualiza a lista de notificações com base nos filtros

    private void updateNotificationsList() {
        // Limpar a lista atual
        notificationsList.getItems().clear();
        
        // Adicionar notificações filtradas
        for (String notification : notifications) {
            boolean isLogEvent = notification.contains("[Log]");
            boolean isGameEvent = !isLogEvent;
            
            if ((isLogEvent && showLogs) || (isGameEvent && showGameEvents)) {
                notificationsList.getItems().add(notification);
            }
        }
        
        // Rolar para a última notificação
        if (!notificationsList.getItems().isEmpty()) {
            notificationsList.scrollTo(notificationsList.getItems().size() - 1);
        }
    }
    
    /**
     * Toggle para mostrar/esconder logs
     */
    private void toggleLogsFilter() {
        showLogs = btnLogs.isSelected();
        updateStatusLabel();
        updateNotificationsList();
    }
    
    //Toggle para mostrar/esconder eventos de jogo
    private void toggleGameEventsFilter() {
        showGameEvents = btnGameEvents.isSelected();
        updateStatusLabel();
        updateNotificationsList();
    }
    
    // Atualiza o label de status com base nos filtros
    private void updateStatusLabel() {
        if (showLogs && showGameEvents) {
            lblStatus.setText("Monitorando: Logs e Eventos de Jogo");
        } else if (showLogs) {
            lblStatus.setText("Monitorando: Apenas Logs");
        } else if (showGameEvents) {
            lblStatus.setText("Monitorando: Apenas Eventos de Jogo");
        } else {
            lblStatus.setText("Monitorização desativado");
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Executar na thread da UI
        javafx.application.Platform.runLater(() -> {
            String propName = evt.getPropertyName();
            
            // Eventos de log
            if (ModelLog.PROP_LOG_ENTRY_ADDED.equals(propName)) {
                String newEntry = evt.getNewValue().toString();
                String lastEntry = newEntry.substring(newEntry.lastIndexOf(System.lineSeparator()) + 1);
                if (!lastEntry.isEmpty()) {
                    addNotification("[Log] " + lastEntry.trim());
                }
            } 
            else if (ModelLog.PROP_LOG_CLEARED.equals(propName)) {
                addNotification("[Log] Logs foram limpos");
            }
            
            // Eventos de jogo
            else if (ChessGameManager.PROP_GAME_OVER.equals(propName)) {
                addNotification("[Jogo] Fim de jogo: " + evt.getNewValue());
            }
            else if (ChessGameManager.PROP_CHECK_STATE.equals(propName)) {
                addNotification("[Jogo] Xeque: " + evt.getNewValue());
            }
            else if (ChessGameManager.PROP_CURRENT_PLAYER.equals(propName)) {
                addNotification("[Jogo] Troca de jogador");
            }
            else if (ChessGameManager.PROP_BOARD_STATE.equals(propName)) {
                addNotification("[Jogo] Tabuleiro atualizado");
            }
        });
    }
}