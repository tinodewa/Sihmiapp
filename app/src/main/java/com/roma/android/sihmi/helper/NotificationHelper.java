package com.roma.android.sihmi.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.messaging.RemoteMessage;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Agenda;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.GroupChat;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.entity.notification.Message;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.AgendaSingleResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.AgendaDetailActivity;
import com.roma.android.sihmi.view.activity.ChatActivity;
import com.roma.android.sihmi.view.activity.ChatGroupActivity;
import com.roma.android.sihmi.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class NotificationHelper {
    private static final HashMap<String, List<Message>> MESSAGE = new HashMap<>();
    private static final List<Integer> UNUSED_MESSAGE_RC = new ArrayList<>();
    private static final List<Integer> USED_MESSAGE_RC = new ArrayList<>();
    private static final HashMap<String, Integer> MESSAGE_RC = new HashMap<>();
    private static final String CHANNEL_ID = "com.example.root.sihmi";
    public static final int ID_CHAT = 1;
    private static final int ID_GENERAL = 2;
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";
    private static final String TYPE_MESSAGE = "__type_message__";
    public static final String TYPE_AGENDA = "__type_agenda__";

    public static void sendNotification(RemoteMessage remoteMessage, Contact contact, Context context) {
        String body = remoteMessage.getData().get("body");
        String groupChat = remoteMessage.getData().get("groupChat");
        String type = remoteMessage.getData().get("type");
        String groupName = null;
        GroupChat gChat = null;

        if (groupChat != null && groupChat.equals("GROUP_CHAT")) {
            groupName = remoteMessage.getData().get("sented");
            gChat = CoreApplication.get().getConstant().getGroupChatDao().getGroupChatByName(groupName);
        }

        if (type != null) {
            if (type.equals(TYPE_MESSAGE)) {
                if ((groupName == null && !contact.isBisukan()) || (groupName != null && !gChat.isBisukan())) {
                    Message message = new Message(contact.getNama_panggilan(), (body != null) ? Tools.convertUTF8ToString(body) : "", System.currentTimeMillis());
                    if (groupName != null) {
                        message.setGroupName(groupName);
                    }
                    sendNotificationMessaging(message, context, contact, groupName);
                }
            }
            else if (type.equals(TYPE_AGENDA)) {
                if (body == null) return;
                String[] bodySplit = body.split(";");
                String agendaId = bodySplit[0];
                String agendaType = bodySplit[1];
                String agendaName = bodySplit[2];
                long agendaExpire = Long.parseLong(bodySplit[3]);
                String agendaLocation = bodySplit[4];

                User me = CoreApplication.get().getConstant().getUserDao().getUser();

                String notifTitle = context.getString(R.string.new_agenda);
                String notifContent = agendaName + " " + context.getString(R.string.pada_tanggal) + " " + Tools.getDateTimeLaporanFromMillis(agendaExpire) + " " + context.getString(R.string.di_agenda) + " " + agendaLocation;

                if (Tools.isSuperAdmin() || agendaType.equalsIgnoreCase("PB HMI") || me.getKomisariat().equalsIgnoreCase(agendaType) || me.getCabang().equalsIgnoreCase(agendaType)) {
                    getAgendaById(agendaId, notifTitle, notifContent, context, 3);
                }
            }
        }
    }

    private static void getAgendaById(String agendaId, String notifTitle, String notifContent, Context context, int retry) {
        MasterService service = ApiClient.getInstance().getApi();
        AppDb appDb = CoreApplication.get().getConstant().getAppDb();

        Call<AgendaSingleResponse> call = service.getAgenda(Constant.getToken(), "0", agendaId);

        call.enqueue(new Callback<AgendaSingleResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<AgendaSingleResponse> call, Response<AgendaSingleResponse> response) {
                if (response.isSuccessful()) {
                    AgendaSingleResponse body = response.body();
                    if (body != null && body.getStatus().equalsIgnoreCase("ok")) {
                        Agenda agenda = body.getData();

                        if (agenda != null) {
                            appDb.agendaDao().insertAgenda(agenda);

                            Intent intent = new Intent(context, AgendaDetailActivity.class)
                                    .putExtra(AgendaDetailActivity.ID_AGENDA, agenda.get_id());
                            sendNotificationGeneral(notifTitle, notifContent, context, TYPE_AGENDA, intent);

                            AgendaScheduler.setupUpcomingAgendaNotifier(context);
                        }
                    }
                    else {
                        // failed
                        if (retry > 1) getAgendaById(agendaId, notifTitle, notifContent, context, retry-1);
                    }
                }
                else {
                    // failed
                    if (retry > 1) getAgendaById(agendaId, notifTitle, notifContent, context, retry-1);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<AgendaSingleResponse> call, Throwable t) {
                // failed
                if (retry > 1) getAgendaById(agendaId, notifTitle, notifContent, context, retry-1);
            }
        });
    }

    private static void sendNotificationMessaging(Message message, Context context, Contact contact, String groupName) {
        int SUMMARY_ID = 0;
        boolean isGroup = groupName != null;
        String key;
        if (isGroup) {
            key = groupName+"_"+ID_CHAT;
        }
        else {
            key = contact.get_id()+"_"+ID_CHAT;
        }

        List<Message> messages = MESSAGE.get(key);

        if (messages == null) {
            messages = new ArrayList<>();
        }

        Integer messageRequestCode = MESSAGE_RC.get(key);
        if (messageRequestCode == null) {
            if (UNUSED_MESSAGE_RC.size() > 0) {
                messageRequestCode = UNUSED_MESSAGE_RC.get(0);
            }
            else {
                messageRequestCode = USED_MESSAGE_RC.size();
            }
            USED_MESSAGE_RC.add(messageRequestCode);
            Collections.sort(USED_MESSAGE_RC);
        }

        Intent intent;
        if (isGroup) {
            intent = new Intent(context, ChatGroupActivity.class)
                    .putExtra(ChatGroupActivity.NAMA_GROUP, groupName);
        }
        else {
            intent = new Intent(context, ChatActivity.class)
                    .putExtra("nama", contact.getFullName()).putExtra("iduser", contact.get_id());
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, messageRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        messages.add(message);
        MESSAGE.put(key, messages);

        Notification notification;

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("REMOVE MESSAGE", "REMOVE MESSAGE " + key);
                removeHistoryMessage(key);
                Integer messageRc = MESSAGE_RC.get(key);
                if (messageRc != null) {
                    MESSAGE_RC.remove(key);
                    USED_MESSAGE_RC.remove((int) messageRc);
                    UNUSED_MESSAGE_RC.add(messageRc);

                    Collections.sort(USED_MESSAGE_RC);
                    Collections.sort(UNUSED_MESSAGE_RC);
                }
                context.unregisterReceiver(this);
            }
        };

        Intent cancelIntent = new Intent(NOTIFICATION_DELETED_ACTION);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        context.registerReceiver(receiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));

        String GROUP_NOTIFICATION_MESSAGE = "com.roma.android.sihmi.MESSAGE";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("NOTIFICATION HELPER", "NOTIFICATION HELPER oreo");
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "SIHMI", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification.MessagingStyle messagingStyle = new Notification.MessagingStyle(context.getString(R.string.you));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                messagingStyle.setGroupConversation(isGroup);
                messagingStyle.setConversationTitle(groupName);
            }

            for (Message m : messages) {
                Notification.MessagingStyle.Message mMessage;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    String imageUrl = contact.getImage();


                    Person person;
                    if (imageUrl != null && imageUrl.trim().length() != 0) {
                        try {
                            Bitmap bitmap = Glide.with(context)
                                    .asBitmap()
                                    .load(imageUrl)
                                    .apply(new RequestOptions().circleCrop())
                                    .submit(50, 50)
                                    .get();

                            person = new Person.Builder()
                                    .setName(m.getSender())
                                    .setKey(contact.get_id())
                                    .setIcon(Icon.createWithBitmap(bitmap))
                                    .build();
                        } catch (Exception e) {
                            e.printStackTrace();
                            person = new Person.Builder()
                                    .setName(m.getSender())
                                    .setKey(contact.get_id())
                                    .build();
                        }
                    }
                    else {
                        person = new Person.Builder()
                                .setName(m.getSender())
                                .setKey(contact.get_id())
                                .build();
                    }


                    mMessage = new Notification.MessagingStyle.Message(m.getText(), m.getTimestamp(), person);
                }
                else {
                    mMessage = new Notification.MessagingStyle.Message(m.getText(), m.getTimestamp(), m.getSender());
                }
                messagingStyle.addMessage(mMessage);
            }

            notification = new Notification.Builder(context, notificationChannel.getId())
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setColor(ContextCompat.getColor(context, R.color.colorlogo))
                    .setStyle(messagingStyle)
                    .setShowWhen(true)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setDeleteIntent(cancelPendingIntent)
                    .setAutoCancel(true)
                    .setGroup(GROUP_NOTIFICATION_MESSAGE)
                    .build();
        }
        else {
            Log.d("NOTIFICATION HELPER", "NOTIFICATION HELPER not oreo");
            //noinspection deprecation
            NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(context.getString(R.string.you));
            messagingStyle.setGroupConversation(isGroup);
            messagingStyle.setConversationTitle(groupName);

            for (Message m : messages) {
                //noinspection deprecation
                NotificationCompat.MessagingStyle.Message mMessage = new NotificationCompat.MessagingStyle.Message(m.getText(), m.getTimestamp(), m.getSender());
                messagingStyle.addMessage(mMessage);
            }

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setColor(ContextCompat.getColor(context, R.color.colorlogo))
                    .setStyle(messagingStyle)
                    .setShowWhen(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setDeleteIntent(cancelPendingIntent)
                    .setAutoCancel(true)
                    .setGroup(GROUP_NOTIFICATION_MESSAGE)
                    .build();
        }

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle(getSummaryTitle())
                .setSummaryText(getSummaryTitleShort());

        for (String summary : getMessageSummaryItem()) {
            inboxStyle.addLine(summary);
        }

        Intent mainActivityIntent = new Intent(context, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification summaryNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(getSummaryTitleShort())
                .setSmallIcon(R.drawable.ic_stat_name)
                .setColor(ContextCompat.getColor(context, R.color.colorlogo))
                .setShowWhen(true)
                .setStyle(inboxStyle)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup(GROUP_NOTIFICATION_MESSAGE)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setContentIntent(mainActivityPendingIntent)
                .build();

        String tagName;
        if (isGroup) {
            tagName = groupName;
        }
        else {
            tagName = contact.get_id();
        }
        notificationManager.notify(tagName, ID_CHAT, notification);
        notificationManager.notify(SUMMARY_ID, summaryNotification);
    }

    @SuppressWarnings("unused")
    public static void removeAllMessageRC() {
        MESSAGE_RC.clear();

        USED_MESSAGE_RC.clear();
        UNUSED_MESSAGE_RC.clear();
    }

    public static void sendNotificationGeneral(String title, String body, Context context, String tagName, Intent navigateIntent) {
        Log.d("NOTIFICATION HELPER", "NOTIFICATION HELPER general");
        Intent intent;
        PendingIntent pendingIntent;
        intent = navigateIntent;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "SIHMI", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);

            notification = new Notification.Builder(context, notificationChannel.getId())
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setColor(ContextCompat.getColor(context, R.color.colorlogo))
                    .setContentTitle(title)
                    .setContentText(Tools.convertUTF8ToString(body))
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(Tools.convertUTF8ToString(body)))
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        }
        else {
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setColor(ContextCompat.getColor(context, R.color.colorlogo))
                    .setContentTitle(title)
                    .setContentText(Tools.convertUTF8ToString(body))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(Tools.convertUTF8ToString(body)))
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        }

        notificationManager.notify(tagName, ID_GENERAL, notification);
    }

    public static void removeHistoryMessage(String key) {
        MESSAGE.remove(key);
    }

    public static void removeAllHistoryMessage() {
        MESSAGE.clear();
    }

    private static int getCountAllMessage() {
        int count = 0;
        for (String key: MESSAGE.keySet()) {
            List<Message> m = MESSAGE.get(key);
            if (m != null) {
                count += m.size();
            }
        }
        return count;
    }

    private static String getSummaryTitle() {
        int countC = MESSAGE.size();
        int countM = getCountAllMessage();
        String messageName = (countM > 1) ? "messages" : "message";
        String chatName = (countC > 1) ? "chats" : "chat";

        return countM + " new " + messageName + " from " + countC + " " + chatName;
    }

    private static String getSummaryTitleShort() {
        int countC = MESSAGE.size();

        return countC + " new " + (countC > 0 ? "messages" : "message");
    }

    private static List<String> getMessageSummaryItem() {
        List<String> summaries = new ArrayList<>();
        for (String key: MESSAGE.keySet()) {
            List<Message> m = MESSAGE.get(key);
            if (m != null) {
                Message lastMessage = m.get(m.size()-1);
                if (lastMessage.getGroupName() != null) {
                    summaries.add( "(" + lastMessage.getGroupName() + ")" +  lastMessage.getSender() + " " + lastMessage.getText());
                }
                else {
                    summaries.add(lastMessage.getSender() + " " + lastMessage.getText());
                }
            }
        }

        return summaries;
    }
}

