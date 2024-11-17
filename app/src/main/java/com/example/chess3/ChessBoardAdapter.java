package com.example.chess3;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class ChessBoardAdapter extends BaseAdapter {
    private final Context context;
    private final int[][] boardBackgrounds; // Фон каждой клетки
    private final int[][] boardImages;      // Изображение в каждой клетке

    public ChessBoardAdapter(Context context) {
        this.context = context;
        this.boardBackgrounds = new int[8][8];
        this.boardImages = new int[8][8];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardBackgrounds[i][j] = (i + j) % 2 == 0 ? R.color.whiter : R.color.green;
                boardImages[i][j] = 0; // Начальное значение без изображения
            }
        }
    }

    public void clearBoard(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardBackgrounds[i][j] = (i + j) % 2 == 0 ? R.color.whiter : R.color.green;
            }
        }
        notifyDataSetChanged();
    }

    public void updateCellImage(int row, int col, int drawableResId) {
        boardImages[row][col] = drawableResId;
        notifyDataSetChanged(); // Обновить представления
    }

    public void lightUpCell(int row, int col){
        if(boardImages[row][col]!=0)
            boardBackgrounds[row][col] = R.drawable.red;
        else if(boardBackgrounds[row][col] == R.color.whiter)
            boardBackgrounds[row][col] = R.drawable.white_selected;
        else if(boardBackgrounds[row][col] == R.color.green)
            boardBackgrounds[row][col] = R.drawable.green_selected;
        notifyDataSetChanged();
    }

    public void lightCell(int row, int col){
        if(boardBackgrounds[row][col] == R.color.whiter)
            boardBackgrounds[row][col] = R.color.light_white;
        else if(boardBackgrounds[row][col] == R.color.green)
            boardBackgrounds[row][col] = R.color.light_green;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 64;
    }

    @Override
    public Object getItem(int position) {
        int row = position / 8;
        int col = position % 8;
        return boardImages[row][col];
    }

    public int getItemRow(int position) {
        int row = position / 8;
        return row;
    }
    public int getItemCol(int position) {
        int col = position % 8;
        return col;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(screenWidth/8, screenWidth/8));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        int row = position / 8;
        int col = position % 8;

        // Устанавливаем фон, который не будет изменяться
        imageView.setBackgroundResource(boardBackgrounds[row][col]);

        // Устанавливаем изображение в клетке, если оно есть
        if (boardImages[row][col] != 0) {
            imageView.setImageResource(boardImages[row][col]);
        } else {
            imageView.setImageDrawable(null); // Убираем изображение, если нет ресурса
        }

        return imageView;
    }
}
