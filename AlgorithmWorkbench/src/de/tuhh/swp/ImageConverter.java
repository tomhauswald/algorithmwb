/**
 * <=========================================================================================>
 * File: IPrintable.java
 * Created: 16.12.2015
 * Author: HAUSWALD, Tom.
 * <=========================================================================================>
 */
package de.tuhh.swp;

import org.garret.perst.IterableIterator;

import java.util.ArrayList;

/**
 * TODO: Document this type.
 */
public class ImageConverter extends AbstractConverter<ImageValue[]> {
    // ===========================================================
    // Constants
    // ===========================================================

    private static final int MAGIC_NUMBER = 2051;

    // ===========================================================
    // Fields
    // ===========================================================

    private byte[] labels;
    private Workbench workbench;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ImageConverter(Workbench workbench, byte[] labels) {
        this.labels = labels;
        this.workbench = workbench;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    ;;

    // ===========================================================
    // Override Methods
    // ===========================================================

    @Override
    public ImageValue[] toInternal(byte[] external) {

        int magicNumber = AbstractConverter.bytesToInt(external, 0);
        if (magicNumber != MAGIC_NUMBER) {
            System.err.println("Expected magic number " + MAGIC_NUMBER + ", but got " + magicNumber + ".");
            System.exit(-1);
        }

        int numImages = AbstractConverter.bytesToInt(external, 4);
        ImageValue[] images = new ImageValue[numImages];

        int width = AbstractConverter.bytesToInt(external, 12);
        int height = AbstractConverter.bytesToInt(external, 8);
        ImageValue image;

        int x, y; // Coords relative to current image.
        int offset = 16; // Offset in byte array.

        for (int i = 0; i < numImages; ++i) {
            ImageDefinition definition = new ImageDefinition(width, height);
            image = images[i] = new ImageValue(definition, labels[i]);
            for (y = 0; y < height; ++y) {
                for (x = 0; x < width; ++x) {
                    image.setPixel(x, y, external[offset++]);
                }
            }
            workbench.getDatabase().addRecord(image);
        }

        long count = 0;
        for(int label = 0; label <= 9; ++label){
            count = getLabelledImages(label).size();
            System.out.println("Retrieved " + count + " images labelled '" + label + " from database.");
        }

        return images;
    }

    @Override
    public byte[] toExternal(ImageValue[] internal) {
        return null;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private ArrayList<ImageValue> getLabelledImages(int label){
        return workbench.getDatabase().<ImageValue>select(ImageValue.class, "label = " + label).toList();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    ;;

}
