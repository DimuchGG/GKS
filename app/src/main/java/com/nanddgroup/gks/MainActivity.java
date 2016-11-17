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
    //массив строк "деталей"
    private static String[] data;
    //лист листов с деталями
    private static List<List<Element>> alDetails;
    //лист разновидностей деталей
    private static List<Element> listTotalElements;
    //Матрица (наличия)
    private static int[][] matrixExistence;
    //Матрица (совпадения)
    private static int[][] matrixMatch;
    //лист групп деталей
    private static List<List<List<Element>>> alGroupDetails;
    //лист групп уникальных (неповторяющихся) деталей
    private static List<List<Element>> alGroupUniqueElements;
    //лист упрощенных групп деталей
    private static List<List<List<Element>>> alSimpleGroupDetails;

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
        allResult = "";
        tvResult.setText(allResult);

        switch (view.getId()) {
            case R.id.bBuild:
                if (!etData.getText().toString().isEmpty()) {
                    data = etData.getText().toString().trim().split("\n");

                    //Analysis - класс для работы с data[]
                    Analysis analysis = new Analysis(data);
                    if (analysis.checkInput()) {
                        //Получаем список деталей
                        alDetails = analysis.getAlDetails();
                        //Получаем список всех элементов
                        listTotalElements = analysis.getTotalElements();
                        Log.d("my", listTotalElements.toString());

                        //Формирование 1 матрицы (наличия)
                        constructionMatrixExistence();
                        showMatrixExistence();

                        //Формирование 2 матрицы (совпадения)
                        constructionMatrixMatch();
                        showMatrixMatch();

                        //объединения в группы
                        unionOfDetailsInGroups();
                        //                    showGroupDetails();

                        //объединения в группы
                        createGroupUniqueDetails();
                        //                    showGroupUniqueDetails();

                        //сортировка листов групп по колличеству элементов в листе уникальных элементов
                        sortListDetailsAndListUniqueDetails();
                        showGroupDetails(alGroupDetails);
                        //                    showGroupUniqueDetails();

                        //упрощение групп
                        simplifyGroups();
                        showGroupDetails(alSimpleGroupDetails);
                    }
                    tvResult.setText(allResult);
                }
                break;
            case R.id.bClear:
                etData.setText("");
                tvResult.setText("");
                allResult = "";
                break;
        }
    }

    private static void constructionMatrixExistence() {
        //выделение памяти под 1 матрицу (наличия)
        matrixExistence = new int[data.length][];
        for (int i = 0; i < data.length; i++)
            matrixExistence[i] = new int[listTotalElements.size()];

        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < listTotalElements.size(); j++) {
                String[] parts = data[i].split(" ");
                for (String str : parts) {
//                    Log.d("my", String.valueOf(listTotalElements.get(j).getName() + "   *   " + str + " = " + (listTotalElements.get(j).getName().charAt(0) == str.charAt(0) &&
//                            listTotalElements.get(j).getName().charAt(1) == str.charAt(1))));
                    if (listTotalElements.get(j).getName().charAt(0) == str.charAt(0) &&
                            listTotalElements.get(j).getName().charAt(1) == str.charAt(1)) {
                        matrixExistence[i][j] = 1;
                        break;
                    } else
                        matrixExistence[i][j] = 0;
                }
            }
    }

    private static void showMatrixExistence() {
//        System.out.println();
        allResult += "\n";
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < listTotalElements.size(); j++)
//                System.out.print(matrixExistence[i][j]);
//                allResult.concat(String.valueOf(matrixExistence[i][j])).concat(" ");
                allResult += String.valueOf(matrixExistence[i][j]) + " ";
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
                for (int k = 0; k < listTotalElements.size(); k++) {
                    if (matrixExistence[i][k] == matrixExistence[j][k])
                        count++;
                }
                if (i != j) matrixMatch[i][j] = count;
                count = 0;
            }
        }
    }

    private static void showMatrixMatch() {
//        System.out.println();
        allResult += "\n";
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
//                System.out.print(matrixMatch[i][j]);
//                allResult.concat(String.valueOf(matrixMatch[i][j])).concat(" ");
                allResult += String.valueOf(matrixMatch[i][j]) + " ";
            }
//        System.out.println();
            allResult += "\n";
        }
    }

    private static void unionOfDetailsInGroups() {
        alGroupDetails = new ArrayList<>();

        int[] arrayDetail = new int[data.length];
        for (int i = 0; i < data.length; i++)
            arrayDetail[i] = i + 1;

        while (arrayDetailIsExist(arrayDetail)) {
//            System.out.println(String.valueOf(arrayDetailIsExist(arrayDetail)));
            int maxI = 0;
            int maxJ = 0;

            for (int i = 0; i < data.length; i++)
                for (int j = 0; j < data.length; j++)
                    if (matrixMatch[i][j] > matrixMatch[maxI][maxJ]) {
                        maxI = i;
                        maxJ = j;
                    }

            arrayDetail[maxI] = 0;
            arrayDetail[maxJ] = 0;
//            countGroup++;
//            countRow += 2;
            if (matrixMatch[maxI][maxJ] != 0) {
                List<List<Element>> alDetailsInGroup = new ArrayList<>();
                alDetailsInGroup.add(alDetails.get(maxI));
                alDetailsInGroup.add(alDetails.get(maxJ));
//                System.out.print("\nGroup " + countGroup + ": " + (maxI + 1) + ", " + (maxJ + 1));

                for (int i = 0; i < data.length; i++)
                    if (matrixMatch[i][maxJ] == matrixMatch[maxI][maxJ] && i != maxI) {
                        alDetailsInGroup.add(alDetails.get(i));
//                        System.out.print(", " + (i + 1));
//                        countRow++;
                        arrayDetail[i] = 0;
                        for (int k = 0; k < data.length; k++) {
                            matrixMatch[i][k] = 0;
                            matrixMatch[k][i] = 0;
                        }
                    }
                for (int j = 0; j < data.length; j++)
                    if (matrixMatch[maxI][j] == matrixMatch[maxI][maxJ] && j != maxJ) {
                        alDetailsInGroup.add(alDetails.get(j));
//                        System.out.print(", " + (j + 1));
//                        countRow++;
                        arrayDetail[j] = 0;
                        for (int k = 0; k < data.length; k++) {
                            matrixMatch[k][j] = 0;
                            matrixMatch[j][k] = 0;
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
                alGroupDetails.add(alDetailsInGroup);
            } else {
                List<List<Element>> alDetailsInGroup = new ArrayList<>();
//                System.out.print("\nGroup " + countGroup + ": ");
                for (int i = 0; i < data.length; i++)
                    if (arrayDetail[i] != 0) {
                        alDetailsInGroup.add(alDetails.get(i));
//                        System.out.print((i + 1) + ". ");
                        arrayDetail[i] = 0;
                    }
                alGroupDetails.add(alDetailsInGroup);
            }
        }
    }

    private static void showGroupDetails(List<List<List<Element>>> alGroupDetails) {
//        System.out.println("");
        allResult += "\n";
        for (int i = 0; i < alGroupDetails.size(); i++) {
//            System.out.print("Group " + (i + 1) + ":");
            allResult += "Group " + (i + 1) + ":";
            for (int j = 0; j < alGroupDetails.get(i).size(); j++) {
//                System.out.print("  " + alGroupDetails.get(i).get(j).toString());
//                System.out.print("  " + String.valueOf(alDetails.indexOf(alGroupDetails.get(i).get(j)) + 1));
                allResult += "  " + String.valueOf(alDetails.indexOf(alGroupDetails.get(i).get(j)) + 1);
            }
//            System.out.println(".");
            allResult += ".\n";
        }
    }

    private static void createGroupUniqueDetails() {
        alGroupUniqueElements = new ArrayList<>();

        for (int i = 0; i < alGroupDetails.size(); i++) {
            List<Element> alElementsInGroup = new ArrayList<>();
            for (int j = 0; j < alGroupDetails.get(i).size(); j++)
                for (int k = 0; k < alGroupDetails.get(i).get(j).size(); k++) {
                    if (alElementsInGroup.isEmpty()) {
                        alElementsInGroup.add(alGroupDetails.get(i).get(j).get(0));
                        k++;
                    }
                    for (int l = 0; l < alElementsInGroup.size(); l++)
                        if (alGroupDetails.get(i).get(j).get(k).getName().equals(alElementsInGroup.get(l).getName())) {
                            break;
                        } else if (l == alElementsInGroup.size() - 1) {
                            alElementsInGroup.add(alGroupDetails.get(i).get(j).get(k));
                            break;
                        }
                }
            alGroupUniqueElements.add(alElementsInGroup);
        }
    }

    private static void showGroupUniqueDetails() {
        System.out.println("");
        for (int i = 0; i < alGroupUniqueElements.size(); i++)
            System.out.println("Group " + (i + 1) + ":" + "  " + alGroupUniqueElements.get(i).toString() + ".");
    }

    private static void sortListDetailsAndListUniqueDetails() {
        for (int i = 0; i < alGroupUniqueElements.size(); i++) {
            for (int j = i; j < alGroupUniqueElements.size(); j++) {
                if (alGroupUniqueElements.get(j).size() > alGroupUniqueElements.get(i).size()) {
                    List<List<Element>> lleTemp = alGroupDetails.get(j);
                    alGroupDetails.set(j, alGroupDetails.get(i));
                    alGroupDetails.set(i, lleTemp);

                    List<Element> leTemp = alGroupUniqueElements.get(j);
                    alGroupUniqueElements.set(j, alGroupUniqueElements.get(i));
                    alGroupUniqueElements.set(i, leTemp);
                }
            }
        }
    }

    private static void simplifyGroups() {
        alSimpleGroupDetails = new ArrayList<>();

        for (int i = 0; i < alGroupDetails.size(); i++) {
            if (alGroupDetails.get(i).size() == 0)
                break;
            else
                alSimpleGroupDetails.add(alGroupDetails.get(i));
            for (int j = alGroupDetails.size() - 1; j > i; j--) {
                for (int k = alGroupDetails.get(j).size() - 1; k >= 0; k--) {
                    if (isIncludeListElements(alGroupUniqueElements.get(i), alGroupDetails.get(j).get(k))) {
                        alSimpleGroupDetails.get(i).add(alGroupDetails.get(j).get(k));
                        alGroupDetails.get(j).remove(k);
                    }
                }
            }
            createGroupUniqueDetails();
            sortListDetailsAndListUniqueDetails();
        }
    }

    private static boolean isIncludeListElements(List<Element> alUniqueElements, List<Element> alElementsInDetail) {
        int count = 0;
        for (int i = 0; i < alElementsInDetail.size(); i++)
            for (int j = 0; j < alUniqueElements.size(); j++)
                if (alElementsInDetail.get(i).getName().equals(alUniqueElements.get(j).getName())) {
                    count++;
                    break;
                }
        if (alElementsInDetail.size() == count)
            return true;
        else
            return false;
    }

    private static boolean arrayDetailIsExist(int[] arrayDetail) {
        for (int i = 0; i < data.length; i++)
            if (arrayDetail[i] != 0)
                return true;
        return false;
    }

}
