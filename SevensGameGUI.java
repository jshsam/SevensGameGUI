import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SevensGameGUI extends Application {
    private TextField[] textFields = new TextField[6];
    private Button[] buttons = new Button[6];
    private TextArea outputTextArea;
    private Button rollDiceButton;
    private Button calculateTotalButton;
    private Button endTurnButton;
    private GridPane inputGridPane;
	private HBox buttonsHBox;
	private BorderPane mainPane;
    private Scene scene;

    private int[] playerScore = new int[5];
    private int[] diceRoll = new int[6];
    private boolean[] isDiscarded = new boolean[6];
    private final int MAX_THROWS = 3;
    private final int DISCARD_AMOUNT = 7;
    private int diceCount;
    private int playerNumber = 1;
    private int firstPlayerRolls;
    private int currentPlayerRolls;
    private int winningPlayer;
    private int discardNumbers;
    private int discardCount = 0;
    private SevensGameHandler handler;
    

    public void start(Stage primaryStage) {
        handler = new SevensGameHandler();

        createAndRegisterTextFields();

        createAndRegisterButtons();

        initializePlayerScores();

        initializeIsDiscarded();

        outputTextArea = new TextArea();
        
        inputGridPane = new GridPane();
        inputGridPane.setAlignment(Pos.CENTER);
        inputGridPane.setHgap(15);
        inputGridPane.setVgap(10);
        inputGridPane.setPadding(new Insets(10, 50, 25, 0));
        inputGridPane.add(textFields[0], 5, 2);
        inputGridPane.add(textFields[1], 10, 2);
        inputGridPane.add(textFields[2], 15, 2);
        inputGridPane.add(textFields[3], 20, 2);
        inputGridPane.add(textFields[4], 25, 2);
        inputGridPane.add(textFields[5], 30, 2);
        inputGridPane.add(buttons[0], 5, 4);
        inputGridPane.add(buttons[1], 10, 4);
        inputGridPane.add(buttons[2], 15, 4);
        inputGridPane.add(buttons[3], 20, 4);
        inputGridPane.add(buttons[4], 25, 4);
        inputGridPane.add(buttons[5], 30, 4);

        buttonsHBox = new HBox(100);
        buttonsHBox.setAlignment(Pos.CENTER);
        buttonsHBox.setPadding(new Insets(10));
        buttonsHBox.getChildren().add(rollDiceButton);
        buttonsHBox.getChildren().add(calculateTotalButton);
        buttonsHBox.getChildren().add(endTurnButton);

        mainPane = new BorderPane();
        mainPane.setTop(inputGridPane);
        mainPane.setCenter(outputTextArea);
        mainPane.setBottom(buttonsHBox);

        scene = new Scene(mainPane, 1000, 500);
        primaryStage.setTitle("Sevens Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }//end of start

    private class SevensGameHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent ae) {

            if (ae.getSource() == rollDiceButton) {
                diceCount = 0;
                discardCount = 0;

                for (int i = 0;i < isDiscarded.length;i++) {
                    if (!isDiscarded[i]) {
                        textFields[i].setText("");
                        diceRoll[i] = (int)(1 + (Math.random() * 6));
                        textFields[i].setText("" + diceRoll[i]);
                        textFields[i].setStyle("-fx-opacity:1.0");
                        diceCount++;
                        rollDiceButton.setDisable(true);
                        calculateTotalButton.setDisable(false);
                        buttons[i].setDisable(false);
                    }
                    else {
                        diceRoll[i] = 0;
                        textFields[i].setText("");
                    }
                }
                currentPlayerRolls++;
                if ((currentPlayerRolls == MAX_THROWS) || (currentPlayerRolls == firstPlayerRolls) ) {
                    rollDiceButton.setDisable(true);
                    endTurnButton.setDisable(false);
                }
                calculateTotalButton.setDisable(false);
                if (!isSeven() && discardNumbers % DISCARD_AMOUNT == 0) {
                    endTurnButton.setDisable(false);
                }
                else {
                    endTurnButton.setDisable(true);
                }
            }
            else if (ae.getSource() == calculateTotalButton) {
                if (isSeven() || (discardNumbers % DISCARD_AMOUNT != 0)) {
                    displayErrorAlert();
                    for (int i = 0;i < diceRoll.length;i++) {
                        if (isDiscarded[i]) {
                            isDiscarded[i] = false;
                            buttons[i].setDisable(false);
                            textFields[i].setStyle(null);
                            textFields[i].setStyle("-fx-opacity:1.0");
                            discardNumbers = 0;
                        }
                    }
                    calculateTotalButton.setDisable(true);
                    endTurnButton.setDisable(true);
                }
                else {
                    outputTextArea.appendText("\nPlayer #" + playerNumber + "\nScore: " + calculatePlayerTotal() + "\nNumber of Rolls: " + currentPlayerRolls + "\n");
                    playerScore[playerNumber -1] = calculatePlayerTotal();
                    endTurnButton.setDisable(false);
                }
            }
            else if (ae.getSource() == endTurnButton) {
                endTurn();
            }
    
            for (int i = 0;i < buttons.length;i++) {
                if (ae.getSource() == buttons[i]) {
                    buttons[i].setDisable(true);
                    if (!isSeven() && discardNumbers % DISCARD_AMOUNT == 0) {
                        displayInformationAlert();

                        for (int j = 0;j < buttons.length;j++) {
                            buttons[j].setDisable(true);
                        }

                        if (currentPlayerRolls == MAX_THROWS || currentPlayerRolls == firstPlayerRolls) {
                            rollDiceButton.setDisable(true);
                        }

                        endTurnButton.setDisable(false);
                        discardNumbers = 0;
                        discardCount = 0;
                    }
                    else {
                        textFields[i].setStyle("-fx-background-color:lightgray");
                        isDiscarded[i] = true;
                        discardNumbers += diceRoll[i];
                        if (!isSeven() && discardNumbers % DISCARD_AMOUNT == 0) {
                            endTurnButton.setDisable(false);
                        }
            
                        for (int index = 0;index < isDiscarded.length;index++) {
                            if (isDiscarded[index]) {
                                discardCount++;
                            }
                        }
    
                        if (discardCount == isDiscarded.length) {
                            endTurn();
                        }
                        else {
                            discardCount = 0;
                        }
                    }

                    if (!isSeven() && currentPlayerRolls != MAX_THROWS && currentPlayerRolls != firstPlayerRolls && discardNumbers % DISCARD_AMOUNT == 0) {
                        rollDiceButton.setDisable(false);
                    }
                    calculateTotalButton.setDisable(false);
                }             
            }
        }//end of handle(ActionEvent)
    }//end of class SevensGameHandler

    public void createAndRegisterTextFields() {
        for (int i = 0;i < textFields.length;i++) {
            textFields[i] = new TextField();
            textFields[i].setPrefWidth(30);
            textFields[i].setDisable(true);
        }
    }

    public void createAndRegisterButtons() {
        for (int i = 0;i < buttons.length;i++) {
            buttons[i] = new Button("Discard");
            buttons[i].setOnAction(handler);
        }

        rollDiceButton = new Button("_Roll Dice");
        calculateTotalButton = new Button("_Calculate Total");
        endTurnButton = new Button("_End Turn");
        calculateTotalButton.setDisable(true);
        endTurnButton.setDisable(true);

        rollDiceButton.setOnAction(handler);
        calculateTotalButton.setOnAction(handler);
        endTurnButton.setOnAction(handler);
    }

    public void initializePlayerScores() {
        for (int i = 0;i < playerScore.length;i++) {
            playerScore[i] = 0;
        }
    }

    public void initializeDiceRoll() {
        for (int i = 0;i < diceRoll.length;i++) {
            diceRoll[i] = 0;
        }
    }

    public void initializeIsDiscarded() {
        for (int i = 0;i < isDiscarded.length;i++) {
            isDiscarded[i] = false;
        }
    }

    public boolean isSeven() {
        int oneQuantity = 0;
        int twoQuantity = 0;
        int threeQuantity = 0;
        int fourQuantitiy = 0;
        int fiveQuantity = 0;
        int sixQuantity = 0;
        boolean isSeven = false;

        for (int i = 0;i < diceRoll.length;i++) {
            if (!isDiscarded[i]) {
                if (diceRoll[i] == 1) {
                    oneQuantity++;
                }
                else if (diceRoll[i] == 2) {
                    twoQuantity++;
                }
                else if (diceRoll[i] == 3) {
                    threeQuantity++;
                }
                else if (diceRoll[i] == 4) {
                    fourQuantitiy++;
                }
                else if (diceRoll[i] == 5) {
                    fiveQuantity++;
                }
                else if (diceRoll[i] == 6) {
                    sixQuantity++;
                }
            }
        }

        for (int i = 0;i < diceRoll.length && !isSeven;i++) {
            if (!isDiscarded[i]) {
                switch (diceRoll[i]) {
                    case 6:
                        if (oneQuantity > 0) {
                            isSeven = true;
                        }
                        break;
                    case 5:
                        if (twoQuantity > 0 || oneQuantity >= 2) {
                            isSeven = true;
                        }
                        break;
                    case 4:
                        if (threeQuantity > 0 || (twoQuantity > 0 && oneQuantity > 0) || oneQuantity >= 3) {
                            isSeven = true;
                        }
                        break;
                    case 3:
                        if (fourQuantitiy > 0 || (threeQuantity > 1 && oneQuantity > 0) || twoQuantity > 1 || (twoQuantity > 0 && oneQuantity > 1) || oneQuantity > 3) {
                            isSeven = true;
                        }
                        break;
                    case 2:
                        if (fiveQuantity > 0 || (fourQuantitiy > 0 && oneQuantity > 0) || (threeQuantity > 0 && twoQuantity > 1) || (threeQuantity > 0 && oneQuantity > 1) || 
                                (twoQuantity > 1 && oneQuantity > 2) || oneQuantity > 4) {
                            isSeven = true;
                        }
                        break;
                    case 1:
                        if (sixQuantity > 0 || (fiveQuantity > 0 && oneQuantity > 1) || (fourQuantitiy > 0 && twoQuantity > 0) || (fourQuantitiy > 0 && oneQuantity > 2) || 
                                threeQuantity > 1 || (threeQuantity > 0 && twoQuantity > 0 && oneQuantity > 1) || twoQuantity > 2 || (twoQuantity > 1 && oneQuantity > 2) || 
                                    (twoQuantity > 0 && oneQuantity > 4)) {
                            isSeven = true;
                        }
                }
            }
        }

        oneQuantity = 0;
        twoQuantity = 0;
        threeQuantity = 0;
        fourQuantitiy = 0;
        fiveQuantity = 0;
        sixQuantity = 0;

        return isSeven;
    }//end of isSeven() method

    public int calculatePlayerTotal() {
        int total = 0;

        for (int i = 0;i < diceRoll.length;i++) {
            if (!isDiscarded[i]) {
                total += diceRoll[i];
            }
        }
        return total;
    }

    public void displayErrorAlert() {
        Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText("Cannot Calculate");
        errorAlert.setContentText("Please discard any combinations of numbers that add up to " + DISCARD_AMOUNT + "!");
        errorAlert.showAndWait();
    }

    public void displayInformationAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("No more combinations that equal seven to discard!");
        alert.showAndWait();
    }

    public void endTurn() {
        outputTextArea.appendText("\nPlayer #" + playerNumber + "\nScore: " + calculatePlayerTotal() + "\n");

        playerScore[playerNumber -1] = calculatePlayerTotal();

        if (playerNumber == 1) {
            firstPlayerRolls = currentPlayerRolls;
            winningPlayer = playerNumber;
            outputTextArea.appendText("You are the current Winning Player!\n");
        }
        else if (playerNumber != 1 && playerScore[playerNumber - 1] > playerScore[winningPlayer - 1]) {
            winningPlayer = playerNumber;
            outputTextArea.appendText("You are the current Winning Player!\n");
        }
        else if (playerScore[playerNumber - 1] == playerScore[winningPlayer - 1]) {
            outputTextArea.appendText("There was a Tie!\n");
        }
        else {
            outputTextArea.appendText("You did not win! The current Winning Player is Player #" + winningPlayer + " with a high score of " + playerScore[winningPlayer - 1] + ".\n");
        }

        for (int i = 0;i < textFields.length;i++) {
            textFields[i].setStyle(null);
            textFields[i].setText("");
            buttons[i].setDisable(false);
        }
        rollDiceButton.setDisable(false);
        calculateTotalButton.setDisable(true);
        endTurnButton.setDisable(true);
        currentPlayerRolls = 0;
        diceCount = 0;
        initializeIsDiscarded();
        discardNumbers = 0;
        discardCount = 0;

        if (playerNumber == playerScore.length) {
            if (playerScore[playerNumber - 1] == playerScore[winningPlayer - 1]){
                outputTextArea.appendText("\nThere was no Winner!It was a Tie!\n");
            }
            else {
                outputTextArea.appendText("\nThe Winner is: Player #" + winningPlayer + "\nHigh Score: " + playerScore[winningPlayer - 1] + "\n");
            }
            outputTextArea.appendText("\n________________________________________________________________________________________________________________________________________\n");
            initializePlayerScores();
            initializeDiceRoll();
            playerNumber = 1;
            firstPlayerRolls = 0;
            winningPlayer = 0;
        }
        else {
            playerNumber++;
        }
    }
}//end of class SevenGamesGUI