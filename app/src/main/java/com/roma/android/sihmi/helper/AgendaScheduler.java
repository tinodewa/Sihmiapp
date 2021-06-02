package com.roma.android.sihmi.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Agenda;
import com.roma.android.sihmi.receiver.AgendaAlarmReceiver;
import com.roma.android.sihmi.utils.Tools;

import androidx.annotation.NonNull;

public class AgendaScheduler {
    private static final int GENERAL_REQUEST_CODE = 1;

    public static void setupUpcomingAgendaNotifier(Context context) {
        AppDb appDb = CoreApplication.get().getConstant().getAppDb();

        Agenda upcomingAgenda = appDb.agendaDao().getUpcomingAgenda(System.currentTimeMillis());

        if (upcomingAgenda != null) {
            scheduleAgenda(context, upcomingAgenda);
        }
        else {
            cancelAgenda(context);
        }
    }

    public static void cancelAgenda(@NonNull Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AgendaAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, GENERAL_REQUEST_CODE, intent, 0);

        alarmManager.cancel(pendingIntent);
    }

    private static void scheduleAgenda(Context context, Agenda agenda) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        String agendaId = agenda.get_id();
        String agendaName = agenda.getNama();
        long agendaExpire = agenda.getDate_expired();
        String agendaLocation = agenda.getTempat();

        String notifTitle = context.getString(R.string.agenda_dimulai);
        String notifContent = agendaName + " " + context.getString(R.string.pada_tanggal) + " " + Tools.getDateTimeLaporanFromMillis(agendaExpire) + " " + context.getString(R.string.di_agenda) + " " + agendaLocation;

        Intent intent = new Intent(context, AgendaAlarmReceiver.class)
                .putExtra(AgendaAlarmReceiver.AGENDA_ID, agendaId)
                .putExtra(AgendaAlarmReceiver.NOTIFICATION_TITLE, notifTitle)
                .putExtra(AgendaAlarmReceiver.NOTIFICATION_CONTENT, notifContent);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, GENERAL_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, agendaExpire, pendingIntent);
    }
}
