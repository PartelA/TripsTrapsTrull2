package org.example.tripstrapstrull2;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

public class Rakendus extends Application {
    //globaalsed muutujad/javafx objektid, mida kasutavad mitmed meetodid
    private char viimaneKaik = 'X';
    GridPane mangulaud = new GridPane();
    TextArea sissetulevTekst = new TextArea("Tere tulemast mängima Trips-Traps-Trulli!\n Vali kas soovid (L)aadida failist pooleli olevat faili või (U)ut mängu alustada");
    Label kelleKord = new Label("Mängija X kord");
    TextField sisestus = new TextField();

    @Override
    public void start(Stage lava) {
        VBox juur = new VBox();
        // See rida ütleb, et mängulaud peab täitma kogu vaba ruumi
        VBox.setVgrow(mangulaud, Priority.ALWAYS);

        for (int i = 0; i < 3; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 3);
            mangulaud.getColumnConstraints().add(cc);

            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / 3);
            mangulaud.getRowConstraints().add(rc);
        }

        for (int rida = 0; rida < 3; rida++) {
            for (int veerg = 0; veerg < 3; veerg++) {
                mangulaud.add(looRuut(), veerg, rida);
            }
        }

        // 2. ALUMINE OSA: Tekst ja nupud
        VBox aluminePaneel = new VBox(10);
        aluminePaneel.setStyle("-fx-padding: 15; -fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-width: 1 0 0 0;");

        // FIKSEERIME alumise osa kõrguse
        aluminePaneel.setMinHeight(130);
        aluminePaneel.setMaxHeight(130);
        VBox.setVgrow(aluminePaneel, Priority.NEVER); // Keelame sellel kasvamise


        kelleKord.setStyle("-fx-font-weight: bold;");
        sissetulevTekst.setEditable(false);
        sissetulevTekst.setPrefHeight(50);
        sissetulevTekst.setMinHeight(50);
        sisestus.setPromptText("Kirjuta siia...");
        Button nupp = new Button("Saada");

        HBox sisestusRida = new HBox(5, sisestus, nupp);
        HBox.setHgrow(sisestus, Priority.ALWAYS);

        aluminePaneel.getChildren().addAll(kelleKord, sissetulevTekst, sisestusRida);

        juur.getChildren().addAll(mangulaud, aluminePaneel);

        Scene scene = new Scene(juur, 450, 550);
        lava.setMinWidth(300);
        lava.setMinHeight(450);

        lava.setTitle("Tripstrapstrull");
        lava.setScene(scene);
        lava.show();

        //Loome sündmuste kuulajad, mis kasutaja tegevustele reageerivad. Mängulauale käigu lisamine on implementeeritud mängulaua ruudu loomise sees

        //KASUTAJA SISENDILE REAGEERIMINE
        nupp.setOnMouseClicked(e -> {
            try {
                String kasutajaSisend = valideeriSisend(sisestus.getText());
                sisestus.setText("");

                if (kasutajaSisend.equals("U")) {
                    sissetulevTekst.setText("Mäng algas! Võid mängida või valida järgmise tegevuse.\nVali kas soovid (L)aadida failist pooleli olevat mängu, praegust mängu (S)alvestada või (U)ut mängu alustada\n");
                    tühjendaLaud();
                    viimaneKaik = 'X';
                    kelleKord.setText("Mängija X kord");

                } else if (kasutajaSisend.equals("L")) {
                    //failist lugemine
                    try {
                        FailiHaldur.MänguSeis seis = FailiHaldur.loe();
                        rakendaLaudGUI(seis.laud);
                        viimaneKaik = seis.viimaneKaik;
                        char järgmine = (viimaneKaik == 'X') ? 'O' : 'X';
                        kelleKord.setText("Mängija " + järgmine + " kord");
                        sissetulevTekst.setText("Mäng laaditi failist edukalt!\nVali kas soovid praegust mängu (S)alvestada või (U)ut mängu alustada\n");
                    } catch (FailiPoleErind fpe) {
                        sissetulevTekst.setText("Viga: " + fpe.getMessage() + "\n");
                    } catch (IOException ioe) {
                        sissetulevTekst.setText("Faili lugemine ebaõnnestus: " + ioe.getMessage() + "\n");
                    }

                } else if (kasutajaSisend.equals("S")) {
                    //faili salvestamine
                    try {
                        String[][] laud = loeLaudGUIst();
                        FailiHaldur.salvesta(laud, viimaneKaik);
                        sissetulevTekst.setText("Mäng salvestati edukalt faili!\nVali kas soovid (L)aadida failist pooleli olevat mängu või (U)ut mängu alustada\n");
                    } catch (IOException ioe) {
                        sissetulevTekst.setText("Faili salvestamine ebaõnnestus: " + ioe.getMessage() + "\n");
                    }
                }
            } catch (TundmatuSisendErind tse) {
                sissetulevTekst.setText("Viga: " + tse.getMessage() + "\n");
                sisestus.setText("");
            }
        });

    }
    //Ühe mänguvälja ruudu loomine
    private StackPane looRuut() {
        StackPane ruut = new StackPane();
        ruut.setStyle("-fx-border-color: #333; -fx-background-color: white;");

        Label märk = new Label("");
        // Tekst skaleerub vastavalt ruudu suurusele (1/3 ruudu kõrgusest)
        märk.styleProperty().bind(
                javafx.beans.binding.Bindings.concat("-fx-font-size: ", ruut.heightProperty().divide(2.5).asString(), ";")
        );
        ruut.getChildren().add(märk);
        //Muudetakse mängulauda
        ruut.setOnMouseClicked(e -> {
            if (märk.getText().isEmpty()) {
                if (viimaneKaik == 'X') {
                    viimaneKaik = 'O';
                    märk.setText("O");
                    kelleKord.setText("Mängija X kord");
                } else {
                    viimaneKaik = 'X';
                    märk.setText("X");
                    kelleKord.setText("Mängija O kord");
                }
            }
            //Kui mäng lõppeb
            if (kasMangLoppes(mangulaud)) {
                Alert teavitus = new Alert(Alert.AlertType.INFORMATION);
                teavitus.setHeaderText("Mäng on läbi!");
                teavitus.setContentText("Vali järgmine tegevus");
                teavitus.showAndWait();
                sissetulevTekst.setText("Vali kas soovid (L)aadida failist pooleli olevat mängu, praegust mängu (S)alvestada või (U)ut mängu alustada\n");
            }
        });

        return ruut;
    }

    //kasutajalt sisend
    private String valideeriSisend(String a) throws TundmatuSisendErind{
        a = a.strip().toUpperCase();
        if (!(a.equals("S") || a.equals("L") || a.equals("U"))) {
            throw new TundmatuSisendErind("Kasutaja sisestatud '" + a + "' ei vasta ühelegi valikule ('S', 'L', 'U')");
        }
        return a;
    }

    //tühjendab mängulaua GUI ruudud
    private void tühjendaLaud() {
        for (Node ruut : mangulaud.getChildren()) {
            StackPane ruuduSees = (StackPane) ruut;
            Label mark = (Label) ruuduSees.getChildren().get(0);
            mark.setText("");
        }
    }

    //loeb GUI mängulaualt seisu 2D massiivi
    private String[][] loeLaudGUIst() {
        String[][] laud = new String[3][3];
        for (Node ruut : mangulaud.getChildren()) {
            StackPane ruuduSees = (StackPane) ruut;
            Label mark = (Label) ruuduSees.getChildren().get(0);
            int veerg = GridPane.getColumnIndex(ruut);
            int rida = GridPane.getRowIndex(ruut);
            laud[veerg][rida] = mark.getText();
        }
        return laud;
    }

    //rakendab 2D massiivist seisu GUI mängulaualt
    private void rakendaLaudGUI(String[][] laud) {
        for (Node ruut : mangulaud.getChildren()) {
            StackPane ruuduSees = (StackPane) ruut;
            Label mark = (Label) ruuduSees.getChildren().get(0);
            int veerg = GridPane.getColumnIndex(ruut);
            int rida = GridPane.getRowIndex(ruut);
            mark.setText(laud[veerg][rida]);
        }
    }

    //Kontrollime mängulaua praegust seisu, rakendatakse pärast iga vajutust
    private boolean kasMangLoppes(GridPane mangulaud) {
        String[][] laud = new String[3][];
        int kokku = 0;
        for (int i = 0; i < 3; i++) {
            laud[i] = new String[3];
        }
        for (Node ruut : mangulaud.getChildren()) {
            StackPane ruuduSees = (StackPane) ruut;
            Label mark = (Label) ruuduSees.getChildren().get(0);
            laud[GridPane.getColumnIndex(ruut)][GridPane.getRowIndex(ruut)] = mark.getText();
            if (!mark.getText().equals("")) kokku++;
        }
        //kontrollime, kas on rida kokku saadud
        //read
        if (laud[0][0].equals(laud[0][1]) && laud[0][0].equals(laud[0][2]) && !laud[0][0].equals("")) return true;
        if (laud[1][0].equals(laud[1][1]) && laud[1][0].equals(laud[1][2]) && !laud[1][0].equals("")) return true;
        if (laud[2][0].equals(laud[2][1]) && laud[2][0].equals(laud[0][2]) && !laud[2][0].equals("")) return true;
        //veerud
        if (laud[0][0].equals(laud[1][0]) && laud[0][0].equals(laud[2][0]) && !laud[0][0].equals("")) return true;
        if (laud[0][1].equals(laud[1][1]) && laud[0][1].equals(laud[2][1]) && !laud[0][1].equals("")) return true;
        if (laud[0][2].equals(laud[1][2]) && laud[0][2].equals(laud[2][2]) && !laud[0][2].equals("")) return true;
        //diagonaalid
        if (laud[0][0].equals(laud[1][1]) && laud[0][0].equals(laud[2][2]) && !laud[0][0].equals("")) return true;
        if (laud[0][2].equals(laud[1][1]) && laud[0][2].equals(laud[2][0]) && !laud[0][2].equals("")) return true;
        //viik
        if (kokku == 9) return true;

        return false;
    }

}
