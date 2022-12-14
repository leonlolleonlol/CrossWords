package ca.qc.bdeb.inf203.tp1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


public class MainJavaFX extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    //tout les attributs private

    private static final BorderPane borderPane = new BorderPane();
    private static ArrayList<Mot> motsAdeviner = new ArrayList<>();
    private static ArrayList<TextField> textFieldArrayList = new ArrayList<>();
    private static File actualFile = new File("mots-croises1.txt");
    private static MotsCroises partie = new MotsCroises(actualFile);
    private static final Text finiMessage = new Text("Bravo! Mots-croisés complété!");

    @Override
    public void start(Stage primaryStage) {
        // bold trouve grace a https://docs.oracle.com/javafx/2/text/jfxpub-text.htm
        finiMessage.setFont(Font.font("monospace", FontWeight.BOLD, 20));
        finiMessage.setFill(Color.GREEN);
        finiMessage.setVisible(false);
        var fileChooser = new FileChooser();
        var contenuEnHaut = new VBox();//titre en haut et section option et message fini si on a fini
        var option = new HBox();//indication de changer de grille et deux boutons
        var titreEnHaut = new HBox(); //titre et photo
        var deuxBoutons = new VBox();//boutons ChoiceBox et de Button pour fichier personnalide
        var scene = new Scene(borderPane, 900, 800);
        primaryStage.setTitle("Mots-Croisés");
        var title = new Text("Super Mots-Croisés Master 3000");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        var image = new Image("mots.png");
        var imageView = new ImageView(image);
        imageView.setFitWidth(25);
        imageView.setFitHeight(25);
        //Setting image to the image view
        titreEnHaut.getChildren().addAll(imageView, title);
        titreEnHaut.setAlignment(Pos.CENTER);
        var indication = new Text("Changer de grille");
        ChoiceBox<String> choiceBox = new ChoiceBox<>();//on met les fichiers quon possede dans ce projet
        choiceBox.getItems().add("mots-croises1.txt");
        choiceBox.getItems().add("mots-croises2.txt");
        choiceBox.getItems().add("mots-croises3.txt");
        choiceBox.getItems().add("invalide1.txt");
        choiceBox.getItems().add("invalide2.txt");
        choiceBox.getItems().add("invalide3.txt");
        var button = new Button("Ouvrir un autre mots-croisés");
        deuxBoutons.getChildren().addAll(choiceBox, button);
        deuxBoutons.setTranslateY(15);
        option.getChildren().addAll(indication, deuxBoutons);
        option.setSpacing(10);
        option.setAlignment(Pos.CENTER);
        contenuEnHaut.setSpacing(20);
        contenuEnHaut.getChildren().addAll(titreEnHaut, option, finiMessage);
        contenuEnHaut.setAlignment(Pos.CENTER);
        borderPane.setTop(contenuEnHaut);
        totalReset();//on reset pour initialiser avec le premier fichier valide
        //icone ajoute grace a https://stackoverflow.com/questions/10121991/javafx-application-icon
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
        // file retire grace a
        // https://www.geeksforgeeks.org/javafx-filechooser-class/#:~:text=FileChooser%20class%20is%20a%20part,FileChooser%20class%20inherits%20Object%20class.
        button.setOnAction(e -> {
            try {
                actualFile = fileChooser.showOpenDialog(primaryStage);
                //si on peut lire le file
                if (actualFile.exists() && actualFile.canRead())
                    totalReset();
                else
                    messageInvalide();
            } catch (NullPointerException nullPointerException) {
                messageInvalide();
            }
        });
        //choicebox grace a https://jenkov.com/tutorials/javafx/choicebox.html
        choiceBox.setOnAction((event) -> {
            String selectedItem = choiceBox.getSelectionModel().getSelectedItem();
            actualFile = new File(selectedItem);
            totalReset();
        });
        //on quitte
        scene.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });
        //pour tout les textfield
        for (int i = 0; i < textFieldArrayList.size(); i++) {
            int finalI = i;
            //si on press un key dans un textfield
            textFieldArrayList.get(i).setOnKeyPressed((event) -> {
                //si c enter
                if (event.getCode() == KeyCode.ENTER) {
                    String solution = partie.getMots().get(finalI).getMot();
                    //si on a la solution
                    if (Objects.equals(textFieldArrayList.get(finalI).getText(), solution)) {
                        motsAdeviner.remove(partie.getMots().get(finalI));
                        //on cache textfield et on reset la grille
                        //set disable grace a https://stackoverflow.com/questions/17871329/disabling-a-button-in-javafx
                        textFieldArrayList.get(finalI).setDisable(true);
                        borderPane.setCenter(resetGrid());
                        if (motsAdeviner.isEmpty())
                            finiMessage.setVisible(true);
                    }
                }
            });
        }
    }

    public static void totalReset() {
        //le message de completion est cache la partie prend en compte le fichier actuel
        //on reremplit les mots a deviner
        // on reset la grille et en-dessous , c-a-d les textfiels et descriptions
        finiMessage.setVisible(false);
        partie = new MotsCroises(actualFile);
        motsAdeviner = new ArrayList<>(partie.getMots());
        borderPane.setCenter(resetGrid());
        borderPane.setBottom(resetBottom());
    }

    public static HBox resetBottom() {
        //on vide les textfields et on les reremplis et on les montres et on les reset a une string vide
        for (int i = textFieldArrayList.size() - 1; i == 0; i--) {
            textFieldArrayList.remove(i);
        }
        for (int i = 0; i < partie.getMots().size(); i++) {
            textFieldArrayList.add(new TextField());
            textFieldArrayList.get(i).setDisable(false);
            textFieldArrayList.get(i).setText("");
        }
        var contenuEnBas = new HBox();
        var numero = new VBox();
        var textFields = new VBox();
        var descriptions = new VBox();
        numero.setSpacing(10);
        descriptions.setSpacing(10);
        // on a le numero le textfield et la description a chaque ligne sous la grille
        for (int i = 0; i < partie.getMots().size(); i++) {
            numero.getChildren().add(new Text(i + 1 + ". "));
            textFields.getChildren().add(textFieldArrayList.get(i));
            descriptions.getChildren().add(new Text(partie.getMots().get(i).getInfo()));
        }
        contenuEnBas.getChildren().addAll(numero, textFields, descriptions);
        return contenuEnBas;
    }

    public static GridPane resetGrid() {
        var grid = new GridPane();
        if (partie.isFichierAcceptable()) {
            //voir methode
            partie.preparerGrille(partie.getGrille().length, partie.getGrille()[0].length, motsAdeviner, false);
            for (int h = 0; h < partie.getGrille().length; h++) {
                for (int i = 0; i < partie.getGrille()[h].length; i++) {
                    var cellule = new HBox();
                    cellule.setPadding(new Insets(3, 8, 3, 8)); // Un peu de marge autour du contenu
                    cellule.setMaxSize(30, 30); // Force la taille de chaque cellule à 30x30 pixels
                    cellule.setMinSize(30, 30);
                    // Donne une bordure noire et une couleur d'arrière-plan à la cellule
                    cellule.setBorder(new Border(new BorderStroke(Color.BLACK,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    if (Objects.equals(partie.getGrille()[h][i], "."))
                        cellule.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
                    if (Objects.equals(partie.getGrille()[h][i], "?"))
                        cellule.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                    // Ajoute une lettre du mot qui va aller dans la case
                    Text lettre = new Text();
                    //si le string de la case de la grille est un char acceptable on laffiche et la case est en vert
                    if (partie.charAcceptable().contains((int) partie.getGrille()[h][i].charAt(0))) {
                        lettre.setText(String.valueOf(partie.getGrille()[h][i].charAt(0)));
                        cellule.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
                    } else
                        //sinon on dit la string est vide
                        lettre = new Text("");
                    lettre.setFont(Font.font("monospace", 20));
                    cellule.getChildren().add(lettre);
                    // Le petit numéro à afficher si cette case est le début d'un mot
                    for (int k = 0; k < partie.getMots().size(); k++) {
                        if (partie.getMots().get(k).getNbColonne() == i && partie.getMots().get(k).getNbLigne() == h) {
                            var numero = new Text(String.valueOf(k + 1));
                            numero.setFont(Font.font(10));
                            cellule.getChildren().add(numero);
                        }
                    }
                    grid.add(cellule, i, h);
                }
            }
        } else {
            //si fichier invalide

            grid.add(messageInvalide(), 0, 0);
        }
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    public static Text messageInvalide() {
        Text lettre = new Text("Fichier Invalide!");
        lettre.setFont(Font.font("monospace", 20));
        lettre.setFill(Color.RED);
        return lettre;
    }
}