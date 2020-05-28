package com.yunusemregul.prolab23;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class GeneralController implements Initializable
{

    public GeneralController()
    {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

    }

    @FXML
    public void close(ActionEvent e)
    {
        Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void minimize(ActionEvent e)
    {
        Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
}
