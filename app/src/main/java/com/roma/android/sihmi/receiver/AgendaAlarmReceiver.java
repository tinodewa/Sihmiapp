package com.roma.android.sihmi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.roma.android.sihmi.helper.AgendaScheduler;
import com.roma.android.sihmi.helper.NotificationHelper;
import com.roma.android.sihmi.view.activity.AgendaDetailActivity;

public class AgendaAlarmReceiver extends BroadcastReceiver {
    public static String AGENDA_ID = "__agenda_id__";
    public static String NOTIFICATION_TITLE = "__notification_title__";
    public static String NOTIFICATION_CONTENT = "__notification_content__";

    @Override
    public void onReceive(Context context, Intent intent) {
        String agendaId = intent.getStringExtra(AGENDA_ID);
        String notifTitle = intent.getStringExtra(NOTIFICATION_TITLE);
        String notifContent = intent.getStringExtra(NOTIFICATION_CONTENT);

        Intent navigateIntent = new Intent(context, AgendaDetailActivity.class)
                .putExtra(AgendaDetailActivity.ID_AGENDA, agendaId);

        NotificationHelper.sendNotificationGeneral(notifTitle, notifContent, context, NotificationHelper.TYPE_AGENDA, navigateIntent);

        AgendaScheduler.setupUpcomingAgendaNotifier(context);
    }
}
