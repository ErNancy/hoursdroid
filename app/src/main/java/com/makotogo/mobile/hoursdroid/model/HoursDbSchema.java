package com.makotogo.mobile.hoursdroid.model;

/**
 * Created by sperry on 1/3/16.
 */
public class HoursDbSchema {
    public static String computeForeignKey(String fkColumn, String referencesTable, String referencesColumn) {
        return "FOREIGN KEY(" + fkColumn + ") " +
                "REFERENCES " + referencesTable + "(" + referencesColumn + ")"
                ;
    }

    public static final class JobTable {
        public static final String NAME = "job";
        public static final String CREATE_SQL = "CREATE TABLE " + NAME + "(" +
                Column.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                "," + Column.NAME + " TEXT NOT NULL" +
                "," + Column.DESCRIPTION + " TEXT NOT NULL" +
                "," + Column.RATE + " REAL" +
                "," + Column.ACTIVE + " INTEGER NOT NULL" +
                "," + Column.WHEN_CREATED + " INTEGER NOT NULL" +
                "," + "UNIQUE(" + Column.NAME + ")" +
                ")";

        public static final class Column {
            public static final String ID = "_id";
            public static final String NAME = "NAME";
            public static final String DESCRIPTION = "description";
            public static final String RATE = "rate";
            public static final String ACTIVE = "active";
            public static final String WHEN_CREATED = "when_created";
        }
    }

    public static final class ProjectTable {
        public static final String NAME = "project";

        public static final String CREATE_SQL = "CREATE TABLE " + NAME + "(" +
                Column.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                "," + Column.NAME + " TEXT NOT NULL" +
                "," + Column.DESCRIPTION + " TEXT NOT NULL" +
                "," + Column.JOB_ID + " INTEGER  NOT NULL" +
                "," + Column.DEFAULT_FOR_JOB + " INTEGER" +
                "," + computeForeignKey(Column.JOB_ID, JobTable.NAME, JobTable.Column.ID) +
                "," + "UNIQUE(" + Column.JOB_ID + "," + Column.NAME + ")" +
                ")";

        public static final class Column {
            public static final String ID = "_id";
            public static final String NAME = "NAME";
            public static final String DESCRIPTION = "description";
            public static final String JOB_ID = "job_id";
            public static final String DEFAULT_FOR_JOB = "default_for_job";
        }
    }

    public static final class HoursTable {
        public static final String NAME = "hours";
        public static final String CREATE_SQL = "CREATE TABLE " + NAME + "(" +
                Column.ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                "," + Column.BEGIN + " INTEGER NOT NULL" +
                "," + Column.END + " INTEGER" +
                "," + Column.BREAK + " INTEGER" +
                "," + Column.DESCRIPTION + " TEXT" +
                "," + Column.JOB_ID + " INTEGER NOT NULL" +
                "," + Column.PROJECT_ID + " INTEGER NOT NULL" +
                "," + Column.BILLED + " INTEGER" +
                "," + Column.WHEN_CREATED + " INTEGER NOT NULL" +
                "," + computeForeignKey(Column.JOB_ID, JobTable.NAME, JobTable.Column.ID) +
                "," + computeForeignKey(Column.PROJECT_ID, ProjectTable.NAME, ProjectTable.Column.ID) +
                ")";

        public static final class Column {
            public static final String ID = "_id";
            public static final String BEGIN = "begin";
            public static final String END = "end";
            public static final String BREAK = "break";
            public static final String JOB_ID = "job_id";
            public static final String PROJECT_ID = "project_id";
            public static final String DESCRIPTION = "description";
            public static final String BILLED = "billed";
            public static final String WHEN_CREATED = "when_created";
        }
    }

}
