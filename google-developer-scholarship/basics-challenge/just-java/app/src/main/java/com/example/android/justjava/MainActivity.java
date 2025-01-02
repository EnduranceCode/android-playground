/**
 * IMPORTANT: Make sure you are using the correct package name.
 * This example uses the package name:
 * package com.example.android.justjava
 * If you get an error when copying this code into Android studio, update it to match teh package name found
 * in the project's AndroidManifest.xml file.
 **/

package com.example.android.justjava;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity {

    int quantity = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This method is called when the + button is clicked.
     */
    public void increment(View view) {
        if (quantity >= 100) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.upper_limit),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        quantity = quantity + 1;
        displayQuantity(quantity);
    }

    /**
     * This method is called when the - button is clicked.
     */
    public void decrement(View view) {
        if (quantity == 1) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.lower_limit),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        quantity = quantity - 1;
        displayQuantity(quantity);
    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        // Get Client's name
        EditText clientNameInput = (EditText) findViewById(R.id.clientName);
        String clientName = clientNameInput.getText().toString();
        // Check if the client wants Whipped Cream
        CheckBox whippedCreamCheckbox = (CheckBox) findViewById(R.id.ToppingsWhippedCream);
        boolean hasWhippedCream = whippedCreamCheckbox.isChecked();
        // Check if the client wants chocolate
        CheckBox chocolateCheckbox = (CheckBox) findViewById(R.id.ToppingsChocolate);
        boolean hasChocolate = chocolateCheckbox.isChecked();
        // Calculate the order's price
        int price = calculatePrice(hasWhippedCream, hasChocolate);
        String priceMessage = createOrderSummary(clientName, hasWhippedCream, hasChocolate, price);
        //Send e-mail with the order
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_email_subject, clientName));
        intent.putExtra(Intent.EXTRA_TEXT, priceMessage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Calculates the price of the order
     *
     * @return Total Price
     */
    private int calculatePrice(boolean hasWhippedCream, boolean hasChocolate) {
        int basePricePerCup = 5;
        if (hasWhippedCream) {
            basePricePerCup += 1;
        }
        if (hasChocolate) {
            basePricePerCup += 2;
        }
        return quantity * basePricePerCup;
    }

    /**
     * Creates order summary message
     *
     * @param clientName
     * @param hasWhippedCream
     * @param hasChocolate
     * @param price           of the order
     * @return text message
     */
    private String createOrderSummary(String clientName, boolean hasWhippedCream, boolean hasChocolate, int price) {
        String priceMessage = getString(R.string.order_summary_name, clientName);
        priceMessage += "\n" + getString(R.string.order_summary_whipped_cream_option,  hasWhippedCream);
        priceMessage += "\n" + getString(R.string.order_summary_chocolate_option, hasChocolate);
        priceMessage += "\n" + getString(R.string.order_summary_quantity, quantity);
        priceMessage += "\n" + getString(R.string.order_summary_total, price);
        priceMessage += "\n" + getString(R.string.order_summary_thank_you);
        return priceMessage;
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void displayQuantity(int numberOfCoffees) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + numberOfCoffees);
    }
}
