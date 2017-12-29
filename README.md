# TestJobServer
双进程守护案例

1.android 5.0之前采用aidl，双service配合AlarmManager实现后台驻留
2.Android 5.0之后采用JobSchedule进程调度器进行操作，效果会比AlarmManager好
