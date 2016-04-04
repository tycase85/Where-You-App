package mobiledev.unb.ca.whereyouapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by rcase on 03/04/16.
 */
public class View_Holder extends RecyclerView.ViewHolder {

    CardView cv;
    TextView name;
    TextView option;
    TextView email;
    ImageView imageId;

    View_Holder(View itemView) {
        super(itemView);
        email = (TextView) itemView.findViewById(R.id.friendEmail);
    }
}