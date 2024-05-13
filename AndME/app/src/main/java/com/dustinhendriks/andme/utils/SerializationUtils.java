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
    /**
     * Serialize (save) the application data.
     * @param context Application context.
     * @param appSerializableData Data to serialize.
     */
    public static void serializeData(Context context, AppSerializableData appSerializableData) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = context.openFileOutput(context.getString(R.string.application_storage_tag), Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(appSerializableData);
            objectOutputStream.close();
        } catch (IOException ignored)
        {}
    }

    /**
     * Deserialize (load) the application data.
     * @param context Application context.
     * @return Deserialized data.
     */
    public static AppSerializableData DeserializedData(Context context) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(context.openFileInput(context.getString(R.string.application_storage_tag)))) {
            Object obj = objectInputStream.readObject();
            if (obj instanceof Serializable)
                return (AppSerializableData) obj;
        } catch (IOException | ClassNotFoundException ignored) {
        }
        return null;
    }
}
