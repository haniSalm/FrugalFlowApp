package com.sdaproject.frugalflowapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BudgetTracker.db";
    private static final int DATABASE_VERSION = 2;

    // Categories table
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY_NAME = "name";
    private static final String COLUMN_CATEGORY_EMOJI = "emoji";
    private static final String COLUMN_CATEGORY_DESCRIPTION = "description";

    // Products table
    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String COLUMN_PRODUCT_DESCRIPTION = "description";
    private static final String COLUMN_PRODUCT_IMAGE = "image";
    private static final String COLUMN_CATEGORY_FK = "category_id";

    // Expenses table
    private static final String TABLE_EXPENSES = "expenses";
    private static final String COLUMN_EXPENSE_ID = "expense_id";
    private static final String COLUMN_EXPENSE_AMOUNT = "amount";
    private static final String COLUMN_EXPENSE_CATEGORY = "category";
    private static final String COLUMN_EXPENSE_DESCRIPTION = "description";
    private static final String COLUMN_EXPENSE_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create categories table
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CATEGORY_NAME + " TEXT,"
                + COLUMN_CATEGORY_EMOJI + " TEXT,"
                + COLUMN_CATEGORY_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);

        // Create products table
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PRODUCT_NAME + " TEXT,"
                + COLUMN_PRODUCT_PRICE + " REAL,"
                + COLUMN_PRODUCT_DESCRIPTION + " TEXT,"
                + COLUMN_PRODUCT_IMAGE + " TEXT,"
                + COLUMN_CATEGORY_FK + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_CATEGORY_FK + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + ")"
                + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

        // Create expenses table
        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EXPENSE_AMOUNT + " REAL,"
                + COLUMN_EXPENSE_CATEGORY + " TEXT,"
                + COLUMN_EXPENSE_DESCRIPTION + " TEXT,"
                + COLUMN_EXPENSE_DATE + " INTEGER"  // Changed to store timestamp
                + ")";
        db.execSQL(CREATE_EXPENSES_TABLE);

        // Insert default data
        initializeDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    private void initializeDefaultData(SQLiteDatabase db) {
        // Insert default categories
        insertDefaultCategory(db, "Food", "🍔", "Delicious meals and snacks");
        insertDefaultCategory(db, "Transport", "🚗", "Transportation costs");
        insertDefaultCategory(db, "Entertainment", "🎬", "Movies and fun activities");
        insertDefaultCategory(db, "Utilities", "💡", "Bills and utilities");
        insertDefaultCategory(db, "Other", "📦", "Miscellaneous expenses");
    }

    private void insertDefaultCategory(SQLiteDatabase db, String name, String emoji, String description) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, name);
        values.put(COLUMN_CATEGORY_EMOJI, emoji);
        values.put(COLUMN_CATEGORY_DESCRIPTION, description);
        db.insert(TABLE_CATEGORIES, null, values);
    }

    public long addExpense(double amount, String category, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXPENSE_AMOUNT, amount);
        values.put(COLUMN_EXPENSE_CATEGORY, category);
        values.put(COLUMN_EXPENSE_DESCRIPTION, description);
        values.put(COLUMN_EXPENSE_DATE, System.currentTimeMillis());
        return db.insert(TABLE_EXPENSES, null, values);
    }

    public List<Expense> getExpensesByCategory(String category) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_EXPENSES +
                " WHERE " + COLUMN_EXPENSE_CATEGORY + "=?" +
                " ORDER BY " + COLUMN_EXPENSE_DATE + " DESC";

        try (Cursor cursor = db.rawQuery(query, new String[]{category})) {
            while (cursor.moveToNext()) {
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_AMOUNT));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_DESCRIPTION));
                long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_DATE));

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                String date = sdf.format(new Date(dateMillis));

                ExpenseCategory expenseCategory = new ExpenseCategory(category, getIconResourceForCategory(category));
                expenses.add(new Expense(description, amount, expenseCategory, date));
            }
        }
        return expenses;
    }

    public double getMonthlyTotalByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSES +
                " WHERE " + COLUMN_EXPENSE_CATEGORY + "=? AND " +
                "strftime('%Y-%m', " + COLUMN_EXPENSE_DATE + "/1000, 'unixepoch') = strftime('%Y-%m', 'now')";

        try (Cursor cursor = db.rawQuery(query, new String[]{category})) {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
        }
        return 0.0;
    }

    private int getIconResourceForCategory(String category) {
        switch (category.toLowerCase()) {
            case "food": return R.drawable.ic_food;
            case "transport": return R.drawable.ic_transport;
            case "entertainment": return R.drawable.ic_entertainment;
            case "utilities": return R.drawable.ic_utilities;
            default: return R.drawable.ic_other;
        }
    }

    public List<Category> getAllCategoriesWithProducts() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor categoryCursor = db.query(TABLE_CATEGORIES,
                null, null, null, null, null, null)) {

            if (categoryCursor.moveToFirst()) {
                do {
                    int categoryId = categoryCursor.getInt(categoryCursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
                    String name = categoryCursor.getString(categoryCursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME));
                    String emoji = categoryCursor.getString(categoryCursor.getColumnIndexOrThrow(COLUMN_CATEGORY_EMOJI));
                    String description = categoryCursor.getString(categoryCursor.getColumnIndexOrThrow(COLUMN_CATEGORY_DESCRIPTION));

                    List<Product> products = getProductsForCategory(categoryId);
                    categories.add(new Category(name, emoji, description, products));
                } while (categoryCursor.moveToNext());
            }
        }
        return categories;
    }

    private List<Product> getProductsForCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor productCursor = db.query(TABLE_PRODUCTS,
                null,
                COLUMN_CATEGORY_FK + "=?",
                new String[]{String.valueOf(categoryId)},
                null, null, null)) {

            if (productCursor.moveToFirst()) {
                do {
                    String name = productCursor.getString(productCursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                    double price = productCursor.getDouble(productCursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE));
                    String description = productCursor.getString(productCursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION));
                    String image = productCursor.getString(productCursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE));

                    products.add(new Product(name, price, description, image));
                } while (productCursor.moveToNext());
            }
        }
        return products;
    }
}