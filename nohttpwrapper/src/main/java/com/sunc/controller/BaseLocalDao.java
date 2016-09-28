package com.sunc.controller;

import com.yolanda.nohttp.NoHttp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by suncheng on 2016/9/7.
 */
public class BaseLocalDao {
    protected String createFileName(String type){
        return new StringBuilder()
                .append(type)
                .toString();
    }

    protected boolean writeObj(String name, Object obj) {
        File file = null;
        ObjectOutputStream objOut = null;
        FileOutputStream fileOut = null;

        try {
            file = new File(NoHttp.getContext().getCacheDir(), name);
            fileOut = new FileOutputStream(file);

            objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(obj);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally{
            if(fileOut!=null){
                try {
                    fileOut.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    objOut.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    protected Object readObj(String name) {
        Object obj = null;
        ObjectInputStream objIn = null;
        FileInputStream fileIn = null;
        File file = null;
        try {
            file = new File(NoHttp.getContext().getCacheDir(), name);
            fileIn = new FileInputStream(file);

            objIn = new ObjectInputStream(fileIn);
            obj = objIn.readObject();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally{
            if(fileIn!=null){
                try {
                    fileIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    objIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
