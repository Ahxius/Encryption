package com.example.encryptionraw;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloController {
    @FXML
    private Button accessWordlistButton, runHashButton;
    @FXML
    private CheckBox solveStartMinimized, enableVerbosity;
    @FXML
    private ComboBox<String> hashComboBox, solveComboBox;
    @FXML
    private ListView<String> encryptPreListView, encryptPostListView;
    @FXML
    private TabPane tabPane;
    @FXML
    private TextArea hashOutput, encryptOutput, encryptKey, hashInput, hashKeyInput, hashKeyOutput, solveInputTextArea,
            encryptInput, encryptIV, encryptSecondaryInput, encryptTertiaryInput;
    @FXML
    private TextField threadCountInput, updateIntervalInput;
    private String wordlistName = "Custom";
    private StringBuilder wordlist = new StringBuilder();
    private final static String[] HASH_ALGORITHMS = {"md4", "md5", "highway64", "highway128",
            "highway256", "sha1", "sha256"}, CRACKABLE_ALGORITHMS = {"md4", "md5", "sha1", "sha256"},
            ENCRYPTION_ALGORITHMS = {"bacon", "caesar", "monoalphabetic", "aes"};

    @FXML
    public void initialize() {
        for (String algorithm : HASH_ALGORITHMS) hashComboBox.getItems().add(algorithm.toUpperCase(Locale.ROOT));
        for (String algorithm : CRACKABLE_ALGORITHMS) solveComboBox.getItems().add(algorithm.toUpperCase(Locale.ROOT));
        for (String algorithm : ENCRYPTION_ALGORITHMS)
            encryptPreListView.getItems().add(algorithm.toUpperCase(Locale.ROOT));
    }

    @FXML
    public void addEncryptionStage() {
        if (encryptPostListView.getItems().isEmpty()) runEncryption(encryptInput.getText());
        else runEncryption(encryptOutput.getText());
        encryptPostListView.getItems().add(encryptPreListView.getSelectionModel().getSelectedItem());
    }

    private void runEncryption(String input) {
        switch (encryptPreListView.getSelectionModel().getSelectedItem().toLowerCase(Locale.ROOT)) {
            case "bacon" -> {
                if ((encryptSecondaryInput.getText().isEmpty() ^ encryptTertiaryInput.getText().isEmpty()) ||
                        (encryptSecondaryInput.getText().isEmpty() && encryptTertiaryInput.getText().isEmpty()))
                encryptOutput.setText(Encrypt.bacon(input));
                else encryptOutput.setText(Encrypt.bacon(input, encryptSecondaryInput.getText().charAt(0),
                        encryptTertiaryInput.getText().charAt(0)));
                encryptKey.setText("");
                encryptIV.setText("");
            }
            case "caesar" -> {
                if (encryptSecondaryInput.getText().isEmpty()) encryptOutput.setText(Encrypt.caesar(input));
                else encryptOutput.setText(Encrypt.caesar(input, Integer.parseInt(encryptSecondaryInput.getText())));
                encryptKey.setText("");
                encryptIV.setText("");
            }
            case "monoalphabetic" -> {
                String[] cipherText;
                if (encryptSecondaryInput.getText().isEmpty()) cipherText = Encrypt.monoalphabetic(input);
                else cipherText = Encrypt.monoalphabetic(input, encryptSecondaryInput.getText());
                encryptOutput.setText(cipherText[0]);
                encryptKey.setText(cipherText[1]);
                encryptIV.setText("");
            }
            case "aes" -> {
                String[] cipherText;
                if ((encryptSecondaryInput.getText().isEmpty() ^ encryptTertiaryInput.getText().isEmpty()) ||
                        (encryptSecondaryInput.getText().isEmpty() && encryptTertiaryInput.getText().isEmpty()))
                    cipherText = Encrypt.aes(input);
                else cipherText = Encrypt.aes(input, encryptSecondaryInput.getText(), encryptTertiaryInput.getText());
                assert cipherText != null;
                encryptOutput.setText(cipherText[0]);
                encryptKey.setText(cipherText[1]);
                encryptIV.setText(cipherText[2]);
            }
        }
    }

    @FXML
    public void clearStages() {
        encryptPostListView.getItems().clear();
        encryptOutput.setText("");
        encryptInput.setText("");
        encryptSecondaryInput.setText("");
        encryptTertiaryInput.setText("");
        encryptKey.setText("");
        encryptIV.setText("");
    }

    @FXML
    public void hashComboChange() {
        runHashButton.setDisable(false);
        if (hashComboBox.getSelectionModel().getSelectedItem().contains("HIGHWAY")) {
            hashInput.setPrefWidth(245);
            hashKeyInput.setVisible(true);
            hashOutput.setPrefWidth(245);
            hashKeyOutput.setVisible(true);
        } else {
            hashInput.setPrefWidth(348);
            hashKeyInput.setVisible(false);
            hashOutput.setPrefWidth(348);
            hashKeyOutput.setVisible(false);
        }
    }

    @FXML
    public void copyToClipboard() {
        if (tabPane.getSelectionModel().getSelectedItem().getText().equals("Hash")) {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(hashOutput.getText().substring(6)), null);
        } else if (tabPane.getSelectionModel().getSelectedItem().getText().equals("Encrypt")) {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(encryptOutput.getText().substring(6) + " : "
                            + encryptKey.getText()), null);
        }
    }

    @FXML
    public void runHash() {
        String[] output;
        if (hashKeyInput.getText().isEmpty()) {
            switch (hashComboBox.getSelectionModel().getSelectedItem().toLowerCase()) {
                case "md4" -> output = Hash.md4(hashInput.getText());
                case "md5" -> output = Hash.md5(hashInput.getText());
                case "highway64" -> {
                    output = Hash.highway64(hashInput.getText());
                    hashKeyOutput.setText(output[1]);
                }
                case "highway128" -> {
                    output = Hash.highway128(hashInput.getText());
                    hashKeyOutput.setText(output[1]);
                }
                case "highway256" -> {
                    output = Hash.highway256(hashInput.getText());
                    hashKeyOutput.setText(output[1]);
                }
                case "sha1" -> output = Hash.sha1(hashInput.getText());
                case "sha256" -> output = Hash.sha256(hashInput.getText());
                case default -> throw new NullPointerException();
            }
            hashOutput.setText(output[0]);
        } else {
            switch (hashComboBox.getSelectionModel().getSelectedItem().toLowerCase()) {
                case "highway64" -> output = Hash.highway64(hashInput.getText(), hashKeyInput.getText());
                case "highway128" -> output = Hash.highway128(hashInput.getText(), hashKeyInput.getText());
                case "highway256" -> output = Hash.highway256(hashInput.getText(), hashKeyInput.getText());
                case default -> throw new NullPointerException();
            }
            hashOutput.setText(output[0]);
            hashKeyOutput.setText(output[1]);
        }
    }

    @FXML
    public void importWordlist() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Wordlist");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        try {
            File selectedFile = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
            Scanner fileAsScanner = new Scanner(selectedFile);
            wordlist = new StringBuilder();
            Thread task = new Thread(() -> {
                while (fileAsScanner.hasNextLine()) wordlist.append(fileAsScanner.nextLine()).append("\n");
            });
            task.start();
            task.join();
            wordlistName = selectedFile.getName();
            if (wordlist.length() > 10000) {
                JOptionPane.showMessageDialog(null, "The \"Access Wordlist\" feature is " +
                        "not supported with lists greater than 10,000 words. Please clear the wordlist to " +
                        "re-enable this feature.", "Alert", JOptionPane.WARNING_MESSAGE);
                accessWordlistButton.setDisable(true);
            } else accessWordlistButton.setDisable(false);
        } catch (IOException | InterruptedException e) { e.printStackTrace(); }
    }

    @FXML
    public void clearWordlist() {
        if (JOptionPane.showConfirmDialog(null, "Clear wordlist?", "Alert",
                JOptionPane.YES_NO_OPTION) == 0) {
            wordlist = new StringBuilder();
            accessWordlistButton.setDisable(false);
            wordlistName = "Custom";
        }
    }

    @FXML
    public void accessWordlist() {
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane, 700, 500);
        stage.setScene(scene);
        TextArea textArea = new TextArea(wordlist.toString());
        textArea.setPrefSize(700, 400);
        textArea.setLayoutY(100);
        Button button = new Button("SAVE");
        Thread task = new Thread(() -> wordlist = new StringBuilder(textArea.getText()));
        button.setOnAction(event -> task.start());
        pane.getChildren().addAll(textArea, button);
        stage.setResizable(false);
        stage.setTitle("Access Wordlist");
        stage.show();
    }

    @FXML
    public void runSoloBrute() {
        int threadCount = Integer.parseInt(threadCountInput.getText()) > 0
                ? Integer.parseInt(threadCountInput.getText()) : 1,
                updateInterval = updateIntervalInput.getText().isEmpty()
                        ? 100 : Integer.parseInt(updateIntervalInput.getText());
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        TextArea console = new TextArea();
        AtomicInteger activeThreads = new AtomicInteger(threadCount);
        ArrayList<XYChart.Series<Integer, Integer>> wordsPerSecondSeries = new ArrayList<>();
        ProgressBar progressBar = new ProgressBar();
        Label activeThreadsLabel = new Label("Active Threads: " + threadCount + "/" + threadCount);
        fillObjects(stage, pane, progressBar, console, wordsPerSecondSeries, activeThreadsLabel, threadCount);
        final int[] totalWordsCompleted = new int[threadCount];
        int[] seconds = {0}, wordsCompleted = new int[threadCount];
        var ref = new Object() {
            String found = null;
        };
        String[] cumulativeArray = wordlist.toString().split("\n");
        int constant = cumulativeArray.length/threadCount;
        String[][] arrays = new String[threadCount][];
        arrays[0] = new String[constant + (cumulativeArray.length - constant * threadCount)];
        for (int i = 1; i < arrays.length; i++) arrays[i] = new String[constant];
        for (int i = 0, j = 0; i < arrays.length; i++)
            for (int k = 0; k < arrays[i].length; k++, j++)
                arrays[i][k] = cumulativeArray[j];
        for (int j = 0; j < arrays.length; j++) {
            int finalJ = j;
            Thread task = new Thread(() -> {
                switch (solveComboBox.getSelectionModel().getSelectedItem().toLowerCase(Locale.ROOT)) {
                    case "md4" -> {
                        for (int i = 0; i < arrays[finalJ].length && ref.found == null; i++) {
                            String word = Hash.md4(arrays[finalJ][i])[0];
                            ref.found = checkWord(updateInterval, console, totalWordsCompleted, progressBar,
                                    wordsCompleted, cumulativeArray, arrays, finalJ, i, word);
                        }
                        Platform.runLater(() -> activeThreadsLabel.setText("Active Threads: " +
                                activeThreads.decrementAndGet() + "/" + threadCount));
                    }
                    case "md5" -> {
                        for (int i = 0; i < arrays[finalJ].length && ref.found == null; i++) {
                            String word = Hash.md5(arrays[finalJ][i])[0];
                            ref.found = checkWord(updateInterval, console, totalWordsCompleted, progressBar,
                                    wordsCompleted, cumulativeArray, arrays, finalJ, i, word);
                        }
                        Platform.runLater(() -> activeThreadsLabel.setText("Active Threads: " +
                                activeThreads.decrementAndGet() + "/" + threadCount));
                    }
                    case "sha1" -> {
                        for (int i = 0; i < arrays[finalJ].length && ref.found == null; i++) {
                            String word = Hash.sha1(arrays[finalJ][i])[0];
                            ref.found = checkWord(updateInterval, console, totalWordsCompleted, progressBar,
                                    wordsCompleted, cumulativeArray, arrays, finalJ, i, word);
                        }
                        Platform.runLater(() -> activeThreadsLabel.setText("Active Threads: " +
                                activeThreads.decrementAndGet() + "/" + threadCount));
                    }
                    case "sha256" -> {
                        for (int i = 0; i < arrays[finalJ].length && ref.found == null; i++) {
                            String word = Hash.sha256(arrays[finalJ][i])[0];
                            ref.found = checkWord(updateInterval, console, totalWordsCompleted, progressBar,
                                    wordsCompleted, cumulativeArray, arrays, finalJ, i, word);
                        }
                        Platform.runLater(() -> activeThreadsLabel.setText("Active Threads: " +
                                activeThreads.decrementAndGet() + "/" + threadCount));
                    }
                }
            });
            task.start();
        }
        final long[] lastUpdate = {System.nanoTime()};
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate[0] >= 1000000000 && stage.isShowing() && ref.found == null) {
                    lastUpdate[0] = System.nanoTime();
                    for (int i = 0; i < threadCount; i++) {
                        wordsPerSecondSeries.get(i).getData().add(new XYChart.Data<>(seconds[0], wordsCompleted[i]));
                        wordsCompleted[i] = 0;
                    }
                    seconds[0]++;
                }
            }
        }.start();
        if (solveStartMinimized.isSelected()) stage.setIconified(true);
        stage.show();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void fillObjects(Stage stage, AnchorPane pane, ProgressBar progressBar, TextArea console,
                             ArrayList<XYChart.Series<Integer, Integer>> wordsPerSecondSeries, Label activeThreadsLabel,
                             int threadCount) {
        Scene scene = new Scene(pane, 900, 500);
        stage.setScene(scene);
        for (int i = 0; i < threadCount; i++) {
            wordsPerSecondSeries.add(new XYChart.Series<>());
            wordsPerSecondSeries.get(i).setName("Thread " + (i + 1));
        }
        NumberAxis xAxis = new NumberAxis(), yAxis = new NumberAxis();
        xAxis.setLabel("Seconds");
        yAxis.setLabel("Words");
        LineChart wordsPerSecondChart = new LineChart<>(xAxis, yAxis);
        wordsPerSecondChart.getData().addAll(wordsPerSecondSeries);
        wordsPerSecondChart.setLayoutX(21);
        wordsPerSecondChart.setLayoutY(199);
        wordsPerSecondChart.setPrefSize(434, 272);
        console.textProperty().addListener((observable, oldValue, newValue) -> {
            if (console.getScrollTop() % 1 == 0) console.setScrollTop(Double.MAX_VALUE);
        });
        console.setEditable(false);
        console.setPrefSize(400, 443);
        console.setLayoutX(467);
        console.setLayoutY(28);
        Label title = new Label(solveInputTextArea.getText() + " - " +
                solveComboBox.getSelectionModel().getSelectedItem()),
                wordlistLabel = new Label("Wordlist: " + wordlistName);
        title.setLayoutX(41);
        title.setLayoutY(28);
        title.setMaxSize(400, 52);
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD,17));
        title.setAlignment(Pos.CENTER);
        wordlistLabel.setLayoutX(258);
        wordlistLabel.setLayoutY(106);
        wordlistLabel.setMaxSize(200, 300);
        wordlistLabel.setAlignment(Pos.CENTER);
        activeThreadsLabel.setLayoutX(88);
        activeThreadsLabel.setLayoutY(106);
        activeThreadsLabel.setMaxSize(200, 30);
        activeThreadsLabel.setAlignment(Pos.CENTER);
        progressBar.setPrefSize(400, 30);
        progressBar.setLayoutX(38);
        progressBar.setLayoutY(158);
        pane.getChildren().addAll(wordsPerSecondChart, console, title, wordlistLabel, activeThreadsLabel, progressBar);
    }

    private String checkWord(int updateInterval, TextArea console, int[] totalWordsCompleted, ProgressBar progressBar,
                             int[] wordsCompleted, String[] cumulativeArray, String[][] arrays, int thread, int i,
                             String word) {
        if (word.equals(solveInputTextArea.getText())) {
            String found = arrays[thread][i];
            Platform.runLater(() -> {
                if (console.getText().isEmpty() && thread == 0) console.appendText("PASS - " +
                        arrays[thread][i] + ":" + word);
                else console.appendText("\nPASS - " + arrays[thread][i] + ":" + word);
                hashOutput.setText(arrays[thread][i] + ":" + word);
            });
            return found;
        } else if (enableVerbosity.isSelected())
            Platform.runLater(() -> {
                if (console.getText().isEmpty()) console.appendText("FAIL - " + arrays[thread][i] + ":" + word);
                else console.appendText("\nFAIL - " + arrays[thread][i] + ":" + word);
            });
        wordsCompleted[thread]++;
        totalWordsCompleted[0]++;
        Platform.runLater(() -> progressBar.setProgress(totalWordsCompleted[0] /
                (double) cumulativeArray.length));
        if (i % updateInterval == 0 && i != 0)
            Platform.runLater(() -> console.appendText("\nUPDATE - THREAD " + thread + " REACHED "
                    + i));
        return null;
    }

}