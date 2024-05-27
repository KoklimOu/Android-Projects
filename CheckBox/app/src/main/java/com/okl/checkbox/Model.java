package com.okl.checkbox;

class Model {
    private boolean isSelected;
    private String player;
    String getPlayer() {
        return player;
    }
    void setPlayer(String player) {
        this.player = player;
    }
    boolean getSelected() {
        return isSelected;
    }
    void setSelected(boolean selected) {
        isSelected = selected;
    }
}