package com.endurancecode.inventoryappstagetwo.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public final class InventoryContract {

    /**
     * To prevent someone from accidentally instantiating the contract class,
     * make the constructor private.
     */
    private InventoryContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.
     * A convenient string to use for the content authority is the package name for the app,
     * which is guaranteed to be unique on the device.
     */
    static final String CONTENT_AUTHORITY = "com.endurancecode.inventoryappstagetwo";
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.endurancecode.inventoryappstagetwo/products/ is a valid path for
     * looking at products data. content://com.endurancecode.inventoryappstagetwo/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    static final String PATH_PRODUCTS = "products";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    private static final Uri BASE_CONTENT_AUTHORITY = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Inner class that defines the table contents
     * <p>
     * METHODS INDEX
     * -------------
     * - isInvalidName()
     * - isInvalidPrice()
     * - isInvalidQuantity()
     * - isInvalidSupplier()
     * - isInvalidSupplierPhone()
     */
    public static class Products implements BaseColumns {

        /**
         * The content URI to access the products data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_AUTHORITY, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_PRODUCTS;
        /* Name of database table for products */
        static final String TABLE_NAME = "products";
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_PRODUCTS;

        /**
         * Unique ID number for the product
         * <p>
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Name of the product
         * <p>
         * Type: TEXT
         */
        public static final String PRODUCT_NAME = "name";

        /**
         * Price of the product
         * <p>
         * Type: REAL
         */
        public static final String PRICE = "price";

        /**
         * Quantity of the product
         * <p>
         * Type: INTEGER
         */
        public static final String QUANTITY = "quantity";

        /**
         * Name of the product's supplier
         * <p>
         * Type: TEXT
         */
        public static final String SUPPLIER = "supplier";

        /**
         * Phone number of the product's supplier
         * <p>
         * Type: TEXT
         */
        public static final String SUPPLIER_PHONE = "supplier_phone";

        /**
         * Checks if the product's name is invalid, i.e., if it is null or empty
         *
         * @param name Product's name
         */
        public static boolean isInvalidProductName(String name) {
            return TextUtils.isEmpty(name);
        }

        /**
         * Checks if the product's price is invalid, i.e., if it is less or equal to zero
         *
         * @param price Product's price
         */
        public static boolean isInvalidPrice(double price) {
            return price <= 0;
        }

        /**
         * Checks if the product's quantity is invalid, i.e., if it is less than zero
         *
         * @param quantity Product's quantity
         */
        public static boolean isInvalidQuantity(int quantity) {
            return quantity < 0;
        }

        /**
         * Checks if the product's supplier is invalid, i.e., if it is null or empty
         *
         * @param supplier Product's supplier
         */
        public static boolean isInvalidSupplier(String supplier) {
            return TextUtils.isEmpty(supplier);
        }

        /**
         * Checks if the product's supplier phone is invalid, i.e., if it is null or empty
         *
         * @param supplierPhone Product's supplier phone
         */
        public static boolean isInvalidSupplierPhone(String supplierPhone) {
            return TextUtils.isEmpty(supplierPhone);
        }
    }
}
