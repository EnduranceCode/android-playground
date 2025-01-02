package com.endurancecode.inventoryappstagetwo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.endurancecode.inventoryappstagetwo.data.InventoryContract.Products;
import com.endurancecode.inventoryappstagetwo.data.ProductProvider;

/**
 * Display the products details
 * <p>
 * METHODS INDEX
 * -------------
 * - onCreate()
 * - onCreateOptionsMenu()
 * - onOptionsItemSelected()
 * - editProduct()
 * - callSupplier()
 * - showDeleteConfirmationDialog()
 * - deleteProduct()
 * - onBackPressed()
 * - onCreateLoader()
 * - onLoadFinished()
 * - onLoaderReset()
 * - isCursorValid()
 */
public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /**
     * Identifier for the product's data loader
     */
    private static final int DETAILS_PRODUCT_LOADER = 1;

    /**
     * Track the product's name TextView
     */
    private TextView nameTextView;

    /**
     * Track the product's quantity TextView
     */
    private TextView quantityTextView;

    /**
     * Track the product's quantity
     */
    private int quantity;

    /**
     * Track the product's price TextView
     */
    private TextView priceTextView;

    /**
     * Track the product's supplier TextView
     */
    private TextView supplierTextView;

    /**
     * Track the product's supplier phone number TextView
     */
    private TextView supplierPhoneTextView;

    /**
     * Content URI for the existing product
     */
    private Uri currentProductUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        /* Get the current's product URI */
        Intent detailsActivityIntent = getIntent();
        currentProductUri = detailsActivityIntent.getData();

        Log.e(LOG_TAG, "Current Product URI: " + currentProductUri);

        /* Initialize the loader to get the current's product data */
        //noinspection deprecation
        getSupportLoaderManager().initLoader(DETAILS_PRODUCT_LOADER, null, this);

        /* Find all relevant TextViews that we will need update with the data from the Cursor */
        nameTextView = findViewById(R.id.name);
        quantityTextView = findViewById(R.id.quantity_value);
        priceTextView = findViewById(R.id.price_value);
        supplierTextView = findViewById(R.id.supplier_value);
        supplierPhoneTextView = findViewById(R.id.supplier_phone_value);

        /* Set an onClickListener method on the delete product button */
        FloatingActionButton deleteProductButton = findViewById(R.id.floatingDeleteButton);
        deleteProductButton.setOnClickListener(new FloatingActionButton.OnClickListener() {

            @Override
            public void onClick(View view) {

                showDeleteConfirmationDialog();
            }
        });

        /* Set an onClickListener method on the edit product button */
        FloatingActionButton editProductButton = findViewById(R.id.floatingEditButton);
        editProductButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {

                editProduct();
            }
        });

        /* Set an onClickListener method on the call button */
        FloatingActionButton callButton = findViewById(R.id.floatingCallButton);
        callButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {

                callSupplier();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /*
         * Inflate the menu options from the res/menu/product_details_menu.xml file.
         * This adds menu items to the app bar.
         */
        getMenuInflater().inflate(R.menu.product_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /* User clicked on a menu option in the app bar overflow menu */
        switch (item.getItemId()) {

            /* Respond to a click on the "Edit product" menu option */
            case R.id.edit_product:
                editProduct();
                return true;

            /* Respond to a click on the "Call supplier" menu option */
            case R.id.call_supplier:
                callSupplier();
                return true;

            /* Respond to a click on the "Delete product" menu option */
            case R.id.delete_product:
                showDeleteConfirmationDialog();
                return true;

            /* Respond to the action bar's Up/Home button */
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Start the Product Editor Activity
     */
    private void editProduct() {

        Intent productEditorActivityIntent = new Intent(ProductDetailsActivity.this, ProductEditorActivity.class);

        /* Set the URI on the data field of the intent */
        productEditorActivityIntent.setData(currentProductUri);

        /* Launch the {@link ProductEditorActivity} to display the data for the current product */
        startActivity(productEditorActivityIntent);
    }

    private void callSupplier() {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + String.valueOf(supplierPhoneTextView.getText())));
        startActivity(dialIntent);
    }

    /**
     * Prompt the user to confirm product's deletion
     */
    private void showDeleteConfirmationDialog() {

        /*
         * Create an AlertDialog.Builder and set the message, and click listeners
         * for the positive and negative buttons on the dialog.
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_single_product_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                /* User clicked the "Delete" button, so delete the product */
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                /*
                 * User clicked the "Cancel" button, so dismiss the dialog
                 * and continue editing the pet.
                 */
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        /* Create and show the AlertDialog */
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {

        /* Only perform the delete if this is an existing product */
        if (currentProductUri != null) {

            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);

            /* Show a toast message depending on whether or not the deletion was successful */
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful), Toast.LENGTH_SHORT).show();
            }
        }

        /* Close the activity */
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent mainActivityIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        /*
         * Since we want to show all products attributes, define a projection that contains
         * all columns from the products table
         */
        String[] projection = {
                Products._ID,
                Products.PRODUCT_NAME,
                Products.QUANTITY,
                Products.PRICE,
                Products.SUPPLIER,
                Products.SUPPLIER_PHONE
        };

        /*
         * This loader will execute the ContentProvider's query method on a background thread
         *
         * Provided input parameters:
         *  - Parent activity context
         *  - Provider content URI to query
         *  - Columns to include in the resulting Cursor
         *  - No selection clause
         *  - No selection arguments
         *  - Default sort order
         */
        return new CursorLoader(
                this,
                currentProductUri,
                projection,
                null,
                null,
                null
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        if (isCursorValid(cursor)) {

            /*
             * Proceed with moving to the first row of the cursor and reading data from it
             * (This should be the only row in the cursor)
             */
            if (cursor.moveToFirst()) {

                /* Find index of the columns of product's attributes that we're interested in */
                int nameColumnIndex = cursor.getColumnIndex(Products.PRODUCT_NAME);
                int quantityColumnIndex = cursor.getColumnIndex(Products.QUANTITY);
                int priceQuantityColumnIndex = cursor.getColumnIndex(Products.PRICE);
                int supplierColumnIndex = cursor.getColumnIndex(Products.SUPPLIER);
                int supplierPhoneColumnIndex = cursor.getColumnIndex(Products.SUPPLIER_PHONE);

                /* Extract out the value from the Cursor for the given column index */
                String name = cursor.getString(nameColumnIndex);
                quantity = cursor.getInt(quantityColumnIndex);
                double price = cursor.getDouble(priceQuantityColumnIndex);
                String supplier = cursor.getString(supplierColumnIndex);
                String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

                /* Update the TextViews in the layout with the extracted values from the cursor */
                nameTextView.setText(name);
                quantityTextView.setText(Integer.toString(quantity));
                priceTextView.setText(Double.toString(price));
                supplierTextView.setText(supplier);
                supplierPhoneTextView.setText(supplierPhone);

                /* Set an onClickListener method on the increase button */
                ImageButton increaseButton = findViewById(R.id.increase_button);
                increaseButton.setOnClickListener(new ImageButton.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        /*
                         * Create a ContentValues object where column names are the keys,
                         * and product attributes from the cursor are the values.
                         * We only want to update the quantity, therefore it is the only pair key/value
                         * that we use
                         *
                         * The inserted quantity value is the current quantity increased by one
                         */
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Products.QUANTITY, quantity + 1);

                        /* Update the database with the new product's quantity */
                        getContentResolver().update(
                                currentProductUri,
                                contentValues,
                                null,
                                null);
                    }
                });

                /* Set an onItemClickListener method on the decrease button */
                ImageButton decreaseButton = findViewById(R.id.decrease_button);
                decreaseButton.setOnClickListener(new ImageButton.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (quantity <= 0) {

                            /*
                             * We don't allow negative quantities, therefore we don't update the database
                             * and we let the user know
                             */
                            Toast.makeText(getBaseContext(), getString(R.string.negative_quantities_warning),
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            /*
                             * Create a ContentValues object where column names are the keys,
                             * and product attributes from the cursor are the values.
                             * We only want to update the quantity, therefore it is the only pair key/value
                             * that we use
                             *
                             * The inserted quantity value is the current quantity decreased by one
                             */
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Products.QUANTITY, quantity - 1);

                            /* Update the database with the new product's quantity */
                            getContentResolver().update(
                                    currentProductUri,
                                    contentValues,
                                    null,
                                    null);
                        }
                    }
                });
            }

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        /* If the loader is invalidated, clear out all the data from the input fields */
        nameTextView.setText(null);
        quantityTextView.setText(null);
        priceTextView.setText(null);
        supplierTextView.setText(null);
        supplierPhoneTextView.setText(null);
    }

    /**
     * Check if the cursor is not null and if it has at least one row
     *
     * @param cursor The cursor to be checked
     */
    private boolean isCursorValid(Cursor cursor) {
        return cursor != null && cursor.getCount() >= 1;
    }
}
