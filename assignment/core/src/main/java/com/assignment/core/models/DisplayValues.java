package com.assignment.core.models;

/**
 * The {@code DisplayValues} class represents the values authored in the dialog.
 * This class is used to encapsulate and manage the different display values.
 */
public class DisplayValues {

    private String title;
    private String image;
    private String path;
    private String text;

    /**
     * Constructs a new {@code DisplayValues} instance with the specified values.
     *
     * @param title the title value
     * @param image the image path
     * @param path  the path selected in the pathbrowser
     * @param text  the text field value
     */
    public DisplayValues(String title, String image, String path, String text) {
        this.title = title;
        this.image = image;
        this.path = path;
        this.text = text;
    }

    /**
     * Returns the title value.
     *
     * @return a {@code String} representing the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the image path.
     *
     * @return a {@code String} representing the image path.
     */
    public String getImage() {
        return image;
    }

    /**
     * Returns the path selected in the pathbrowser.
     *
     * @return a {@code String} representing the path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the text field value.
     *
     * @return a {@code String} representing the text field value.
     */
    public String getText() {
        return text;
    }
}
