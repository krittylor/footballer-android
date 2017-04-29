package com.example.admin.footballer.SwipeSelector;

public class SwipeItem {
    static final String UNSELECTED_ITEM_VALUE = "com.roughike.swipeselector.UNSELECTED_ITEM_VALUE";
    private String value;
    private String title;
    private String description;

    SwipeItem() {
    }

    public SwipeItem(String value, String title, String description) {
        this.value = value;
        this.title = title;
        this.description = description;
    }

    /**
     * Set the value for this SwipeItem.
     *
     * @param value The unique value for this item, which is used for identifying which
     *           item the user has selected in this SwipeSelector.
     */
    void setValue(String value) {
        this.value = value;
    }

    /**
     * Set the title for this SwipeItem.
     *
     * @param title A short descriptive title for this item, such as "Pizza".
     */
    void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set the description for this SwipeItem.
     *
     * @param description Longer explanation related to the title, such as
     *                    "Pizzas are healthy, because pizza sauces contain tomato. And tomatoes
     *                    are healthy, just ask anyone."
     */
    void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the value for this SwipeItem.
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the title from a string resource (if available), or straight
     * from the "title" field.
     *
     * @return the title for this SwipeItem.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the description from a string resource (if available), or straight
     * from the "description" field.
     *
     * @return the description for this SwipeItem.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Determines if this item is a unselected state item, in other words, not a real possible
     * selection for this SwipeSelector.
     *
     * Unselected items are specifed by the "swipe_unselectedItemTitle" and "swipe_unselectedItemDescription"
     * attributes and created automagically for you if those attributes exist.
     *
     * @return true if this item is a real selected item by the user, false otherwise.
     */
    boolean isRealItem() {
        return !UNSELECTED_ITEM_VALUE.equals(value);
    }
}
