package com.example.spiraltest;

import android.graphics.Path;

import com.androidplot.xy.XYSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Resembles a Path, stores x, y, t coordinates for now
 */

class Team3Trace implements XYSeries {

    private int n = 0;
    private boolean inc = true;
    private float cx, cy;

    private String title = null;
    private List<Coord> coords;
    private List<Path> paths;

    // will auto transform screen coords to center coords
    Team3Trace(float center_x, float center_y) {
        coords = new ArrayList<>();
        paths = new ArrayList<>();
        cx = center_x;
        cy = center_y;
    }

    Team3Trace(float center_x, float center_y, String title) {
        this(center_x, center_y);
        this.title = title;
    }

    void add(float x, float y, long t) {
        coords.add(new Coord(x-cx, y-cy, t));

        if (inc) {
            n += 1;
            inc = false;
            paths.add(new Path());
            paths.get(n-1).moveTo(x, y);
        } else {
            paths.get(n-1).lineTo(x, y);
        }
    }

    void addBreak() {
        coords.add(null);
        inc = true;
    }

    public List<Coord> getCoords () {
        return coords;
    }

    int getNTraces() {
        return n;
    }

    List<Path> getPaths() {
        return paths;
    }

    void clear() {
        coords.clear();
        paths.clear();
        n = 0;
        inc = true;
    }

    void setOrigin (float x, float y) {
        clear();
        cx = x;
        cy = y;
    }

    void writeToFile(File f) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(f);
        PrintStream printStream = new PrintStream(outputStream);

        for (Coord c : coords) {
            StringBuilder sb = new StringBuilder();
            if (c == null) {
                sb.append("NaN,NaN,NaN");
            } else {
                sb.append((int) c.x).append(",").append((int) c.y).append(",").append(c.t);
            }
            printStream.println(sb.toString());
        }

        printStream.close();
        outputStream.close();
    }

    void readFromFile (File f) throws IOException {
        clear();
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null) {
            String[] pieces = line.split(",");
            try {
                int x = Integer.valueOf(pieces[0]);
                int y = Integer.valueOf(pieces[1]);
                long t = Long.valueOf(pieces[2]);
                add(x, y, t);
            } catch (NumberFormatException nfe) {
                // ignore
            }
        }
    }

    @Override
    public int size() {
        return coords.size();
    }

    @Override
    public Number getX(int index) {
        return coords.get(index).x;
    }

    @Override
    public Number getY(int index) {
        return coords.get(index).y;
    }

    @Override
    public String getTitle() {
        return title == null ? "" : title;
    }

    private class Coord {
        float x, y;
        long t;

        Coord (float x, float y, long t) {
            this.x = x;
            this.y = y;
            this.t = t;
        }
    }

}