package chimp;

import chimp.command.Command;
import chimp.controls.DialogBox;
import chimp.core.*;
import chimp.exception.*;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * The Chimp class represents the main application class for the Chimp chatbot.
 * It extends the Application class and provides the user interface for the chatbot.
 * The chatbot interacts with the user through a graphical user interface (GUI) and
 * responds to user inputs by generating appropriate responses.
 */
public class Chimp extends Application {
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;
    private Image user = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image duke = new Image(getClass().getResourceAsStream("/images/DaDuke.png"));

    private Ui ui;
    private TaskList tasks;
    private Storage storage;

    /**
     * Constructs a Chimp object.
     * Initializes the user interface, storage, and tasks.
     */
    public Chimp() {
        this.ui = new Ui();
        this.storage = new Storage();
        Storage.createFileIfNotExist();
        this.tasks = Storage.readOutputFromFile();
    }

    /**
     * Starts the application by initializing the main layout, formatting the window, and adding functionality.
     *
     * @param stage the primary stage of the application
     */
    @Override
    public void start(Stage stage) {
        AnchorPane mainLayout = getMainLayout(stage);
        formatWindow(stage, mainLayout);
        addFunctionality();
        displayOpeningMessage();
    }

    private void addFunctionality() {
        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });

        userInput.setOnAction((event) -> {
            handleUserInput();
        });
    }

    private void formatWindow(Stage stage, AnchorPane mainLayout) {
        stage.setTitle("Chimp");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        userInput.setPrefWidth(325.0);

        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);

        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        // Scroll down to the end every time dialogContainer's height changes.
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
    }

    private AnchorPane getMainLayout(Stage stage) {
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.show();
        return mainLayout;
    }

    private void handleUserInput() {
        String input = userInput.getText();
        Label userText = new Label(input);

        String response = generateResponse(input);
        assert response != null : "response should not be null";

        Label dukeText = new Label(response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, new ImageView(user)),
                DialogBox.getChimpDialog(dukeText, new ImageView(duke)));
        userInput.clear();
    }

    private String generateResponse(String input) {
        try {
            Command c = Parser.parse(input);
            String response = c.execute(this.tasks, this.ui, this.storage);
            if (c.isExit()) {
                exitChimp();
            }
            Storage.saveOutputToFile(this.tasks);
            return response;
        } catch (InvalidCommandException
                | CommandParseException
                | CommandExecuteException
                | IndexOutOfBoundsException e) {
            return ui.say("hoo");
        }
    }

    private void exitChimp() {
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }

    private void displayOpeningMessage() {
        String openingMessage = ui.say("greet");
        Label openingTextLabel = new Label(openingMessage);
        dialogContainer.getChildren().add(DialogBox.getChimpDialog(openingTextLabel, new ImageView(duke)));
        scrollPane.setVvalue(1.0);
    }
}