package com.heyjude.androidapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.activity.AddCardActivity;
import com.heyjude.androidapp.constant.ChatConstants;
import com.heyjude.androidapp.customview.PinnedSectionListView;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.GPSTracker;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.utility.Util;
import com.heyjude.androidapp.xmpp.ChatMessage;
import com.squareup.picasso.Picasso;
import com.usebutton.sdk.ButtonDropin;
import com.usebutton.sdk.PlacementContext;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class ChatAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

    private static final String TAG = "CHATADAPTER";
    private LayoutInflater li;
    private Activity activity;
    private ArrayList<ChatMessage> listChatMessage;
    private String amount;
    Bundle bundle;
    private boolean isFromHistory = false;
    private SharedPreferences sharedPreferences;
    private LoginData loginData;
    private String judeId;

    public ChatAdapter(Activity activity, ArrayList<ChatMessage> listChatMessage,
                       String userId, Bundle savedInstanceState,
                       boolean isFromHistory,
                       String judeId) {

        this.activity = activity;
        this.listChatMessage = listChatMessage;
        this.bundle = savedInstanceState;
        this.isFromHistory = isFromHistory;
        this.judeId = judeId;

        li = LayoutInflater.from(activity);

        Gson gson = new Gson();
        sharedPreferences = activity.getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        com.usebutton.sdk.Button.getButton(activity).handleIntent(activity.getIntent());
    }

    public ChatAdapter(Activity activity, ArrayList<ChatMessage> listChatMessage) {

        this.activity = activity;
        li = LayoutInflater.from(activity);
        this.listChatMessage = listChatMessage;
    }

//    public void addItems(ArrayList<ChatMessage> list) {
//        if (listChatMessage == null)
//            listChatMessage = new ArrayList<ChatMessage>();
//
//        listChatMessage.addAll(list);
//        notifyDataSetChanged();
//    }


    public void addItems(ChatMessage chat) {

        if (listChatMessage == null)
            listChatMessage = new ArrayList<>();


        /*if (listChatMessage.size() == 0 || (listChatMessage.size() > 0 && !isEqualDate(listChatMessage.get(listChatMessage.size() - 1).showdate, chat.showdate.toString()))) {*/

        /*if (listChatMessage.size() == 0 || (listChatMessage.size() > 0 && !listChatMessage.get(listChatMessage.size() - 1).
                showdate.subSequence(0, 10).toString().equalsIgnoreCase(chat.showdate.subSequence(0, 10).toString()))) {
            ChatMessage chatDate = new ChatMessage();
            chatDate.showdate = ChatConstants.getCurrentUTCDate();
            listChatMessage.add(chatDate);
        }*/

        listChatMessage.add(chat);

        /**
         * First sort the arraylist according to timestamp
         */
        //Collections.sort(listChatMessage, new sortBasedOnDetails());


        //Now Notify
        notifyDataSetChanged();

        //return listChatMessage.size() - 1;

    }

    private class sortBasedOnDetails implements Comparator<ChatMessage> {

        @Override
        public int compare(ChatMessage chatMessage, ChatMessage t1) {
            return chatMessage.newtimestamp.compareTo(t1.newtimestamp);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }

    public void clear() {

    }

    @Override
    public int getCount() {
        return listChatMessage.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return listChatMessage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chat = getItem(position);
        if (!TextUtils.isEmpty(chat.flag)) {
            if (chat.flag.equalsIgnoreCase(String.valueOf(ChatConstants.CHAT_TYPE_ME_CHAT))) {
                // My Chat (TEXT)
                return ChatConstants.CHAT_TYPE_ME_CHAT;
            } else if (chat.flag.equalsIgnoreCase(String.valueOf(ChatConstants.CHAT_TYPE_TEXT_JUDE))) {
                //Jude Chat (TEXT)
                return ChatConstants.CHAT_TYPE_TEXT_JUDE;
            } else if (chat.flag.equalsIgnoreCase(String.valueOf(ChatConstants.CHAT_TYPE_PAYMENT))) {
                //Jude Chat (PAYMENT)
                return ChatConstants.CHAT_TYPE_PAYMENT;
            } else if (chat.flag.equalsIgnoreCase(String.valueOf(ChatConstants.CHAT_TYPE_MAP))) {
                //Jude Chat (MAP)
                return ChatConstants.CHAT_TYPE_MAP;
            } else if (chat.flag.equalsIgnoreCase(String.valueOf(ChatConstants.CHAT_TYPE_DEALS))) {
                return ChatConstants.CHAT_TYPE_DEALS;
            }
        }
        return ChatConstants.CHAT_TYPE_DATE;
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        int viewType = getItemViewType(position);

        if (convertView == null) {

            holder = new ViewHolder();

            switch (viewType) {

                case ChatConstants.CHAT_TYPE_DATE:
                    convertView = li.inflate(R.layout.row_chat_header, parent, false);
                    holder.txtDate = (TextView) convertView.findViewById(R.id.text_header_date);
                    break;

                case ChatConstants.CHAT_TYPE_ME_CHAT:
                    convertView = li.inflate(R.layout.row_chat_by_me, parent, false);
                    holder.txtComment = (TextView) convertView.findViewById(R.id.tvSentMsg);
                    holder.imgSender = (ImageView) convertView.findViewById(R.id.imgSender);
                    holder.tvDateMe = (TextView) convertView.findViewById(R.id.tvDateMe);
                    break;

                case ChatConstants.CHAT_TYPE_TEXT_JUDE:
                    convertView = li.inflate(R.layout.row_chat_by_jude, parent, false);
                    holder.txtComment = (TextView) convertView.findViewById(R.id.tvReceiveMsg);
                    holder.tvDateJude = (TextView) convertView.findViewById(R.id.tvDateJude);
                    break;

                case ChatConstants.CHAT_TYPE_PAYMENT:
                    convertView = li.inflate(R.layout.row_chat_payment, parent, false);
                    holder.tvPayment = (TextView) convertView.findViewById(R.id.btnPayment);

                    if (isFromHistory) {
                        holder.tvPayment.setEnabled(false);
                    } else {
                        holder.tvPayment.setEnabled(true);
                    }

                    break;

                case ChatConstants.CHAT_TYPE_MAP:
                    convertView = li.inflate(R.layout.row_chat_map, parent, false);
                    holder.imgMap = (ImageView) convertView.findViewById(R.id.imgMap);
                    holder.tvUber = (ButtonDropin) convertView.findViewById(R.id.tvUber);
                    holder.pbMap = (ProgressBar) convertView.findViewById(R.id.pbMap);
                    break;

                case ChatConstants.CHAT_TYPE_DEALS:
                    convertView = li.inflate(R.layout.row_vendor_list, parent, false);
                    holder.viewPager = (ViewPager) convertView.findViewById(R.id.vendorpager);
                    holder.viewPager.setOffscreenPageLimit(3);
                    holder.circlePageIndicator = (CirclePageIndicator) convertView.findViewById(R.id.indicator);
                    break;

                case ChatConstants.CHAT_TYPE_ME_RATING:
                    convertView = li.inflate(R.layout.row_chat_vendor_rating, parent, false);
                    holder.tvRating = (TextView) convertView.findViewById(R.id.btnPayment);
                    if (isFromHistory) {
                        holder.tvPayment.setEnabled(false);
                    } else {
                        holder.tvPayment.setEnabled(true);
                    }
                    break;

                default:
                    break;
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        bindValues(getItem(position), holder, position);

        return convertView;
    }


    static class ViewHolder {
        TextView txtComment, txtDate; /*tvUber*/

        TextView tvDateMe, tvDateJude, tvPayment, tvRating;

        ButtonDropin tvUber;
        ImageView imgSender, imgMap;
        ViewPager viewPager;
        ProgressBar pbMap;


        CirclePageIndicator circlePageIndicator;
    }

    private void bindValues(final ChatMessage chat, final ViewHolder holder, final int position) {

        int viewType = getItemViewType(position);

        if (viewType == ChatConstants.CHAT_TYPE_DATE) {
            //holder.txtDate.setText(ChatConstants.getFormatedDate(Utility.getDate(chat.msg_date, true, true), ConstantCode.DATE_FORMAT_CHAT_LISTING_HEADER, false));
            String dataTime = ChatConstants.CommonDateConvertor(chat.showutcdate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd hh:mm a");
            String[] parts = dataTime.trim().split(" ");
            String part1 = parts[0];
            String part2 = parts[1];
            holder.txtDate.setText(part1);
            //updateStatusIcon(chat.status, holder);


        }
        // JUDE TYPE CHAT
        else if (viewType == ChatConstants.CHAT_TYPE_TEXT_JUDE) {
            Calendar cal = ChatConstants.getDate(ChatConstants.CommonDateConvertor(chat.showutcdate, "yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd kk:mm:ss"), true, true);
            String dataTime = ChatConstants.getFormatedDate(cal, "dd-MMM-yyyy hh:mm a", false);
            String[] parts = dataTime.trim().split(" ");

            holder.txtComment.setText(chat.chatmsg);
            holder.tvDateJude.setText(parts[0] + " " + parts[1] + " " + parts[2]);

        }
        // ME TYPE CHAT
        else if (viewType == ChatConstants.CHAT_TYPE_ME_CHAT) {

            Calendar cal = ChatConstants.getDate(chat.showutcdate, true, true);
            String dataTime = ChatConstants.getFormatedDate(cal, "dd-MMM-yyyy hh:mm a", false);
            String[] parts = dataTime.trim().split(" ");

            //Picasso.with(activity).load(user.getImageProfile()).placeholder(R.drawable.ic_no_profile_pic).into(holder.imgSender);

            if (!loginData.getData().getImageProfile().isEmpty())
                Picasso.with(activity).load(loginData.getData().getImageProfile()).placeholder(R.drawable.ic_no_profile_pic).into(holder.imgSender);
            else
                Picasso.with(activity).load(R.drawable.ic_no_profile_pic).fit().centerCrop().placeholder(R.drawable.ic_no_profile_pic).error(R.drawable.ic_no_profile_pic).into(holder.imgSender);

            holder.txtComment.setText(chat.chatmsg);
            holder.tvDateMe.setText(parts[0] + " " + parts[1] + " " + parts[2]);
            //holder.tvDateMe.setText("TEMPDATE");

        } else if (viewType == ChatConstants.CHAT_TYPE_PAYMENT) {

            holder.tvPayment.setText("Make Payment of R" + chat.amt);
            amount = chat.amt;


            holder.tvPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, AddCardActivity.class);
                    intent.putExtra("AMOUNT", amount);
                    activity.startActivity(intent);
                }
            });

        } else if (viewType == ChatConstants.CHAT_TYPE_ME_RATING) {
            holder.tvRating.setText(chat.chatmsg);

            holder.tvRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*VendorRatingDialog vendorRating = new VendorRatingDialog(activity, chat.amt, chat.chatmsg, chat.taskid);
                    vendorRating.show();*/
                }
            });

        } else if (viewType == ChatConstants.CHAT_TYPE_MAP) {

            GPSTracker gpsTracker = new GPSTracker(activity);

            if (gpsTracker.getIsGPSTrackingEnabled(activity)) {

                Util.getCurrentLocation(activity);

                holder.pbMap.setVisibility(View.VISIBLE);

                Picasso.with(activity).load(chat.imageurl).placeholder(R.drawable.ic_restaurant).into(holder.imgMap);
                holder.pbMap.setVisibility(View.GONE);
                //holder.imgMap.setOnClickListener(new OnItemClickListener(position, chat.lat, chat.lon, "0", "MAP"));

                holder.imgMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    /*String urlAddress = "http://maps.google.com/maps?q=" + Double.parseDouble(chat.lat) + "," + Double.parseDouble(chat.lon) + "(" + "Location" + ")&iwloc=A&hl=es";
                    Uri gmmIntentUri = Uri.parse(urlAddress);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    activity.startActivity(mapIntent);*/

                        String url = "http://maps.google.com/maps?saddr=" + Util.latitude + "," + Util.longitude + "&daddr=" + Double.parseDouble(chat.lat) + "," + Double.parseDouble(chat.lon) + "&mode=driving";
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        activity.startActivity(intent);

                    }
                });

           /* Location locationA = new Location("point A");

            locationA.setLatitude(Double.parseDouble(Util.latitude));
            locationA.setLongitude(Double.parseDouble(Util.longitude));

            Location locationB = new Location("point B");

            locationB.setLatitude(Double.parseDouble(chat.lat));
            locationB.setLongitude(Double.parseDouble(chat.lon));

            float distance = locationA.distanceTo(locationB);

            Log.e("Distance", "" + distance);
*/

                // Create a PlacementContext for the location you want a ride to.
                final PlacementContext context = PlacementContext
                        .forStartLocation("My Location", Double.parseDouble(Util.latitude), Double.parseDouble(Util.longitude))
                        .withEndLocation(chat.destination, Double.parseDouble(chat.lat), Double.parseDouble(chat.lon));

                // Prepare the Button for display with our context
                holder.tvUber.prepareForDisplayWithContext(context, new ButtonDropin.Listener() {
                    @Override
                    public void onPrepared(final boolean willDisplay) {

                        // Toggle visibility of UI items here if necessary
                        Log.e("ButtonSDK", ":" + willDisplay);

                        if (!willDisplay) {
                            Toast.makeText(activity, "Please select Location within 50 Miles", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }


        } else if (viewType == ChatConstants.CHAT_TYPE_DEALS) {

            Log.e(TAG, "Inside type deals " + position);

           /* pagerAdapter = new ViewPagerIndicator(activity, vendor_id, vendor_title, vendor_content, vendor_star, vendor_address,
                    vendor_comments, vendor_distance, lat, lon, mobile);*/


//            pagerAdapter = new ViewPagerIndicator(activity, chat.venodr_list);


            if (chat.venodr_list != null && chat.venodr_list.size() > 0) {
                holder.viewPager.setAdapter(new ViewPagerIndicator(bundle, activity, chat.venodr_list, chat.taskid, isFromHistory,judeId));

                holder.viewPager.setClipToPadding(false);
                int leftMarginInPIX = (int) Util.convertDpToPixel(15.0f, activity);
                holder.viewPager.setPadding(leftMarginInPIX, 0, leftMarginInPIX, 0);
                holder.viewPager.setPageMargin((int) Util.convertDpToPixel(10.0f, activity));
                holder.viewPager.setCurrentItem(1);

//                holder.viewPager.setAdapter(new TestFragmentAdapter(frgamentManager, chat.venodr_list));
//                holder.viewPager.storeAdapter(new TestFragmentAdapter(frgamentManager, chat.venodr_list));
//                holder.viewPager.setPagerIndicator(holder.circlePageIndicator);
                holder.circlePageIndicator.setViewPager(holder.viewPager);
            }

        }
    }


    /*private void updateStatusIcon(String status, ViewHolder holder) {
        if (!TextUtils.isEmpty(status)) {
            if (status.equalsIgnoreCase(ChatConstants.STATUS_TYPE_PROCESS) || status.equalsIgnoreCase(ChatConstants.STATUS_TYPE_PENDING) || status.equalsIgnoreCase(ChatConstants.STATUS_TYPE_DOWNLOAD_PROCESS)) {
                holder.imgMsgStatus.setBackgroundResource(R.drawable.ic_chat_pending);
            } else if (status.equalsIgnoreCase(ChatConstants.STATUS_TYPE_SENT)) {
                holder.imgMsgStatus.setBackgroundResource(R.drawable.ic_chat_send);
            } else if (status.equalsIgnoreCase(ChatConstants.STATUS_TYPE_DELIVER)) {
                holder.imgMsgStatus.setBackgroundResource(R.drawable.ic_chat_read);
                holder.imgMsgStatus.setBackgroundResource(R.drawable.ic_chat_send);
            } else {
                holder.imgMsgStatus.setBackgroundResource(R.drawable.ic_chat_pending);
            }
        }

    }*/

    public void setMessageComplete(int pos, String status) {
        if (listChatMessage.size() > pos) {
            listChatMessage.get(pos).status = status;
            notifyDataSetChanged();
        }
    }

    public void updateItem(ChatMessage chatMessage) {
        if (chatMessage != null) {
            int pos = 0;
            for (ChatMessage msg : listChatMessage) {
                //if (id !=null && id.equalsIgnoreCase(id)) {
                if (msg.msg_id != null && msg.msg_id.equalsIgnoreCase(chatMessage.msg_id)) {
                    listChatMessage.set(pos, chatMessage);
                    notifyDataSetChanged();
                    break;
                }
                pos++;
            }
        }
    }

    public void updateItem(String id, String status) {
        if (id != null) {
            for (ChatMessage msg : listChatMessage) {
                if (msg.msg_id != null && msg.msg_id.equalsIgnoreCase(id)) {
                    msg.status = status;
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == ChatConstants.CHAT_TYPE_DATE;
    }

    public boolean isEqualDate(String date1, String date2) {
        if (date2.length() > 9) {
            if (date1.trim().startsWith(date2.trim().substring(0, 10))) {
                return true;
            }
            return false;
        } else {
            if (date1.trim().equalsIgnoreCase(date2.trim()))
                return true;
            else
                return false;
        }
    }

}