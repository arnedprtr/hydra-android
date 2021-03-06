package be.ugent.zeus.hydra.activities.minerva;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.viewpager.MinervaCoursePagerAdapter;

/**
 * Activity that displays a certain course.
 *
 * @author Niko Strijbol
 */
public class CourseActivity extends ToolbarActivity {

    public static final String ARG_COURSE = "argCourse";
    public static final String ARG_TAB = "argTab";

    public static final int TAB_INFO = 0;
    public static final int TAB_ANNOUNCEMENTS = 1;
    public static final int TAB_AGENDA = 2;

    private static final String ONLINE_URL_DESKTOP = "https://minerva.ugent.be/main/course_home/course_home.php?cidReq=%s";
    private static final String ONLINE_URL_MOBILE = "https://minerva.ugent.be/mobile/courses/%s";

    private Course course;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minerva_course);

        Intent intent = getIntent();
        course = intent.getParcelableExtra(ARG_COURSE);

        TabLayout tabLayout = $(R.id.tab_layout);
        ViewPager viewPager = $(R.id.pager);

        viewPager.setAdapter(new MinervaCoursePagerAdapter(getSupportFragmentManager(), course));

        viewPager.setCurrentItem(getIntent().getIntExtra(ARG_TAB, TAB_ANNOUNCEMENTS), false);
        tabLayout.setupWithViewPager(viewPager);

        getToolBar().setTitle(course.getTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.minerva_course_link:
                NetworkUtils.maybeLaunchBrowser(this, getOnlineUrl());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_minerva_course, menu);
        tintToolbarIcons(menu, R.id.minerva_course_link);
        return super.onCreateOptionsMenu(menu);
    }

    private String getOnlineUrl() {
        //TODO: use preferences
        return String.format(ONLINE_URL_DESKTOP, course.getId());
    }

    public static void start(Context context, Course course) {
        Intent intent = new Intent(context, CourseActivity.class);
        intent.putExtra(ARG_COURSE, (Parcelable) course);
        context.startActivity(intent);
    }
}