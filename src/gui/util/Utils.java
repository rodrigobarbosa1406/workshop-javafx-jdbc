package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	public static Stage stageAtual(ActionEvent event) {
		return ((Stage) ((Node) event.getSource()).getScene().getWindow());
	}
	
	public static Integer tentarConverterParaInt(String strValor) {
		try {
			return Integer.parseInt(strValor);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}