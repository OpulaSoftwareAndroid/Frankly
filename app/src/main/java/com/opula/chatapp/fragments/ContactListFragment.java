package com.opula.chatapp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.model.ContactVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactListFragment extends Fragment {
    SharedPreference sharedPreference;
    RecyclerView recycler_contact;
    List<ContactVO> userList;
    List<ContactVO> selectedUserList = new ArrayList<>();
    Button btnNext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactlist, container, false);

        MainActivity.hideFloatingActionButton();

        sharedPreference = new SharedPreference();
        initViews(view);

        recycler_contact.setHasFixedSize(true);
        recycler_contact.setLayoutManager(new LinearLayoutManager(getActivity()));
        userList = new ArrayList<>();

        getContactList();

        AllContactsAdapter contactAdapter = new AllContactsAdapter(userList, getContext());
        recycler_contact.setAdapter(contactAdapter);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < selectedUserList.size(); i++) {
                    map.put(selectedUserList.get(i).getContactNumber(), selectedUserList.get(i).getContactName());
                }
                Log.d("Contact_User", map.toString()+"/");
                Set keys = map.keySet();

            }
        });
        return view;
    }

    private void getContactList() {
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (phones != null) {
            while (phones.moveToNext()) {
                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String avatarUriString = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                Uri avatarUri = null;
                if (avatarUriString != null)
                    avatarUri = Uri.parse(avatarUriString);

                // get
                if (Integer.parseInt(phones.getString(phones.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    while (pCur != null && pCur.moveToNext()) {
                        ContactVO user = new ContactVO();
                        user.setContactName(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                        user.setContactNumber(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        userList.add(user);
                    }
                    pCur.close();
                }
            }
            phones.close();
        }
    }

    private void initViews(View view) {
        recycler_contact = view.findViewById(R.id.recycler_contact);
        btnNext = view.findViewById(R.id.btnNext);
    }

    //Contact list Adapter
    public class AllContactsAdapter extends RecyclerView.Adapter<AllContactsAdapter.ContactViewHolder> {

        private List<ContactVO> contactVOList;
        private Context mContext;
        private SparseBooleanArray itemStateArray = new SparseBooleanArray();


        public AllContactsAdapter(List<ContactVO> contactVOList, Context mContext) {
            this.contactVOList = contactVOList;
            this.mContext = mContext;
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.single_contact_view, null);
            ContactViewHolder contactViewHolder = new ContactViewHolder(view);
            return contactViewHolder;
        }

        @Override
        public void onBindViewHolder(final ContactViewHolder holder, final int position) {
            ContactVO contactVO = contactVOList.get(position);
            holder.tvContactName.setText(contactVO.getContactName());
            holder.tvPhoneNumber.setText(contactVO.getContactNumber());

            holder.bind(position);
            holder.cbContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = position;
                    if (!itemStateArray.get(adapterPosition, false)) {
                        holder.cbContact.setChecked(true);
                        itemStateArray.put(adapterPosition, true);
                        ContactVO user = new ContactVO();
                        user.setContactName(holder.tvContactName.getText().toString());
                        user.setContactNumber(holder.tvPhoneNumber.getText().toString());
                        selectedUserList.add(user);
                    } else {
                        holder.cbContact.setChecked(false);
                        itemStateArray.put(adapterPosition, false);
                        for (int i = 0; i < selectedUserList.size(); i++) {
                            if (holder.tvPhoneNumber.getText().toString().contains(selectedUserList.get(i).getContactNumber())) {
                                selectedUserList.remove(i);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return contactVOList.size();
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder {

            CircleImageView ivContactImage;
            TextView tvContactName;
            TextView tvPhoneNumber;
            CheckBox cbContact;

            public ContactViewHolder(View itemView) {
                super(itemView);
                ivContactImage = itemView.findViewById(R.id.ivContactImage);
                tvContactName = itemView.findViewById(R.id.tvContactName);
                tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
                cbContact = itemView.findViewById(R.id.cbContact);
            }

            void bind(int arg1) {
                // use the sparse boolean array to check
                if (!itemStateArray.get(arg1, false)) {
                    cbContact.setChecked(false);
                } else {
                    cbContact.setChecked(true);
                }
            }
        }
    }
}