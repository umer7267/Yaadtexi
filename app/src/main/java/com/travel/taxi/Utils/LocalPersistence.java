package com.travel.taxi.Utils;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class LocalPersistence {

    static String filename="nameofsignin";


        public static void witeObjectToFile(Context context, Object object) {

            ObjectOutputStream objectOut = null;
            try {

                FileOutputStream fileOut = context.openFileOutput(filename, Activity.MODE_PRIVATE);
                objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(object);
                fileOut.getFD().sync();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (objectOut != null) {
                    try {
                        objectOut.close();
                    } catch (IOException e) {
                        // do nowt
                    }
                }
            }
        }



        public static Object readObjectFromFile(Context context) {

            ObjectInputStream objectIn = null;
            Object object = null;
            try {

                FileInputStream fileIn = context.getApplicationContext().openFileInput(filename);
                objectIn = new ObjectInputStream(fileIn);
                object = objectIn.readObject();

            } catch (FileNotFoundException e) {
                // Do nothing
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (objectIn != null) {
                    try {
                        objectIn.close();
                    } catch (IOException e) {
                        // do nowt
                    }
                }
            }

            return object;
        }



    public static void  deletefile(Context context){
        File file=new File(context.getFilesDir().getAbsolutePath()+"/"+filename);
        if(file.exists())file.delete();
    }


    }


