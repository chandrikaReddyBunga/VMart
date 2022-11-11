package vmart.example.mypc.vedasmart.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.model.ItemsInfo;
import vmart.example.mypc.vedasmart.model.OrderInfo;

public class InvoiceDisplayActivity extends AppCompatActivity {

    private WebView webView;
    private Bitmap bitmap;
    private OrderInfo orderInfo;
    private TextView downloadInvoiceButton, shareInvoiceButton;
    private RelativeLayout invoiceRootLayout;
    public static final int WRITE_STORAGE_PERMISSION_CODE = 101;
    public static final int READ_EXTERNAL_STORAGE_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_display);
        // this method is responsible for casting all views in xml with java file
        castingViews();
        if (getIntent().getExtras() != null) {
            orderInfo = (OrderInfo) getIntent().getExtras().getSerializable("purchasedOrder");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebview(orderInfo);
        }
        downloadInvoiceButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(InvoiceDisplayActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    downloadInvoice(orderInfo.getOrderId(), false);
                } else {
                    requestWriteExternalStoragePermission();
                }
            }
        });
        shareInvoiceButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(InvoiceDisplayActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(InvoiceDisplayActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    downloadInvoice(orderInfo.getOrderId(), true);
                } else {
                    requestReadExternalStoragePermission();
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadInvoice(orderInfo.getOrderId(), false);
                } else {
                    Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                }
                break;
            case READ_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadInvoice(orderInfo.getOrderId(), true);
                } else {
                    Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void downloadInvoice(String orderId, boolean isFromShareInvoice) {
        bitmap = loadBitmapFromView(invoiceRootLayout, invoiceRootLayout.getWidth(), invoiceRootLayout.getHeight());
        try {
            if (isFromShareInvoice) {
                createPdfFileAndShare(orderId);
            } else {
                createPdfFileAndSave(orderId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void castingViews() {
        webView = findViewById(R.id.invoiceWebview);
        downloadInvoiceButton = findViewById(R.id.downloadInvoiceBtn);
        shareInvoiceButton = findViewById(R.id.shareInvoiceButton);
        invoiceRootLayout = findViewById(R.id.invoiceRootLayout);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setWebview(OrderInfo orderInfo) {
        webView.setInitialScale(0);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        createWebview(orderInfo);
    }

    private String getTotalQuantities(OrderInfo orderInfo) {
        int quantity = 0;
        for (int i = 0; i < orderInfo.getItemsInfo().size(); i++) {
            quantity = quantity + Integer.valueOf(orderInfo.getItemsInfo().get(i).getQuantity());
        }
        return String.valueOf(quantity);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createWebview(OrderInfo orderInfo) {

        String htmlContentString = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                " </meta>" +
                "    <title>Vedas JobCard</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"font-family:Arial, Helvetica,sans-serif; table-layout: fixed; margin: 0 auto; max-width: 700px\">\n" +
                "    <tr>\n" +
                "        <td>\n" +
                "            <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"font-family:Arial, Helvetica,sans-serif; table-layout: fixed; margin: 0 auto; max-width: 700px\">\n" +
                "                <td rowspan=\"2\" style=\"width: 30%;border: 1px solid black; border-right: none\">\n" +
                "                    <img src=\"file:///android_asset/vedas.jpg\" align=\"left\" style=\"display:block;margin-left: 10px\"/>\n" +
                "                </td>\n" +
                "                <td style=\"padding-bottom: 0;border: 1px solid black;border-left: none\">\n" +
                "                    <h2 style=\"text-align: center;margin-top: 12px;margin-right: 90px\">Vedas Labs</h2>\n" +
                "                    <p style=\"text-align: left;padding-top: 0\">Flat No 404,Sairadha Residency,Vijaya Nagar Colony,<br/>Anantapur-515001,Andhra Pradesh,Ph:+91-8106522578</p>\n" +
                "                </td>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td style=\"margin-top: 10px\">\n" +
                "            <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"font-family:Arial, Helvetica,sans-serif; table-layout: fixed; margin: 0 auto; max-width: 700px;margin-top: 15px;\">\n" +
                "                <tr>\n" +
                "                    <td style=\"border: 1px solid black;padding: 10px;border-right: none;background-color: lightgray\">GST NO: 37AFFPR6516N2ZT</td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 10px;border-left: none;background-color: lightgray\">INVOICE NO: VLIN #InvoiceNumber# </td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td style=\"margin-top: 10px\">\n" +
                "            <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"font-family:Arial, Helvetica,sans-serif; table-layout: fixed; margin: 0 auto; max-width: 700px;margin-top: 15px;\">\n" +
                "                <tr>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;background-color: lightgray;border-bottom: none\">Order Number: #OrderNumber# </td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;background-color: lightgray;border-bottom: none\">Customer Name: #customerName# </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;background-color: lightgray;border-bottom: none;border-top: none\">Order Date: #orderDate#</td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;background-color: lightgray;border-bottom: none;border-top: none\">Mobile Number: #mobileNumber#</td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;background-color: lightgray;border-bottom: none;border-top: none\">#DeliveryDate#: #deliveryDate#</td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;background-color: lightgray;border-bottom: none;border-top: none\">Delivery Mode: #deliveryMode#</td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;background-color: lightgray;border-bottom: none;border-top: none\">#DeliveryTime#: #deliveryTime#</td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;background-color: lightgray;border-bottom: none;border-top: none\">#Address#: #deliveryAddress#</td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;background-color: lightgray;border-top: none;border-bottom: none\">Payment Mode: #paymentMode#</td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;background-color: lightgray;border-top: none;border-bottom: none\"></td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;background-color: lightgray;border-top: none\">Total Items: #totalItems#\n" +
                "\t\t\t\t\t<span style=\"margin-left: 50px\">Total Quantity: #totalQuantity</span></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;background-color: lightgray;border-top: none\"></td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>\n" +
                "            <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"font-family:Arial, Helvetica,sans-serif; table-layout: fixed; margin: 0 auto; max-width: 700px;margin-top: 15px;\">\n" +
                "                <thead>\n" +
                "                <tr>\n" +
                "                    <th style=\"border: 1px solid black;padding: 6px;background-color: lightgray;width: 10%;text-align: center\">S.No </th>\n" +
                "                    <th style=\"border: 1px solid black;padding: 6px;background-color: lightgray;width: 40%;text-align: center\">ITEMS PURCHASED </th>\n" +
                "                    <th style=\"border: 1px solid black;padding: 6px;background-color: lightgray;width: 12%;text-align: center\">QTY </th>\n" +
                "                    <th style=\"border: 1px solid black;padding: 6px;background-color: lightgray;width: 17%;text-align: center\">PRICE </th>\n" +
                "                    <th style=\"border: 1px solid black;padding: 6px;background-color: lightgray;width: 21%;text-align: center\">TOTAL AMOUNT </th>\n" +
                "                </tr>\n" +
                "                </thead> \n" +
                "                <tbody> \n " +
                "                 #PurchasedItems# \n " +
                "                 <tr>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 10%;text-align: center\"></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 40%;text-align: center\">Delivery Charges</td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 12%;text-align: center\"></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 17%;text-align: center\"></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 21%;text-align: center\">#DeliveryCharges#</td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 10%;text-align: center\"></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 40%;text-align: center\">CGST</td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 12%;text-align: center\"></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 17%;text-align: center\"></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 21%;text-align: center\">#Cgst#</td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 10%;text-align: center\"></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 40%;text-align: center\">SGST</td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 12%;text-align: center\"></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 17%;text-align: center\"></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 21%;text-align: center\">#Sgst#</td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 10%;text-align: center\"></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 40%;text-align: center\">AMOUNT PAYABLE<br><span style=\"font-size: small\">(Include of all charges)</span></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 12%;text-align: center\"></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 17%;text-align: center\"></td>\n" +
                "                    <td style=\"border: 1px solid black;padding: 6px;width: 21%;text-align: center\">#AmountPayable#</td>\n" +
                "                </tr> \n " +
                "                </tbody> \n" +
                "                 </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "</body>\n" +
                "</html>";
        String mainChapterNumber = orderInfo.getTotalCost().split("\\.", 2)[0];
        Log.e("d1", "" + mainChapterNumber);


        String delivertytype = orderInfo.getDeliveryType();
        htmlContentString = htmlContentString.replace("#OrderNumber#", orderInfo.getOrderId())
                .replace("#InvoiceNumber#", orderInfo.getOrderId().replace("Order_", ""))
                .replace("#orderDate#", getDateFromTimeStamp(orderInfo.getTimeStamp()))
                .replace("#mobileNumber#", orderInfo.getUserId())
                .replace("#deliveryDate#", orderInfo.getDeliveryInfo().getDeliveryDate())
                .replace("#deliveryMode#", delivertytype)
                .replace("#deliveryTime#", orderInfo.getDeliveryInfo().getDeliveryTime())
                .replace("#deliveryAddress#", orderInfo.getDeliveryInfo().getAddress())
                .replace("#paymentMode#", orderInfo.getPaymentType())
                .replace("#totalItems#", String.valueOf(orderInfo.getItemsInfo().size()))
                .replace("#totalQuantity", getTotalQuantities(orderInfo))
                .replace("#AmountPayable#", "\u20B9" + orderInfo.getTotalCost())
                .replace("#DeliveryCharges#", "\u20B9" + orderInfo.getDeliveryCharges())
                .replace("#Cgst#", "0")
                .replace("#Sgst#", "0");

        if (orderInfo.getDeliveryType().equalsIgnoreCase("Self Pick Up")) {
            htmlContentString = htmlContentString.replace("#DeliveryDate#", "Pick Up Date")
                    .replace("#customerName#", orderInfo.getUserId())
                    .replace("#DeliveryTime#", "Pick Up Time")
                    .replace("#Address#", "Pick Up Address");
        } else {
            htmlContentString = htmlContentString.replace("#DeliveryDate#", "Delivery Date")
                    .replace("#customerName#", orderInfo.getDeliveryInfo().getName())
                    .replace("#DeliveryTime#", "Delivery Time")
                    .replace("#Address#", "Delivery Address");
        }
        htmlContentString = createItemsListInHTML(orderInfo, htmlContentString);
        webView.loadDataWithBaseURL("file:///android_asset/", htmlContentString, "text/html", "utf-8", "");
    }

    private String createItemsListInHTML(OrderInfo orderInfo, String htmlContentString) {
        StringBuilder purchasedItemsStringBuilder = new StringBuilder();
        int serialNumber = 1;
        for (ItemsInfo itemsInfo : orderInfo.getItemsInfo()) {
            String htmlItemsList = "<tr>\t\n" +
                    "                    <td style=\"border: 1px solid black;padding: 6px;width: 10%;text-align: center\"> #SNO# </td>\n" +
                    "                    <td style=\"border: 1px solid black;padding: 6px;width: 40%;text-align: center\"> #ItemsPurchased# </td>\n" +
                    "                    <td style=\"border: 1px solid black;padding: 6px;width: 12%;text-align: center\"> #Quantity# </td>\n" +
                    "                    <td style=\"border: 1px solid black;padding: 6px;width: 17%;text-align: center\"> #Price# </td>\n" +
                    "                    <td style=\"border: 1px solid black;padding: 6px;width: 21%;text-align: center\"> #TotalAmount# </td>\n" +
                    "                </tr>";
            double d = Double.parseDouble(itemsInfo.getPrice()) / Integer.valueOf(itemsInfo.getQuantity());
            String itemPrice = String.valueOf(d);
            htmlItemsList = htmlItemsList.replace("#SNO#", String.valueOf(serialNumber))
                    .replace("#ItemsPurchased#", itemsInfo.getItemName())
                    .replace("#Quantity#", itemsInfo.getQuantity())
                    .replace("#Price#", itemPrice)
                    .replace("#TotalAmount#", itemsInfo.getPrice() + ".0");
            purchasedItemsStringBuilder.append(htmlItemsList);
            serialNumber++;
        }
        return htmlContentString = htmlContentString.replace("#PurchasedItems#", purchasedItemsStringBuilder);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdfFileAndSave(String orderId) throws IOException {

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;
        int convertHeight = (int) height, convertWidth = (int) width;
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 150, null);
        document.finishPage(page);

        String invoicesFolderString = Environment.getExternalStorageDirectory() + "/V-Mart/Invoices";
        File invoicesFolder = new File(invoicesFolderString);
        if (!invoicesFolder.exists()) {
            File invoicesDirectory = new File(invoicesFolderString);
            invoicesDirectory.mkdirs();
        }
        String PdfFileName = "Invoice_" + orderId + ".pdf";
        File pdfFile = new File(invoicesFolderString, PdfFileName);
        // write the document content
        document.writeTo(new FileOutputStream(pdfFile));
        Toast.makeText(this, "Invoice Saved to " + pdfFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        document.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdfFileAndShare(String orderId) throws IOException {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;
        int convertHeight = (int) height, convertWidth = (int) width;
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 150, null);
        document.finishPage(page);
        String pdfFileName = "Invoice_" + orderId;
        File pdfStorageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File pdfFile = File.createTempFile(pdfFileName, ".pdf", pdfStorageDir);
        String pdfFilePath = pdfFile.getAbsolutePath();
        document.writeTo(new FileOutputStream(pdfFile));
        document.close();

        File pdfFile1 = null;
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        if (pdfFilePath != null) {
            pdfFile = new File(pdfFilePath);
            if (pdfFile.exists()) {
                Uri pdfUri = FileProvider.getUriForFile(InvoiceDisplayActivity.this,
                        "com.vmart.fileprovider", pdfFile);
                intentShareFile.setType("application/pdf");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, pdfUri);
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "V-Mart Order Invoice File");
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Please Find your invoice in the Attachment");
                startActivity(Intent.createChooser(intentShareFile, "Share Invoice via"));
            }
        }
    }

    public Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private void requestReadExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Info")
                    .setMessage("This Permission is Required For Sharing Your Invoice")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(InvoiceDisplayActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        }
    }

    private void requestWriteExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Info")
                    .setMessage("This Permission is needed for Saving your Invoice To Local Storage")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(InvoiceDisplayActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(InvoiceDisplayActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION_CODE);
        }
    }

    private String getDateFromTimeStamp(String timeStamp) {
        long itemLong = Long.parseLong(timeStamp) * 1000L;
        Log.e("timeinLong", " " + itemLong);
        Date orderedDate = new Date(itemLong);
        return new SimpleDateFormat("dd-MMM-yyyy", new Locale("en", "US")).format(orderedDate);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
