package com.cs437.esauceda.myapplication;

import android.provider.BaseColumns;

/**
 * Created by esauceda on 3/3/16.
 */
public final class Animal {
    public Animal() {}

    public static abstract class AnimalEntry implements BaseColumns {
        public static String TABLE_NAME = "animals";
        public static final String ANIMAL_ENTRY_ID = "animal_id";
        public static final String ANIMAL_NAME = "animal_name";
        public static final String ANIMAL_SCI_NAME = "scientific_name";
        public static final String ANIMAL_ORIGIN = "origin";
        public static final String ANIMAL_DESC = "description";
        public static final String ANIMAL_IMG = "image_url";
    }


}
