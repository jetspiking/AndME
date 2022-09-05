package com.dustinhendriks.andme.utils;

import android.content.Context;
import android.util.Log;

import com.dustinhendriks.andme.models.AppSerializableData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializationUtils {
    private static final String TAG = "SerializationUtils";
    public static void serializeData(Context context, AppSerializableData object) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = context.openFileOutput("data", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
        } catch (IOException ioException) {
            Log.e(TAG, "loadSerializedData: Error",ioException);
        }
    }

    public static AppSerializableData loadSerializedData(Context context) {
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(context.openFileInput("data"));
            Object obj = objectInputStream.readObject();
            if (obj instanceof Serializable)
                return (AppSerializableData) obj;
        } catch (IOException | ClassNotFoundException exception) { Log.e(TAG, "loadSerializedData: Error",exception);}
        finally {
            try {
                if (objectInputStream!=null) {
                    objectInputStream.close();
                }
            } catch (IOException exception) {
                Log.e(TAG, "loadSerializedData: Error",exception);
            }
        }
        return null;
    }
}
