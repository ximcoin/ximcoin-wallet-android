package tech.duchess.luminawallet.view.contacts;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.contacts.Contact;
import tech.duchess.luminawallet.presenter.contacts.ContactListContract;
import tech.duchess.luminawallet.view.common.BaseViewFragment;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class ContactListFragment extends BaseViewFragment<ContactListContract.ContactListPresenter>
        implements ContactListContract.ContactListView {

    @BindView(R.id.btn_add_contact)
    FloatingActionButton addContactButton;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.contact_list_message)
    TextView message;

    private ContactListAdapter adapter;
    private int[] contactColors;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contact_list_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((ContactsFlowManager) activity)
                        .setTitle(getString(R.string.contact_list_fragment_title)));
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        initRecyclerView();
    }

    private void initRecyclerView() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        contactColors = context.getResources().getIntArray(R.array.contact_colors);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setItemAnimator(new SlideUpAnimator());
        adapter = new ContactListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        ViewUtils.addDividerDecoration(recyclerView, context, layoutManager.getOrientation());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && addContactButton.isShown()) {
                    addContactButton.hide();
                } else if (dy < 0 && !addContactButton.isShown()) {
                    addContactButton.show();
                }
            }
        });
    }

    @Override
    public void showLoading(boolean isLoading) {
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((ContactsFlowManager) activity).showLoading(isLoading));
    }

    @Override
    public void showContacts(@NonNull List<Contact> contacts) {
        if (contacts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            message.setText(R.string.contacts_empty_message);
            message.setTextColor(ContextCompat.getColor(activityContext, R.color.colorPrimaryText));
            message.setVisibility(View.VISIBLE);
            adapter.setContacts(contacts);
        } else {
            message.setVisibility(View.GONE);
            adapter.setContacts(contacts);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void goToAddNewContact() {
        ViewUtils.whenNonNull(getActivity(), parentActivity ->
                ((ContactsFlowManager) parentActivity).onAddContactRequested());
    }

    @Override
    public void propagateContactSelection(@NonNull Contact contact) {
        ViewUtils.whenNonNull(getActivity(), parentActivity ->
                ((ContactsFlowManager) parentActivity).onContactSelected(contact));
    }

    @Override
    public void showLoadFailedError() {
        recyclerView.setVisibility(View.GONE);
        message.setText(R.string.contacts_load_error_message);
        message.setTextColor(ContextCompat.getColor(activityContext, R.color.warningColor));
        message.setVisibility(View.VISIBLE);
        adapter.setContacts(new ArrayList<>());
    }

    @OnClick(R.id.btn_add_contact)
    public void onUserRequestedAddContact() {
        presenter.onUserRequestAddContact();
    }

    private int getContactColor(int colorPosition) {
        if (contactColors == null || colorPosition > contactColors.length || colorPosition < 0) {
            return activityContext.getResources().getColor(R.color.contact_blue);
        } else {
            return contactColors[colorPosition];
        }
    }

    private class ContactListAdapter extends RecyclerView.Adapter<ContactViewHolder> {
        private final ArrayList<Contact> contacts = new ArrayList<>();

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ContactViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, int position) {
            holder.bindData(contacts.get(position));
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        void setContacts(@NonNull List<Contact> contacts) {
            this.contacts.clear();
            this.contacts.addAll(contacts);
            notifyDataSetChanged();
        }
    }

    class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.initials)
        MaterialLetterIcon initials;

        @BindView(R.id.name)
        TextView name;

        ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindData(@NonNull Contact contact) {
            initials.setShapeColor(ContactUtil.getContactColor(activityContext,
                    contact.getColorIndex()));
            initials.setLetter(contact.getName());
            name.setText(contact.getName());
        }

        @Override
        public void onClick(View v) {
            presenter.onUserSelectedContact(adapter.contacts.get(getAdapterPosition()));
        }
    }
}
