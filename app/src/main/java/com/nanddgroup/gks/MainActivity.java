package com.nanddgroup.gks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //строка всех данных
    private static String allResult = "";
    //строка всех данных
    private static String allData;
    //массив строк с "деталями"
    private static String[] data;
    //лист листов с деталями
    private static List<List<Detail>> alDetails;
    //лист разновидностей деталей
    private static List<String> listTotalDetails;
    private static int[][] matrixExistence;
    private static int[][] matrixMatch;

    AdapterHelper ah;
    SimpleExpandableListAdapter adapter;

    ExpandableListView elvVariant;
    EditText etData;
    TextView tvResult;
    Button bClear;
    Button bBuild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bClear = (Button) findViewById(R.id.bClear);
        bClear.setOnClickListener(this);
        bBuild = (Button) findViewById(R.id.bBuild);
        bBuild.setOnClickListener(this);

        etData = (EditText) findViewById(R.id.etData);
        tvResult = (TextView) findViewById(R.id.textView2);

        ah = new AdapterHelper(this);
        adapter = ah.getAdapter();

        elvVariant = (ExpandableListView) findViewById(R.id.elvVariant);
        elvVariant.setAdapter(adapter);

        // нажатие на элемент
        elvVariant.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                elvVariant.collapseGroup(groupPosition);
                switch (childPosition) {
                    case 0:
                        etData.setText(Utils.loadDataFromFile(getApplicationContext(),
                                "dataFile_Dmitry.txt"));
                        break;
                    case 1:
                        etData.setText(Utils.loadDataFromFile(getApplicationContext(),
                                "dataFile_Catherine.txt"));
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        tvResult.setText("");
        allData = "";
        allResult = "";

        switch (view.getId()) {
            case R.id.bBuild:
                allData = etData.getText().toString();
                allData.trim();
                data = allData.split("\n");

                //Analysis - класс для работы с data[]
                Analysis analysis = new Analysis(data);
                if (analysis.checkInput()) {
                    alDetails = analysis.feelData();
                    listTotalDetails = analysis.checkDetails();

                    //Формирование 1 матрицы (наличия)
                    constructionMatrixExistence();

                    //Формирование 2 матрицы (совпадения)
                    constructionMatrixMatch();

                    //объединения в группы
                    grouping();
                }
                tvResult.setText(allResult);
                break;
            case R.id.bClear:
                etData.setText("");
                tvResult.setText("");
                allData = "";
                allResult = "";
                break;
        }
    }

    private static void constructionMatrixExistence() {
        //выделение памяти под 1 матрицу (наличия)
        matrixExistence = new int[data.length][];
        for (int i = 0; i < data.length; i++)
            matrixExistence[i] = new int[listTotalDetails.size()];

        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < listTotalDetails.size(); j++) {
                String[] parts = data[i].split(" ");
                for (String str : parts)
                    if (listTotalDetails.get(j).charAt(0) == str.charAt(0) &&
                            listTotalDetails.get(j).charAt(1) == str.charAt(1)) {
                        matrixExistence[i][j] = 1;
                        break;
                    } else
                        matrixExistence[i][j] = 0;
            }
        showMatrixExistence();
    }

    private static void showMatrixExistence() {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < listTotalDetails.size(); j++) {
//                System.out.print(matrixExistence[i][j]);
//                allResult.concat(String.valueOf(matrixExistence[i][j])).concat(" ");
                allResult += String.valueOf(matrixExistence[i][j]) + " ";
            }
//            System.out.println();
//            allResult.concat("\n");
            allResult += "\n";
        }
    }

    private static void constructionMatrixMatch() {

        matrixMatch = new int[data.length][];
        for (int i = 0; i < data.length; i++)
            matrixMatch[i] = new int[data.length];

        int count = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                for (int k = 0; k < listTotalDetails.size(); k++) {
                    if (matrixExistence[i][k] == matrixExistence[j][k])
                        count++;
                }
                if (i != j) matrixMatch[i][j] = count;
                count = 0;
            }
        }
        showMatrixMatch();
    }

    private static void showMatrixMatch() {
        for (int i = 0; i < data.length; i++) {
//            System.out.println();
//            allResult.concat("\n");
            allResult += "\n";
            for (int j = 0; j < data.length; j++) {
//                System.out.print(matrixMatch[i][j]);
//                allResult.concat(String.valueOf(matrixMatch[i][j])).concat(" ");
                allResult += String.valueOf(matrixMatch[i][j]) + " ";
            }
        }
    }

    private static void grouping() {
        int countGroup = 0;
        int countRow = 0;
        int[] arrayDetail = new int[data.length];
        for (int i = 0; i < data.length; i++)
            arrayDetail[i] = i + 1;

//        System.out.print();
//        allResult.concat("\n");
        allResult += "\n";

        while (countGroup < data.length && countRow < data.length) {
            int maxI = 0;
            int maxJ = 0;

            for (int i = 0; i < data.length; i++)
                for (int j = 0; j < data.length; j++) {
                    if (matrixMatch[i][j] > matrixMatch[maxI][maxJ]) {
                        maxI = i;
                        maxJ = j;
                    }
                }

            arrayDetail[maxI] = 0;
            arrayDetail[maxJ] = 0;
            countGroup++;
            countRow += 2;
//            System.out.print("\nGroup " + countGroup + ": " + (maxI + 1) + ", " + (maxJ + 1));
//            allResult.concat("\nGroup ").concat(String.valueOf(countGroup)).
//                    concat(": ").concat(String.valueOf(maxI + 1)).
//                    concat(", ").concat(String.valueOf(maxJ + 1));
            if (matrixMatch[maxI][maxJ] != 0) {
                allResult += "\nGroup " + String.valueOf(countGroup) +
                        ":  " + String.valueOf(maxI + 1) +
                        ", " + String.valueOf(maxJ + 1);

                for (int i = 0; i < data.length; i++) {
                    if (matrixMatch[i][maxJ] == matrixMatch[maxI][maxJ] && i != maxI) {
//                        System.out.print(", " + (i + 1));
                        allResult += ", " + (i + 1);
                        countRow++;
                        arrayDetail[i] = 0;
                        for (int k = 0; k < data.length; k++) {
                            matrixMatch[i][k] = 0;
                            matrixMatch[k][i] = 0;
                        }
                    }
                }

                for (int j = 0; j < data.length; j++) {
                    if (matrixMatch[maxI][j] == matrixMatch[maxI][maxJ] && j != maxJ) {
//                        System.out.print(", " + (j + 1));
                        allResult += ", " + (j + 1);
                        countRow++;
                        arrayDetail[j] = 0;
                        for (int k = 0; k < data.length; k++) {
                            matrixMatch[k][j] = 0;
                            matrixMatch[j][k] = 0;
                        }
                    }
                }

                for (int i = 0; i < data.length; i++) {
                    matrixMatch[i][maxJ] = 0;
                    matrixMatch[maxJ][i] = 0;
                }

                for (int j = 0; j < data.length; j++) {
                    matrixMatch[maxI][j] = 0;
                    matrixMatch[j][maxI] = 0;
                }
            } else {
//                System.out.print("\nGroup " + countGroup + ": ");
                allResult += "\nGroup " + countGroup + ":   ";
                for (int i = 0; i < data.length; i++)
                    if (arrayDetail[i] != 0) {
//                        System.out.print((i + 1) + ". ");
                        allResult += (i + 1) + ". ";
                        arrayDetail[i] = 0;
                    }
            }
        }
        allResult += "\n\n";
    }

}
