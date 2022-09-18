package com.dustinhendriks.andme.utils;

import android.content.Context;

import com.dustinhendriks.andme.R;
import com.dustinhendriks.andme.models.AppSerializableData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Handles the serialization of application launcher data.
 */
public class SerializationUtils {
    public static void serializeData(Context context, AppSerializableData object) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = context.openFileOutput(context.getString(R.string.application_storage_tag), Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
        } catch (IOException ignored)
        {}
    }

    public static AppSerializableData loadSerializedData(Context context) {
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(context.openFileInput(context.getString(R.string.application_storage_tag)));
            Object obj = objectInputStream.readObject();
            if (obj instanceof Serializable)
                return (AppSerializableData) obj;
        } catch (IOException | ClassNotFoundException ignored) {}
        finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (IOException ignored)
            {}
        }
        return null;
    }
}
