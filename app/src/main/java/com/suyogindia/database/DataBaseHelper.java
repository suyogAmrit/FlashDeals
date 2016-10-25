package com.suyogindia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.suyogindia.model.CartItem;
import com.suyogindia.model.Deals;
import com.suyogindia.model.Seller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suyogcomputech on 19/10/16.
 */

public class DataBaseHelper {
    private static final String ID = "id";
    private static final String DEALID = "deal_id";
    private static final String DEALDESC = "deal_desc";
    private static final String SELLEREMAIL = "seller_email";
    private static final String SELLERNAME = "seller_name";
    private static final String MRP = "mrp";
    private static final String DISCOUNT = "disount";
    private static final String OFFERPRICE = "offer_price";
    private static final String QUANTITY = "quantity";
    private static final String TOTALPRICE = "total_price";
    private static final String MAXPRICE = "max_price";
    private static final String SHIPPINGCHARGE = "shipping_carge";
    private static final String MAXQTY = "max_quantity";
    private static final String CATEGORY = "deal_category";

    private static final String CARTTABLE = "cart";
    private static final String DATABASE = "flashdeals";
    private static final int VERSION = 1;

    private Context myContext;
    private SQLiteDatabase myDatabase;

    public DataBaseHelper(Context context) {
        myContext = context;
    }

    public void open() {
        DbHelper myDbHelper = new DbHelper(myContext);
        myDatabase = myDbHelper.getWritableDatabase();
    }

    public long addDeal(String qty, String totalPrice, Deals myDeals) {
        long row = 0;

        Cursor c = myDatabase.query(CARTTABLE, null, DEALID + "=?", new String[]{myDeals.getDeal_id()}, null, null, null);
        if (c.getCount() > 0) {
            c.close();
            row = 0;
        } else {
            ContentValues cv = new ContentValues();
            cv.put(DEALID, myDeals.getDeal_id());
            cv.put(DEALDESC, myDeals.getDesciption());
            cv.put(MRP, myDeals.getMrp());
            cv.put(DISCOUNT, myDeals.getDiscount());
            cv.put(OFFERPRICE, myDeals.getOffer_price());
            cv.put(QUANTITY, qty);
            cv.put(MAXPRICE, myDeals.getMax_price());
            cv.put(SHIPPINGCHARGE, myDeals.getShipping_charge());
            cv.put(SELLEREMAIL, myDeals.getSeller_email());
            cv.put(SELLERNAME, myDeals.getSeller_name());
            cv.put(TOTALPRICE, totalPrice);
            cv.put(MAXQTY, myDeals.getQuantity());
            cv.put(CATEGORY, myDeals.getCategory());

            Log.i("qty", cv.getAsString(MAXQTY));
            row = myDatabase.insert(CARTTABLE, null, cv);
        }

        return row;
    }

    public void close() {
        myDatabase.close();
    }

    public int getNumberOfDeals() {
        int count = 0;
        Cursor myCursor = myDatabase.query(CARTTABLE, null, null, null, null, null, null);
        if (myCursor.getCount() > 0) {
            count = myCursor.getCount();
            myCursor.close();
        }
        return count;
    }

    public long clearCart() {

        return myDatabase.delete(CARTTABLE, null, null);
    }

    public ArrayList<CartItem> getCartData() {
        List<Seller> sellerList = getSellersFromList();
        ArrayList<CartItem> cartItemList = new ArrayList<>();
        for (Seller s :
                sellerList) {
            String[] cartData = {DEALID, DEALDESC, MRP, OFFERPRICE, TOTALPRICE, DISCOUNT, QUANTITY, MAXQTY, SELLEREMAIL};
            Cursor mCursor = myDatabase.query(CARTTABLE, cartData, SELLEREMAIL + "=?", new String[]{s.getEmail()}, null, null, null);
            CartItem item1 = new CartItem(null, null, null, null, null, null, null, null, null);
            item1.setType(0);
            item1.setSellerName(s.getName());
            item1.setCategory(s.getCategory());

            cartItemList.add(item1);
            if (mCursor.getCount() > 0) {
                double sellerTotalPrice = 0;
                while (mCursor.moveToNext()) {
                    String dealId = mCursor.getString(mCursor.getColumnIndex(DEALID));
                    String desc = mCursor.getString(mCursor.getColumnIndex(DEALDESC));
                    String mrp = mCursor.getString(mCursor.getColumnIndex(MRP));
                    String offerPrice = mCursor.getString(mCursor.getColumnIndex(OFFERPRICE));
                    String totaPrice = mCursor.getString(mCursor.getColumnIndex(TOTALPRICE));
                    String discount = mCursor.getString(mCursor.getColumnIndex(DISCOUNT));
                    String qunatity = mCursor.getString(mCursor.getColumnIndex(QUANTITY));
                    String maxQty = mCursor.getString(mCursor.getColumnIndex(MAXQTY));
                    String sellerEmail = mCursor.getString(mCursor.getColumnIndex(SELLEREMAIL));
                    Log.i("toalitemPrice", totaPrice);
                    CartItem item2 = new CartItem(dealId, desc, mrp, offerPrice, qunatity, discount, totaPrice, maxQty, sellerEmail);
                    item2.setType(1);
                    sellerTotalPrice = sellerTotalPrice + Double.parseDouble(totaPrice);
                    cartItemList.add(item2);
                }
                mCursor.close();
                s.setTotalPrice(String.valueOf(sellerTotalPrice));
                CartItem item3 = new CartItem(null, null, null, null, null, null, null, null, null);
                item3.setSeller(s);
                item3.setType(2);
                cartItemList.add(item3);
            }
        }
        return cartItemList;
    }

    private List<Seller> getSellersFromList() {
        List<Seller> sellerList = null;
        String[] sellerData = {SELLEREMAIL, SELLERNAME, MAXPRICE, SHIPPINGCHARGE,CATEGORY};
        Cursor cursor = myDatabase.query(CARTTABLE, sellerData, null, null, SELLEREMAIL, null, null, null);
        if (cursor.getCount() > 0) {
            sellerList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String email = cursor.getString(cursor.getColumnIndex(SELLEREMAIL));
                String name = cursor.getString(cursor.getColumnIndex(SELLERNAME));
                String marPrice = cursor.getString(cursor.getColumnIndex(MAXPRICE));
                String shippingPrice = cursor.getString(cursor.getColumnIndex(SHIPPINGCHARGE));
                String category = cursor.getString(cursor.getColumnIndex(CATEGORY));
                Seller mySeller = new Seller(email, name, marPrice, shippingPrice,category);
                sellerList.add(mySeller);
            }
            cursor.close();
            Log.i("sellers", sellerList.size() + "");

        }
        return sellerList;
    }

    public long removeFromCart(String delaId) {

        return myDatabase.delete(CARTTABLE, DEALID + "=?", new String[]{delaId});
    }

    public long updateDeal(String qty, String totalPrice, String dealId) {
        long row = 0;
        ContentValues cv = new ContentValues();
        cv.put(QUANTITY, qty);
        cv.put(TOTALPRICE, totalPrice);
        row = myDatabase.update(CARTTABLE, cv, DEALID + "=?", new String[]{dealId});
        return row;
    }

    private class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE " + CARTTABLE + "( " + ID + " PRIMARY KEY," + DEALID + " TEXT," + DEALDESC + " TEXT," + CATEGORY + " TEXT," +
                    SELLEREMAIL + " TEXT," + MRP + " TEXT," + DISCOUNT + " TEXT," + OFFERPRICE + " TEXT," + SELLERNAME + " TEXT,"
                    + QUANTITY + " TEXT," + MAXQTY + " TEXT," + MAXPRICE + " TEXT," + SHIPPINGCHARGE + " TEXT," + TOTALPRICE + " TEXT);";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String sql = "DROP TABLE " + CARTTABLE + " IF EXIST;";
            db.execSQL(sql);
            onCreate(db);
        }
    }

}
