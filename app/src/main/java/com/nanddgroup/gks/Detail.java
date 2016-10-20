package com.nanddgroup.gks;

/**
 * Created by Dimuch on 18.10.2016.
 */
public abstract class Detail {

    private String symbolKey;
    private String numKey;
    private String name;

    public Detail() {
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        name = getSymbolKey() + getNumKey();
        return name;
    }

    public String getSymbolKey() {
        return symbolKey;
    }

    public void setSymbolKey(String symbolKey) {
        this.symbolKey = symbolKey;
    }

    public String getNumKey() {
        return numKey;
    }

    public void setNumKey(String numKey) {
        this.numKey = numKey;
    }
}
