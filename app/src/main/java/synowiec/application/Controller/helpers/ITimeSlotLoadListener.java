package synowiec.application.Controller.helpers;

import java.util.List;

public interface ITimeSlotLoadListener {
    void onTimeSlotLoadSuccess(List<String> timeSlotList, int firstHourPosition);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();
}
