package com.byteshaft.callnote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements Switch.OnCheckedChangeListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    Helpers mHelpers;
    private boolean mViewCreated;
    DataBaseHelpers mDbHelpers;
    ArrayList<String> arrayList;
    ListView listView;
    TextView textViewTitle;
    private ArrayList<String> mNoteSummaries;
    private OverlayHelpers mOverlayHelpers;
    private Switch mToggleSwitch;
    ArrayAdapter<String> mModeAdapter;
//    private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689F39")));
        textViewTitle = (TextView) findViewById(R.id.title);
        mHelpers = new Helpers(getApplicationContext());
        mToggleSwitch = (Switch) findViewById(R.id.aSwitch);
        mDbHelpers = new DataBaseHelpers(getApplicationContext());
        mToggleSwitch.setOnCheckedChangeListener(this);
        mOverlayHelpers = new OverlayHelpers(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToggleSwitch.setChecked(mHelpers.isServiceSettingEnabled());
        arrayList = mDbHelpers.getAllPresentNotes();
        mNoteSummaries = mDbHelpers.getDescriptions();
        mModeAdapter = new NotesArrayList(this, R.layout.row, arrayList);
        listView = (ListView) findViewById(R.id.listView_main);
        listView.setAdapter(mModeAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setDivider(null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_overlay:
//                if (mViewCreated) {
//                    mOverlayHelpers.removePopupNote();
//                    mViewCreated = false;
//                } else {
//                    mOverlayHelpers.showSingleNoteOverlay("Hey yo", "Get some eggs");
//                    mViewCreated = true;
//                }
            case R.id.action_addNote:
                startActivity(new Intent(this, NoteActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689F39")));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            startService(new Intent(this, OverlayService.class));
        } else {
            stopService(new Intent(this, OverlayService.class));
        }
        mHelpers.saveServiceStateEnabled(isChecked);
    }

    public void openActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(this, NoteActivity.class));
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("note_title", arrayList.get(position));
        intent.putExtra("note_summary", mNoteSummaries.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        System.out.println(parent.getItemAtPosition(position));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                DataBaseHelpers dataBaseHelpers = new DataBaseHelpers(getApplicationContext());
                dataBaseHelpers.deleteItem(SqliteHelpers.NOTES_COLUMN, (String)
                        parent.getItemAtPosition(position));
                mModeAdapter.remove(mModeAdapter.getItem(position));
                mModeAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        return true;
    }

    class NotesArrayList extends ArrayAdapter<String> {

            public NotesArrayList(Context context, int resource, ArrayList<String> videos) {
                super(context, resource, videos);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    LayoutInflater inflater = getLayoutInflater();
                    convertView = inflater.inflate(R.layout.row, parent, false);
                    holder = new ViewHolder();
                    holder.title = (TextView) convertView.findViewById(R.id.FilePath);
                    holder.summary = (TextView) convertView.findViewById(R.id.summary);
                    holder.thumbnail = (ImageView) convertView.findViewById(R.id.Thumbnail);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.title.setText(arrayList.get(position));
                holder.summary.setText(mNoteSummaries.get(position));
                Uri uri = Uri.parse(mDbHelpers.getIconLinkForNote(arrayList.get(position)));
                holder.thumbnail.setImageURI(uri);
                return convertView;
            }
        }

    static class ViewHolder {
        public TextView title;
        public TextView summary;
        public ImageView thumbnail;
    }
}