# Mosaic project
A two-day surge to complete the mandatory project in TDT4100 with my ambitious idea of making a picture mosaic generator. The idea is to let the user upload a master image, and further a set of at least 100+ unique images to generate a mosaic of the master image with. The idea at the core is similar to how ML systems use(d?) to process images - splitting into matrices and assigning color values. Following the user's bulk upload of images, we walk through each square of the grid, and match the picture based on light/dark and color, and place the best-fitting image there. Things I will have to figure out underway: 

- Should reuse of an image be allowed? If so, how many, how close to each other? (No reuse or limited reuse might add some complexity, need to score entire grid before assigning)
- Should we do some light filtering, color-grading og brightness adjustments? 
- Where do I even start? 

## Structure
Thought it would be a good idea to outline some sort of project structure. 

/main
//java
///mosaic-project
 - MosaciApp.java
 - MosaicController.java
 - Scorable.java. 
    + Interface contracting the abstract class AbstractImg to implement the getColor method - this is the backbone of the comparison. 
- AbstractImg.java
    + Handling all common methods between MasterCell and Tile, and implementing the Scorable interface. 
- MasterCellImg.java
    + Scanning a single cell of the grid of the master image. Implements scorable. 
- MasterImgHandling.java
    + Handling of the uploaded image, not the upload itself. Splits into a nxn-sized grid, and handles the 2D-list of MasterCellImg image scores. These are the values the bulk images will be matched after. 
- TileImg.java
    + Scanning a single image and saving the score, implements scorable.  
- TileImgHandling.java
    + Responsible for the collection of tile images, providing the file paths for the bunch of images uploaded as the tiles. 
- Matching.java
    + Where the magic happens. Comparing both 2D-list we use some established algorithm for optimal matching. 
- ResultMosaic.java
    + The home of the final mosaic. Copy of the tileImg collection mapped to the correct tiles, probably best if it only containes the file-paths, or maybe the scores would be nice to have an optionality to view. 
- FileHandling.java
    + Save the current state of a editing/viewing session, and possibility to open a saved session. Probably save the current state as a text-file. Not allowed to use libraries or such, I have to write this logic myself. 
///resources
- App.fxml
    + Vibecode the frontend itself (not the controller connecting fe and be), as it is not on the syllabus. 
/test
//java
///mosaic-project
- MosaicTest.java





