package com.nanddgroup.gks;

/**
 * Created by Dimuch on 18.10.2016.
 */
public abstract class Element {

    private String symbolKey;
    private String numKey;
    private String name;

    public Element() {
    }

    public Element(String name) {
        setName(name);
    }

    public String getName() {
        name = getSymbolKey() + getNumKey();
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setSymbolKey(String.valueOf(name.charAt(0)));
        setNumKey(String.valueOf(name.charAt(1)));
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

    @Override
    public String toString() {
        return "" + name + "";
    }
}
