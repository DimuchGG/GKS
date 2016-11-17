package com.nanddgroup.gks;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dimuch on 18.10.2016.
 */
public class Analysis {

    private String[] data;
    //всего разновидностей деталей
    private int total;
    //максимальное кол-во деталей в одной строке
    private int max;
    //лист деталей
    private List<Element> alElements;
    //лист листов с деталями
    private List<List<Element>> alDetails;
    private List<Element> listTotalElements;

    public Analysis(String[] data) {
        this.data = data;
    }

    //проверяет корректность ввода String, заполняет все поля
    public boolean checkInput() {

        if (true) {
            max = checkMaxElementsInDetails();
            total = checkTotalElements();
            feelData();

//            System.out.println("max = " + max + "   total = " + total);
//            Log.d("my", "max = " + max + "   total = " + total);
            return true;
        } else
            return false;
    }

    //возвращает кол-во разновидностей деталей
    public int checkTotalElements() {
        listTotalElements = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            String[] parts = data[i].split(" ");
            for (int j = 0; j < parts.length; j++) {
                if (listTotalElements.size() == 0)
                    listTotalElements.add(new SpecificElement(parts[0]));
                for (int k = 0; k < listTotalElements.size(); k++) {
//                    System.out.println(parts[j] + " * " + listTotalElements.get(k) + listTotalElements.toString());
//                    if (parts[j].equals(listTotalElements.get(k).getName()))
                    if (parts[j].charAt(0) == listTotalElements.get(k).getName().charAt(0) &&
                            parts[j].charAt(1) == listTotalElements.get(k).getName().charAt(1))
                        break;
                    else if (k == listTotalElements.size() - 1) {
                        listTotalElements.add(new SpecificElement(parts[j]));
                        break;
                    }
                }
            }
        }
//        System.out.println(listTotalElements.toString());
        return listTotalElements.size();
    }

    //возвращает максимальное кол-во элементов в деталях
    private int checkMaxElementsInDetails() {
        int maxDetails = 0;

        for (int i = 0; i < data.length; i++) {
            String[] parts = data[i].split(" ");
            if (maxDetails < parts.length)
                maxDetails = parts.length;
        }
        return maxDetails;
    }

    //заполняет листы деталей и элементов
    public void feelData() {
        alDetails = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            alElements = new ArrayList<>();
            String[] parts = data[i].split(" ");
            for (int j = 0; j < parts.length; j++) {
                Element element = new SpecificElement(parts[j]);
//                System.out.println(parts[j].charAt(0) + "-" + parts[j].charAt(1));
//                element.setSymbolKey(String.valueOf(parts[j].charAt(0)));
//                element.setNumKey(String.valueOf(parts[j].charAt(1)));
                alElements.add(element);
            }
            alDetails.add(alElements);
        }
//        for (int i = 0; i < data.length; i++)
//            for (int j = 0; j < max; j++) {
//                System.out.println(alDetails.get(i).get(j).getName());
//            }
    }

    public List<Element> getAlElements() {
        return alElements;
    }

    public List<List<Element>> getAlDetails() {
        return alDetails;
    }

    public List<Element> getTotalElements() {
        return listTotalElements;
    }
}
