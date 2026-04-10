package mosaicproject;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class MosaicController {
    private int minRes;
    private MasterImgHandling master;
    private File masterFile;
    private TileImgHandling tiles;
    private List<String> acceptedFormats = List.of("jpg", "jpeg", "png", "bmp", "gif");
    private List<File> tileList;
    private List<File> mosaicList;

    @FXML private VBox step1, step2, step3;
    @FXML private TextField minResField;
    @FXML private GridPane mosaicGrid;
    @FXML private Label masterFileNameLabel;
    @FXML private Label tileFilesStatusLabel;
    @FXML private javafx.scene.control.Button saveProjectBtn;

    private void showStep(int step) {
        step1.setVisible(step == 1);
        step2.setVisible(step == 2);
        step3.setVisible(step == 3);
        if (saveProjectBtn != null) { // visible if at step 3
        saveProjectBtn.setVisible(step == 3); 
    }
    }

    @FXML
    public void initialize() {
        // vibe coded event listeners logic
        mosaicGrid.setOnScroll(event -> {
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            double currentScale = mosaicGrid.getScaleX();
            double newScale;

            if (deltaY > 0) {
                newScale = currentScale * zoomFactor; // zoom in
            } else {
                newScale = currentScale / zoomFactor; // zoom out
            }

            if (newScale < 0.1) newScale = 0.1;
            if (newScale > 10.0) newScale = 10.0;

            mosaicGrid.setScaleX(newScale);
            mosaicGrid.setScaleY(newScale);

            event.consume();
        });
    }

    @FXML
    private void handleMasterUpload() {
        System.out.println("Master upload successfully called.");
        try {
            FileChooser chooser = new FileChooser();
            File file= chooser.showOpenDialog(null);
            if (checkFile(file)) {
                    this.masterFile = file;
                    masterFileNameLabel.setText("Uploaded: " + masterFile.getName());
                    masterFileNameLabel.setTextFill(javafx.scene.paint.Color.GREEN);
                }
                else {
                    masterFileNameLabel.setText("Could not upload file.");
                    masterFileNameLabel.setTextFill(javafx.scene.paint.Color.RED);
                }
            }
        catch (Exception e) {
            System.out.println("Upload a valid file and write only integer as min.resolution. Error: " + e.getMessage());
        }
    }

    @FXML 
    private void goToStep2() {
        try {
            this.minRes = Integer.parseInt(minResField.getText());
            this.master = new MasterImgHandling(masterFile, this.minRes);
            System.out.println("We are now calling master img handling.");
            showStep(2);
        }
        catch (NumberFormatException e) {
            minResField.setStyle("-fx-text-fill: red;");
        }
        catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    @FXML
    private void handleTilesUpload() {
        try {
            FileChooser chooser = new FileChooser();
            List<File> list = chooser.showOpenMultipleDialog(null);
            if (checkFiles(list)) {
                this.tileList = list;
                tileFilesStatusLabel.setText("Uploaded " + tileList.size() + " files");
                tileFilesStatusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            }
            else {
                tileFilesStatusLabel.setText("Could not upload files.");
                tileFilesStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
            }
        }
        catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    public void goToStep3() {
        try {
            this.tiles = new TileImgHandling(this.tileList, this.minRes);
            Matching matching = new Matching(master.getCellColorList(), tiles.getTileColorMap(), master.getnCellsWide());
            this.mosaicList = matching.match();
            mosaicGrid.getChildren().clear();
            int cols = master.getnCellsWide();
            // init the cache
            java.util.Map<String, Image> imageCache = new java.util.HashMap<>();
            for (int i = 0; i < this.mosaicList.size(); i++) {
                File imgFile = this.mosaicList.get(i);
                String imagePath = imgFile.getAbsolutePath();
                if (!imageCache.containsKey(imagePath)) { // check cache (obv)
                    Image compressedImg = new Image(imgFile.toURI().toString(), 20, 20, true, true); // force compression for faster render
                    imageCache.put(imagePath, compressedImg); 
                }
                ImageView iv = new ImageView(imageCache.get(imagePath)); 
                iv.setFitWidth(50);
                iv.setFitHeight(50);
                mosaicGrid.add(iv, i % cols, i / cols); // normal placement from 1D to 2D. 
            }
            showStep(3);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML private void saveProject() {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        chooser.getExtensionFilters().add(extFilter);
        File file = chooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                int nCellsWide = this.master.getnCellsWide(); 
                writer.println(nCellsWide); // first element is the number of columns
                for (File imgFile : this.mosaicList) {
                    writer.println(imgFile.getAbsolutePath());
                }
                System.out.println("Project saved successfully!");

            } catch (Exception e) {
                System.out.println("Critical error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void openProject() {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        chooser.getExtensionFilters().add(extFilter);
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            try {
                List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
                if (lines.isEmpty()) return; 
                int cols = Integer.parseInt(lines.get(0));
                mosaicGrid.getChildren().clear();
                java.util.Map<String, Image> imageCache = new java.util.HashMap<>();
                for (int i = 1; i < lines.size(); i++) {
                    String imagePath = lines.get(i);
                    if (!imageCache.containsKey(imagePath)) { // same logivc from here on as with step3. 
                        File imgFile = new File(imagePath);
                        Image compressedImg = new Image(imgFile.toURI().toString(), 20, 20, true, true);
                        imageCache.put(imagePath, compressedImg);
                    }
                    ImageView iv = new ImageView(imageCache.get(imagePath));
                    iv.setFitWidth(50);
                    iv.setFitHeight(50);
                    int gridIndex = i - 1; 
                    mosaicGrid.add(iv, gridIndex % cols, gridIndex / cols);
                }
                showStep(3);

            } catch (Exception e) {
                System.out.println("Kunne ikke laste prosjektet. Er filen korrupt? Error: " + e.getMessage());
            }
        }
    }

    public boolean checkFile(File file) {
        if (file != null) {
            String[] parts = file.getName().toLowerCase().split("\\.");
            String postfix = parts[parts.length - 1];
            return this.acceptedFormats.contains(postfix);
        }
        return false;
    }

    public boolean checkFiles(List<File> files) {
        if (files != null) {
            for (File file : files) {
                if (!checkFile(file)) return false;
            }
            return true;
        }
        return false;
    }
}

