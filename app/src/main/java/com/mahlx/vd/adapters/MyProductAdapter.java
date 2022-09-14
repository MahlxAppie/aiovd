package com.infusiblecoder.allinonevideodownloader.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.activities.SplashScreen;
import com.infusiblecoder.allinonevideodownloader.interfaces.IRecyclerClickListener;

import java.util.List;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.MyViewHolder> {

    AppCompatActivity appCompatActivity;
    List<SkuDetails> skuDetailsList;
    BillingClient billingClient;

    public MyProductAdapter(AppCompatActivity appCompatActivity, List<SkuDetails> skuDetailsList, BillingClient billingClient) {
        this.appCompatActivity = appCompatActivity;
        this.skuDetailsList = skuDetailsList;
        this.billingClient = billingClient;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(appCompatActivity.getBaseContext())
                .inflate(R.layout.layout_product_display, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_product_name.setText(skuDetailsList.get(position).getTitle());
        holder.txt_description.setText(skuDetailsList.get(position).getDescription());
        holder.txt_price.setText(skuDetailsList.get(position).getPrice());

        //Product click
        holder.setListener((view, position1) -> {

            try {

                // Launch billing flow
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList.get(position1))
                        .build();
                int reponse = billingClient.launchBillingFlow(appCompatActivity, billingFlowParams)
                        .getResponseCode();
                switch (reponse) {
                    case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                        Toast.makeText(appCompatActivity, appCompatActivity.getResources().getString(R.string.billunavaliable) + "", Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                        Toast.makeText(appCompatActivity, appCompatActivity.getResources().getString(R.string.developererrer) + "", Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                        Toast.makeText(appCompatActivity, appCompatActivity.getResources().getString(R.string.featurenotsupp) + "", Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                        Toast.makeText(appCompatActivity, appCompatActivity.getResources().getString(R.string.alreadowned) + "", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor prefs = appCompatActivity.getSharedPreferences("whatsapp_pref",
                                Context.MODE_PRIVATE).edit();
                        prefs.putString("inappads", "ppp");
                        prefs.apply();


                        Intent intent = new Intent(appCompatActivity, SplashScreen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        appCompatActivity.startActivity(intent);
                        appCompatActivity.finish();

                        break;
                    case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                        Toast.makeText(appCompatActivity, appCompatActivity.getResources().getString(R.string.servicediss) + "", Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                        Toast.makeText(appCompatActivity, appCompatActivity.getResources().getString(R.string.servicetime) + "", Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                        Toast.makeText(appCompatActivity, appCompatActivity.getResources().getString(R.string.itemunaval) + "", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;

                }
            } catch (Exception ignored) {
            }
        });

    }

    @Override
    public int getItemCount() {
        try {
            return skuDetailsList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_product_name;
        TextView txt_price;
        TextView txt_description;
        IRecyclerClickListener listener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_description = itemView.findViewById(R.id.txt_description);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_price = itemView.findViewById(R.id.txt_price);
            itemView.setOnClickListener(this);
        }

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            listener.onClickRec(view, getAdapterPosition());

        }


    }
}
