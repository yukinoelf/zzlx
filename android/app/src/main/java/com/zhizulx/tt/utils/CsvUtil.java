
package com.zhizulx.tt.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CsvUtil {

    public static final String TAG = "CsvUtil";

    private BufferedReader bufferedreader = null;
    private List<String> list = new ArrayList<String>();
    private List<List<String>> csv = new ArrayList<List<String>>();

    public CsvUtil(Context ctx, String filename) throws IOException {
        AssetManager assetManager = ctx.getAssets();
        InputStream is = assetManager.open(filename);
        bufferedreader = new BufferedReader(new InputStreamReader(is));

        String stemp;
        stemp = bufferedreader.readLine();//jump title
        while ((stemp = bufferedreader.readLine()) != null) {
            list.add(stemp);
        }
    }

    public List getList() throws IOException {
        return list;
    }

    public int getRowNum() {
        return list.size();
    }

    public int getColNum() {
        if (!list.toString().equals("[]")) {
            if (list.get(0).toString().contains(",")) {
                return list.get(0).toString().split(",").length;
            } else if (list.get(0).toString().trim().length() != 0) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public String getRow(int index) {
        if (this.list.size() != 0)
            return (String) list.get(index);
        else
            return null;
    }

    public String getCol(int index) {
        if (this.getColNum() == 0) {
            return null;
        }

        StringBuffer scol = new StringBuffer();
        String temp = null;
        int colnum = this.getColNum();

        if (colnum > 1) {
            for (Iterator it = list.iterator(); it.hasNext();) {
                temp = it.next().toString();
                scol = scol.append(temp.split(",")[index] + ",");
            }
        } else {
            for (Iterator it = list.iterator(); it.hasNext();) {
                temp = it.next().toString();
                scol = scol.append(temp + ",");
            }
        }
        String str = new String(scol.toString());
        str = str.substring(0, str.length() - 1);
        return str;
    }

    public String getString(int row, int col) {
        String temp = null;
        int colnum = this.getColNum();
        if (colnum > 1) {
            temp = list.get(row).toString().split(",")[col];
        } else if (colnum == 1) {
            temp = list.get(row).toString();
        } else {
            temp = null;
        }
        return temp.replace("\"", "");
    }

    public void CsvClose() throws IOException {
        this.bufferedreader.close();
    }

    public void run() throws IOException {
        //这里需要根据具体的CSV文件内容和业务需求来决定取那些字段
        //the title not used, so we start from 1 row
        for (int i = 0; i < getRowNum(); i++) {
            List<String> col = new ArrayList<>();
            for (int j = 0; j < getColNum(); j++) {
                col.add(getString(i,j));
            }
            csv.add(col);
        }
        CsvClose();
    }

    public List<List<String>> getCsv() {
        return csv;
    }
}
