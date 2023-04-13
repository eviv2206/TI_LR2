package com.example.lr2;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    @FXML
    private Button executeBtn;

    @FXML
    private TextField fileNameText;

    @FXML
    private RadioButton decodeBtn;

    @FXML
    private RadioButton encodeBtn;

    @FXML
    private Button getPathBtn;

    @FXML
    private TextField keyText;

    @FXML
    private ToggleGroup method;

    @FXML
    private TextArea resultFileText;

    @FXML
    private TextArea sourceFileText;

    @FXML
    private TextArea fullKeyText;

    @FXML
    private TextField sourcePathText;

    private String currentPath;

    @FXML
    void getSourcePath(ActionEvent event) {
        Stage stage = (Stage) getPathBtn.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            currentPath = selectedFile.getAbsolutePath();
            sourcePathText.setText(selectedFile.getName());
        }
    }

    @FXML
    void onExecute(ActionEvent event) {
        sourceFileText.clear();
        resultFileText.clear();
        fullKeyText.clear();
        String key = keyText.getText();
        if (isKeyValid(key) && !Objects.equals(sourcePathText.getText(), "")) {
            LFSR lfsr = new LFSR(key);
            String[] res;
            try {
                if (encodeBtn.isSelected()) {
                    res = lfsr.encode(currentPath);
                } else {
                    res = lfsr.decode(currentPath);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String sourceText = cutStr(res[1]);
            String keyText = cutStr(res[0]);
            String resultText = cutStr(res[2]);
            fullKeyText.setText(keyText);
            resultFileText.setText(resultText);
            sourceFileText.setText(sourceText);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setContentText("Ключ задан неверно или не выбран файл");
            alert.showAndWait();
            keyText.clear();
        }
    }

    private String cutStr(String str){
        if (str.length() > 8 * 30) {
            return str.substring(0, 8 * 30) + "\n ... \n" + str.substring(str.length() - 8 * 30);
        }
        return str;
    }

    private boolean isKeyValid(String str) {
        if (str.length() != 38) { // проверяем длину строки
            return false;
        }
        for (char c : str.toCharArray()) { // проходим по каждому символу строки
            if (c != '0' && c != '1') { // проверяем, является ли символ нулём или единицей
                return false;
            }
        }
        return true; // если все символы являются нулями или единицами, возвращаем true
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        keyText.setText("11111111111111111111111111111111111111");
    }
}
