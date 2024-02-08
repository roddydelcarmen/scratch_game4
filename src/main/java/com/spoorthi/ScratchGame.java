package com.spoorthi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.*;

public class ScratchGame {
    private int rows;
    private int columns;
    private Map<String, Symbol> symbols;
    private List<WinCombination> winCombinations;
    private String[][] matrix;

    public ScratchGame(int rows, int columns, Map<String, Symbol> symbols, List<WinCombination> winCombinations) {
        this.rows = rows;
        this.columns = columns;
        this.symbols = symbols;
        this.winCombinations = winCombinations;
        this.matrix = new String[rows][columns]; // Cambiar a String[][]
    }

    public ScratchGame(int rows, int columns) {
        this.matrix = new String[rows][columns];
    }

    public void fillMatrixFromFile(String filePath) {
        try {
            // Leer el contenido del archivo JSON
            String content = new String(Files.readAllBytes(Paths.get(filePath)));

            // Convertir el contenido en un objeto JSONObject
            JSONObject jsonObject = new JSONObject(content);

            // Obtener la configuración de la matriz del JSON
            JSONArray matrixConfig = jsonObject.getJSONArray("matrix");

            // Llenar la matriz con los símbolos del JSON
            for (int i = 0; i < matrixConfig.length(); i++) {
                JSONArray row = matrixConfig.getJSONArray(i);
                for (int j = 0; j < row.length(); j++) {
                    matrix[i][j] = row.getString(j);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateMatrix() {
        Random random = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // Generate a random symbol based on probabilities
                // You need to implement this based on the probabilities specified in the config
                // For simplicity, let's assume each symbol has equal probability for now
                String[] symbolNames = symbols.keySet().toArray(new String[0]);
                String randomSymbol = symbolNames[random.nextInt(symbolNames.length)];
                matrix[i][j] = randomSymbol;
            }
        }
    }

    /*
     * public void printMatrix() {
     * for (int i = 0; i < rows; i++) {
     * for (int j = 0; j < columns; j++) {
     * System.out.print(matrix[i][j] + " ");
     * }
     * System.out.println();
     * }
     * }
     */

    public void printMatrix() {
        for (String[] row : matrix) {
            for (String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public int calculateReward(int betAmount) {
        int reward = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // Check for winning combinations and calculate reward
                // You need to implement this logic based on the winCombinations list
                // For simplicity, let's assume no winning combinations for now
            }
        }
        return reward;
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
        /*
         * // Parse the configuration file and initialize the game
         * // You need to implement this part
         * // For simplicity, let's assume we have hardcoded values for now
         * int rows = 3;
         * int columns = 3;
         * Map<String, Symbol> symbols = new HashMap<>();
         * symbols.put("A", new Symbol(50, SymbolType.STANDARD));
         * // Add other symbols
         * List<WinCombination> winCombinations = new ArrayList<>();
         * // Add win combinations
         * ScratchGame game = new ScratchGame(rows, columns, symbols, winCombinations);
         * game.generateMatrix();
         * game.printMatrix();
         */

        // Crear una instancia de ScratchGame
        ScratchGame game = new ScratchGame(3, 3);

        // Llenar la matriz desde el archivo de configuración
        game.fillMatrixFromFile("src/main/resources/config.json");

        // Imprimir la matriz
        game.printMatrix();
    }

    private static JSONObject readConfigFromFile(String fileName) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return new JSONObject(sb.toString());
    }

    private static Map<String, Symbol> parseSymbols(JSONObject symbolsObj) throws JSONException {
        Map<String, Symbol> symbols = new HashMap<>();
        Iterator<String> keys = symbolsObj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject symbolObj = symbolsObj.getJSONObject(key);
            int rewardMultiplier = symbolObj.getInt("reward_multiplier");
            SymbolType type = SymbolType.valueOf(symbolObj.getString("type").toUpperCase());
            symbols.put(key, new Symbol(rewardMultiplier, type));
        }
        return symbols;
    }

    private static List<WinCombination> parseWinCombinations(JSONObject winCombinationsObj) throws JSONException {
        List<WinCombination> winCombinations = new ArrayList<>();
        Iterator<String> keys = winCombinationsObj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject winCombinationObj = winCombinationsObj.getJSONObject(key);
            int rewardMultiplier = winCombinationObj.getInt("reward_multiplier");
            int count = winCombinationObj.getInt("count");
            WinCondition condition = WinCondition.valueOf(winCombinationObj.getString("when").toUpperCase());
            String group = winCombinationObj.optString("group");
            JSONArray coveredAreasArr = winCombinationObj.optJSONArray("covered_areas");
            List<List<String>> coveredAreas = new ArrayList<>();
            if (coveredAreasArr != null) {
                for (int i = 0; i < coveredAreasArr.length(); i++) {
                    JSONArray areaArr = coveredAreasArr.getJSONArray(i);
                    List<String> area = new ArrayList<>();
                    for (int j = 0; j < areaArr.length(); j++) {
                        area.add(areaArr.getString(j));
                    }
                    coveredAreas.add(area);
                }
            }
            winCombinations.add(new WinCombination(rewardMultiplier, count, condition, group, coveredAreas));
        }
        return winCombinations;
    }
}

class Symbol {
    private int rewardMultiplier;
    private SymbolType type;

    public Symbol(int rewardMultiplier, SymbolType type) {
        this.rewardMultiplier = rewardMultiplier;
        this.type = type;
    }

    // Getters and setters
}

enum SymbolType {
    STANDARD, BONUS
}

class WinCombination {
    private int rewardMultiplier;
    private int count;
    private WinCondition condition;
    private String group;
    private List<List<String>> coveredAreas;

    public WinCombination(int rewardMultiplier, int count, WinCondition condition, String group,
            List<List<String>> coveredAreas) {
        this.rewardMultiplier = rewardMultiplier;
        this.count = count;
        this.condition = condition;
        this.group = group;
        this.coveredAreas = coveredAreas;
    }
}

enum WinCondition {
    SAME_SYMBOLS, LINEAR_SYMBOLS
}
