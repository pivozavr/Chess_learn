package com.example.chess3;

public class FenParser {
    private String fen;

    public FenParser(String fen){
        this.fen = fen;
    }

    public void SetFen(String fen){
        this.fen = fen;
    }

    public String GetFen(){
        return this.fen;
    }

    public String[][] parse() {
        String justString = this.fen.substring(0, fen.indexOf(' '));
        StringBuilder result = new StringBuilder();
        for (char ch : justString.toCharArray()) {
            if (Character.isDigit(ch)) {
                int count = Character.getNumericValue(ch);
                result.append("a".repeat(count));
            } else {
                result.append(ch);
            }
        }

        String[] oneDimensionArraay = result.toString().split("/");
        String[][] twoDementionArray = new String[8][8];


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                twoDementionArray[i][j] = oneDimensionArraay[i].substring(j, j + 1);
            }
        }

        return twoDementionArray;
    }
}