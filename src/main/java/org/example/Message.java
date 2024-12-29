package org.example;

/**
 * The type Message.
 */
public class Message {


    /**
     * The Alert.
     */
    boolean alert;
    /**
     * The Description.
     */
    String description;
    /**
     * The Variation.
     */
    String variation;

    /**
     * Instantiates a new Message.
     *
     * @param alert       the alert
     * @param variation   the variation
     * @param description the description
     */
    public Message(boolean alert, String variation,String description) {
        this.variation = variation;
        this.alert = alert;
        this.description = description;
    }

    /**
     * Gets variation.
     *
     * @return the variation
     */
    public String getVariation() {
        return variation;
    }

    /**
     * Sets variation.
     *
     * @param variation the variation
     */
    public void setVariation(String variation) {
        this.variation = variation;
    }

    /**
     * Is alert boolean.
     *
     * @return the boolean
     */
    public boolean isAlert() {
        return alert;
    }

    /**
     * Sets alert.
     *
     * @param alert the alert
     */
    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }


}
