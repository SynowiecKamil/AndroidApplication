package synowiec.application.Controller.Helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import synowiec.application.Controller.Fragments.ReservationConfirmFragment;
import synowiec.application.Controller.Fragments.ReservationTreatmentFragment;
import synowiec.application.Controller.Fragments.ReservationCalendarFragment;

public class MyViewPagerAdapter extends FragmentPagerAdapter {


    public MyViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i){
        switch(i){
            case 0:
                return ReservationCalendarFragment.getInstance();
            case 1:
                return ReservationTreatmentFragment.getInstance();
            case 2:
                return ReservationConfirmFragment.getInstance();
        }
        return null;
    }



    @Override
    public int getCount() {
        return 3;
    }
}
