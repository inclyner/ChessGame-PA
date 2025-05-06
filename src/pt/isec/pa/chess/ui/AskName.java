package pt.isec.pa.chess.ui;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class AskName extends Stage {
    ModelData data;

    TextField tfName;
    Button btnConfirm, btnCancel;

    public AskName(ModelData data) {
        this.data=data;

        createViews();
        registerHandlers();
        update();
    }
    private void createViews() {
        tfName = new TextField();
        tfName.setPrefWidth(200);
        Label lb = new Label("Player name:");
        lb.setMinWidth(50);
        HBox fields = new HBox(lb,tfName);
        fields.setAlignment(Pos.BASELINE_LEFT);
        fields.setSpacing(10);
        btnConfirm = new Button("Confirm");
        btnConfirm.setPrefWidth(9999);
        btnCancel  = new Button("Cancel");
        btnCancel.setPrefWidth(9999);
        HBox btns = new HBox(btnCancel,btnConfirm);
        btns.setSpacing(20);
        VBox root = new VBox(fields,btns);
        root.setSpacing(10);
        root.setPadding(new Insets(16));
        Scene scene = new Scene(root,250,100);
        this.setScene(scene);
        this.setResizable(false);
    }

    private void registerHandlers() {
        btnCancel.setOnAction(actionEvent -> this.close());
        btnConfirm.setOnAction(actionEvent -> {
            System.out.println(tfName.getText());
            this.close();
        });
    }

    private void update() {
    }
}
